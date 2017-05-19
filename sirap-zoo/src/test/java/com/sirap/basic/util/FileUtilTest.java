package com.sirap.basic.util;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.tool.FileDeeper;

public class FileUtilTest {
	
	@Test
	public void attr() {
		Date bjTime = new Date(System.currentTimeMillis());
		Date time = DateUtil.add(bjTime, Calendar.HOUR_OF_DAY, -8);
		D.pl(DateUtil.TIMEZONE_JVM);
		D.pl(bjTime);
		D.pl(time);
		String filepath = "G:\\The Mafia\\National.Geographic.The.Mafia.EP03.The.Great.Betrayal.mkv";
		C.list(FileUtil.detail(filepath));
		String value = "2013-03-30T18:54:44.832249Z";
		String temp = value.replaceAll("\\.\\d+Z", "");
		C.pl(StrUtil.flatCamelCase("camelToSpaceLower"));
		C.pl(StrUtil.flatCamelCase("CamelToSpaceLower"));
	}
	
	public void parseSize() {
		C.pl(FileUtil.parseFileSize("100G"));
		C.pl(FileUtil.parseFileSize("02B"));
		C.pl(FileUtil.parseFileSize("09K"));
		C.pl(FileUtil.parseFileSize("19k"));
		C.pl(FileUtil.parseFileSize("12M"));
	}
	
//	@Test
	public void imageSize() {
		String filepath = "E:\\KDB\\tasks\\0327_QRCodeChinese\\JM.pngs";
		filepath = "E:\\KDB\\tasks\\0409_MoreVideoDetail\\Fake.png";
		int[] arr = ImageUtil.readImageWidthHeight(filepath);
		D.arr(arr);
	}
	
	//@Test
	public void format() {
		NumberFormat pretty = NumberFormat.getNumberInstance();
		pretty.setMaximumFractionDigits(2);
		pretty.setRoundingMode(RoundingMode.HALF_UP);
		
		double number = 1025.230;
		String value = pretty.format(number);
		D.sink(value);
	}
//	@Test
	public void displaySize() {
		//1025230 1,001.2K
		D.pl(FileUtil.formatFileSize(1025230));
		D.pl(FileUtil.parseFileSize("1000k"));
		D.pl(FileUtil.parseFileSize("98M"));
		long value = 1001;
		long step = 1000;
		C.pl(FileUtil.formatFileSize((long)(Math.pow(step, 10))));
		for(int i = 1110; i < 16; i++) {
			long temp = (long)(value * Math.pow(step, i));
			C.pl(temp);
			String display = FileUtil.formatFileSize(temp);
			C.pl(display);
		}
	}
	public void textSplitter() {
		String suffixesStr = ".bat;tiesstxt"; 
		String[] suffixes = suffixesStr.split("[;|,]");
		D.pl(suffixes.length);
	}
	
	public void fileSearch() {
		//List<File> files = FileUtil.scanFolder(folders, 9, suffixes, false);
		String foldersStr = "D:/Github/mas/scripts,F:/Docs/Speech";
//		foldersStr = "F:/Docs/Speech"; 
		String suffixesStr = ".bat;ties;speech\\11";
//		suffixesStr = ".bat;gen";
		List<String> folders = StrUtil.splitByRegex(foldersStr);
		String[] suffixes = suffixesStr.split(";");
		List<File> files = FileUtil.scanFolder(folders, 9, suffixes, false);
		C.list(files);
	}
	public void searchFile() {
		String location = "D:/Projects/start-kit/branches/code/1.4/auto-tools/auto-deploy/src/main";
		String fileName = "JConfig.java";
		//String filePath = FileUtil.searchFileByFilename(location, fileName);
	}
	
	public void allDisks() {
		List<String> list = FileUtil.availableDiskNames();
		C.list(list);
	}
	
	@Test(enabled=false)
	public void isFolder() {
		String path = "\\\\PIRATEWITHOUTSE\\MasPro";
	}
	
	@DataProvider
	public Object[][] pFolderAndFile() {
		Object[][] data = {{"E:\\\\\\max.pdf"}, {"E:/abc/max.pdf"}};
		
		return data;
	}
	
	@Test(enabled=false, dataProvider="pFolderAndFile")
	public void splitFolderAndFile(String filepath) {
		D.pl(filepath);
		String[] arr = FileUtil.splitFolderAndFile(filepath);
		D.pl(arr);
		D.pl();
	}
	
	@DataProvider
	public Object[][] diskP() {
		Object[][] data = {{"D:/", true},{"DD", false},{"D:D", true},{"d:\\Cargo", true}, 
				{"d:/", true}, {"f:\\Cargo", true}, {"The book", false}};
		
		return data;
	}

	@Test(enabled=false, dataProvider="diskP")
	public void disk(String input, boolean expected) {
		assertEquals(FileUtil.startWithDiskName(input), expected);
	}
	
	@DataProvider
	public Object[][] cleverP() {
		Object[][] data = {{""},{"."},{"..."},{"....."},{"C:.."},{"C:"},{"C:"},
				{"D:.."},{"D:"},{"D:"}
				};
		
		return data;
	}

	@Test(enabled=false, dataProvider="cleverP")
	public void clever(String input) {
		FileUtil.getCleverPath(input);
//		assertEquals(FileUtil.startWithDiskName(input), expected);
	}
	
	@DataProvider
	public Object[][] normalFolderP() {
		//(\\.+|/+|\\\\+)
		Object[][] data = {{"D:/", true},{"/////", true}, {"...", true}};
		
		return data;
	}

	@Test(enabled=false, dataProvider="normalFolderP")
	public void normalFolder(String input, boolean expected) {
		File file = FileUtil.getIfNormalFolder(input);
		if(file != null) {
			D.pl(file.getAbsoluteFile());
		}
		assertEquals(FileUtil.getIfNormalFolder(input) != null, expected);
	}
	
	@DataProvider
	public Object[][] maliciousP() {
		//(\\.+|/+|\\\\+)
		Object[][] data = {{".", true},{"..", true}, {"...", true},
				{"/", true},{"//", true}, {"///", true},
				{"\\", true},{"\\\\", true}, {"\\\\\\", true}, {"\\\\/", true}, {"s...\\\\/", false}
				};
		
		return data;
	}

	@Test(enabled=false, dataProvider="maliciousP")
	public void malicious(String input, boolean expected) {
		assertEquals(FileUtil.isMaliciousPath(input), expected);
	}
	
	@Test(enabled=false)
	public void legalFilename() {
		char[] carr = FileUtil.BAD_CHARS_FOR_FILENAME_WINDOWS;
		String temp = new String(carr);
		temp = temp.replace("\\", "\\\\");
		D.pl(temp);
		String result = FileUtil.generateLegalFileName("allbeingwell.[" + temp + "]what happen");
		D.pl(result);
	}
	
	public void suffix() {
		String[] suffixes = {"txt", "doc", "xls"};
		C.list(FileUtil.scanFolder("E:", 2, suffixes, false));//1446
	}
	
	public void walk() {
		D.pl("$RECYCLE.BIN".startsWith("$"));
		List<File> records = FileUtil.scanFolder("G:\\Kill Bill", 3);
		D.ls(records);
	}
	public void read() {
		String url = "E:\\original.jpg";
//		url = "E:\\A.txt";
		D.ls(IOUtil.readFileIntoList(url));
	}
}
