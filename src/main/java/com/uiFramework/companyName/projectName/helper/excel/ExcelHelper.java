package com.uiFramework.companyName.projectName.helper.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.uiFramework.companyName.projectName.helper.logger.LoggerHelper;
import com.uiFramework.companyName.projectName.helper.resource.ResourceHelper;

public class ExcelHelper {

	private Logger log = LoggerHelper.getLogger(ExcelHelper.class);

	public Object[][] getExcelData(String excelLocation, String sheetName) {
		try {

			Object dataSets[][] = null;
			FileInputStream file = new FileInputStream(new File(excelLocation));

			// Create workbook instance
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get sheet name from Workbook
			XSSFSheet sheet = workbook.getSheet(sheetName);

			// count number of active rows in excel sheet
			int totalRow = sheet.getLastRowNum();

			// count active columns in row
			int totalColumn = sheet.getRow(0).getLastCellNum();

			dataSets = new Object[totalRow][totalColumn-1];

			// Iterate through each row one by one.

			Iterator<Row> rowIterator = sheet.iterator();
			int i = 0;
			while (rowIterator.hasNext()) {
				i++;
				// For every row we need to iterate over columns
				Row row = rowIterator.next();

				Iterator<Cell> cellIterator = row.cellIterator();
				int j = 0;
				while (cellIterator.hasNext()) {
				//	j++;
					Cell cell = cellIterator.next();
                    if(cell.getStringCellValue().contains("Start"))
                    {	i=0;
                    	break;
                    }	   	
					switch (cell.getCellTypeEnum()) {
					case STRING:
						dataSets[i - 1][j++] = cell.getStringCellValue();
						break;
					case NUMERIC:
						dataSets[i - 1][j++] = cell.getNumericCellValue();
						break;
					case BOOLEAN:
						dataSets[i - 1][j++] = cell.getStringCellValue();
						break;
					case FORMULA:
						dataSets[i - 1][j++] = cell.getCellFormula();
						break;
					default:
						System.out.println("no matching enum data type found");
						break;
					}
				}
			}
			return dataSets;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	public void updateResult(String excelLocation, String sheetName, String testCaseName, String testStatus) {
		try {

			FileInputStream file = new FileInputStream(new File(excelLocation));
			// Create workbook instance
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			// Get sheet name from Workbook
			XSSFSheet sheet = workbook.getSheet(sheetName);
			// count number of active rows in excel sheet
			int totalRow = sheet.getLastRowNum() + 1;
			for (int i = 1; i < totalRow; i++) {
				XSSFRow r = sheet.getRow(i);
				String ce = r.getCell(0).getStringCellValue();
				if (ce.contains(testCaseName)) {
					r.createCell(1).setCellValue(testStatus);
					file.close();
					log.info("result updated..");
					FileOutputStream out = new FileOutputStream(new File(excelLocation));
					workbook.write(out);
					out.close();
					break;
				}
			}

		} catch (Exception e) {

		}

	}

	public static void main(String args[]) {
		ExcelHelper excelHelper = new ExcelHelper();
		String excelLocation = ResourceHelper.getResourcePath("/src/main/resources/configfile/testData.xlsx");
		// Object[][] data = excelHelper.getExcelData(excelLocation, "Login");
		// System.out.println(data);
		excelHelper.updateResult(excelLocation, "TestScripts", "Login", "PASS");
		excelHelper.updateResult(excelLocation, "TestScripts", "Registration", "FAIL");
		excelHelper.updateResult(excelLocation, "TestScripts", "Add to Cart", "PASS");
	}

}