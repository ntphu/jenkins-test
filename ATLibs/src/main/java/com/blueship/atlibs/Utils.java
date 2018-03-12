package com.blueship.atlibs;

import static com.blueship.atlibs.TestLogger.*;

import java.awt.AWTException;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
//import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.imageio.ImageIO;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

//import java.util.Hashtable;

/**
 * Utils.java
 * 
 * @author
 * 
 */
public class Utils {
	/**
	 * Commented out the line
	 * "org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();" as
	 * it is never used
	 * 
	 * @author HuyNK1
	 * @param timeInMillis
	 */
	static Properties appConfig;
	static String dbUrl;
	static String dbUsername;
	static String dbPassword;

	public static void pause(long timeInMillis) {
		// org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();

		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Capture the screen of the current graphics device
	 * 
	 * @author
	 * @param fileName
	 *            : input an image name (String)
	 * @throws InterruptedException
	 */
	public static void captureScreen(String fileName) {
		String path;
		BufferedImage screenCapture;
		pause(3000);
		try {
			Robot robot = new Robot();
			Rectangle screenSize = getScreenSize();
			screenCapture = robot.createScreenCapture(screenSize);
			// Save as PNG
			String curDir = System.getProperty("user.dir");
			path = curDir + "/target/screenshot/";
			File f = new File(path);
			if (!f.exists())
				f.mkdir();
			ImageIO.write(screenCapture, "png", new File(path + fileName));

		} catch (AWTException e) {
			error("Failed to capture screenshot");
		} catch (IOException e) {
			path = "Failed to capture screenshot: " + e.getMessage();
		}
	}

	/**
	 * Capture the screen use selenium's web driver screen shot
	 * */
	public static void captureScreen(WebDriver driver, String fileName) {
		String path;
		try {
			// Save as PNG
			String curDir = System.getProperty("user.dir");
			path = curDir + "/target/screenshot/";
			File f = new File(path);
			if (!f.exists())
				f.mkdir();
			File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(file, new File(path + fileName));
		} catch (Exception e) {
			path = "Failed to capture screenshot: " + e.getMessage();
		}
	}

	/**
	 * 
	 * @return the size of the default screen
	 */
	public static Rectangle getScreenSize() {
		GraphicsEnvironment graphE = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice graphD = graphE.getDefaultScreenDevice();
		DisplayMode displayM = graphD.getDisplayMode();
		return new Rectangle(displayM.getWidth(), displayM.getHeight());
	}

	/**
	 * Simulating keyboard presses
	 * 
	 * @author
	 * @param firstKey
	 *            : send the first key (type: KeyEvent)
	 * @param secondKey
	 *            : send the second key (type: KeyEvent)
	 * @throws InterruptedException
	 */
	public static void javaSimulateKeyPress(int firstKey, Object... params) {
		int secondKey = (Integer) (params.length > 0 ? params[0] : KeyEvent.VK_ENTER);
		try {
			Robot robot = new Robot();
			// Simulate a key press
			robot.keyPress(firstKey);
			robot.keyPress(secondKey);
			pause(3000);
			robot.keyRelease(secondKey);
			robot.keyRelease(firstKey);

		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	// This function returns a absolute path from a relative path
	public static String getAbsoluteFilePath(String relativeFilePath) {
		String curDir = System.getProperty("user.dir");
		String absolutePath = curDir + "/src/main/resources/" + relativeFilePath;
		return absolutePath;
	}

	// InputStream to String
	// Get a File Content
	public static String getFileContent(String filePath) {
		String path = getAbsoluteFilePath(filePath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			error("Failed to find location of... " + filePath);
		}
		Scanner scanner = new Scanner(fis, "UTF-8");
		String inputStreamString = scanner.useDelimiter("\\A").next();
		scanner.close();
		return inputStreamString;
	}

	// Get a file name from current Url
	public static String getFileNameFromCurrentUrl(WebDriver driver, Object... params) {
		Boolean extension = (Boolean) (params.length > 0 ? params[0] : false);

		String currentUrl = driver.getCurrentUrl();
		File file = new File(currentUrl);
		String fileNameWithExt = file.getName();

		if (extension) {
			int position = fileNameWithExt.lastIndexOf(".");
			String fileNameWithOutExt = null;
			if (position >= 0) {
				fileNameWithOutExt = fileNameWithExt.substring(0, position);
			} else {
				fileNameWithOutExt = fileNameWithExt;
			}
			return fileNameWithOutExt;
		} else {
			return fileNameWithExt;
		}
	}

	/**
	 * Get number of rows
	 * 
	 * @author ThoaLT
	 * @param xlFilePath
	 *            : path to the file
	 * @param sheetName
	 *            : name of sheet
	 */
	public static int getNrData(String xlFilePath, String sheetName) {
		int nrRow = 0;
		try {
			Workbook workbook;
			workbook = Workbook.getWorkbook(new File(xlFilePath));
			Sheet sheet = workbook.getSheet(sheetName);
			nrRow = sheet.getRows();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			error("error in getNrData()");
		}
		return nrRow - 1;
	}

	/**
	 * get number of columns
	 * 
	 * @author ThoaLT
	 * @param xlFilePath
	 *            : path to the file
	 * @param sheetName
	 *            : name of sheet
	 */
	public static int getNrColumns(String xlFilePath, String sheetName) {
		int nrColumn = 0;
		try {
			Workbook workbook;
			workbook = Workbook.getWorkbook(new File(xlFilePath));
			Sheet sheet = workbook.getSheet(sheetName);
			nrColumn = sheet.getColumns();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			error("error in getNrColumns()");
		}
		return nrColumn;
	}

	/**
	 * get values of file and store to an array
	 * 
	 * @author ThoaLT
	 * @param xlFilePath
	 *            : path to the file
	 * @param sheetName
	 *            : name of sheet
	 * @author HuyNK1 Modified logging
	 */
	public static String[][] getTableArray(String xlFilePath, String sheetName, String tableName) {
		String[][] tabArray = null;
		try {
			Workbook workbook = Workbook.getWorkbook(new File(xlFilePath));
			Sheet sheet = workbook.getSheet(sheetName);
			int startRow, startCol, endRow, endCol, ci, cj;
			Cell tableStart = sheet.findCell(tableName);
			startRow = tableStart.getRow();
			startCol = tableStart.getColumn();

			Cell tableEnd = sheet.findCell(tableName, startCol + 1, startRow + 1, 100, 64000, false);

			endRow = tableEnd.getRow();
			endCol = tableEnd.getColumn();

			info("Loaded data from Excel: Row[" + startRow + ".." + endRow + "], Columns[" + startCol + ".." + endCol
					+ "]");

			tabArray = new String[endRow - startRow - 1][endCol - startCol - 1];
			ci = 0;

			for (int i = startRow + 1; i < endRow; i++, ci++) {
				cj = 0;
				for (int j = startCol + 1; j < endCol; j++, cj++) {
					tabArray[ci][cj] = sheet.getCell(j, i).getContents();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (tabArray);
	}
	
	/**
	 * get values of file and store to Map hash table 
	 *   
	 * @author QuanLV
	 * @param xlFilePath
	 *            : path to the file
	 * @param sheetName
	 *            : name of sheet
	 * @param caseName
	 *            : name of test case which need data
	 */
	public static List<HashMap<String,String>> getTestData(String xlFilePath, String sheetName, String tableName) {
		List<HashMap<String,String>> listData = new ArrayList<HashMap<String,String>>();
		try {
			Workbook workbook = Workbook.getWorkbook(new File(xlFilePath));
			Sheet sheet = workbook.getSheet(sheetName);
			int startRow, startCol, endRow, endCol;
			Cell tableStart = sheet.findCell(tableName);
			startRow = tableStart.getRow();
			startCol = tableStart.getColumn();

			Cell tableEnd = sheet.findCell(tableName, startCol + 1, startRow + 1, 100, 64000, false);

			endRow = tableEnd.getRow();
			endCol = tableEnd.getColumn();

			info("Loaded data from Excel: Row[" + startRow + ".." + endRow + "], Columns[" + startCol + ".." + endCol
					+ "]");

			
			for(int i = startRow + 1; i < endRow; i++)
			{
				HashMap<String,String> valSet = new HashMap<String,String>();
					for(int j = 1; j < endCol; j++)
					{
						valSet.put(sheet.getCell(j, startRow).getContents().trim(), sheet.getCell(j, i).getContents().trim());
					}
					listData.add(valSet);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return (listData);
	}
	
	public static String getEmailSignUp() 
	{		
		try(BufferedReader br = new BufferedReader(new FileReader(config.getProperty("EMAIL_FILE")))) {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        for(String s : sb.toString().split("-"))
	        {
	        	if(s.trim().length() > 0) return s.replace("\n", "").replace("\r", "");
	        }
	    }
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}

	public static String getAccountNameSignUp() 
	{		
		try(BufferedReader br = new BufferedReader(new FileReader(config.getProperty("ACCOUNT_FILE")))) {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        for(String s : sb.toString().split("-"))
	        {
	        	if(s.trim().length() > 0) return s.replace("\n", "").replace("\r", "");
	        }
	    }
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}

	
	public static void removeUsedAccountSignUp(String accountName)
	{
		  try {
			  	BufferedReader br = new BufferedReader(new FileReader(config.getProperty("ACCOUNT_FILE")));
		        StringBuilder sb = new StringBuilder();
		        String line;
		        
		        while ((line = br.readLine()) != null) 
		        {
		        	 sb.append(line);
			         sb.append(System.lineSeparator());		        	
		        }
		        br.close();
		        
		    	FileWriter fileWritter = new FileWriter(config.getProperty("ACCOUNT_FILE"), false);
    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    	        bufferWritter.write(sb.toString().replace(accountName, ""));
    	        bufferWritter.close();    	        
		      } 
	  	catch (Exception e) {return;}
	}
	public static void removeUsedEmailSignUp(String email)
	{
		  try {
			  	BufferedReader br = new BufferedReader(new FileReader(config.getProperty("EMAIL_FILE")));
		        StringBuilder sb = new StringBuilder();
		        String line;
		        
		        while ((line = br.readLine()) != null) 
		        {
		        	 sb.append(line);
			         sb.append(System.lineSeparator());		        	
		        }
		        br.close();
		        
		    	FileWriter fileWritter = new FileWriter(config.getProperty("EMAIL_FILE"), false);
    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    	        bufferWritter.write(sb.toString().replace(email, ""));
    	        bufferWritter.close();    	        
		      } 
	  	catch (Exception e) {return;}
	}
	/**
	 * Load default config file
	 * 
	 * @throws IOException
	 * @author HuyNK1
	 */
	public static Properties loadConfig() throws FileNotFoundException, IOException {
		FileInputStream in = new FileInputStream(LibConstants.DEFAULT_CONFIG_FILE);
		Properties configProperties = new Properties();
		configProperties.load(in);
		in.close();
		return configProperties;
	}

	/**
	 * Load config from properties file
	 * 
	 * @param fileName
	 * @return
	 * @author HuyNK1
	 */
	public static Properties loadConfig(String fileName) {
		Properties configProperties = null;

		FileInputStream in;
		try {
			in = new FileInputStream(fileName);
			configProperties = new Properties();
			configProperties.load(in);
			in.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return configProperties;
	}

	/**
	 * Add/ Save a property to config.properties
	 * 
	 * @param configFile
	 * @param key
	 * @param value
	 * @throws IOException
	 * @author HuyNK1
	 */
	public static void saveProperty(Properties configFile, String key, String value) throws IOException {
		configFile.setProperty(key, value);
		saveConfig(configFile);
	}

	/**
	 * Save to file
	 * 
	 * @param configFile
	 * @throws IOException
	 * @author HuyNK1
	 */
	public static void saveConfig(Properties configFile) throws IOException {
		FileOutputStream out = new FileOutputStream(LibConstants.DEFAULT_CONFIG_FILE);
		configFile.store(out, "---No Comment---");
		out.close();
	}

	/**
	 * get values of object in repository
	 * 
	 * @author Chienlth1
	 * @param xlFilePath
	 *            : path to the file
	 * @param sheetName
	 *            : name of sheet
	 */
	public static String[][] getTableObject(String xlFilePath, String sheetName) {
		String[][] tabArray = null;
		int ci, cj;
		try {
			Workbook workbook = Workbook.getWorkbook(new File(xlFilePath));
			Sheet sheet = workbook.getSheet(sheetName);
			tabArray = new String[sheet.getRows() - 1][3];
			ci = 0;

			for (int i = 1; i < sheet.getRows(); i++, ci++) {
				cj = 0;
				for (int j = 0; j < 3; j++, cj++) {
					tabArray[ci][cj] = sheet.getCell(j, i).getContents();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (tabArray);
	}

	/**
	 * get location of element
	 * 
	 * @author Chienlth1
	 * @param ex
	 *            : expression to get element
	 * @param type
	 *            : type of expression( id, xpath, linkText,...)
	 * @param param
	 *            : parameter in expression
	 */
	public static By getLocation(String ex, String typeEx, String param) {

		By result;
		switch (typeEx) {
		case "id":
			result = By.id(ex);
			break;
		case "xpath":
			result = By.xpath(ex);
			break;
		case "linkText":
			result = By.linkText(ex);
			break;
		case "className":
			result = By.className(ex);
			break;
		case "cssSelector":
			result = By.cssSelector(ex);
			break;
		case "name":
			result = By.name(ex);
			break;
		case "partialLinkText":
			result = By.partialLinkText(ex);
			break;
		case "tagName":
			result = By.tagName(ex);
			break;
		case "xpath_param":
			result = By.xpath(ex.replace("${param}", param));
			break;
		default:
			result = null;
		}
		return result;
	}

	public static By getXpathHasParams(String param, String repo, String page, String locate)
	{
		String[][] testParam = Utils.getTableObject(repo, page);
		int numOfRow = testParam.length;
		for (int i = 0; i < numOfRow; i++) {
			if(testParam[i][0].equals(locate))
				return Utils.getLocation(testParam[i][1],
						testParam[i][2], param);
		}
		return null;
	}
	
	/**
	 * get data from database
	 * 
	 * @author @Chienlth1
	 * @param query
	 *            : query to get data Return resultSet object
	 */
	public static ResultSet getDataFromDB(String query) {
		appConfig = Utils.loadConfig(LibConstants.DEFAULT_CONFIG_FILE);
		dbUrl = appConfig.getProperty("DATABASE_URL");
		dbUsername = appConfig.getProperty("DATABASE_USERNAME");
		dbPassword = appConfig.getProperty("DATABASE_PASSWORD");
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// Prepare connection url.
			// String url = "jdbc:oracle:thin:@10.23.191.242:1521:orcl";
			// Get connection to DB.
			try {
				Connection con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
				// Create statement object which would be used in writing DDL
				// and DML
				// SQL statement.
				Statement stmt = con.createStatement();

				ResultSet result = stmt.executeQuery(query);

				// .executeQuery("select top 1 email_address from user_register_table");
				// String emailaddress = result.getString("email_address");
				return result;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	/**
	 * Capitalize them all
	 * 
	 * @author VinhND2
	 * @param: str = THE_OBJECT_NAME
	 * @return: locTheObjectName
	 * 
	 * */
	public static String capThemAll(String str, boolean usePrefix) {
		String prefix = "loc";
		if(!usePrefix){ prefix = ""; } // prefix is empty in-case we don't use location prefix 
		String[] parts = str.split("_");
		String capString = prefix;
		for (String part : parts) {
			capString = capString + toProperCase(part);
		}
		return capString;
	}

	public static String toProperCase(String str) {
		return str.substring(0, 1).toUpperCase()
				+ str.substring(1).toLowerCase();
	}
	
}