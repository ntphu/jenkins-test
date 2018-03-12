package com.blueship.pages.change.checkincheckout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.blueship.atlibs.Page;
import com.blueship.atlibs.TestBase;
import com.blueship.atlibs.Utils;
import com.blueship.common.Constants;

public class CreateNewCheckoutRequestPage extends Page {

	public CreateNewCheckoutRequestPage(TestBase action) {
		super(action);
		this.setRepoFile(action.config.getProperty("REPO_OBJECT_TRINITY_PATH"));
		this.setLocationSheet(Constants.SHEET_CREATE_NEW_CHECKOUT_REQUEST);
		this.initLocationMap();
	}

	public String isInPageCreateCheckInCheckOutRequest() {
		String result = Constants.IN_CREATE_NEW_CHECKIN_CHECKOUT_REQUEST_PAGE;
		WebElement group = action.getElement(getLocation("Group"));
		WebElement assignee = action.getElement(getLocation("Assignee"));
		WebElement subject = action.getElement(getLocation("Subject"));
		WebElement contents = action.getElement(getLocation("Contents"));
		WebElement checkInDuaDate = action.getElement(getLocation("CheckInDuaDate"));
		WebElement submitCheckoutRequestBtn = action.getElement(getLocation("SubmitCheckoutRequestBtn"));
		if (!group.isDisplayed() || !assignee.isDisplayed()
				|| !subject.isDisplayed() || !contents.isDisplayed()
				|| !checkInDuaDate.isDisplayed()
				|| !submitCheckoutRequestBtn.isDisplayed()) {
			result = Constants.NOT_IN_CREATE_NEW_CHECKIN_CHECKOUT_REQUEST_PAGE;
		}
		return result;
	}

	public CheckInCheckOutRequestListPage createNewCheckOutRequest(
			String group, String assignee, String subject, String contents,
			String checkInDueDate, String category, String milestone,
			String file) {
		action.select(getLocation("Group"), group);
		action.select(getLocation("Assignee"), assignee);
		action.type(getLocation("Subject"), subject, true);
		action.type(getLocation("Contents"), contents, true);
		try {
			Date date = new SimpleDateFormat("dd/MM/yyyy")
					.parse(checkInDueDate);
			checkInDueDate = new SimpleDateFormat("yyyy/MM/dd").format(date);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		action.type(getLocation("CheckInDuaDate"), checkInDueDate, true);

		// category select
		if (!"".equals(category)) {
			WebElement categoryElement = action.getElement(getLocation("Category"));
			Select select = new Select(categoryElement);
			List<WebElement> listOptionCate = select.getOptions();
			boolean exist = false;
			for (WebElement e : listOptionCate) {
				if (category.equals(e.getText())) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				// create category and select;
				action.click(getLocation("CategoryPlus"));
				Utils.pause(Constants.WAIT_SHORT_TIME);
				action.type(getLocation("CategoryPlusInput"), category, true);
				action.click(getLocation("CategoryPlusBtn"));
				Utils.pause(Constants.WAIT_TIME);
			}
			action.select(getLocation("Category"), category);
		}

		// milestone select
		if (!"".equals(milestone)) {
			WebElement milestoneElement = action.getElement(getLocation("Milestone"));
			Select select = new Select(milestoneElement);
			List<WebElement> listOptionCate = select.getOptions();
			boolean exist = false;
			for (WebElement e : listOptionCate) {
				if (milestone.equals(e.getText())) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				// create category and select;
				action.click(getLocation("MilestonePlus"));
				Utils.pause(Constants.WAIT_SHORT_TIME);
				action.type(getLocation("MilestonePlusInput"), milestone, true);
				action.click(getLocation("MilestonePlusBtn"));
				Utils.pause(Constants.WAIT_TIME);
			}
			action.select(getLocation("Milestone"), milestone);
		}
		String[] splitFolder = file.split("/");
		int size = splitFolder.length;
		String[] splitFile = splitFolder[size - 1].split(",");
		for (int i = 0; i < size - 1; i++) {
			info(splitFolder[i]);
			List<WebElement> listTr = action
					.getElements(getLocation("FileViewTableTr"));
			for (WebElement e : listTr) {
				WebElement nameFolder = e
						.findElement(getLocation("FileViewNameFolder"));
				if (splitFolder[i].equals(nameFolder.getText())) {
					action.click(nameFolder);
					Utils.pause(Constants.WAIT_TIME);
					break;
				}
			}
		}

		List<WebElement> listTr = action
				.getElements(getLocation("FileViewTableTr"));
		for (WebElement e : listTr) {
			if (e.findElements(getLocation("FileViewCheckboxFile")).size() <= 0) {
				break;
			}
			WebElement checkbox = e
					.findElement(getLocation("FileViewCheckboxFile"));
			WebElement nameFile = e
					.findElement(getLocation("FileViewNameFile"));
			for (int i = 0; i < splitFile.length; i++) {
				if (splitFile[i].equals(nameFile.getText())) {
					action.click(checkbox);
					Utils.pause(Constants.WAIT_TIME);
				}
			}

		}

		action.click(getLocation("SubmitCheckoutRequestBtn"));
		return new CheckInCheckOutRequestListPage(action);
	}
}
