package com.blueship.atlibs;

import org.apache.log4j.Level;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestReportListener extends TestListenerAdapter {

	private void logResult(ITestResult result) {
        Level level = Level.INFO;
        String message = "";
        switch (result.getStatus()) {
        case ITestResult.STARTED:
        	message = "TEST CASE STARTED: " + result.getName();
        	break;
        case ITestResult.FAILURE:
        	level = Level.ERROR;
        	message = "TEST FAILED" + Constants.LINE_BREAK;
        	break;
        case ITestResult.SKIP:
        	message = "TEST SKIPPED: " + result.getName() + Constants.LINE_BREAK;
        	break;
        case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
        	level = Level.WARN;
        	break;
        case ITestResult.SUCCESS:
        	message = "TEST PASSED" + Constants.LINE_BREAK;
        	break;
        }
        TestLogger.log(message, level);
    	//  write extra log
        //  if(result.getStatus() != ITestResult.SKIP){
        TestBase testbase = this.getTestBaseObject(result); 
        if(result.getStatus() != ITestResult.STARTED){ // test duration
        	long duration = result.getEndMillis() - result.getStartMillis();
            String strDuration = Long.toString(duration/1000);
            if(testbase != null) testbase.log.writeLog("Duration:" + Constants.DELIMITER + strDuration + "s", false);
        }
        if(testbase != null) testbase.log.writeLog(message, false);
    	//  }
    }

    @Override
    public void onTestStart(ITestResult result) {
    	logResult(result);
    }

    /**
     * update function to capture screen use selenium's web driver screenshot
     * modify date: 27-Aug-14 by VinhND2
     * 
     * */
    @Override
    public void onTestFailure(ITestResult result) {
		String sMethodName = result.getMethod().getMethodName();
		Object currentClass = result.getInstance();
		WebDriver driver = ((TestCase)currentClass).action.driver;
		if (driver != null)
        {
           Utils.captureScreen(driver, sMethodName + ".PNG");
        }
		//    	Utils.captureScreen(sMethodName + ".PNG");
    	logResult(result);
    	
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