package com.blueship.atlibs;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import sun.reflect.Reflection;

import com.blueship.atlibs.Constants;
import com.blueship.atlibs.TestBase;
import com.blueship.atlibs.TestLogger;
import com.blueship.atlibs.Utils;

/**
 * The base of pages
 * 
 * @author VinhND2
 * */
public class Page {

	protected TestBase action;
	public HashMap<String, HashMap<String, By>> locationMap;
	protected HashMap<String, String> repoFile;
	protected HashMap<String, String> locationSheet;

	/**
	 * Useless function :)
	 * */
	public Page() {
		// initialize selenium action base
		// action = new TestBase();
	}

	/**
	 * Call constructor if page use internal action
	 * 
	 * @author VinhND2
	 * */
	public Page(TestBase action) {
		this();
		this.action = action;
		waitForPageLoaded(action.driver);
	}

	public void waitForPageLoaded(WebDriver driver) {
		info("start wait for page load");
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript(
						"return document.readyState").equals("complete");
			}
		};
		String url = driver.getCurrentUrl();
		System.out.println("url = " + url);
		if(url.isEmpty() || url.equals("about:blank")){
			return;
		}
		WebDriverWait wait = new WebDriverWait(driver, 90);
		wait.until(pageLoadCondition);
		info("page loaded");
	}

	/**
	 * Load objects location from file into a dictionary of, depend on its class
	 * name <String, By> object
	 * 
	 * @author VinhND2
	 * */
	protected void initLocationMap() {
		if (this.locationMap == null) {
			this.locationMap = new HashMap<String, HashMap<String, By>>();
		}
		HashMap<String, By> map = new HashMap<String, By>();
		String strClassName = Reflection.getCallerClass(2).getSimpleName();

		String[][] repoObjects = Utils.getTableObject(this.getRepoFile(),
				this.getLocationSheet());
		String strLocationName = null;
		for (int i = 0; i < repoObjects.length; i++) {
			strLocationName = repoObjects[i][0];
			if (!strLocationName.isEmpty()) {
				map.put(Utils.capThemAll(strLocationName, false), Utils
						.getLocation(repoObjects[i][1], repoObjects[i][2], ""));
			}
		}
		this.locationMap.put(strClassName, map);
	}

	public String setRepoFile(String strRepoFile) {
		if (this.repoFile == null) {
			this.repoFile = new HashMap<String, String>();
		}
		String strClassName = Reflection.getCallerClass(2).getSimpleName();
		return this.repoFile.put(strClassName, strRepoFile);
	}

	public String getRepoFile() {
		String strClassName = Reflection.getCallerClass(3).getSimpleName();
		return this.repoFile.get(strClassName);
	}

	public String setLocationSheet(String strLocationSheet) {
		if (this.locationSheet == null) {
			this.locationSheet = new HashMap<String, String>();
		}
		String strClassName = Reflection.getCallerClass(2).getSimpleName();
		return this.locationSheet.put(strClassName, strLocationSheet);
	}

	public String getLocationSheet() {
		String strClassName = Reflection.getCallerClass(3).getSimpleName();
		return this.locationSheet.get(strClassName);
	}

	/**
	 * Get location of object by its name
	 * 
	 * @author VinhND2
	 * */
	public By getLocation(String locationName) {
		String rootPage = "Page";
		Class<?> cls = this.getClass();
		String strClassName = cls.getSimpleName();
		HashMap<String, By> map = null;
		By loc = null;
		do {
			map = this.locationMap.get(strClassName);
			if (map.containsKey(locationName)) {
				loc = map.get(locationName);
			}
			// go up the nearest super class
			cls = cls.getSuperclass();
			strClassName = cls.getSimpleName();
		} while (loc == null && !strClassName.equals(rootPage));

		return loc;
	}

	public By _loc(String location) {
		// TODO: for short - going to replace the getLocation function
		return getLocation(location);
	}

	/**
	 * write log with browser type prefix
	 * 
	 * */
	public void info(String message) {
		String browserType = action.driver.toString().toUpperCase();
		if (browserType.contains(Constants.BROWSER_TYPE_FIREFOX.toUpperCase())) {
			browserType = Constants.BROWSER_TYPE_FIREFOX;
		} else if (browserType.contains(Constants.BROWSER_TYPE_CHROME
				.toUpperCase())) {
			browserType = Constants.BROWSER_TYPE_CHROME;
		}

		message = browserType + ": " + message;
		TestLogger.info(message);
	}

	/**
	 * Create page by name
	 * 
	 * @author VinhND2
	 * */
	public Page createPageByName(String strPageName, String strPackageName) {
		String strPageClassName = strPackageName + "." + strPageName;

		// get class & method
		Constructor<?> constructor;
		Class<?> theClass;
		Class<?> clsParams[] = { TestBase.class };
		Page thePageObject;
		Object initargs[] = { action };
		try {
			theClass = Class.forName(strPageClassName);
			constructor = theClass.getConstructor(clsParams);
			thePageObject = (Page) constructor.newInstance(initargs);
			return thePageObject;
		} catch (Exception e) {
			System.out.println("page object: " + strPageName + " not created");
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param fieldName
	 * @param locator
	 * @param value
	 * @author ThanhPV7 - 20140725
	 */
	public void inputText(String fieldName, By locator, String value) {
		if (!"-".equals(value)) {
			info("Enter " + fieldName + ": " + value);
			action.type(locator, value, true);
		}
	}

	/**
	 * 
	 * @param fieldName
	 * @param locator
	 * @param value
	 * @author ThanhPV7 - 20140725
	 */
	public boolean selectOption(String fieldName, By locator, String value) {
		boolean selected = false;

		if (!"-".equals(value)) {
			String option = value.trim();

			Select select = new Select(action.driver.findElement(locator));
			List<WebElement> listOptions = select.getOptions();

			for (WebElement element : listOptions) {
				if (option.equals(element.getText().trim())) {
					action.select(locator, value);
					info(fieldName + " is set: " + value);
					selected = true;
					break;
				}
			}

			if (!selected) {
				info(fieldName + " isn't set: " + value);
				info("Option '" + value + "' isn't existed");
				return false;
			}
		}

		return true;
	}

	/**
	 * 
	 * @param fieldName
	 * @param locator
	 * @param value
	 * @author ThanhPV7 - 20140725
	 */
	public boolean checkBox(String fieldName, By locator, String value) {
		if ("YES".equals(value.trim().toUpperCase())) {
			if (action.getAtributeValue(locator, "checked") == null) {
				info("Tick " + fieldName);
				action.click(locator);
			}
		} else if ("NO".equals(value.trim().toUpperCase())) {
			if (action.getAtributeValue(locator, "checked") != null) {
				info("Tick " + fieldName);
				action.click(locator);
			}
		} else {
			info(fieldName + " data is invalid");
			return false;
		}
		return true;
	}

	public boolean selectMultipleValues(String fieldName, By locator,
			String multipleVals) {
		if (!"-".equals(multipleVals)) {
			info(fieldName + " is set: " + multipleVals);
			String multipleSel[] = multipleVals.split("; ");
			for (String valueToBeSelected : multipleSel) {
				try {
					new Select(action.getElement(locator))
							.selectByVisibleText(valueToBeSelected);
					action.getElement(locator).sendKeys(Keys.CONTROL);
				} catch (Exception ex) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @author ThanhPV7 - 20140728
	 */
	public boolean contains(String fieldName, String actual, String expected) {
		if (!"-".equals(expected)) {
			info("Verify field " + fieldName + ": " + expected);
			return (actual.trim().toUpperCase().contains(expected.trim()
					.toUpperCase()));
		}
		return true;
	}

	/**
	 * @author ThanhPV7 - 20140728
	 */
	public boolean equals(String fieldName, String actual, String expected) {
		if (!"-".equals(expected)) {
			info("Verify field " + fieldName + ": " + actual);
			return (actual.trim().equals(expected.trim()));
		}
		return true;
	}

	/**
	 * @author ThanhPV7 - 20140728
	 */
	public boolean containsImageName(String fieldName, String actual,
			String expected) {
		if ("YES".equals(expected.trim().toUpperCase())) {
			return (actual.contains("checkbox_checked"));
		} else if ("NO".equals(expected.trim().toUpperCase())) {
			return (actual.contains("checkbox_unchecked"));
		} else {
			action.verifyTrue(false, fieldName
					+ " data is invalid, fix to 'Yes' or 'No'");
			return false;
		}
	}

	/**
	 * @author ThanhPV7 - 20140731
	 */
	public boolean containsMutilOptions(String fieldName, String actual,
			String expected) {
		if (!"-".contains(expected.trim())) {
			info("Verify field " + fieldName);
			String[] multiOptions = expected.split("; ");
			actual = actual.trim();

			for (String option : multiOptions) {
				if (!actual.contains(option.trim())) {
					assert (false);
				}
			}
		}
		return true;
	}

	/**
	 * Scroll to an item related list by clicking corresponding context link
	 * 
	 * @param ContextName
	 * @author ThanhPV7 - 20140721
	 */
	public boolean clickContextMenuLink(String contextLink) {
		int i = 0;
		WebElement hoverLink = action
				.waitForAndGetElement(action.setParamXpath(
						_loc("ListHoverLink"), contextLink), 10000);

		while (i < 5) {
			Utils.pause(3000);
			if (hoverLink.isDisplayed()) {
				hoverLink.click();
				return true;
			}
			i++;
		}
		return false;
	}

	/**
	 * @author ThanhPV7 - 20140728
	 */
	public void checkEquals(String fieldName, String actual, String expected) {
		if (!"-".equals(expected)) {
			info("Verify field " + fieldName + ": " + actual);
			action.verifyEquals(actual, expected, "Verify " + fieldName
					+ " field");
		}
	}

	// KienNT28
	// stats waiting element
	// waiting element
	public WebElement findDynamicElement(By by, int timeOut) {
		WebDriverWait wait = new WebDriverWait(action.driver, timeOut);
		WebElement element = wait.until(ExpectedConditions
				.visibilityOfElementLocated(by));
		return element;
	}
	// end wait element
}
