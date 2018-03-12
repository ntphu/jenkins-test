package com.blueship.atlibs;

import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.Reporter;

public class TestLogger {

	static String fileName ;
	static long startTime;
	static String step = "----- ";
	static String blank = "    ";
	static String status= "    ";
	public static String browser;
	public static Properties config = Utils.loadConfig(LibConstants.DEFAULT_CONFIG_FILE);
	
	public static void log(String message, Level level) {
		Logger logger = Logger.getLogger(TestBase.class);
		String logMessage = message;
		logMessage = String.format("%s", message);
		logger.log(level, logMessage);
		
		// write log into HTML report
		Reporter.log(logMessage);
	}

	public static void trace(String message) {
		log(message, Level.TRACE);
	}

	public static void debug(String message) {
		log(message, Level.DEBUG);
	}

	public static void info(String message) {
		log(message, Level.INFO);
	}

	public static void warn(String message) {
		log(message, Level.WARN);
	}

	public static void error(String message) {
		log(message, Level.ERROR);
	}
	
}