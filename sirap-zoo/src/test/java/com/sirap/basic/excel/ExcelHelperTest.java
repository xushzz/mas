package com.sirap.basic.excel;

import java.util.List;

import org.junit.Test;

import com.sirap.basic.thirdparty.excel.ExcelHelper;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;

public class ExcelHelperTest {
	
//	@Test
	public void removeZeroes() {
		C.pl(StrUtil.removePointZeroes("5.12000"));
		C.pl(StrUtil.removePointZeroes("512000."));
		C.pl(StrUtil.removePointZeroes("5.000"));
		C.pl(StrUtil.removePointZeroes("5000"));
		C.pl(StrUtil.removePointZeroes(".12000"));
		C.pl(StrUtil.removePointZeroes("."));
		C.pl(StrUtil.removePointZeroes(".000"));
		C.pl(StrUtil.removePointZeroes("5000"));
//		C.pl(MathUtil.removeTrailingZeroes("5.12000"));
//		C.pl(MathUtil.removeTrailingZeroes("5.000"));
//		C.pl(MathUtil.removeTrailingZeroes("5000"));
	}
	
	@Test
	public void names() {
		String filepath = "E:\\KDB\\statics\\deli\\CLAYOUT.xlsx";
		filepath = "E:\\KDB\\tasks\\0526_TextSearch\\jars.xls";
		List<String> names = ExcelHelper.readSheetNames(filepath);
		C.list(names);
	}
	
	public void read() {
		String filepath = "E:\\KDB\\statics\\deli\\CLAYOUT.xlsx";
//		filepath = "E:\\KDB\\tasks\\0526_TextSearch\\jars.xls";
		int sheetIndex = 0;
		List<String> names = ExcelHelper.readSheetNames(filepath);
		List items = ExcelHelper.readSheetByIndex(filepath, sheetIndex);
	}
}
