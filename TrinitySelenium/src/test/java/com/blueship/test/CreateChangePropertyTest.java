package com.blueship.test;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.blueship.atlibs.TestCase;
import com.blueship.atlibs.Utils;
import com.blueship.common.Constants;
import com.blueship.pages.TrinityDashBoardPage;
import com.blueship.pages.TrinityLoginPage;
import com.blueship.pages.change.property.ChangePropertyDetailPage;
import com.blueship.pages.change.property.ChangePropertyListPage;
import com.blueship.pages.change.property.CreateChangePropertyPage;
import com.blueship.pages.lot.LotDeailsPage;
import com.blueship.report.CustomReport;

public class CreateChangePropertyTest extends TestCase {
	private TrinityDashBoardPage dashBoard;
	private LotDeailsPage lotDetailPage;
	private ChangePropertyListPage changePropertyListPage;
	private CreateChangePropertyPage createPropertyPage;
	private ChangePropertyDetailPage changePropertyDetailPage;
	CustomReport rp = new CustomReport(action.log);

	public CreateChangePropertyTest() {
		this.dataFile = action.config.getProperty("TEST_DATA_PATH");
		this.dataSheet = Constants.SHEET_CREATE_CHANGE_PROPERTY;
	}

	@DataProvider(name = "createChangeProperty")
	public Object[][] dpCreateChangeProperty() {
		return dpPrepareData("createChangeProperty", false);
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

	@Test(dataProvider = "createChangeProperty")
	public void testCreateChangeProperty(
			HashMap<String, String> createChangePropertyData) {
		login();
		dashBoard.clickBell();
		// dashBoard.getFirstNotifi();
		lotDetailPage = dashBoard.gotoLotDetail();
		changePropertyListPage = lotDetailPage.gotoChangePropertyListPage();
		rp.info("goto create new change property");
		testCreateNewChange(createChangePropertyData);
		rp.info("goto change property detail");
		changePropertyDetailPage = changePropertyListPage
				.gotoChangePropertyDetail();
		checkChangePropertyDetail(createChangePropertyData);
	}

	private void testCreateNewChange(HashMap<String, String> createPropertyData) {
		String oldChangeId = changePropertyListPage.getNewChangeId();
		createPropertyPage = changePropertyListPage.gotoCreateChangeProperty();
		rp.assertEquals(createPropertyPage.inCreateChangePropertyPage(),
				Constants.IN_CREATE_CHANGE_PROPERTY_PAGE,
				"In create new change property page");

		changePropertyListPage = createPropertyPage.createNewChangeProperty(
				createPropertyData.get("referenceId").trim(),
				createPropertyData.get("referenceCategoryId").trim(),
				createPropertyData.get("summary").trim(), createPropertyData
						.get("contents").trim(),
				createPropertyData.get("assignee").trim(), createPropertyData
						.get("category").trim(),
				createPropertyData.get("milestone").trim());
		String newChangeId = changePropertyListPage.getNewChangeId();
		String resultNewChangeCreated = newChangeId.equals(oldChangeId) ? Constants.CREATE_NEW_CHANGE_UNSUCCESSFUL
				: Constants.CREATE_NEW_CHANGE_SUCCESS;
		rp.assertEquals(resultNewChangeCreated,
				Constants.CREATE_NEW_CHANGE_SUCCESS,
				"create new change success");
		rp.info("create new change property successful");

	}

	public void checkChangePropertyDetail(HashMap<String, String> changeData) {
		// go to lot detail
		changePropertyDetailPage.getChangePropertyInfo();
		rp.info("Verify referenceId");
		rp.assertEquals(changePropertyDetailPage.getReferenceId(), changeData
				.get("referenceId").trim());
		rp.info("Verify referenceCategoryId");
		rp.assertEquals(changePropertyDetailPage.getReferenceCategoryId(),
				changeData.get("referenceCategoryId").trim());
		rp.info("Verify summary");
		rp.assertEquals(changePropertyDetailPage.getSummary(),
				changeData.get("summary").trim());
		rp.info("Verify contents");
		rp.assertEquals(changePropertyDetailPage.getContents(),
				changeData.get("contents").trim());
		rp.info("Verify assignee");
		rp.assertEquals(changePropertyDetailPage.getAssignee(),
				changeData.get("assignee").trim());
		rp.info("Verify category");
		rp.assertEquals(changePropertyDetailPage.getCategory(),
				changeData.get("category").trim());
		rp.info("Verify milestone");
		rp.assertEquals(changePropertyDetailPage.getMilestone(), changeData
				.get("milestone").trim());
		rp.info("Verify all detail completed");

	}
}
