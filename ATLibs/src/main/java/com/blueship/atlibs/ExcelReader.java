package com.blueship.atlibs;

import static com.blueship.atlibs.TestLogger.info;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

import jxl.write.Label;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Class reflex the UI Screen matrix Excel file
 * @author VinhND2
 * */
public class ExcelReader {
	String filePath;
	String tempFolderPath;
	String tempFilePath;
	Workbook workbook;
	WritableWorkbook workbookToWrite;
	String activeSheetName;
	Sheet activeSheet;
	
	public ExcelReader(String folderName, String fileName){
		String curDir = System.getProperty("user.dir");
		filePath = curDir + "\\" + folderName + "\\" + fileName;
		tempFolderPath = curDir + "\\" + folderName + "\\tmp\\";
		tempFilePath = tempFolderPath + fileName;
		
		try{
			File f = new File(tempFolderPath);
			if (!f.exists())
				f.mkdir();
			
			workbook = Workbook.getWorkbook(new File(filePath));
			workbookToWrite = Workbook.createWorkbook(new File(tempFilePath), workbook);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Sheet setActiveSheet(String sheetName){
		activeSheetName = sheetName;
		activeSheet = workbook.getSheet(sheetName);
		return activeSheet;
	}
	
	public Sheet getActiveSheet(){
		return activeSheet;
	}
	
	public List<HashMap<String,String>> getDataTable(String tableName){
		return Utils.getTestData(this.filePath, this.activeSheetName, tableName);
	}
	
	public String getDateTimeString(){
		Date date = new Date();
		Format formatter = new SimpleDateFormat("dd/MM/yyyy");
		String str = formatter.format(date);
		return str;
	}
	
	public void writeToCell(String value, int rowNum, int colNum){
		try{
			WritableSheet sheet = workbookToWrite.getSheet(activeSheetName);
			sheet.addCell(new Label(colNum, rowNum, value));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void closeWorkbook(){
		try{
			workbookToWrite.write();
			workbookToWrite.close();
			workbook.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void moveTmpToOrg(){
		try{
			Files.move((new File(tempFilePath)).toPath(), 
					   (new File(filePath)).toPath(), 
					   StandardCopyOption.REPLACE_EXISTING);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
