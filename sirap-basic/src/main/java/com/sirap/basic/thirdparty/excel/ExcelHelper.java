package com.sirap.basic.thirdparty.excel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.output.ExcelParams;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("all")
public class ExcelHelper {

	public static final String TYPE_NORMAL = ".xls";
	public static final String TYPE_X = ".xlsx";

	public static List<String> readSheetNames(String filepath) {
		List<String> items = new ArrayList<>();
		try {
			Workbook wb = null;
			if (StrUtil.endsWith(filepath, TYPE_NORMAL)) {
				wb = new HSSFWorkbook(new FileInputStream(filepath));
			} else if (StrUtil.endsWith(filepath, TYPE_X)) {
				wb = new XSSFWorkbook(filepath);
			} else {
				throw new MexException("Invalid excel file: " + filepath);
			}
			
			int count = wb.getNumberOfSheets();
			for(int i = 0; i < count; i++) {
				Sheet st = wb.getSheetAt(i);
				String sheetName = st.getSheetName();
				items.add(sheetName);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException(ex);
		}
		
		return items;
	}
	
	public static List<List<Object>> readSheetByIndex(String filepath, int sheetIndex) {
		List<List<Object>> data = new ArrayList<>();
		try {
			Workbook wb = null;
			if (StrUtil.endsWith(filepath, TYPE_NORMAL)) {
				wb = new HSSFWorkbook(new FileInputStream(filepath));
			} else if (StrUtil.endsWith(filepath, TYPE_X)) {
				wb = new XSSFWorkbook(filepath);
			} else {
				throw new MexException("Invalid excel file: " + filepath);
			}
			
			Sheet st = wb.getSheetAt(sheetIndex);
			for (int rowIndex = 0; rowIndex <= st.getLastRowNum(); rowIndex++) {
				Row row = st.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				
				List<Object> rowItems = new ArrayList<>();
				for (short columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
					Cell cell = row.getCell(columnIndex);
					String value = null;

					if(cell != null) {
						int cellType = cell.getCellType();
						if(Cell.CELL_TYPE_STRING == cellType) {
							value = cell.getStringCellValue();
						} else if(Cell.CELL_TYPE_NUMERIC == cellType) {
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								if(date != null) {
									value = DateUtil.displayDate(date);
								} else {
									value = "null-date";
								}
							} else {
								String temp = cell.getNumericCellValue() + "";
								value = StrUtil.removePointZeroes(temp);
							}
						} else if(Cell.CELL_TYPE_BOOLEAN == cellType) {
							value = cell.getBooleanCellValue() + "";
						} else {
							value = "Ninja" + cell.getCellType();
						}
					}
					rowItems.add(value);
				}
				
				data.add(rowItems);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException(ex);
		}

		return data;
	}

	public static boolean export(List<Object> list, String fullFileName,
			ExcelParams params) {
		Workbook wb = new HSSFWorkbook();
		Sheet she = wb.createSheet("sheet1");

		int startRow = 0;
		if (params != null) {
			Row firstRow = she.createRow(startRow);
			startRow++;
			Cell firstCell = firstRow.createCell(0);
			firstCell.setCellValue(params.getTopInfo());
		}

		for (int i = 0; i < list.size(); i++) {
			Row row = she.createRow(startRow + i);

			Object record = list.get(i);
			if (record instanceof List) {
				List items = (List) record;
				for (int k = 0; k < items.size(); k++) {
					Object item = items.get(k);
					String stuff = item + "";
					Cell cell = row.createCell(k);
					cell.setCellValue(stuff);
				}
			} else {
				String stuff = record + "";
				Cell cell = row.createCell(0);
				cell.setCellValue(stuff);
			}
		}

		try {
			OutputStream stream = new FileOutputStream(fullFileName);
			wb.write(stream);
			stream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return true;
	}
}
