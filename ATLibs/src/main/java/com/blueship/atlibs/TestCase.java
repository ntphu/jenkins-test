package com.blueship.atlibs;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

public class TestCase {
	public TestBase action;
	protected String dataFile;
	protected String dataSheet;
	public FileLogger fileLogger;
	public String pathCustomReport;

	public TestCase() {
		// initialize selenium action base
		action = new TestBase();
		// fileLogger = new FileLogger();
	}

	public TestCase(TestBase action) {
		// action.driver = driver;
		this.action = action;
		// fileLogger = new FileLogger();
	}

	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	public void setDataSheet(String dataSheet) {
		this.dataSheet = dataSheet;
	}

	public List<HashMap<String, String>> loadData(String tableName) {
		return loadData(tableName, true);
	}

	/**
	 * Load test data from data excel file
	 * 
	 * @param tableName
	 * @param isMulti
	 *            separate multiple browser
	 * @return
	 */
	public List<HashMap<String, String>> loadData(String tableName,
			boolean isMulti) {
		String browserType = action.driver.toString().toUpperCase();

		if (browserType.contains(Constants.BROWSER_TYPE_FIREFOX.toUpperCase())) {
			browserType = Constants.BROWSER_TYPE_FIREFOX;
		} else if (browserType.contains(Constants.BROWSER_TYPE_CHROME
				.toUpperCase())) {
			browserType = Constants.BROWSER_TYPE_CHROME;
		}
		if (isMulti) {
			return Utils.getTestData(this.dataFile, this.dataSheet, tableName
					+ "_" + browserType.toUpperCase());
		} else {
			return Utils.getTestData(this.dataFile, this.dataSheet, tableName);
		}
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

	@Parameters("browser")
	@BeforeClass
	public void beforeClass(String browser) throws MalformedURLException {
		fileLogger = null;
		if (browser.equalsIgnoreCase(Constants.PORTAL_BROWSER)) {
			action.initSeleniumTest();
		} else {
			action.initSeleniumGridTest(browser, "");
		}
		// start write log
		// fileLogger = new FileLogger();
		// fileLogger.browser = browser;
		// fileLogger.startWriteLog(this.getClass().getName());

		// start logging
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(Calendar.getInstance().getTime());
		action.log.startLogging(browser + "_" + this.getClass().getSimpleName()
				+ "_" + timestamp);
		// log file header
		action.log.writeLog("Test Report:" + Constants.DELIMITER
				+ this.getClass().getSimpleName(), false);
		action.log.writeLog("Running at:" + Constants.DELIMITER
				+ Calendar.getInstance().getTime() + Constants.LINE_BREAK
				+ Constants.LINE_BREAK, false);
	}

	@AfterClass
	public void afterClass() {
		/**
		 * when use edge browser, can not run deleteAllCookies
		 */
		if (action.driver.toString().matches("^.*(Edge).*$") == false) {
			action.driver.manage().deleteAllCookies();
		}
		action.log.endLogging();
		action.driver.quit();
	}

	@Parameters("browser")
	@BeforeMethod
	public void beforeMethod(Method method, String browser) {
		// fileLogger.writeLogMessage("");
		// fileLogger.writeLogMessage("Begin Test Method: " + method.getName());
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		info("Result: " + result.toString());
		String fileName = result.getInstance().getClass().getSimpleName() + "_" + result.getMethod().getMethodName() + "_"
				+ Constants.CAPTURE_SCREEN_PNG;
		Utils.captureScreen(action.driver, fileName);
		// boolean status = (result.getStatus() == 1) ? true : false;
		// fileLogger.endWriteLog(status);
		action.log.writeLog("Duration:	"
				+ String.valueOf((result.getEndMillis() - result
						.getStartMillis()) / 1000) + "s", false);
		String status = (result.getStatus() == 1) ? Constants.PASSED
				: Constants.FAILED;
		action.log.writeLog("Test Result : " + status, false);
	}

	@BeforeSuite
	public void beforeSuite() {
		// remove log & screenshot in target folder
		String path_source = "/target/";
		String path_log = "log";
		String path_screenshot = "screenshot";
		String path_custom_report = "custom_report";
		try {
			// create report folder if not existed
			String curDir = System.getProperty("user.dir");
			path_source = curDir + path_source;
			String path_log_src = path_source + path_log;
			String path_screenshot_src = path_source + path_screenshot;
			String path_custom_report_src = path_source + path_custom_report;

			System.out.println(path_log_src);
			System.out.println(path_screenshot_src);

			// remove if exist
			File f = new File(path_log_src);
			if (f.exists()) {
				FileUtils.deleteDirectory(f);
			}
			f = new File(path_screenshot_src);
			if (f.exists()) {
				FileUtils.deleteDirectory(f);
			}
			f = new File(path_custom_report_src);
			if (f.exists()) {
				FileUtils.deleteDirectory(f);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterSuite
	public void afterSuite(ITestContext context) {
		info("Start copy the log & screenshot folder to report folder, and log file");
		String path_report = "/report/";
		String path_source = "/target/";

		String path_suite = context.getSuite().getName();
		String timestamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss")
				.format(Calendar.getInstance().getTime());
		path_suite = path_suite + "_" + timestamp;

		String path_log = "log";
		String path_screenshot = "screenshot";
		String path_custom_report = "custom_report";
		// String path_logfile = "test.log";
		String path_logfile = "*.txt";

		try {
			// create report folder if not existed
			String curDir = System.getProperty("user.dir");
			path_report = curDir + path_report;
			path_source = curDir + path_source;
			path_suite = path_report + path_suite + "/";

			String path_log_src = path_source + path_log;
			String path_screenshot_src = path_source + path_screenshot;
			String path_custom_report_src = path_source + path_custom_report;
			File fhtml_report = new File(path_custom_report_src);
			if (!fhtml_report.exists())
				fhtml_report.mkdir();
			new File(path_custom_report_src + "\\path.txt");

			File folder = new File(path_log_src);
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].getName().matches("^.*(\\.txt)$")) {
					path_logfile = path_log + "/" + listOfFiles[i].getName();
					break;
				}
			}

			String path_logfile_src = path_source + path_logfile;

			String path_log_des = path_suite + path_log;
			String path_screenshot_des = path_suite + path_screenshot;
			String path_custom_report_des = path_suite + path_custom_report;
			String path_logfile_des = path_suite + path_logfile;

			// create report folder if not existed
			File f = new File(path_report);
			if (!f.exists())
				f.mkdir();
			// create test suite report folder
			f = new File(path_suite);
			if (!f.exists())
				f.mkdir();

			info("copy log file");
			FileUtils.copyFile(new File(path_logfile_src), new File(
					path_logfile_des));

			info("copy log folder");
			File flog = new File(path_log_src);
			if (flog.exists()) {
				FileUtils.copyDirectory(flog, new File(path_log_des));
			}

			info("copy screenshot folder");
			File fshot = new File(path_screenshot_src);
			if (fshot.exists()) {
				FileUtils.copyDirectory(fshot, new File(path_screenshot_des));
			}
			pathCustomReport = path_custom_report_des;

			/*
			 * info("copy custom_report folder"); File fhtml = new
			 * File(path_custom_report_src); if (fhtml.exists()) {
			 * FileUtils.copyDirectory(fhtml, new File(path_custom_report_des));
			 * }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param actualResult
	 * @param expectedResult
	 * @return
	 * @author ThanhPV7 - 20140812
	 */
	public boolean compareResult(boolean actualResult, String expectedResult) {
		if (actualResult) {
			if ("TRUE".equals(expectedResult.toUpperCase())) {
				return true;
			} else {
				return false;
			}
		} else {
			if ("FALSE".equals(expectedResult.toUpperCase())) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Prepare data for testing
	 * 
	 * @param tblData
	 * @author ThanhPV7 - 20140818
	 */
	public Object[][] dpPrepareData(String tblData, boolean isMulti) {
		int size = 0;
		List<HashMap<String, String>> datas = loadData(tblData, isMulti);
		Object[][] array = new Object[datas.size()][];

		size = datas.size();
		for (int i = 0; i < size; i++) {
			array[i] = new Object[] { datas.get(i) };
		}
		return array;
	}
}
