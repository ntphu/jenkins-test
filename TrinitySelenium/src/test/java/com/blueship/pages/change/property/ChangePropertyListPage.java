package com.blueship.pages.change.property;

import org.openqa.selenium.By;

import com.blueship.common.Constants;
import com.blueship.pages.change.checkincheckout.CheckInCheckOutRequestListPage;
import com.blueship.atlibs.Page;
import com.blueship.atlibs.TestBase;
import com.blueship.atlibs.Utils;

public class ChangePropertyListPage extends Page {

	public ChangePropertyListPage(TestBase action) {
		super(action);
		this.setRepoFile(action.config.getProperty("REPO_OBJECT_TRINITY_PATH"));
		this.setLocationSheet(Constants.SHEET_CHANGE_PROPERTY_LIST);
		this.initLocationMap();
	}

	public CreateChangePropertyPage gotoCreateChangeProperty() {
		action.click(getLocation("CreateNewChangeProperty"));
		Utils.pause(Constants.WAIT_TIME);
		return new CreateChangePropertyPage(action);
	}

	public ChangePropertyDetailPage gotoChangePropertyDetail() {
		action.click(action.getElement(getLocation("FirstChange"))
				.findElement(getLocation("FirstChangeId")));
		Utils.pause(Constants.WAIT_TIME);
		return new ChangePropertyDetailPage(action);
	}

	public String getNewChangeId() {
		String result = "";
		if (action.getElements(getLocation("FirstChange")).size() > 0) {
			result = action.getElement(getLocation("FirstChange"))
					.findElement(getLocation("FirstChangeId")).getText();
		}
		return result;
	}
	
	public CheckInCheckOutRequestListPage getCheckInCheckOutRequestListPage(){
		action.click(By.xpath("//*[@id='lotSubNav']/ul/li[2]/a/span"));
		return new CheckInCheckOutRequestListPage(action);
	}
	
	
}
