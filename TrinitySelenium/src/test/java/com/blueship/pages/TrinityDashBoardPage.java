package com.blueship.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.log4testng.Logger;

import com.blueship.atlibs.Page;
import com.blueship.atlibs.TestBase;
import com.blueship.atlibs.Utils;
import com.blueship.common.Constants;
import com.blueship.pages.lot.CreateNewLotPage;
import com.blueship.pages.lot.LotDeailsPage;

public class TrinityDashBoardPage extends Page {
	public TrinityDashBoardPage(TestBase action) {
		// call super constructor
		super(action);

		this.setRepoFile(action.config.getProperty("REPO_OBJECT_TRINITY_PATH"));
		this.setLocationSheet(Constants.SHEET_DASH_BOAD_TRINYTY);

		// load object location
		this.initLocationMap();
	}

	public String isDashBoard() {
		String result = Constants.IN_DASHBOARD;
		List<WebElement> createNewLotBtn = action.getElements(getLocation("CreateNewLotBtn"));
		List<WebElement> listLot = action.getElements(getLocation("ListLot"));
		List<WebElement> listNotification = action.getElements(getLocation("ListNotification"));
		List<WebElement> listRequest= action.getElements(getLocation("ListRequest"));
		List<WebElement> listRecentUpdate = action.getElements(getLocation("ListRecentUpdate"));
		if (createNewLotBtn.size() == 0 || listLot.size() == 0
				|| listNotification.size() == 0 || listRequest.size() == 0
				|| listRecentUpdate.size() == 0) {
			result = Constants.NOT_IN_DASHBOARD;
		}
		return result;
	}

	public boolean isLogout() {
		action.click(getLocation("LogOut"));
		boolean logoutDisplay = action.isDisplay(getLocation("CreateNewLotBtn"));
		if (logoutDisplay) {
			Assert.assertEquals(logoutDisplay, false, "in dash board page");
		}
		return logoutDisplay;
	}
	
	public CreateNewLotPage gotoCreateNewLotPage(){
		action.click(getLocation("CreateNewLotBtn"));
		return new CreateNewLotPage(action);
	}
	
	public String getFirstLotCurrent(){
		action.click(getLocation("Dashboard"));
		Utils.pause(Constants.WAIT_TIME);
		String result = "";
		if (action.getElements(getLocation("CurrentFirstLot")).size() > 0) {
			result = action.waitForAndGetElement(getLocation("CurrentFirstLot")).getText();
		}
		return result;
	}
	
	public void clickBell(){
		action.click(getLocation("Bell"));
		Utils.pause(Constants.WAIT_TIME);
	}
	
	public LotDeailsPage gotoLotDetail(){
		WebElement firstLotElement = action.waitForAndGetElement(getLocation("FirstNotificationText"));
		action.click(firstLotElement);
		return new LotDeailsPage(action);
	}
	
	public String getFirstNotifi(){
		String nodifiInfo = "";
		WebElement firstNotifiText = action.waitForAndGetElement(getLocation("FirstNotificationText"));
		nodifiInfo = firstNotifiText.getText();
		return nodifiInfo;
	}
}
