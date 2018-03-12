package com.blueship.atlibs;

import static com.blueship.atlibs.TestLogger.debug;
import static com.blueship.atlibs.TestLogger.info;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.opera.core.systems.OperaDriver;

/**
 * TestBase - extract constants to properties file
 * 
 * @author HaiNH2
 * @author ChienLTH1
 * @author HuyNK1
 * 
 */
public class TestBase {
	public WebDriver driver;
	public Actions action;
	public int loopCount;
	public int timesToRepeat;
	public static boolean firstTimeLogin;

	public String baseUrl;
	public int defaultTimeOut;
	public int waitInterval;
	public boolean ieFlag;

	public Properties config;
	public Properties resources;

	public FileLogger log;

	private Map<ITestResult, List<Throwable>> verificationFailuresMap = new HashMap<ITestResult, List<Throwable>>();

	/**
	 * @author HaiNH2
	 * @author HuyNK1
	 * @param configFilePath
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public TestBase() {
		config = Utils.loadConfig(LibConstants.DEFAULT_CONFIG_FILE);
		resources = Utils.loadConfig(LibConstants.RESOURCES_FILE);
		baseUrl = config.getProperty("APP_BASE_URL");

		// Could be improved by adding "Check null"
		loopCount = Integer.parseInt(config.getProperty("TESTBASE_LOOP_COUNT"));
		timesToRepeat = Integer.parseInt(config
				.getProperty("TESTBASE_ACTION_REPEAT"));
		firstTimeLogin = Boolean.parseBoolean(config
				.getProperty("TESTBASE_FIRST_TIME_LOGIN"));
		defaultTimeOut = Integer.parseInt(config
				.getProperty("TESTBASE_DEFAULT_TIMEOUT"));
		waitInterval = Integer.parseInt(config
				.getProperty("TESTBASE_WAIT_INTERVAL"));
		ieFlag = Boolean.parseBoolean(config.getProperty("TESTBASE_IEFLAG"));

		log = new FileLogger();
	}

	/**
	 * Initialize web driver
	 * 
	 * @author HaiNH2
	 * @author HuyNK1
	 * @param webUrl
	 *            : URL of target web app (System Under Test). Will use
	 *            APP_BASE_URL (specified in config.properties) if this
	 *            parameter is not specified
	 */
	public void initSeleniumTest(String... webUrl) {
		String browser = System.getProperty("browser");
		if ("chrome".equalsIgnoreCase(browser)) {
			System.setProperty("webdriver.chrome.driver", config.getProperty("BROWSER_DRIVER_CHROME"));
			driver = new ChromeDriver();
		} else if ("ie".equalsIgnoreCase(browser)) {
			/*System.setProperty("webdriver.ie.driver", config.getProperty("BROWSER_DRIVER_IE"));
			DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
			caps.setCapability("ignoreZoomSetting", true);
			driver = new InternetExplorerDriver(caps);*/
			
			System.setProperty("webdriver.ie.driver",
					config.getProperty("BROWSER_DRIVER_IE"));
			DesiredCapabilities capabilities = DesiredCapabilities
					.internetExplorer();
			capabilities
					.setCapability(
							InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
							true);
			capabilities.setCapability("requireWindowFocus", true);

			capabilities.setCapability("nativeEvents", false);
			capabilities.setCapability("unexpectedAlertBehaviour", "accept");
			capabilities.setCapability("ignoreProtectedModeSettings", true);
			capabilities.setCapability("disable-popup-blocking", true);
			capabilities.setCapability("enablePersistentHover", true);
			capabilities.setCapability("ignoreZoomSetting", true);
			driver = new InternetExplorerDriver(capabilities);
		} else if ("opera".equalsIgnoreCase(browser)) {
			DesiredCapabilities capabilities = DesiredCapabilities.opera();
			capabilities.setCapability("opera.binary", config.getProperty("BROWSER_DRIVER_OPERA"));
			capabilities.setBrowserName("opera");
			driver = new OperaDriver(capabilities);
		} else if ("safari".equalsIgnoreCase(browser)) {
			DesiredCapabilities capability = DesiredCapabilities.safari();
			capability.setBrowserName("safari");
			driver = new SafariDriver(capability);
		} else {
			
			/*FirefoxProfile ffp = new FirefoxProfile();
			ffp.setPreference("browser.cache.disk.enable", false); 
			driver = new FirefoxDriver(ffp);*/
			System.setProperty("webdriver.gecko.driver",
					config.getProperty("BROWSER_DRIVER_FIREFOX"));
			File pathBinary = new File(config.getProperty("BROWSER_FIREFOX"));
			FirefoxBinary firefoxBinary = new FirefoxBinary(pathBinary);
			DesiredCapabilities desired = DesiredCapabilities.firefox();
			FirefoxOptions options = new FirefoxOptions();
			desired.setCapability(FirefoxOptions.FIREFOX_OPTIONS,
					options.setBinary(firefoxBinary));
			driver = new FirefoxDriver(options);
			info("Selenium Grid started with Firefox");
			//LongND_end init webdriver
			
//			System.setProperty("webdriver.chrome.driver", config.getProperty("BROWSER_DRIVER_CHROME"));
//			driver = new ChromeDriver();
		}
	}

	public void gotoPage(String strURL) {
		driver.get(strURL);
		// maximize window browser
		driver.manage().window().maximize();
	}

	/**
	 * Initialize Web Driver for using with Selenium Grid
	 * 
	 * @param browser
	 *            browser name ('firefox' or 'chrome' or ..)
	 * @param webUrl
	 *            : if webUrl is empty string, use APP_BASE_URL
	 */
	public void initSeleniumGridTest(String browser, String webUrl) {

		DesiredCapabilities capability = null;
		//LongND_start init webdriver
		if ("chrome".equals(browser)) {
			System.setProperty("webdriver.chrome.driver",
					config.getProperty("BROWSER_DRIVER_CHROME"));
			/*
			capability = DesiredCapabilities.chrome();
			capability.setBrowserName("chrome");
			capability.setPlatform(Platform.WINDOWS);
			 */
			driver = new ChromeDriver();
			info("Selenium Grid started with Chrome");
		} else if ("edge".equals(browser)) {
			System.setProperty("webdriver.edge.driver",
					config.getProperty("BROWSER_DRIVER_EDGE"));
			driver = new EdgeDriver();
			info("Selenium Grid started with edge");
		} else if ("ie".equals(browser)) {
			System.setProperty("webdriver.ie.driver",
					config.getProperty("BROWSER_DRIVER_IE"));
			/*
			capability = DesiredCapabilities.internetExplorer();
			capability.setBrowserName("internet explorer");
			capability.setPlatform(Platform.WINDOWS);
			 */
			DesiredCapabilities capabilities = DesiredCapabilities
					.internetExplorer();
			capabilities
					.setCapability(
							InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
							true);
			capabilities.setCapability("requireWindowFocus", true);

			capabilities.setCapability("nativeEvents", false);
			capabilities.setCapability("unexpectedAlertBehaviour", "accept");
			capabilities.setCapability("ignoreProtectedModeSettings", true);
			capabilities.setCapability("disable-popup-blocking", true);
			capabilities.setCapability("enablePersistentHover", true);
			capabilities.setCapability("ignoreZoomSetting", true);
			driver = new InternetExplorerDriver(capabilities);
			info("Selenium Grid started with ie");
		} else if ("safari".equals(browser)) {
			capability = DesiredCapabilities.safari();
			capability.setBrowserName("safari");
			info("Selenium Grid started with Safari");
		} else if ("opera".equals(browser)) {
			capability = DesiredCapabilities.opera();
			capability.setBrowserName("opera");
			capability.setPlatform(Platform.WINDOWS);
			info("Selenium Grid started with Opera");
		} else {
			/**
			 * 
			capability = DesiredCapabilities.firefox();
			capability.setBrowserName("firefox");
			capability.setPlatform(Platform.WINDOWS);
			 */
			System.setProperty("webdriver.gecko.driver",
					config.getProperty("BROWSER_DRIVER_FIREFOX"));
			File pathBinary = new File(config.getProperty("BROWSER_FIREFOX"));
			FirefoxBinary firefoxBinary = new FirefoxBinary(pathBinary);
			DesiredCapabilities desired = DesiredCapabilities.firefox();
			FirefoxOptions options = new FirefoxOptions();
			desired.setCapability(FirefoxOptions.FIREFOX_OPTIONS,
					options.setBinary(firefoxBinary));
			driver = new FirefoxDriver(options);
			info("Selenium Grid started with Firefox");
		}

		/*	  
		 try {
			if ("safari".equals(browser)) {
				driver = new SafariDriver(capability);
			} else {
				driver = new RemoteWebDriver(new URL(hubUrl), capability);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}	 
		 */

		//LongND_end
		/*
		 * if (webUrl.equals("")) { driver.get(baseUrl); } else {
		 * driver.get(webUrl); }
		 * 
		 * driver.manage().window().maximize();
		 */
	}

	/**
	 * Get element by xpath locator or xpath string
	 * 
	 * @param locator
	 * @return WebElement if found. Return NULL if not found
	 */
	public WebElement getElement(Object locator) {
		By by = locator instanceof By ? (By) locator : By.xpath(locator
				.toString());
		WebElement elem = null;

		try {
			elem = driver.findElement(by);
		} catch (NoSuchElementException e) {
			// error(e.getMessage());
		}

		return elem;
	}

	// return element only in case the element is displayed.
	public WebElement getDisplayedElement(Object locator, Object... opParams) {
		By by = locator instanceof By ? (By) locator : By.xpath(locator
				.toString());
		WebElement e = null;
		try {
			if (by != null)
				e = driver.findElement(by);
			if (e != null) {
				if (isDisplay(by))
					return e;
			}
		} catch (NoSuchElementException ex) {
			// error("NoSuchElementException");
		} catch (StaleElementReferenceException ex) {
			checkCycling(ex, 10);
			Utils.pause(waitInterval);
			getDisplayedElement(locator);
		} finally {
			loopCount = 0;
		}

		return null;
	}

	public boolean isElementPresent(Object locator) {
		return getElement(locator) != null;
	}

	public boolean isElementNotPresent(Object locator) {
		return !isElementPresent(locator);
	}

	/*
	 * @opPram[0]: timeout
	 * 
	 * @opPram[1]: 0,1 0: No Assert 1: Assert
	 */
	public WebElement waitForAndGetElement(Object locator, int... opParams) {
		WebElement elem = null;
		int timeout = opParams.length > 0 ? opParams[0] : defaultTimeOut;
		int isAssert = opParams.length > 1 ? opParams[1] : 1;
		int notDisplayE = opParams.length > 2 ? opParams[2] : 0;
		for (int tick = 0; tick < timeout / waitInterval; tick++) {
			if (notDisplayE == 2) {
				elem = getElement(locator);
				// elem = getDisplayedElement(locator);
			} else {
				elem = getDisplayedElement(locator);
			}
			if (null != elem)
				return elem;
			Utils.pause(waitInterval);
		}
		if (isAssert == 1)
			assert false : ("Timeout after " + timeout
					+ "ms waiting for element present: " + locator);
		info("cannot find element after " + timeout / 1000 + "s.");
		return null;
	}

	/**
	 * 
	 * @param locator
	 * @param opParams
	 * @opPram[0]: timeout
	 * @opPram[1]: 0,1 0: No Assert 1: Assert
	 * @author HuyNK1
	 * @return
	 */
	public List<WebElement> waitForAndGetElements(By locator, int... opParams) {
		waitForAndGetElement(locator, opParams);
		return getElements(locator);
	}

	/*
	 * @opPram[0]: timeout
	 * 
	 * @opPram[1]: 0,1 0: No Assert 1: Assert
	 */
	public WebElement waitForElementNotPresent(Object locator, int... opParams) {
		WebElement elem = null;
		int timeout = opParams.length > 0 ? opParams[0] : defaultTimeOut;
		int isAssert = opParams.length > 1 ? opParams[1] : 1;
		int notDisplayE = opParams.length > 2 ? opParams[2] : 0;

		for (int tick = 0; tick < timeout / waitInterval; tick++) {
			if (notDisplayE == 2) {
				elem = getElement(locator);
			} else {
				elem = getDisplayedElement(locator);
			}
			if (null == elem)
				return null;
			Utils.pause(waitInterval);
		}

		if (isAssert == 1)
			assert false : ("Timeout after " + timeout
					+ "ms waiting for element not present: " + locator);
		info("Element doesn't disappear after " + timeout / 1000 + "s.");
		return elem;
	}

	public boolean isTextPresent(String text) {
		Utils.pause(500);
		String allVisibleTexts = getText(By.xpath("//body"));
		return allVisibleTexts.contains(text);
	}

	public String getText(Object locator) {
		WebElement element = null;
		try {
			element = waitForAndGetElement(locator);
			return element.getText();
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			return getText(locator);
		} finally {
			loopCount = 0;
		}
	}

	public String getSelectText(Object locator) {
		Select element = null;

		try {
			element = new Select(waitForAndGetElement(locator));
			return element.getFirstSelectedOption().getText();
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			return getSelectText(locator);
		} finally {
			loopCount = 0;
		}
	}

	public List<WebElement> getElements(String xpath) {
		try {
			return driver.findElements(By.xpath(xpath));
		} catch (StaleElementReferenceException e) {
			checkCycling(e, 5);
			Utils.pause(1000);
			return getElements(xpath);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * Get list of elements from an locator
	 * 
	 * @author HuyNK1
	 * @param locator
	 * @return
	 */
	public List<WebElement> getElements(Object locator) {
		By by = locator instanceof By ? (By) locator : By.xpath(locator
				.toString());
		List<WebElement> elements = null;
		try {
			elements = driver.findElements(by);
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}
		return elements;
	}

	public boolean isTextNotPresent(String text) {
		return !isTextPresent(text);
	}

	public void pageShouldContain(String str) {
		if (isTextPresent(str)) {
			info("Page contains " + str);
		} else {
			assert false : ("Verify failed");
			info("Page should have contain " + str + ", but did not");
		}

	}

	/**
	 * @param sourceLocator
	 * @param targetLocator
	 *            Comment out xpathSource as it is not used anywhere
	 * @author HuyNK1
	 */
	public void DragDropJs(Object sourceLocator, Object targetLocator) {

		// String[] xpathSource = sourceLocator.toString().split(":");
		String[] xpathTarget = targetLocator.toString().split(":");

		info("Starting execute JavaScript");
		((JavascriptExecutor) driver)
				.executeScript("function simulate(f,c,d,e)"
						+ "{"
						+ " var b,a=null; "
						+ "  for(b in eventMatchers)"
						+ "  if(eventMatchers[b].test(c))"
						+ "   {a=b; break;} "
						+ "	 if(!a) return!1;"
						+ "	 if (document.createEvent) {"
						+ "	   b=document.createEvent(a);"
						+ "	   if (a=='HTMLEvents') {"
						+ "	       b.initEvent(c,!0,!0)"
						+ "	   } else {"
						+ "	    b.initMouseEvent(c,!0,!0,document.defaultView,0,d,e,d,e,!1,!1,!1,!1,0,null);"
						+ "	    f.dispatchEvent(b);"
						+ "	    if (document.createEventObject) {"
						+ "   	    a=document.createEventObject();"
						+ "  	    a.detail=0;"
						+ "  	    a.screenX=d;"
						+ "   	    a.screenY=e;"
						+ "    	    a.clientX=d;"
						+ "    	    a.clientY=e;"
						+ "    	    a.ctrlKey=!1;"
						+ "   	    a.altKey=!1;"
						+ "    	    a.shiftKey=!1;"
						+ "    	    a.metaKey=!1;"
						+ "    	    a.button=1;"
						+ "   	    f.fireEvent('on',c,a);"
						+ "	    }"
						+ "	   }"
						+ "	 }"

						+ "	 return!0 ;"
						+ "}"
						+ "var eventMatchers={HTMLEvents:/^(?:load|unload|abort|error|select|change|submit|reset|focus|blur|resize|scroll)$/,MouseEvents:/^(?:click|dblclick|mouse(?:down|up|over|move|out))$/};"
						+ "var dragElem = window.frames[0].document.getElementById("
						+ xpathTarget[1]
						+ ");"
						+ "var dropPx = window.frames[0].document.getElementById('ctl00_CPH1_RadDock5_C_Handle_ctl00_CPH1_RadDock5');"

						// + "var dropPx = document.evaluate("
						// + xpathSource[1]
						// + ",document,null,9,null).singleNodeValue;"
						+ "var position = dropPx.getBoundingClientRect();"
						+ "var x = position.left;" + "var y = position.top;"
						+ "simulate(dragElem,'mousedown',0,0); "
						+ "simulate(dragElem,'mousemove',x,y); "
						+ "simulate(dragElem,'mouseup',x,y); ");
	}

	public void dragAndDropToObject(Object sourceLocator, Object targetLocator) {
		info("--Drag and drop to object--");
		Actions action = new Actions(driver);
		try {
			String driverInfo = driver.toString().toLowerCase();
			info("inside drag drop function");
			if (driverInfo.contains("safari")) {
				DragDropJs(sourceLocator, targetLocator);
			} else {
				WebElement source = waitForAndGetElement(sourceLocator);
				WebElement target = waitForAndGetElement(targetLocator);
				action.dragAndDrop(source, target).build().perform();
			}

		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			dragAndDropToObject(sourceLocator, targetLocator);
		} catch (UnhandledAlertException e) {
			try {
				Alert alert = driver.switchTo().alert();
				alert.accept();
				switchToParentWindow();
			} catch (NoAlertPresentException eNoAlert) {
			}
		}

		finally {
			loopCount = 0;
		}
		Utils.pause(1000);
	}

	/**
	 * 
	 * @param locator
	 * @param opParams
	 *            Modified on 2014-May-23: Add param isAssert
	 */
	public void click(Object locator, Object... opParams) {

		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		int isAssert = (Integer) (opParams.length > 1 ? opParams[1] : 1);

		//Actions actions = new Actions(driver);
		try {
			WebElement element = waitForAndGetElement(locator, defaultTimeOut,
					isAssert, notDisplay);
			String driverInfo = driver.toString().toLowerCase();
			if (element.isEnabled()) {
				// info(driverInfo);
				if (driverInfo.contains("safari")
						|| driverInfo.contains("firefox") || driverInfo.contains("chrome")) {
					element.click();
					// actions.click(element).perform();
				} else {
					//LongND_start fix click on IE and EDGE
					//actions.moveToElement(element).click().perform();
					element.sendKeys(Keys.ENTER);
					//LongND_end
				}
			} else {
				debug("Element is not enabled");
				click(locator, notDisplay);
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			click(locator, notDisplay);
		} catch (ElementNotVisibleException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			click(locator, notDisplay);
		} finally {
			loopCount = 0;
		}
	}

	public void clearCache() {
		Actions actionObject = new Actions(driver);
		try {
			actionObject.sendKeys(Keys.CONTROL).sendKeys(Keys.F5).build()
					.perform();
		} catch (WebDriverException e) {
			debug("Retrying clear cache...");
			actionObject.sendKeys(Keys.CONTROL).sendKeys(Keys.F5).build()
					.perform();
		}
	}

	// Use this function to verify if a check-box is checked (using when
	// creating a portal/publicMode)
	public void check(Object locator, int... opParams) {
		int notDisplayE = opParams.length > 0 ? opParams[0] : 0;
		Actions actions = new Actions(driver);
		try {
			WebElement element = waitForAndGetElement(locator, defaultTimeOut,
					1, notDisplayE);

			if (!element.isSelected()) {
				actions.click(element).perform();
			} else {
				info("Element " + locator + " is already checked .");
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			check(locator, opParams);
		} finally {
			loopCount = 0;
		}
	}

	public String getValue(Object locator) {
		try {
			return waitForAndGetElement(locator).getAttribute("value");
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			return getValue(locator);
		} finally {
			loopCount = 0;
		}
	}

	public String getAtributeValue(Object locator, String attributeName) {
		try {
			return waitForAndGetElement(locator).getAttribute(attributeName);
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			return getAtributeValue(locator, attributeName);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * 
	 * @param locator
	 * @param safeToSERE
	 *            try xx times (refer to TESTBASE_ACTION_REPEAT in
	 *            config.properties) if true, not repeat if false
	 * @param opParams
	 */
	public void mouseOver(Object locator, boolean safeToSERE,
			Object... opParams) {
		WebElement element;
		Actions actions = new Actions(driver);
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		try {
			if (safeToSERE) {
				for (int i = 1; i < timesToRepeat; i++) {
					element = waitForAndGetElement(locator, 5000, 0, notDisplay);
					if (element == null) {
						Utils.pause(waitInterval);
					} else {
						actions.moveToElement(element).perform();
						break;
					}
				}
			} else {
				element = waitForAndGetElement(locator);
				actions.moveToElement(element).perform();
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			mouseOver(locator, safeToSERE);
		} finally {
			loopCount = 0;
		}
	}

	public void mouseOverAndClick(Object locator) {
		WebElement element;
		Actions actions = new Actions(driver);
		String driverInfo = driver.toString().toLowerCase();

		if (driverInfo.contains("Internet Explorer")) {
			element = getDisplayedElement(locator);
		} else {
			element = waitForAndGetElement(locator);
		}
		actions.moveToElement(element).click(element).build().perform();
	}

	public void waitForTextPresent(String text, int... wait) {
		int waitTime = wait.length > 0 ? wait[0] : defaultTimeOut;
		for (int second = 0;; second++) {
			if (second >= waitTime / waitInterval) {
				Assert.fail("Timeout at waitForTextPresent: " + text);
			}
			if (isTextPresent(text)) {
				break;
			}
			Utils.pause(waitInterval);
		}
	}

	public void waitForTextNotPresent(String text, int... wait) {
		int waitTime = wait.length > 0 ? wait[0] : defaultTimeOut;
		for (int second = 0;; second++) {
			if (second >= waitTime / waitInterval) {
				Assert.fail("Timeout at waitForTextNotPresent: " + text);
			}
			if (isTextNotPresent(text)) {
				break;
			}
			Utils.pause(waitInterval);
		}
	}

	public void waitForMessage(String message, int... wait) {
		int waitTime = wait.length > 0 ? wait[0] : defaultTimeOut;
		// info("--Verify message: " + message);
		Utils.pause(500);
		waitForTextPresent(message, waitTime);
	}

	public void type(Object locator, String value, boolean validate) {
		try {
			if (value.equals("-"))
				return;
			for (int loop = 1;; loop++) {
				if (loop >= timesToRepeat) {
					Assert.fail("Timeout at type: " + value + " into "
							+ locator);
				}
				WebElement element = waitForAndGetElement(locator, 5000, 0);

				if (element != null) {
					if (validate)
						element.clear();
					if (element.getAttribute("readonly") != null
							&& element.getAttribute("readonly").equals("true"))
						((JavascriptExecutor) driver).executeScript(
								"arguments[0].value=arguments[1]", element,
								value);
					else {
						// element.click();
						element.sendKeys(value);
					}
					if (!validate || value.equals(getValue(locator))
							|| value.equals(element.getText().trim())) {
						break;
					}
				}
				info("Repeat action..." + loop + "time(s)");
				Utils.pause(waitInterval);
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			type(locator, value, validate);
			// } catch (ElementNotVisibleException e) {
			// checkCycling(e, defaultTimeOut / waitInterval);
			// Utils.pause(waitInterval);
			// type(locator, value, validate);
		} finally {
			loopCount = 0;
		}
	}

	/*
	 * Same type method but for special key: Keys.Enter....
	 */
	public void shortkey(Object locator, Keys value) {
		try {
			for (int loop = 1;; loop++) {
				if (loop >= timesToRepeat) {
					Assert.fail("Timeout at type: " + value + " into "
							+ locator);
				}
				WebElement element = waitForAndGetElement(locator, 5000, 0);
				if (element != null) {
					element.sendKeys(value);
					break;
				}
				info("Repeat action..." + loop + "time(s)");
				Utils.pause(waitInterval);
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			shortkey(locator, value);
		} catch (ElementNotVisibleException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			shortkey(locator, value);
		} finally {
			loopCount = 0;
		}
	}

	// Select option from combo box
	public void select(Object locator, String option) {
		try {
			if (option.length() <= 0 || option.equals("-"))
				return;
			for (int second = 0;; second++) {
				if (second >= defaultTimeOut / waitInterval) {
					Assert.fail("Timeout at select: " + option + " into "
							+ locator);
				}
				// Select select = new Select(waitForAndGetElement(locator));
				if (isElementNotPresent(locator))
					break;

				Select select = new Select(waitForAndGetElement(locator, 5000,
						0, 2));
				select.selectByVisibleText(option);
				if (option.equals(select.getFirstSelectedOption().getText())) {
					break;
				}
				Utils.pause(waitInterval);
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			select(locator, option);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * Quanlv
	 * 
	 * @param multipleVals
	 * @param propId
	 */
	public void selectMultipelValues(String multipleVals, Object location) {
		String multipleSel[] = multipleVals.split(",");
		for (String valueToBeSelected : multipleSel) {
			try {
				new Select(getElement(location))
						.selectByVisibleText(valueToBeSelected);
				getElement(location).sendKeys(Keys.CONTROL);
			} catch (Exception ex) {
				return;
			}
		}

	}

	// un-check a checked-box
	public void uncheck(Object locator, int... opParams) {
		int notDisplayE = opParams.length > 0 ? opParams[0] : 0;
		Actions actions = new Actions(driver);
		try {
			WebElement element = waitForAndGetElement(locator, defaultTimeOut,
					1, notDisplayE);

			if (element.isSelected()) {
				actions.click(element).perform();
			} else {
				info("Element " + locator + " is already unchecked .");
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, 5);
			Utils.pause(1000);
			uncheck(locator, opParams);
		} finally {
			loopCount = 0;
		}
	}

	public void rightClickOnElement(Object locator) {
		Actions actions = new Actions(driver);
		Utils.pause(500);
		try {
			WebElement element = waitForAndGetElement(locator);
			actions.contextClick(element).perform();
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			rightClickOnElement(locator);
		} catch (ElementNotVisibleException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			click(locator);
		} finally {
			loopCount = 0;
		}
	}

	// doubleClickOnElement
	public void doubleClickOnElement(Object locator) {
		Actions actions = new Actions(driver);
		try {
			WebElement element = waitForAndGetElement(locator);
			actions.doubleClick(element).perform();
		} catch (StaleElementReferenceException e) {
			checkCycling(e, 5);
			Utils.pause(1000);
			doubleClickOnElement(locator);
		} finally {
			loopCount = 0;
		}
	}

	public void checkCycling(Exception e, int loopCountAllowed) {
		info("Exception:" + e.getClass().getName());
		if (loopCount > loopCountAllowed) {
			Assert.fail("Cycled: " + e.getMessage());
		}
		info("Repeat... " + loopCount + "time(s)");
		loopCount++;
	}

	// function to switch to parent windows
	public void switchToParentWindow() {
		try {
			Set<String> availableWindows = driver.getWindowHandles();
			String WindowIdParent = null;
			int counter = 1;
			for (String windowId : availableWindows) {
				if (counter == 1) {
					WindowIdParent = windowId;
				}
				counter++;
			}
			driver.switchTo().window(WindowIdParent);
			Utils.pause(1000);
		} catch (WebDriverException e) {
			e.printStackTrace();
		}
	}

	public void switchtoFrame(String frameId) {
		if (isElementPresent(By.id(frameId)))
			driver.switchTo().frame(driver.findElement(By.id(frameId)));
		else
			assert (false) : "Cannot find frame element with Id: " + frameId;
	}

	public boolean isDisplay(Object locator) {
		boolean bool = false;
		WebElement e = getElement(locator);
		try {
			if (e != null)
				bool = e.isDisplayed();
		} catch (StaleElementReferenceException ex) {
			checkCycling(ex, 10);
			Utils.pause(waitInterval);
			isDisplay(locator);
		} finally {
			loopCount = 0;
		}
		return bool;
	}

	public boolean isDisplay(Object locator, int timeout) {
		int i = 0;
		int time = timeout / waitInterval;

		while (i < time) {
			Utils.pause(waitInterval);
			if (isDisplay(locator)) {
				return true;
			} else {
				i++;
			}
		}
		return false;
	}

	public void waitForElementNotDisplay(Object locator, int timeout) {
		int i = 0;
		int time = timeout / waitInterval;

		while (i < time) {
			if (isDisplay(locator)) {
				Utils.pause(waitInterval);
				i++;
			} else {
				break;
			}
		}
	}

	/**
	 * function: set driver to auto save file to TestData/TestOutput
	 * 
	 * @author HuyNK1 Remove this line: baseUrl =
	 *         appConfig.getProperty("APP_BASEURL"); as baseUrl is already
	 *         initialized in constructor
	 */
	public void getDriverAutoSave() {
		String pathFile = System.getProperty("user.dir")
				+ "/src/main/resources/TestData/TestOutput";

		FirefoxProfile fp = new FirefoxProfile();
		fp.setPreference("browser.download.folderList", 2);
		info("Save file to " + pathFile);
		fp.setPreference("browser.download.dir", pathFile);
		// fp.setPreference("browser.helperApps.neverAsk.saveToDisk",
		// "application/x-zip;application/x-zip-compressed;application/x-winzip;application/zip;application/bzip2;"
		// +
		// "gzip/document;multipart/x-zip;application/x-gunzip;application/x-gzip;application/x-gzip-compressed;"
		// +
		// "application/x-bzip;application/gzipped;application/gzip-compressed;application/gzip;application/octet-stream");

		fp.setPreference(
				"browser.helperApps.neverAsk.saveToDisk",
				"application/x-xpinstall;"
						+ "application/x-zip;application/x-zip-compressed;application/x-winzip;application/zip;"
						+ "gzip/document;multipart/x-zip;application/x-gunzip;application/x-gzip;application/x-gzip-compressed;"
						+ "application/x-bzip;application/gzipped;application/gzip-compressed;application/gzip"
						+ "application/octet-stream"
						+ ";application/pdf;application/msword;text/plain;"
						+ "application/octet;text/calendar;text/x-vcalendar;text/Calendar;"
						+ "text/x-vCalendar;image/jpeg;image/jpg;image/jp_;application/jpg;"
						+ "application/x-jpg;image/pjpeg;image/pipeg;image/vnd.swiftview-jpeg;image/x-xbitmap;image/png;application/xml");

		fp.setPreference("browser.helperApps.alwaysAsk.force", false);
		// driver = new FirefoxDriver();
		action = new Actions(driver);
	}

	/**
	 * function set driver to auto open new window when click link
	 * 
	 * @author HuyNK1 Remove this line: baseUrl =
	 *         appConfig.getProperty("APP_BASEURL"); as baseUrl is already
	 *         initialized in constructor
	 */
	public void getDriverAutoOpenWindow() {
		// FirefoxProfile fp = new FirefoxProfile();
		// fp.setPreference("browser.link.open_newwindow.restriction", 2);
		// driver = new FirefoxDriver();
		action = new Actions(driver);
	}

	/**
	 * function: check a file existed in folder
	 * 
	 * @author
	 * @param file
	 *            : file name (eg: export.zip)
	 * @return: true -> file exist false-> file is not exist
	 */
	public boolean checkFileExisted(String file) {
		String pathFile = System.getProperty("user.dir")
				+ "/src/main/resources/TestData/TestOutput/" + file;
		boolean found = false;

		if (new File(pathFile).isFile()) {
			found = true;
		}
		info("File exists: " + found);
		return found;
	}

	/**
	 * function delete file in folder test output
	 * 
	 * @author
	 * @param file
	 *            : file name
	 */
	public void deleteFile(String file) {
		String pathFile = System.getProperty("user.dir")
				+ "/src/main/resources/TestData/" + file;
		File Files = new File(pathFile);

		Files.setWritable(true);
		Files.delete();
		if (checkFileExisted(file) == false) {
			info("Delete file successfully");
		} else
			info("Have error when delete file");
	}

	/**
	 * @author
	 * @param fileName
	 */
	public void cutPasteFileFromOutputToTestData(String fileName) {
		String source = System.getProperty("user.dir")
				+ "/src/main/resources/TestData/TestOutput/" + fileName;
		// directory where file will be copied
		String target = System.getProperty("user.dir")
				+ "/src/main/resources/TestData/";

		// name of source file
		File sourceFile = new File(source);
		String name = sourceFile.getName();

		File targetFile = new File(target + name);

		// copy file from one location to other
		try {
			FileUtils.copyFile(sourceFile, targetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// delete file in TestOutput
		deleteFile("TestOutput/" + fileName);
	}

	public enum Language {
		en, fr, vi, lo;
	}

	/**
	 * @author HuyNK1 Remove this line: baseUrl =
	 *         appConfig.getProperty("APP_BASEURL"); as baseUrl is already
	 *         initialized in constructor
	 * @param language
	 */
	public void getDriverSetLanguage(Language language) {

		// driver = new FirefoxDriver();
		action = new Actions(driver);
	}

	/**
	 * Do Comparison: If element[i] != element[i+1] => test case failed
	 * 
	 * @param values
	 * @return true if all elements are equal
	 * @author HuyNK1
	 */
	public boolean areAllElementsEqual(int[] values) {
		for (int i = 0; i < values.length - 1; i++) {
			if (values[i] != values[i + 1]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if a date string is equal to today This compare dates only (without
	 * caring about time)
	 * 
	 * @author HuyNK1
	 * @param sourceDateString
	 * @param sourceDateFormat
	 * @return
	 */
	public boolean isToday(String sourceDateString, String sourceDateFormat) {
		SimpleDateFormat formatter = new SimpleDateFormat(sourceDateFormat);
		Date sourceDate;
		Date today = new Date();
		try {

			sourceDate = formatter.parse(sourceDateString);
			today = formatter.parse(formatter.format(today));

			// Logging
			info("Source date: " + sourceDate.toString());
			info("System date (today): " + today.toString());
			// End of logging

			if (sourceDate.compareTo(today) == 0) {
				return true;
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * @author HuyNK1
	 * @param sourceDateString
	 * @param sourceDateFormat
	 * @return > 0 if source date-time after before now; = 0 if equal; < 0 if
	 *         source date-time is before now
	 * @throws ParseException
	 */
	public int compareWithSystemTime(String sourceDateString,
			String sourceDateFormat) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(sourceDateFormat);
		Date sourceDate;
		Date today = new Date();

		sourceDate = formatter.parse(sourceDateString);
		today = formatter.parse(formatter.format(today));

		// Logging
		info("Source date: " + sourceDate.toString());
		info("System datetime: " + today.toString());
		// End of logging

		return sourceDate.compareTo(today);
	}

	/**
	 * Verify if an image has been loaded or not
	 * 
	 * @param imageElement
	 * @return true if loaded, false if not
	 * @author HuyNK1
	 * @throws InterruptedException
	 */
	public boolean waitAndCheckImageLoaded(WebElement imageElement,
			int... timeoutMiliSeconds) throws InterruptedException {
		int timeStep = 1000;
		int count = 0;
		int timesToTry;

		// Get timeout param
		if (timeoutMiliSeconds.length > 0) {
			timesToTry = timeoutMiliSeconds[0] / timeStep;
		} else {
			timesToTry = defaultTimeOut / timeStep;
		}

		// Repeat until timeout
		while (count <= timesToTry) {
			if (isImageLoaded(imageElement)) {
				return true;
			}

			count++;
			Thread.sleep(timeStep);
		}

		return false;
	}

	/**
	 * Verify if list of images has been loaded or not
	 * 
	 * @param byListImageElement
	 * @param timeoutMiliSeconds
	 * @return
	 * @throws InterruptedException
	 */
	public boolean waitAndCheckImageListLoaded(By byListImageElement,
			int... timeoutMiliSeconds) throws InterruptedException {
		int timeStep = 1000; // how long is each wait
		int timesToTry;
		int count = 0;

		// Get timeout param
		if (timeoutMiliSeconds.length > 0) {
			timesToTry = timeoutMiliSeconds[0] / timeStep;
		} else {
			timesToTry = defaultTimeOut / timeStep;
		}

		// Repeat until timeout
		while (count <= timesToTry) {
			if (isImageListLoaded(byListImageElement)) {
				return true;
			}

			count++;
			Thread.sleep(timeStep);
		}

		return false;
	}

	// ================= Begin of Internal utilities =================//
	/**
	 * Use JS to verify if ALL IMAGES have been loaded or not
	 * 
	 * @param byListImageElement
	 * @return true if loaded, false if not
	 * @author HuyNK1
	 */
	private boolean isImageListLoaded(By byListImageElement) {
		List<WebElement> listLogos = waitForAndGetElements(byListImageElement,
				2000);

		// loop through each image and check
		for (int i = 0; i < listLogos.size(); i++) {
			if (!isImageLoaded(listLogos.get(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Use JS to verify if an image has been loaded or not
	 * 
	 * @param imageElement
	 * @return true if loaded, false if not
	 * @author HuyNK1
	 */
	private boolean isImageLoaded(WebElement imageElement) {
		// info("Check if image loaded: " +
		// imageElement.getAttribute("src"));
		Boolean isLoaded = (Boolean) ((JavascriptExecutor) driver)
				.executeScript(
						"return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0",
						imageElement);
		return isLoaded;
	}

	// ================= End of Internal utilities=================//

	/**
	 * Set parameter for xpath
	 * 
	 * @param by
	 * @param param
	 * @return
	 * @author ThanhPV7 - 20140716
	 */
	public By setParamXpath(By by, String param) {
		by = By.xpath(by.toString().replace("By.xpath: ", "")
				.replace("${param}", param));
		return by;
	}

	public void back() {
		driver.navigate().back();
	}

	/* Begin: Functions support Assertion and Soft Assertion */

	/* Assertion */

	public void assertTrue(boolean condition) throws Throwable {
		try {
			Assert.assertTrue(condition);
		} catch (Throwable e) {
			log.writeLog(e.getMessage(), Constants.FAILED);
			throw e;
		}
	}

	public void assertTrue(boolean condition, String message) throws Throwable {
		try {
			Assert.assertTrue(condition, message);
			log.writeLog(message, Constants.PASSED);
		} catch (Throwable e) {
			log.writeLog(e.getMessage(), Constants.FAILED);
			throw e;
		}
	}

	public void assertFalse(boolean condition) throws Throwable {
		try {
			Assert.assertFalse(condition);
		} catch (Throwable e) {
			log.writeLog(e.getMessage(), Constants.FAILED);
			throw e;
		}
	}

	public void assertFalse(boolean condition, String message) throws Throwable {
		try {
			Assert.assertFalse(condition, message);
			log.writeLog(message, Constants.PASSED);
		} catch (Throwable e) {
			log.writeLog(e.getMessage(), Constants.FAILED);
			throw e;
		}
	}

	public void assertEquals(boolean actual, boolean expected) throws Throwable {
		try {
			Assert.assertEquals(actual, expected);
		} catch (Throwable e) {
			log.writeLog(e.getMessage(), Constants.FAILED);
			throw e;
		}
	}

	public void assertEquals(Object actual, Object expected) throws Throwable {
		try {
			Assert.assertEquals(actual, expected);

		} catch (Throwable e) {
			log.writeLog(e.getMessage(), Constants.FAILED);
			throw e;
		}
	}

	public void assertEquals(Object[] actual, Object[] expected)
			throws Throwable {
		try {
			Assert.assertEquals(actual, expected);
		} catch (Throwable e) {
			log.writeLog(e.getMessage(), Constants.FAILED);
			throw e;
		}
	}

	public void assertEquals(Object actual, Object expected, String message)
			throws Throwable {
		try {
			Assert.assertEquals(actual, expected, message);
			log.writeLog(message, Constants.PASSED);
		} catch (Throwable e) {
			log.writeLog(e.getMessage(), Constants.FAILED);
			throw e;
		}
	}

	/* Verify - Soft Assertion */

	public void verifyTrue(boolean condition) {
		try {
			assertTrue(condition);
		} catch (Throwable e) {
			addVerificationFailure(e);
		}
	}

	public void verifyTrue(boolean condition, String message) {
		try {
			assertTrue(condition, message);
		} catch (Throwable e) {
			addVerificationFailure(e);
		}
	}

	public void verifyFalse(boolean condition) {
		try {
			assertFalse(condition);
		} catch (Throwable e) {
			addVerificationFailure(e);
		}
	}

	public void verifyFalse(boolean condition, String message) {
		try {
			assertFalse(condition, message);
		} catch (Throwable e) {
			addVerificationFailure(e);
		}
	}

	public void verifyEquals(boolean actual, boolean expected) {
		try {
			assertEquals(actual, expected);
		} catch (Throwable e) {
			addVerificationFailure(e);
		}
	}

	public void verifyEquals(Object actual, Object expected) {
		try {
			assertEquals(actual, expected);
		} catch (Throwable e) {
			addVerificationFailure(e);
		}
	}

	public void verifyEquals(Object actual, Object expected, String message) {
		try {
			assertEquals(actual, expected, message);
		} catch (Throwable e) {
			addVerificationFailure(e);
		}
	}

	public void verifyEquals(Object[] actual, Object[] expected) {
		try {
			assertEquals(actual, expected);
		} catch (Throwable e) {
			addVerificationFailure(e);
		}
	}

	public static void fail(String message) {
		Assert.fail(message);
	}

	public List<Throwable> getVerificationFailures() {
		List<Throwable> verificationFailures = verificationFailuresMap
				.get(Reporter.getCurrentTestResult());
		return verificationFailures == null ? new ArrayList<Throwable>()
				: verificationFailures;
	}

	private void addVerificationFailure(Throwable e) {
		List<Throwable> verificationFailures = getVerificationFailures();
		verificationFailuresMap.put(Reporter.getCurrentTestResult(),
				verificationFailures);
		verificationFailures.add(e);
	}

	/* End: Functions support Assertion and Soft Assertion */
	
	/**
	 * LongND click element
	 * @param element
	 * @param opParams
	 */
	public void click(WebElement element, Object... opParams) {

		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);

		//Actions actions = new Actions(driver);
		try {
			String driverInfo = driver.toString().toLowerCase();
			if (element.isEnabled()) {
				// info(driverInfo);
				if (driverInfo.contains("safari")
						|| driverInfo.contains("firefox") || driverInfo.contains("chrome")) {
					element.click();
					// actions.click(element).perform();
				} else {
					//LongND_start fix click on IE and EDGE
					//actions.moveToElement(element).click().perform();
					element.sendKeys(Keys.ENTER);
					//LongND_end
				}
			} else {
				debug("Element is not enabled");
				click(element, notDisplay);
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			click(element, notDisplay);
		} catch (ElementNotVisibleException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			click(element, notDisplay);
		} finally {
			loopCount = 0;
		}
	}
	
	/**
	 * @author blueship
	 * @param element
	 * @param opParams
	 */
	// Use this function to verify if a check-box is checked (using when
	// creating a portal/publicMode)
	public void check(WebElement element, int... opParams) {
		Actions actions = new Actions(driver);
		try {
			if (!element.isSelected()) {
				actions.click(element).perform();
			} else {
				info("Element is already checked .");
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, defaultTimeOut / waitInterval);
			Utils.pause(waitInterval);
			check(element, opParams);
		} finally {
			loopCount = 0;
		}
	}
}
