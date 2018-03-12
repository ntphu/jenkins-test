package com.blueship.pages.lot;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import com.blueship.atlibs.Page;
import com.blueship.atlibs.TestBase;
import com.blueship.common.Constants;
import com.blueship.pages.TrinityDashBoardPage;

public class CreateNewLotPage extends Page {
	private static final Logger LOGGER = Logger
			.getLogger(CreateNewLotPage.class);

	public CreateNewLotPage(TestBase action) {
		super(action);
		this.setRepoFile(action.config.getProperty("REPO_OBJECT_TRINITY_PATH"));
		this.setLocationSheet(Constants.SHEET_CREATE_NEW_LOT_TRINYTY);
		this.initLocationMap();
	}

	public String isCreateNewLotPage() {	
		String result = Constants.IN_CREATE_LOT_PAGE;
		List<WebElement> headerTitle = action.getElements(getLocation("HeaderTitle"));
		List<WebElement> submitCreateNewLotBtn = action.getElements(getLocation("SubmitCreateNewLotBtn"));
		if (headerTitle.size() == 0 || submitCreateNewLotBtn.size() == 0) {
			result = Constants.NOT_IN_CREATE_LOT_PAGE;
		}
		return result;
	}

	public TrinityDashBoardPage createNewLot(String lotName, String summary,
			String content, String baselineTag, String publicPath,
			String privatePath, String authorizedGroup, String emailGroup,
			String inputModule, String buildEnviroment01,
			String buildEnviroment02, String releaseEnvironment) {
		action.click(getLocation("CheckPublicPath"));
		action.click(getLocation("CheckPrivatePath"));
		action.type(getLocation("InputLotName"), lotName, true);
		action.type(getLocation("InputSummary"), summary, true);
		action.type(getLocation("InputContent"), content, true);
		action.type(getLocation("InputBaselineTag"), baselineTag, true);
		action.type(getLocation("PublicPath"), publicPath, true);
		action.type(getLocation("PrivatePath"), privatePath, true);

		LOGGER.info("get element");

		WebElement authorizedSection = action.getElement(getLocation("AuthorizedSection"));

		List<WebElement> listAuthorized = authorizedSection
				.findElements(getLocation("ListAuthorized"));
		String[] listAutho = authorizedGroup.split(",");
		for (WebElement au : listAuthorized) {
			WebElement label = au.findElement(getLocation("AuthorizedLabel"));
			WebElement checkbox = au.findElement(getLocation("AuthorizedCheckbox"));
			if (checkbox.isSelected() && listAutho.length > 0) {
				action.click(checkbox);
			}
			// list input data
			for (int i = 0; i < listAutho.length; i++) {
				if (listAutho[i].equalsIgnoreCase(label.getText())) {
					action.click(checkbox);

				}
			}

		}

		// WebElement elementEmail = listSection.get(3);
		WebElement emailSection = action.getElement(getLocation("EmailSection"));
		List<WebElement> listEmail = emailSection
				.findElements(getLocation("ListEmail"));

		String[] listDataEmail = emailGroup.split(",");
		for (WebElement e : listEmail) {
			WebElement label = e.findElement(getLocation("EmailLabel"));
			WebElement checkbox = e.findElement(getLocation("EmailCheckbox"));
			if (checkbox.isSelected()) {
				action.click(checkbox);
			}
			for (int i = 0; i < listDataEmail.length; i++) {
				if (listDataEmail[i].equalsIgnoreCase(label.getText())) {
					action.click(checkbox);
				}
			}
		}

		// select module
		WebElement moduleSection = action.getElement(getLocation("ModuleSection"));

		List<WebElement> listModule = moduleSection.findElements(getLocation("ListModule"));
		String[] listDataModule = inputModule.split(",");
		for (WebElement e : listModule) {
			WebElement label = e.findElement(getLocation("ModuleLabel"));
			WebElement checkbox = e.findElement(getLocation("ModuleCheckbox"));
			if (checkbox.isSelected()) {
				action.click(checkbox);
			}
			for (int i = 0; i < listDataModule.length; i++) {
				if (listDataModule[i].equalsIgnoreCase(label.getText())) {
					action.click(checkbox);
				}
			}
		}

		// select buildEnvironment
		List<String> buildEnvInput = new ArrayList<String>();
		buildEnvInput.add(buildEnviroment01);
		buildEnvInput.add(buildEnviroment02);
		List<WebElement> listBuildEnvsTr = action.getElements(getLocation("BuildEnvsTr"));
		for (WebElement e : listBuildEnvsTr) {
			String envName = e.findElement(getLocation("BuildEnvsName")).getText();
			String envId = e.findElement(getLocation("BuildEnvsId")).getText();
			List<WebElement> listLabel1 = e.findElements(getLocation("BuildEnvsLabel"));
			List<WebElement> listRadio1 = e.findElements(getLocation("BuildEnvsRadio"));
			
			buildEnvLoop:
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
				if (envName.equals(inputEnvName) && envId.equals(inputEnvId)) {
					for (String s : inputEnvValue) {
						for (int i = 0; i < listLabel1.size(); i++) {
							if (s.equalsIgnoreCase(listLabel1.get(i).getText())) {
								action.click(listRadio1.get(i));
								break buildEnvLoop;
							}
						}
					}
				}
			}			
		}

		// select releaseEnvironment
		String[] listDataReleaseInput = releaseEnvironment.split(",");
		
		List<WebElement> listReleaseEnvTr = action.getElements(getLocation("ReleaseEnvsTr"));
		for (WebElement e : listReleaseEnvTr) {
			String releaseEnvName = e.findElement(getLocation("ReleaseEnvsName")).getText();
			String releaseEnvId = e.findElement(getLocation("ReleaseEnvsId")).getText();
			WebElement checkbox = e.findElement(getLocation("ReleaseEnvsCheckbox"));
			
			releaseEnvLoop:
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
				if (releaseEnvName.equals(inputName) && releaseEnvId.equals(inputId)) {
					action.check(checkbox);
					break releaseEnvLoop;				
				}
			}			
		}

		action.click(getLocation("SubmitCreateNewLotBtn"));
		WebElement elementYes = findDynamicElement(getLocation("ConfirmYesCreateNewLot"), Constants.WAIT_DISPLAY);

		action.click(elementYes);
		return new TrinityDashBoardPage(action);

	}
}
