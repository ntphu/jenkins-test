package com.blueship.pages.change.checkincheckout;

import com.blueship.atlibs.Page;
import com.blueship.atlibs.TestBase;
import com.blueship.common.Constants;

public class CreateNewCheckinRequestPage extends Page {
	public CreateNewCheckinRequestPage(TestBase action) {
		super(action);
		this.setRepoFile(action.config.getProperty("REPO_OBJECT_TRINITY_PATH"));
		this.setLocationSheet(Constants.SHEET_CREATE_NEW_CHECKIN_REQUEST);
		this.initLocationMap();
	}
	
	public CheckinRequestProgressPage createNewCheckinRequest(){
		action.click(getLocation("SubmitCheckinRequestBtn"));
		return new CheckinRequestProgressPage(action);
	}
}
