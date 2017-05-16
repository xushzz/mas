package com.sirap.common.test;

import java.io.File;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.FileUtil;
import com.sirap.common.framework.command.target.TargetAnalyzer;


public class TargetAnalyzerTest extends TargetAnalyzer {

	@Override
	public String getDefaultExportFolder() {
		return "D:\\Books";
	}

	@Override
	public String parseRealFolderPath(String param) {
		File file = FileUtil.getIfNormalFolder(param);
		if(file != null) {
			return file.getPath();
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		TargetAnalyzerTest wang = new TargetAnalyzerTest();
		//wang.c1();
		//C.pl();
		//wang.c2();
		wang.c2PDFWithRegularPath();
	}
	
	public void c2() {
		C.pl(parse("ABC", ".pdf"));
		C.pl(parse("ABC", "E:,.PDF"));
		C.pl(parse("ABC", "E:,max.pdf"));
		C.pl(parse("ABC", "max.pdf"));
	}
	
	public void c2PDFWithRegularPath() {
		//C.pl(parse("ABC", ".pdf"));
		C.pl(parse("ABC", "d:\\.PDF"));
		C.pl(parse("ABC", "d:\\max.pdf"));
		C.pl(parse("ABC", "d:\\mas-3.2\\max.pdf"));
		//C.pl(parse("ABC", "max.pdf"));
	}
	
	public void c1() {
		C.pl(parse("ABC", "$"));
		C.pl(parse("ABC", "$="));
		C.pl(parse("ABC", "$E:"));
		C.pl(parse("ABC", "$E:="));
		C.pl(parse("ABC", "$max="));
		C.pl(parse("ABC", "$max"));
		C.pl(parse("ABC", "."));
		C.pl(parse("ABC", "E:"));
		C.pl(parse("ABC", "E:,max"));
		C.pl(parse("ABC", "E:,max.txt"));
		C.pl(parse("ABC", "fuck,max"));
		C.pl(parse("ABC", "max"));
		C.pl(parse("ABC", "*"));
		C.pl(parse("ABC", "*E:"));
		C.pl(parse("ABC", "*E:,ma.txtx"));
		C.pl(parse("ABC", "*E:,max.txt"));
		C.pl(parse("ABC", "*fuck,max"));
		C.pl(parse("ABC", "*max"));
		C.pl(parse("ABC", ".pdf"));
		C.pl(parse("ABC", "E:,.PDF"));
		C.pl(parse("ABC", "E:,max.pdf"));
		C.pl(parse("ABC", "max.pdf"));
	}
}