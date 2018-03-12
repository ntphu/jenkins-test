package com.blueship.pages.lot;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.blueship.common.Constants;
import com.blueship.pages.TrinityDashBoardPage;
import com.blueship.pages.change.property.ChangePropertyListPage;
import com.blueship.atlibs.Page;
import com.blueship.atlibs.TestBase;

public class LotDeailsPage extends Page {
	private String lotName;
	private String summary;
	private String content;
	private String baselineTag;
	private String publicPath;
	private String privatePath;
	private String authorizedGroup;
	private String emailGroup;
	private String moduleName;
	private List<BuildEnvs> listBuildEnvs;
	private List<ReleaseEnvs> listReleaseEnvs;

	public LotDeailsPage(TestBase action) {
		super(action);
		this.setRepoFile(action.config.getProperty("REPO_OBJECT_TRINITY_PATH"));
		this.setLocationSheet(Constants.SHEET_LOT_DETAIL);
		this.initLocationMap();
	}

	public TrinityDashBoardPage gotoDashBoardPage() {
		action.click(getLocation("Dashboard"));
		return new TrinityDashBoardPage(action);
	}

	public ChangePropertyListPage gotoChangePropertyListPage() {
		WebElement firstLotElementChange = action
				.waitForAndGetElement(getLocation("ChangeProperty"));
		action.click(firstLotElementChange);
		return new ChangePropertyListPage(action);
	}

	public void getLotInfo() {
		WebElement sectionBasicInfo = action.getElement(getLocation("SectionBasic"));

		lotName = sectionBasicInfo.findElement(getLocation("LotName")).getText();
		summary = sectionBasicInfo.findElement(getLocation("Summary")).getText();
		content = sectionBasicInfo.findElement(getLocation("Content")).getText();
		baselineTag = sectionBasicInfo.findElement(getLocation("BaselineTag")).getText();
		publicPath = action.getElement(getLocation("PublicPath")).getText();
		privatePath = action.getElement(getLocation("PrivatePath")).getText();

		WebElement sectionAuthorized = action.getElement(getLocation("SectionAuthorized"));
		List<WebElement> listAuthor = sectionAuthorized
				.findElements(getLocation("ListAuthorized"));
		authorizedGroup = "";
		for (WebElement e : listAuthor) {
			String prefix = "";
			if (!authorizedGroup.isEmpty()) {
				prefix = ",";
			}
			authorizedGroup += prefix + e.getText();
		}

		WebElement sectionEmail = action.getElement(getLocation("SectionEmail"));
		List<WebElement> listEmail = sectionEmail
				.findElements(getLocation("ListEmail"));
		emailGroup = "";
		for (WebElement e : listEmail) {
			String prefix = "";
			if (!emailGroup.isEmpty()) {
				prefix = ",";
			}
			emailGroup += prefix + e.getText();
		}

		WebElement sectionModule = action.getElement(getLocation("SectionModule"));
		List<WebElement> listModule = sectionModule
				.findElements(getLocation("ListModule"));
		moduleName = "";
		for (WebElement e : listModule) {
			String prefix = "";
			if (!moduleName.isEmpty()) {
				prefix = ",";
			}
			moduleName += prefix + e.getText();
		}


		List<WebElement> listBuildEnvsTr = action.getElements(getLocation("BuildEnvsTr"));
		listBuildEnvs = new ArrayList<BuildEnvs>();

		for (WebElement e : listBuildEnvsTr) {
			String name = e.findElement(getLocation("BuildEnvsName")).getText();
			String id = e.findElement(getLocation("BuildEnvsId")).getText();
			String value = e.findElement(getLocation("BuildEnvsValue")).getText();
			listBuildEnvs.add(new BuildEnvs(name, id, value));
		}

		List<WebElement> listReleaseEnvTr = action.getElements(getLocation("ReleaseEnvsTr"));
		listReleaseEnvs = new ArrayList<ReleaseEnvs>();
		for (WebElement e : listReleaseEnvTr) {
			String releaseEnvName = e.findElement(getLocation("ReleaseEnvsName")).getText();
			String releaseEnvId = e.findElement(getLocation("ReleaseEnvsId")).getText();
			listReleaseEnvs.add(new ReleaseEnvs(releaseEnvName, releaseEnvId));
		}

	}
	
	public String verifyBuildEnvs(String buildEnviroment01, String buildEnviroment02){
		List<String> buildEnvInput = new ArrayList<String>();
		buildEnvInput.add(buildEnviroment01);
		buildEnvInput.add(buildEnviroment02);
		int count = 0;
		String stringListBuild = "";
		for (BuildEnvs env : listBuildEnvs) {
			stringListBuild += "name=" + env.getName() + "id=" + env.getId() + "value=" + env.getValue();
			for (String buildEnv : buildEnvInput) {
				String[] inputBuildEnv = buildEnv.split(";");
				String inputEnvName = "";
				String inputEnvId = "";
				String[] inputEnvValue = new String[2];
				for (String s : inputBuildEnv) {
					if (s.matches("name=.*")) {
						inputEnvName = s.split("name=")[1];
						continue;
					} else if (s.matches("id=.*")) {
						inputEnvId = s.split("id=")[1];
						continue;
					} else if (s.matches("value=.*")) {
						inputEnvValue = s.split("value=")[1].split("\\|\\|");
						continue;
					}
				}
				if (env.getName().equals(inputEnvName) && env.getId().equals(inputEnvId)) {
					for (String s : inputEnvValue) {
						if (env.getValue().equals(s)) {
							count ++;
						}
					}
				}
			}			
		}
		return count == 2 ? Constants.BUILD_ENVIRONMENT_IS_SAME : stringListBuild;
	}
	
	public String verifyReleaseEnvs(String inputReleaseEnvs) {

		int count = 0;
		String stringListRelease = "";
		String[] listDataReleaseInput = inputReleaseEnvs.split(",");
		for (ReleaseEnvs rEnv : listReleaseEnvs) {
			if (!stringListRelease.equals("")){
				stringListRelease += ",";
			}
			stringListRelease += "name=" + rEnv.getName() + "id=" + rEnv.getId();
			for (String r : listDataReleaseInput) {
				String[] inputReleaseEnv = r.split(";");
				String inputName = "";
				String inputId = "";

				for (String s : inputReleaseEnv) {
					if (s.matches("name=.*")) {
						inputName = s.split("name=")[1];
						continue;
					} else if (s.matches("id=.*")) {
						inputId = s.split("id=")[1];
						continue;
					}
				}
				if (rEnv.getName().equals(inputName) && rEnv.getId().equals(inputId)) {
					count++;
				}
			}
		}
		return count == listDataReleaseInput.length ? Constants.RELEASE_ENVIRONMENT_IS_SAME : stringListRelease;
	}

	public String getLotName() {
		return lotName;
	}

	public void setLotName(String lotName) {
		this.lotName = lotName;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBaselineTag() {
		return baselineTag;
	}

	public void setBaselineTag(String baselineTag) {
		this.baselineTag = baselineTag;
	}

	public String getPublicPath() {
		return publicPath;
	}

	public void setPublicPath(String publicPath) {
		this.publicPath = publicPath;
	}

	public String getPrivatePath() {
		return privatePath;
	}

	public void setPrivatePath(String privatePath) {
		this.privatePath = privatePath;
	}

	public String getAuthorizedGroup() {
		return authorizedGroup;
	}

	public void setAuthorizedGroup(String authorizedGroup) {
		this.authorizedGroup = authorizedGroup;
	}

	public String getEmailGroup() {
		return emailGroup;
	}

	public void setEmailGroup(String emailGroup) {
		this.emailGroup = emailGroup;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public List<BuildEnvs> getListBuildEnvs() {
		return listBuildEnvs;
	}

	public void setListBuildEnvs(List<BuildEnvs> listBuildEnvs) {
		this.listBuildEnvs = listBuildEnvs;
	}

	public List<ReleaseEnvs> getListReleaseEnvs() {
		return listReleaseEnvs;
	}

	public void setListReleaseEnvs(List<ReleaseEnvs> listReleaseEnvs) {
		this.listReleaseEnvs = listReleaseEnvs;
	}

	class BuildEnvs {
		private String name;
		private String id;
		private String value;
	
		public BuildEnvs(String name, String id, String value) {
			super();
			this.name = name;
			this.id = id;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
	
	class ReleaseEnvs {
		private String name;
		private String id;

		public ReleaseEnvs(String name, String id) {
			super();
			this.name = name;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

}
