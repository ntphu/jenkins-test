package com.blueship.test;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.blueship.atlibs.TestBase;
import com.blueship.atlibs.TestCase;
import com.blueship.common.Constants;
import com.blueship.pages.TrinityDashBoardPage;
import com.blueship.pages.TrinityLoginPage;
import com.blueship.report.CustomReport;

public class LoginTest extends TestCase {
	private CustomReport rp = new CustomReport(action.log);
	private TrinityLoginPage trinityLoginPage;
	private TrinityDashBoardPage dashBoadPage;

	public LoginTest() {
		this.dataFile = action.config.getProperty("TEST_DATA_PATH");
		this.dataSheet = Constants.SHEET_LOGIN_TRINYTY;
	}

	public LoginTest(TestBase action) {
		super(action);
		this.dataFile = action.config.getProperty("TEST_DATA_PATH");
		this.dataSheet = Constants.SHEET_LOGIN_TRINYTY;
	}

	@DataProvider(name = "userSignupDataTrinity")
	public Object[][] dpTrinityLogin() {
		return dpPrepareData("userSignupDataTrinity", false);
	}
	
	@DataProvider(name = "wrongUserSignupDataTrinity")
	public Object[][] dpTrinityLoginFail() {
		return dpPrepareData("wrongUserSignupDataTrinity", false);
	}

	@Test(dataProvider = "userSignupDataTrinity", priority = 0)
	public void loginTrinity(HashMap<String, String> userSignupDataTrinity) {
		rp.info("go to Login page");
		//Open Login page
		action.gotoPage(action.config.getProperty("TRINITY_LOGIN_URL"));
		trinityLoginPage = new TrinityLoginPage(action);
		rp.assertEquals(trinityLoginPage.isTrinityLoginPage(), Constants.IN_LOGIN_PAGE, "In login page");
		dashBoadPage = trinityLoginPage.loginTrinity(userSignupDataTrinity.get("userId").trim(), userSignupDataTrinity.get("password").trim());
		dashBoadPage.isDashBoard().equals(Constants.IN_DASHBOARD);
		rp.assertEquals(dashBoadPage.isDashBoard(), Constants.IN_DASHBOARD, "Login success and display dashboard");

	}
	
	@Test(dataProvider = "wrongUserSignupDataTrinity", priority = 1)
	public void loginTrinityFail(HashMap<String, String> wrongUserSignupDataTrinity) {
		rp.info("go to Login page");
		//Open Login page
		action.gotoPage(action.config.getProperty("TRINITY_LOGIN_URL"));
		trinityLoginPage = new TrinityLoginPage(action);
		rp.assertEquals(trinityLoginPage.isTrinityLoginPage(), Constants.IN_LOGIN_PAGE, "In login page");
		dashBoadPage = trinityLoginPage.loginTrinity(wrongUserSignupDataTrinity.get("userId").trim(),wrongUserSignupDataTrinity.get("password").trim());
		rp.assertEquals(dashBoadPage.isDashBoard(), Constants.NOT_IN_DASHBOARD, "Login faile and not display dashboard");
	

	}

}
