package com.blueship.pages.change.property;

import com.blueship.atlibs.Page;
import com.blueship.atlibs.TestBase;
import com.blueship.atlibs.Utils;
import com.blueship.common.Constants;
import com.blueship.pages.change.checkincheckout.CreateNewCheckoutRequestPage;

public class ChangePropertyDetailPage extends Page {

	private String referenceId;
	private String referenceCategoryId;
	private String summary;
	private String contents;
	private String assignee;
	private String category;
	private String milestone;

	public ChangePropertyDetailPage(TestBase action) {
		super(action);
		this.setRepoFile(action.config.getProperty("REPO_OBJECT_TRINITY_PATH"));
		this.setLocationSheet(Constants.SHEET_CHANGE_PROPERTY_DETAIL);
		this.initLocationMap();
	}

	public CreateNewCheckoutRequestPage gotoCreateNewCheckoutRequestPage() {
		action.click(getLocation("CreateNewCheckoutRequest"));
		Utils.pause(Constants.WAIT_TIME);
		return new CreateNewCheckoutRequestPage(action);
	}
	
	public void getChangePropertyInfo(){
		referenceId = action.getElement(getLocation("ReferenceId")).getText();
		referenceCategoryId = action.getElement(getLocation("ReferenceCategoryId")).getText();
		summary = action.getElement(getLocation("Summary")).getText();
		contents = action.getElement(getLocation("Contents")).getText();
		assignee = action.getElement(getLocation("Assignee")).getText();
		category = action.getElement(getLocation("Category")).getText();
		milestone = action.getElement(getLocation("Milestone")).getText();
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getReferenceCategoryId() {
		return referenceCategoryId;
	}

	public void setReferenceCategoryId(String referenceCategoryId) {
		this.referenceCategoryId = referenceCategoryId;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getMilestone() {
		return milestone;
	}

	public void setMilestone(String milestone) {
		this.milestone = milestone;
	}

}
