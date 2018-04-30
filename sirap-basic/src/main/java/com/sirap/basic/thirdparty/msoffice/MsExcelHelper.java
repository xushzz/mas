package com.sirap.basic.thirdparty.msoffice;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.output.ExcelParams;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("rawtypes")
public class MsExcelHelper {

	public static List<String> readSheetNames(String filepath) {
		try {
			if (FileUtil.isExcel(filepath)) {
				return readXlsSheetNames(filepath);
			} else if (FileUtil.isExcelX(filepath)) {
				return ExcelXReader.readSheetNames(filepath);
			} else {
				throw new MexException("Invalid excel file: " + filepath);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException(ex);
		}
	}

	public static List<List<String>> readFirstSheet(String filepath) {
		return readSheetByIndex(filepath, 0);
	}
	
	public static List<List<String>> readSheetByIndex(String filepath, int index) {
		try {
//			D.sink("readXlsSheetByIndex " + filepath);
//			D.sink("readXlsSheetByIndex " + FileUtil.formatSize(filepath));
			if (FileUtil.isExcel(filepath)) {
				return readXlsSheetByIndex(filepath, index);
			} else if (FileUtil.isExcelX(filepath)) {
				return ExcelXReader.readSheetByIndex(filepath, index);
			} else {
				throw new MexException("Invalid excel file: " + filepath);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException(ex);
		}
	}
	
	private static Workbook workbookOf(String filepath) throws MexException {
		try(FileInputStream fis = new FileInputStream(filepath)) {
			if (FileUtil.isExcel(filepath)) {
				return new HSSFWorkbook(fis);
			} else if (FileUtil.isExcelX(filepath)) {
				return new XSSFWorkbook(fis);
			} else {
				throw new MexException("Invalid excel file: " + filepath);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException(ex);
		}
	}
	
	public static Workbook newWorkbook(String filepath) throws MexException {
		if (FileUtil.isExcel(filepath)) {
			return new HSSFWorkbook();
		} else if (FileUtil.isExcelX(filepath)) {
			return new XSSFWorkbook();
		} else {
			throw new MexException("Invalid excel file: " + filepath);
		}
	}

	public static List<String> readXlsSheetNames(String filepath) {
		List<String> items = Lists.newArrayList();
		Workbook wb = workbookOf(filepath);
		int count = wb.getNumberOfSheets();
		for(int i = 0; i < count; i++) {
			Sheet st = wb.getSheetAt(i);
			String sheetName = st.getSheetName();
			items.add(sheetName);
		}
		
		return items;
	}
	
	public static List<List<String>> readXlsSheetByIndex(String filepath, int sheetIndex) {
		List<List<String>> data = Lists.newArrayList();
		try(FileInputStream fis = new FileInputStream(filepath)) {
			Workbook wb = WorkbookFactory.create(fis);
			
			Sheet st = wb.getSheetAt(sheetIndex);
			for (int rowIndex = 0; rowIndex <= st.getLastRowNum(); rowIndex++) {
				Row row = st.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				
				List<String> rowItems = MsExcelHelper.row2items(row);
				data.add(rowItems);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException(ex);
		}

		return data;
	}

	public static boolean export(List list, String fullFileName, ExcelParams params) {
		Workbook wb = newWorkbook(fullFileName);
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

		try(OutputStream stream = new FileOutputStream(fullFileName)) {
			wb.write(stream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return true;
	}
	
	public static List<String> row2items(Row row) {
		List<String> rowItems = Lists.newArrayList();
		
		for (int columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++) {
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
						String temp = MathUtil.toBigDecimal(cell.getNumericCellValue()).toPlainString();
						value = StrUtil.removePointZeroes(temp);
					}
				} else if(Cell.CELL_TYPE_BOOLEAN == cellType) {
					value = cell.getBooleanCellValue() + "";
				} else {
					value = Konstants.SHITED_FACE;
				}
				rowItems.add(value);
			}
		}
		
		return rowItems;
	}
}
