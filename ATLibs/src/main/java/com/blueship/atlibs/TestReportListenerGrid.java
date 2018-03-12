package com.blueship.atlibs;

import org.apache.log4j.Level;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * As when running with Grid, the log will be mixed. So it's not correct to show
 * each test case one after one
 * 
 * @author HuyNK1
 * 
 */
public class TestReportListenerGrid extends TestListenerAdapter {

	private void logResult(ITestResult result) {
		Level level = Level.INFO;
		String message = "";
		switch (result.getStatus()) {
		case ITestResult.STARTED:
			message = "[" + result.getName() + "] ---> STARTED";
			break;
		case ITestResult.FAILURE:
			level = Level.ERROR;
			message = "[" + result.getName() + "] ---> FAILED";
			break;
		case ITestResult.SKIP:
			message = "[" + result.getName() + "] ---> SKIPPED";
			break;
		case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
			level = Level.WARN;
			break;
		case ITestResult.SUCCESS:
			message = "[" + result.getName() + "] ---> PASSED";
			break;
		}
		
		TestLogger.log(message, level);
    	// write extra log
    	this.getTestBaseObject(result).log.writeLog(message);
	}

	@Override
	public void onTestStart(ITestResult result) {
		logResult(result);
	}

	/**
	 * Capture screen use remote web driver screenshot function
	 * */
	@Override
	public void onTestFailure(ITestResult result) {
		String sMethodName = result.getMethod().getMethodName();
		Object currentClass = result.getInstance();
		WebDriver driver = ((TestCase)currentClass).action.driver;
		WebDriver augmentedDriver = new Augmenter().augment(driver);
		if (driver != null)
        {
           Utils.captureScreen(augmentedDriver, sMethodName + ".PNG");
        }
//		Utils.captureScreen(sMethodName + ".PNG");
//		logResult(result);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		logResult(result);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		logResult(result);
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		logResult(result);
	}
	
	private TestBase getTestBaseObject(ITestResult result){
    	Object currentClass = result.getInstance();
		return ((TestCase)currentClass).action;
    }
}