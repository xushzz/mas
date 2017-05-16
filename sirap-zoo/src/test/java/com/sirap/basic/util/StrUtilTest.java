package com.sirap.basic.util;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.junit.Test;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;

public class StrUtilTest {
	
	@Test
	public void decode() {
		String va = "--**1CC6CC87C4B7CB92E86017F91EE62332**,**A96D3FB916661Ad18093F09516D8A7DF4**-ninja";
		String pc = "Obamacare";
		va = "**F4509E64F8848856BE51225B7C6162307051132094E9E3FE14ABC5642DB7BB81**";
//		va = "**4EAF2A8F9B8BB088D50E6EC4B2EB580s0**";
		va = "**0DB0D87068B72AEA8BB39B4_EF523EE79**";
		String value = TrumpUtil.decodeMixedTextBySIRAP(va, pc);
		C.pl(value);
	}
	
	public void occupy() {
		String source = " cap s{0}av${0}se ture ${sa()ve} screen and ${save} as JPG file, other formats are ${image.formats}";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("save", 12121);
		params.put("0", "_JACK_");
		params.put("image.formats", Konstants.IMG_FORMATS);
		String va = StrUtil.occupy(source, params);
		C.pl(va);
	}
	
	public void parseCommandRecord() {
		String regex = "((\\d{1,3}),|)(.+?)(,(.*)|)";
		String va = "com.sirap.db.CommandSql, outsider of, the case";
		//va = "1,com.sirap.db.CommandSql, outsider";
		//va = "1,com.sirap.db.CommandSql"; //[1,, 1, com.sirap.db.CommandSql, , outsider, outsider]
		va = "com.sirap.db.CommandSql"; //[, null, com.sirap.db.CommandSql, , null]
		va = "com.sirap.extractor.CommandExtractor";
		String[] params = StrUtil.parseParams(regex, va);
		C.pl("#" + va);
		C.list(Arrays.asList(params));
		//D.pl(params.length);
	}
	public void parseHelp() {
		String regex = "[?|'|h](.*?)";
		String singleParam = StrUtil.parseParam(regex, "?abc");
		D.pl(StrUtil.parseParam(regex, "?abc"));
		D.pl(StrUtil.parseParam(regex, "dhabc"));
		D.pl(StrUtil.parseParam(regex, "'abc"));
	}
	public void split2() {
		int nTimes = 1000;
		double value = 1 - Math.pow(1-1/36.0, nTimes);
		for(int i = 1; i <= nTimes; i++) {
			 D.pl(i, 1 - Math.pow(1-1/36.0, i));
		}
		D.ts(value);
		String source = "nich olas  cage";
		source = null;
		source = "";
		source = StrUtil.DIGITS_LETTERS;
		source = ",,,,A,B,C";
		source = ",,,,";
		String regex = "taylor";
		regex = " ";
		regex = "";
		regex = ",";
//		regex = "nich";
//		regex = source;
//		regex = null;
		D.sink();
		List<String> items = StrUtil.splitByRegex(source, regex);
		C.pl(items);
		D.sink();
		List<String> items2 = StrUtil.split(source, regex, false);
		C.pl(items2);
		D.sink();
		String[] arr = source.split(regex);
		D.pl(arr);
		
		D.ts();
	}
	
	//@Test
	public void connect() {
		List<String> list = StrUtil.split("IDCardUtil.checkCodeChina(\"45272319940110083X\")", "\"");
		list = StrUtil.split("a,bc,def,ghij,klmno,pqrstu,vwx,yz");
		D.pl(File.separator, File.pathSeparator, File.pathSeparatorChar, File.separatorChar);
		C.list(list);
		C.pl();
		C.pl(StrUtil.connect(list));
		C.pl();
		C.pl(StrUtil.connect(list, File.separator));
		C.pl();
		C.pl(StrUtil.connect(list, null));
		C.pl();
		C.pl(StrUtil.connectWithComma(list));
		C.pl();
		C.pl(StrUtil.connectWithLineSeparator(list));
	}
	
	//@Test
	public void checkCode() {
		C.pl('1' - '0');
//		C.pl(StrUtil.checkCode("45272319940110083a"));
		C.pl(IDCardUtil.checkCodeChina("45272319940110083X"));
		C.pl(IDCardUtil.checkCodeChina("132302198908270037"));
		C.pl(IDCardUtil.checkCodeChina("45272319880529083"));
	}
	
	//@Test
	public void splitByNewline() {
		String source = "alex\njames\nphil\n";
		D.ts();
		source = "\n \n \n \na \n   \n  ";
		C.pl(StrUtil.split(source, '\n'));
		C.pl(StrUtil.split(source, '\n'));
		C.pl(StrUtil.split(source, "\n", false));
		C.pl(StrUtil.splitByRegex(source, "\n").size());
		D.ts();
	}
	//@Test
	public void splitAsIs() {
		C.pl(StrUtil.split("1,2,9,"));
		C.pl(StrUtil.split(""));
		C.pl(StrUtil.split(" "));
//		C.pl(StrUtil.split(null));
		C.pl(StrUtil.split("abc"));
		C.pl(StrUtil.split(",a , b, c "));
		C.pl(StrUtil.split(",,,"));
	}
	//@Test
	public void splitMore() {
		StringTokenizer brody = new StringTokenizer("1,223228,9,", ",");
		C.pl(brody.countTokens());
		brody = new StringTokenizer(",223228,9,3", ",");
		C.pl(brody.countTokens());
		C.list(StrUtil.split("cn=ABCND,cn=JACKDAWSON,cn=LinkingInJava", "cn=", true));
		C.pl(StrUtil.splitByRegex("1,223228,9,").size());
		C.pl(StrUtil.splitByRegex(",223228,9,3").size());
	}	
	
	public void startWith() {
		String KEYS_QUERY = "select;show;desc;describe";
		String[] keyArr = KEYS_QUERY.split(";");
		String sql = null;
//		String sql = "SELECT * from information_schema.TABLES";
		sql = "select * from information_schema.TABLES";
		boolean flag = StrUtil.startsWith(sql, keyArr);
		C.pl(flag);
	}
	
	//@Test
	public void contains() {
		String keywords = "url|driver|user,name|passw,ord";
		List<String> list2 = StrUtil.splitByRegex(keywords, "\\||,");
		C.list(list2);
		List<String> list = StrUtil.split(keywords, '|');
		String regex = "(.*?)\\.(" + keywords + ")";
		C.pl(list);
		C.pl(regex);
		
		String key = "mysqlB.url";
		key = "db.mysqlB.url";
//		key = "mysqlA.driver";
//		key = "mysqlAsdriver";//(.*?)\\.(url|driver)
		String dbName = StrUtil.parseParam(regex, key);
		C.pl(dbName);
		
		String source = "mysq?lA.url";
		C.pl(source.contains("aurl"));
		C.pl(source.contains("url"));
		C.pl(source.contains("."));
		C.pl(source.contains("?"));
		C.pl(source.contains("a"));
	}
	public void startWith2() {
		String source = "update tableA set name = ame";
		source = "insert tableA set name = ame";
		String keys = "update,insert,delete";
		boolean flag = StrUtil.startsWith(source, keys.split(","));
		C.pl(flag);
	}
	
	//@DataProvider(name="ipSource")
	public Object[][] isSourceFunc() {
		return new Object[][]{
				{"yu12zh34end5", false},{"10.118.10.25", true},{"259.12.-78.123", false},
		};
	}
	
	//@Test(enabled=true, dataProvider="ipSource")
	public void isLegalIP(String source, boolean expected) {
		boolean result = StrUtil.isLegalIP(source);
		assertEquals(result, expected);
	}

	
	public void split() {
		D.ts();
		String[] arr = "abc d".split(NetworkUtil.BAD_CHARS_FOR_HOSTNAME);
		D.pl(arr);
	}
//	@DataProvider(name="find1")
//	public Object[][] providerOccupy() {
//		return new Object[][]{
//				{"yu12zh34end5", "\\d+", new String[]{"12", "34", "5"}}
//		};
//	}
//	
//	@Test(enabled=true, dataProvider="occupy")
//	public void findAllMatchedItems(String source, String regex, String[] expected) {
//		List<String> results = StrUtil.findAllMatchedItems(regex, source);
//		assertEquals(results, Arrays.asList(expected));
//	}
	
	public void digitsOnly() {
//		findAllMatchedItems();
		isDigitsOnly1();
		isDigitsOnly2();
	}

	public static void findAllMatchedItems() {
		String source = "yu12zh34end5";
		String regex = "\\d+";
		List<String> results = StrUtil.findAllMatchedItems(regex, source);
		D.list(results);
		source = "yu12zh34end5";
		regex = "[a-zA-Z]+";
		regex = "za";
		results = StrUtil.findAllMatchedItems(regex, source);
		D.list(results);
	}

	public static void isDigitsOnly1() {
		assertEquals(StrUtil.isDigitsOnly("23"), true);
		assertEquals(StrUtil.isDigitsOnly("1"), true);
		assertEquals(StrUtil.isDigitsOnly("3445"), true);
	}
	
	public static void isDigitsOnly2() {
		assertEquals(StrUtil.isDigitsOnly(null), false);
		assertEquals(StrUtil.isDigitsOnly(""), false);
		assertEquals(StrUtil.isDigitsOnly(" "), false);
		assertEquals(StrUtil.isDigitsOnly(" 23"), false);
		assertEquals(StrUtil.isDigitsOnly("s"), false);
		assertEquals(StrUtil.isDigitsOnly("dd"), false);
	}

}
