package com.blueship.pages.change.checkincheckout;

import com.blueship.atlibs.Page;
import com.blueship.atlibs.TestBase;
import com.blueship.common.Constants;

public class CheckInCheckOutRequestListPage extends Page {
	public CheckInCheckOutRequestListPage(TestBase action) {
		super(action);
		this.setRepoFile(action.config.getProperty("REPO_OBJECT_TRINITY_PATH"));
		this.setLocationSheet(Constants.SHEET_CHECK_IN_CHECK_OUT_REQUEST_LIST);
		this.initLocationMap();
	}
	
	public CheckoutRequestDetailPage gotoCheckoutRequestDetail(){
		action.click(getLocation("FirstCheckoutRequestId"));
		return new CheckoutRequestDetailPage(action);
	}
}
