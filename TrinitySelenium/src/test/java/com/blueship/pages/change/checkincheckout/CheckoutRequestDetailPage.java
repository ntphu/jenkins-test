package com.blueship.pages.change.checkincheckout;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebElement;

import com.blueship.atlibs.Page;
import com.blueship.atlibs.TestBase;
import com.blueship.atlibs.Utils;
import com.blueship.common.Constants;

public class CheckoutRequestDetailPage extends Page {
	private String publicPath;

	private String file;

	public CheckoutRequestDetailPage(TestBase action) {
		super(action);
		this.setRepoFile(action.config.getProperty("REPO_OBJECT_TRINITY_PATH"));
		this.setLocationSheet(Constants.SHEET_CHECKOUT_REQUEST_DETAIL);
		this.initLocationMap();
	}

	public void changeFile(String file) throws IOException {
		WebElement checkout = action.getElement(getLocation("CheckoutPath"));
		WebElement checkin = action.getElement(getLocation("CheckinPath"));
		String path_src = checkout.getAttribute("data-clipboard-text");
		String path_des = checkin.getAttribute("data-clipboard-text");
		path_src = path_src.replace(Constants.PUBLIC_PATH_DEFAULT, publicPath);
		path_des = path_des.replace(Constants.PUBLIC_PATH_DEFAULT, publicPath);
		File fcheckout = new File(path_src);
		if (fcheckout.exists()) {
			FileUtils.copyDirectory(fcheckout, new File(path_des));
		}
		String[] splitFolder = file.split("/");
		int size = splitFolder.length;
		String[] splitFile = splitFolder[size - 1].split(",");
		for (int i = 0; i < size - 1; i++) {
			path_des += "\\" + splitFolder[i];
		}
		info(path_src);
		info(path_des);

		for (int i = 0; i < splitFile.length; i++) {
			String pathFile = path_des + "\\" + splitFile[i];
			File f = new File(pathFile);
			if (f.exists()) {
				FileWriter txtFileHandle = new FileWriter(pathFile);
				txtFileHandle.write(new SimpleDateFormat("yyyy.MM.dd HH.mm.ss")
						.format(Calendar.getInstance().getTime()));
				txtFileHandle.flush();
				txtFileHandle.close();
			}

		}
	}

	public void getCheckoutRequestDetail() {
		file = "";
		while (action.getElements(getLocation("FileViewNameFolder")).size() > 0) {
			WebElement folder = action
					.getElement(getLocation("FileViewNameFolder"));
			file += folder.getText() + "/";
			info(file);
			action.click(folder);
			Utils.pause(Constants.WAIT_TIME);

		}
		List<WebElement> files = action
				.getElements(getLocation("FileViewNameFile"));
		for (WebElement ef : files) {
			file += ef.getText() + ",";

		}

		if (file != null && file.length() > 0
				&& file.charAt(file.length() - 1) == ',') {
			file = file.substring(0, file.length() - 1);
		}
		info(file);

	}
	
	public CreateNewCheckinRequestPage gotoCreateNewCheckinRequestPage(){
		action.click(getLocation("SubmitCheckinRequestBtn"));
		return new CreateNewCheckinRequestPage(action);
	}
	
	public String getPublicPath() {
		return publicPath;
	}

	public void setPublicPath(String publicPath) {
		this.publicPath = publicPath;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
