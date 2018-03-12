package com.blueship.atlibs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

/**
 *
 */
public class FileLogger {
	public String txtLogFileName;
	private FileWriter txtFileHandle;
	public String xslLogFileName;
	private boolean stepVerifyResult = true;
	
	public String fileName ;
	public long startTime;
	public String step = "----- ";
	public String blank = "    ";
	public String status= "    ";
	public String browser;
	
	Properties config = Utils.loadConfig(LibConstants.DEFAULT_CONFIG_FILE);
	
	public void startWriteLog(String name)
	{
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(Calendar.getInstance().getTime());
		fileName = config.getProperty("LOG_FOLDER") + browser + "_" + name + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".log.txt";
		File logFile = new File(fileName);
		
		System.out.println("Create log file name:" + fileName);
		
		try {
			startTime = Calendar.getInstance().getTimeInMillis();
			FileOutputStream fileStream = new FileOutputStream(logFile);
			OutputStreamWriter osw = new OutputStreamWriter(fileStream);
			PrintWriter w = new PrintWriter(osw);
	        w.println(step + "Case name: "+ name );
	        w.println(step + "Running at: "+ Calendar.getInstance().getTime());
	        w.println("");
	        w.println("");
	        w.println(timeStamp + blank + step + "Start test");
	        w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * write a free-style log message
	 * 
	 * */
	public void writeLogMessage(String message){
		try{
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
		    out.println(message);
		    out.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stepWriteLog(String message, boolean result)
	{
		stepWriteLog(message, result, false);
	}
	
	public void stepWriteLog(String message, boolean result, boolean isBreak)
	{
		String status = (result? "PASSED": "FAILED");
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(Calendar.getInstance().getTime());
		try{
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
		    out.println(timeStamp +  blank + status + blank + step + message);
		    step = "----- ";
		    out.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			if(!result && isBreak)
				throw new AssertionError(message + ": " + status);
		}
	}
	
	public void endWriteLog(boolean result)
	{
		System.out.println("Ending log file:" + fileName);
		
		String strResult = (result? "PASSED" : "FAILED");
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(Calendar.getInstance().getTime());
		long duration = (Calendar.getInstance().getTimeInMillis() - startTime)/1000;
		try{ 
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			out.println(timeStamp + blank + step +"Duration: "+duration+"s");
		    out.println(timeStamp + blank + step +"End test" + blank + strResult);
		    out.close();
//		    fileName = new String();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void setTextLogFileName(String fileName){
		txtLogFileName = fileName + ".txt";
	}
	
	public void setExcelLogFileName(String fileName){
		xslLogFileName = fileName + ".xsl";
	}
	
	public void startLogging(String fileName){
		setTextLogFileName(fileName);
		setExcelLogFileName(fileName);

		//create log folder if not existed
		String curDir = System.getProperty("user.dir");
		String path = curDir + "/target/log/";
		File f = new File(path);
		if (!f.exists()) f.mkdir();
		try{
			txtFileHandle = new FileWriter(path + txtLogFileName);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void writeLog(String message, boolean... opParams){
		String timestamp = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(Calendar.getInstance().getTime());
		boolean timeDisplay = (boolean) (opParams.length > 0 ? opParams[0] : true);
		try{
			if(timeDisplay)
				txtFileHandle.write(timestamp + Constants.DELIMITER + message);
			else
				txtFileHandle.write(message);
			txtFileHandle.write("\r\n");
			txtFileHandle.flush();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void writeLog(String message, String status){
		writeLog(status + Constants.DELIMITER_DASH + message);
		
		// if(status.equals(Constants.PASSED)) setVerifyResult(true);
		if(status.equals(Constants.FAILED)) setVerifyResult(false);
	}
	
	public void endLogging(){
		try{
			if(txtFileHandle != null){
				txtFileHandle.close();
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * call this function at the beginning of your verify function
	 * */
	public void startStepVerify(String message){
		stepVerifyResult = true;
		writeLog("Start verify {" + Constants.DELIMITER + message, Constants.INFO);
	}
	
	/**
	 * this function will be called when an assertion or verification failed
	 * */
	public void setVerifyResult(boolean result){
		stepVerifyResult = result;
	}
	/**
	 * call this function at the end of your verify function
	 * */
	public void endStepVerify(String message){
		String verifyResult = (stepVerifyResult == true)?Constants.PASSED:Constants.FAILED;
		writeLog("}  End verify " + Constants.DELIMITER + message, verifyResult);
//		writeLog(message, verifyResult);
	}
}