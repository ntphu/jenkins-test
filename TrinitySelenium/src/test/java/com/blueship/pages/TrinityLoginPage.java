package com.blueship.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.blueship.atlibs.Page;
import com.blueship.atlibs.TestBase;
import com.blueship.common.Constants;

public class TrinityLoginPage extends Page {

	public TrinityLoginPage(TestBase action) {
		// call super constructor
		super(action);

		this.setRepoFile(action.config.getProperty("REPO_OBJECT_TRINITY_PATH"));
		this.setLocationSheet(Constants.SHEET_LOGIN_TRINYTY);

		// load object location
		this.initLocationMap();
	}
	
	public String isTrinityLoginPage() {
		String result = Constants.IN_LOGIN_PAGE;
		List<WebElement> userId = action.getElements(getLocation("LoginBtn"));
		List<WebElement> password = action.getElements(getLocation("Password"));
		List<WebElement> loginBtn = action.getElements(getLocation("LoginBtn"));
		if (userId.size() == 0 || password.size() == 0 || loginBtn.size() == 0) {
			result = Constants.NOT_IN_LOGIN_PAGE;
		}
		return result;
	}

	public TrinityDashBoardPage loginTrinity(String username, String password) {
		action.type(getLocation("UserId"), username, true);
		action.type(getLocation("Password"), password, true);
		action.click(getLocation("LoginBtn"));
		return new TrinityDashBoardPage(action);
	}

}
