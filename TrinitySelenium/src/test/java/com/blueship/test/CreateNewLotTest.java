package com.blueship.test;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.blueship.atlibs.TestCase;
import com.blueship.atlibs.Utils;
import com.blueship.common.Constants;
import com.blueship.pages.TrinityDashBoardPage;
import com.blueship.pages.TrinityLoginPage;
import com.blueship.pages.lot.CreateNewLotPage;
import com.blueship.pages.lot.LotDeailsPage;
import com.blueship.report.CustomReport;

public class CreateNewLotTest extends TestCase {
	private TrinityDashBoardPage dashBoard;
	private CreateNewLotPage createNewLotPage;
	private LotDeailsPage lotDetailPage;
	CustomReport rp = new CustomReport(action.log);

	public CreateNewLotTest() {
		this.dataFile = action.config.getProperty("TEST_DATA_PATH");
		this.dataSheet = Constants.SHEET_CREATE_NEW_LOT_TRINYTY;
	}

	@DataProvider(name = "createNewLotData")
	public Object[][] dpTrinityCreateNewLot() {
		return dpPrepareData("createNewLotData", false);
	}

	public void login() {
		HashMap<String, String> loginData = Utils.getTestData(this.dataFile,
				Constants.SHEET_LOGIN_TRINYTY, "userSignupDataTrinity").get(0);
		// login
		rp.info("Login to trinity");
		action.gotoPage(action.config.getProperty("TRINITY_LOGIN_URL"));
		TrinityLoginPage trinityLogin = new TrinityLoginPage(action);
		dashBoard = trinityLogin.loginTrinity(loginData.get("userId").trim(),
				loginData.get("password").trim());
		// check login success and go to dashboard
		rp.assertEquals(dashBoard.isDashBoard(), Constants.IN_DASHBOARD,
				"Login success, display dashborad");

	}

	@Test(dataProvider = "createNewLotData")
	public void testCreateNewLot(HashMap<String, String> createNewLotData) {
		login();
		createNewLot(createNewLotData);
		lotDetailPage = dashBoard.gotoLotDetail();
		checkLotDetail(createNewLotData);
	}

	private void createNewLot(HashMap<String, String> createNewLotData) {
		String firstLotOld = dashBoard.getFirstLotCurrent();
		rp.info("goto create new lot page");
		createNewLotPage = dashBoard.gotoCreateNewLotPage();
		rp.assertEquals(createNewLotPage.isCreateNewLotPage(),
				Constants.IN_CREATE_LOT_PAGE, "In create new lot page");

		rp.info("input data");
		dashBoard = createNewLotPage.createNewLot(
				createNewLotData.get("lotName").trim(),
				createNewLotData.get("summary").trim(),
				createNewLotData.get("content").trim(),
				createNewLotData.get("baselineTag").trim(), createNewLotData
						.get("publicPath").trim(),
				createNewLotData.get("privatePath").trim(), createNewLotData
						.get("authorizedGroup").trim(),
				createNewLotData.get("emailGroup").trim(), createNewLotData
						.get("moduleName").trim(),
				createNewLotData.get("buildEnviroment01").trim(),
				createNewLotData.get("buildEnviroment02").trim(),
				createNewLotData.get("releaseEnviroment").trim());
		dashBoard.clickBell();
		String firstNotifi = dashBoard.getFirstNotifi();
		String resultNewLotCreated = firstNotifi.equals(firstLotOld) ? Constants.CREATE_NEW_LOT_UNSUCCESSFUL
				: Constants.CREATE_NEW_LOT_SUCCESS;
		rp.assertEquals(resultNewLotCreated, Constants.CREATE_NEW_LOT_SUCCESS,
				"Create new lot success");
	}

	private void checkLotDetail(HashMap<String, String> createNewLotData) {
		rp.info("get lot detail");
		lotDetailPage.getLotInfo();
		rp.info("Verify lot detail");
		rp.info("Verify lot name");
		rp.assertEquals(lotDetailPage.getLotName(),
				createNewLotData.get("lotName").trim());
		rp.info("Verify summary");
		rp.assertEquals(lotDetailPage.getSummary(),
				createNewLotData.get("summary").trim());
		rp.info("Verify content");
		rp.assertEquals(lotDetailPage.getContent(),
				createNewLotData.get("content").trim());
		rp.info("Verify baselineTag");
		rp.assertEquals(lotDetailPage.getBaselineTag(),
				createNewLotData.get("baselineTag").trim());
		rp.info("Verify public path");
		rp.assertEquals(lotDetailPage.getPublicPath(),
				createNewLotData.get("publicPath").trim());
		rp.info("Verify private path");
		rp.assertEquals(lotDetailPage.getPrivatePath(),
				createNewLotData.get("privatePath").trim());
		rp.info("Verify authorized group");
		rp.assertEquals(lotDetailPage.getAuthorizedGroup(), createNewLotData
				.get("authorizedGroup").trim());
		rp.info("Verify email group");
		rp.assertEquals(lotDetailPage.getEmailGroup(),
				createNewLotData.get("emailGroup").trim());
		String buildEnviroment01 = createNewLotData.get("buildEnviroment01")
				.trim();
		String buildEnviroment02 = createNewLotData.get("buildEnviroment02")
				.trim();
		rp.info("Verify build invironments");
		rp.assertEquals(lotDetailPage.verifyBuildEnvs(buildEnviroment01, buildEnviroment02), Constants.BUILD_ENVIRONMENT_IS_SAME);
		rp.info("Verify releaseEnvironment");
		rp.assertEquals(lotDetailPage.verifyReleaseEnvs(createNewLotData.get("releaseEnviroment")
				.trim()), Constants.RELEASE_ENVIRONMENT_IS_SAME);
	}
}
