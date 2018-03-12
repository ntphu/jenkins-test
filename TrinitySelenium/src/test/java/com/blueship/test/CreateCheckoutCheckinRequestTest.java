package com.blueship.test;

import java.io.IOException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.blueship.atlibs.TestCase;
import com.blueship.atlibs.Utils;
import com.blueship.common.Constants;
import com.blueship.pages.TrinityDashBoardPage;
import com.blueship.pages.TrinityLoginPage;
import com.blueship.pages.change.checkincheckout.CheckInCheckOutRequestListPage;
import com.blueship.pages.change.checkincheckout.CheckinRequestProgressPage;
import com.blueship.pages.change.checkincheckout.CheckoutRequestDetailPage;
import com.blueship.pages.change.checkincheckout.CreateNewCheckinRequestPage;
import com.blueship.pages.change.checkincheckout.CreateNewCheckoutRequestPage;
import com.blueship.pages.change.property.ChangePropertyDetailPage;
import com.blueship.pages.change.property.ChangePropertyListPage;
import com.blueship.pages.lot.LotDeailsPage;
import com.blueship.report.CustomReport;

public class CreateCheckoutCheckinRequestTest extends TestCase {
	private TrinityDashBoardPage dashBoard;
	private LotDeailsPage lotDetailPage;
	private ChangePropertyListPage changePropertyListPage;
	private ChangePropertyDetailPage changePropertyDetailPage;
	private CreateNewCheckoutRequestPage createNewCheckoutRequestPage;
	private CheckInCheckOutRequestListPage checkInCheckOutRequestListPage;
	private CheckoutRequestDetailPage checkoutRequestDetailPage;
	private CreateNewCheckinRequestPage createNewCheckinRequestPage;
	private CheckinRequestProgressPage checkinRequestProgressPage;
	CustomReport rp = new CustomReport(action.log);

	public CreateCheckoutCheckinRequestTest() {
		this.dataFile = action.config.getProperty("TEST_DATA_PATH");
		this.dataSheet = Constants.SHEET_CREATE_NEW_CHECKOUT_REQUEST;
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

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateCheckoutRequest() throws IOException {
		login();
		dashBoard.clickBell();
		rp.info("Go to lot detail");
		lotDetailPage = dashBoard.gotoLotDetail();
		lotDetailPage.getLotInfo();
		String publicPath = lotDetailPage.getPublicPath();
		rp.info("Go to change property list");
		changePropertyListPage = lotDetailPage.gotoChangePropertyListPage();
		rp.info("Go to change property detail");
		changePropertyDetailPage = changePropertyListPage.gotoChangePropertyDetail();
		 
		HashMap<String, String> createNewCheckOutRequestData = (HashMap<String, String>) dpPrepareData(
				"createNewCheckOutRequest", false)[0][0];
		testCreateNewCheckoutRequest(createNewCheckOutRequestData);
		// need remove
		/*checkInCheckOutRequestListPage = changePropertyListPage
				.getCheckInCheckOutRequestListPage();*/
		//
		rp.info("Go to checkin/checkout request list");
		checkoutRequestDetailPage = checkInCheckOutRequestListPage
				.gotoCheckoutRequestDetail();
		checkoutRequestDetailPage.setPublicPath(publicPath);
		checkoutRequestDetailPage.changeFile(createNewCheckOutRequestData.get(
				"file").trim());
		checkoutRequestDetailPage.getCheckoutRequestDetail();
		rp.info("Verify file");
		rp.assertEquals(checkoutRequestDetailPage.getFile(),
				createNewCheckOutRequestData.get("file").trim(),
				"Have file as upload");
		createNewCheckinRequestPage = checkoutRequestDetailPage.gotoCreateNewCheckinRequestPage();
		checkinRequestProgressPage = createNewCheckinRequestPage.createNewCheckinRequest();
		rp.assertEquals(checkinRequestProgressPage.isCheckinRequestProcessPage(), Constants.IN_CHECKIN_REQUEST_PROGRESS_PAGE,
				"create checkin request success, display checkin progress page");
		
	}

	private void testCreateNewCheckoutRequest(
			HashMap<String, String> checkOutRequestData) {
		rp.info("go to create new checkout request");
		createNewCheckoutRequestPage = changePropertyDetailPage
				.gotoCreateNewCheckoutRequestPage();
		rp.assertEquals(createNewCheckoutRequestPage
				.isInPageCreateCheckInCheckOutRequest(),
				Constants.IN_CREATE_NEW_CHECKIN_CHECKOUT_REQUEST_PAGE,
				Constants.IN_CREATE_NEW_CHECKIN_CHECKOUT_REQUEST_PAGE);
		checkInCheckOutRequestListPage = createNewCheckoutRequestPage
				.createNewCheckOutRequest(checkOutRequestData.get("group")
						.trim(), checkOutRequestData.get("assignee").trim(),
						checkOutRequestData.get("subject").trim(),
						checkOutRequestData.get("contents").trim(),
						checkOutRequestData.get("checkinDueDate").trim(),
						checkOutRequestData.get("category").trim(),
						checkOutRequestData.get("milestone").trim(),
						checkOutRequestData.get("file").trim());
	}
}
