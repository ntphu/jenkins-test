package com.blueship.pages.change.property;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.blueship.common.Constants;
import com.blueship.atlibs.Page;
import com.blueship.atlibs.TestBase;
import com.blueship.atlibs.Utils;

public class CreateChangePropertyPage extends Page {
	public CreateChangePropertyPage(TestBase action) {
		super(action);
		this.setRepoFile(action.config.getProperty("REPO_OBJECT_TRINITY_PATH"));
		this.setLocationSheet(Constants.SHEET_CREATE_CHANGE_PROPERTY);
		this.initLocationMap();
	}

	public String inCreateChangePropertyPage() {
		String result = Constants.IN_CREATE_CHANGE_PROPERTY_PAGE;
		WebElement referenceId = action.getElement(getLocation("ReferenceId"));
		WebElement referenceCategoryId = action.getElement(getLocation("ReferenceCategoryId"));
		WebElement assignee = action.getElement(getLocation("Assignee"));
		WebElement category = action.getElement(getLocation("Category"));
		WebElement milestone = action.getElement(getLocation("Milestone"));
		WebElement createBtn = action.getElement(getLocation("CreateBtn"));
		if (!referenceId.isDisplayed() || !referenceCategoryId.isDisplayed()
				|| !assignee.isDisplayed() || !category.isDisplayed()
				|| !milestone.isDisplayed() || !createBtn.isDisplayed()) {
			result = Constants.NOT_IN_CREATE_CHANGE_PROPERTY_PAGE;
		}
		return result;
	}

	public ChangePropertyListPage createNewChangeProperty(String referenceId,
			String referenceCategoryId, String summary, String contents,
			String assignee, String category, String milestone) {
		action.type(getLocation("ReferenceId"), referenceId, true);
		action.select(getLocation("ReferenceCategoryId"), referenceCategoryId);
		action.type(getLocation("Summary"), summary, true);
		action.type(getLocation("Contents"), contents, true);
		action.select(getLocation("Assignee"), assignee);

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
			if (!exist){
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
			if (!exist){
				// create category and select;
				action.click(getLocation("MilestonePlus"));
				Utils.pause(Constants.WAIT_SHORT_TIME);
				action.type(getLocation("MilestonePlusInput"), milestone, true);
				action.click(getLocation("MilestonePlusBtn"));
				Utils.pause(Constants.WAIT_TIME);
			} 
			action.select(getLocation("Milestone"), milestone);
		}

		action.click(getLocation("CreateBtn"));
		Utils.pause(Constants.WAIT_TIME);
		return new ChangePropertyListPage(action);
	}
}
