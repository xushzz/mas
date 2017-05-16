package com.sirap.basic.thirdparty.excel;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.sirap.basic.output.ExcelParams;

@SuppressWarnings("rawtypes")
public class ExcelHelper {
	
	public static boolean export(List<Object> list, String fullFileName, ExcelParams params)  {
		Workbook wb = new HSSFWorkbook();
		Sheet she = wb.createSheet("sheet1");
		
		int startRow = 0;
		if(params != null) {
			Row firstRow = she.createRow(startRow);
			startRow++;
			Cell firstCell = firstRow.createCell(0);
			firstCell.setCellValue(params.getTopInfo());
		}
		
		for (int i = 0; i < list.size(); i++) {
			Row row = she.createRow(startRow + i);
			
			Object record = list.get(i);
			if(record instanceof List) {
				List items = (List)record;
				for(int k = 0; k < items.size(); k++) {
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
