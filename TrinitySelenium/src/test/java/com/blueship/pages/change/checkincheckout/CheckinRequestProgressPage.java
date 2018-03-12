package com.blueship.pages.change.checkincheckout;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.blueship.atlibs.Page;
import com.blueship.atlibs.TestBase;
import com.blueship.common.Constants;

public class CheckinRequestProgressPage extends Page {
	public CheckinRequestProgressPage(TestBase action) {
		super(action);
		this.setRepoFile(action.config.getProperty("REPO_OBJECT_TRINITY_PATH"));
		this.setLocationSheet(Constants.SHEET_CHECKIN_REQUEST_PROCESS);
		this.initLocationMap();
	}
	
	public String isCheckinRequestProcessPage(){
		String result = Constants.NOT_IN_CHECKIN_REQUEST_PROGRESS_PAGE;
		List<WebElement> checkinRequestProgress = action.getElements(getLocation("CheckinRequestProgressTitle"));
		if (1 == checkinRequestProgress.size()){
			result = Constants.IN_CHECKIN_REQUEST_PROGRESS_PAGE;
		}
		return result;
	}
}
