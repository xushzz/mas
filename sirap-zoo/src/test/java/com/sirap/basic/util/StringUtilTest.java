package com.sirap.basic.util;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;

public class StringUtilTest {

	@Test(enabled=false)
	public void locael() {
		Locale l = Locale.CHINA;
		D.pl(l, l.getLanguage(), l.getCountry());
	}
	
	public void parse() {
		C.pl(MathUtil.toInteger("2"));
		C.pl(MathUtil.toInteger("+2"));
		C.pl(MathUtil.toInteger("-2"));
//		M.pl(StrUtil.parseParam("\\d", null));
	}

	public void split() {
		String obama = "al/ex[/]nick\\a moment[] when the outcom[]e of our revolution w[]as most i[]n doubt";
		D.ls(StrUtil.split(obama, "/"));
	}
	
	@DataProvider(name="occupy")
	public Object[][] providerOccupy() {
		return new Object[][]{
				{"/CONFIG{0}.prop{1}er{1}ties", new String[]{"Apple", "A"}}, 
				{"<{0}>{1}</{0}>", new String[]{"Visibility", "greater than 7 mile(s):0"}}
		};
	}
	
	@Test(enabled=true, dataProvider="occupy")
	public void holders(String source, String[] params) {
		C.pl(StrUtil.occupy(source, params));
	}
	public void replace() {
//		String a = "character to be escaped is missing";
		String src = "E:/Klose/uefa";
		C.pl(src.replace("/", File.separator));
	}
	
	public void fileName() {
	}
	
	public void parseInt() {
		C.pl(MathUtil.toInteger("+10"));
		C.pl(MathUtil.toInteger("10"));
		C.pl(MathUtil.toInteger("0000000091"));
		C.pl(MathUtil.toInteger("1-00000091"));
		C.pl(MathUtil.toInteger("2-01"));
	}
	
	public void md5() {
//		M.pl(PanaceaBox.md5("1234"));
	}

	@Test(enabled=false, dataProvider="james")
	public void contains(String country, String code, int minLen) {
		C.pl(StrUtil.contains(country, code, minLen));
	}
	
	@DataProvider(name="james")
	public Object[][] provideContains() {
		return new Object[][]{
				{"portugal", "or", 3},
				{"portugal", "ors", 3},
				{"portugal", "Por", 3},
				{"portugal", null, 3},
				{null, null, -1},
		};
	}
	
	@Test
	public void pseudo() {
		C.pl(StrUtil.pseudoEncrypt("csa"));
		C.pl(StrUtil.pseudoPartlyEncrypt("csa"));
	}
	public void isEmail() {
		String src = "kevin.yu@moodys.com";
		C.pl(StrUtil.isEmail(src));
	}
	public void regex() {
		String source = "D:/workspace.luna/common/src/main/java/com/pirate/common/extractor/util/ExtractorUtil.java";
		String regex = "\\d";
	}
	
	public void newLine() {
		String str2="abcd\nabcd";
		System.out.println(str2);
		String str1=str2.replaceAll("\n", "&&");
		System.out.println(str1);
		String src = "afwefwe\nfwefwf\nfwfe\nberer";
		C.pl(StrUtil.split(src, "\n"));
	}
	
	public void code() {
		C.pl((int)'–');
		C.pl((int)'—');
		C.pl((int)'-');
	}

	@DataProvider
    public Object[][] pReplace(){
        Object[][] result = {{"abcd.txt"},{"a.txtbdcd.txt"},{"abcd.mp3"}};
        return result;
    }
	
	@Test(enabled=false, dataProvider="pReplace")
	public void replace(String input) {
		String fileName = input.replaceAll(".txt$", "");
		D.pl(fileName);
	}

	@DataProvider
    public Object[][] pReplace2(){
        Object[][] result = {{"[22]"},{"a.txtbdcd.txt"},{"abcd.mp3"}};
        return result;
    }
	
	@Test(enabled=false, dataProvider="pReplace2")
	public void replace2(String input) {
		String fileName = input.replaceAll("\\[\\d{1,2}\\]", "");
		D.pl(fileName);
	}
	
	@Test(enabled=false)
	public void care() {
		String source = "3 / 7 x 49/9 - 4/3 + 78 - 89*23";
		String regex = "([\\+|\\-|\\*|X|x|/])";
		String[] items = source.split(regex);
		D.pl(items);
	}
	
	@DataProvider
    public Object[][] path(){
        Object[][] result = {{"\\", true},{"\\\\", true},{"/", true}, {"//", true}};
        return result;
    }
	
	@Test(enabled=false, dataProvider="path")
	public void path(String key, boolean result) {
		String[] targetArr = "\\,\\\\,/,//".split(",");
		assertEquals(StrUtil.exists(targetArr, key, true), result);
	}
	
	@SuppressWarnings("rawtypes")
	public static List empList = Collections.EMPTY_LIST;
	
	@DataProvider
    public Object[][] isDigitsOnlyP1(){
        Object[][] result = {{"23"},{"1"},{"2323"}};
        return result;
    }
	@DataProvider
    public Object[][] isDigitsOnlyP2(){
        Object[][] result = {{null},{""},{" "},{" 23"},{"s"},{"fsfd"}};
        return result;
    }

	@Test(enabled=false, dataProvider="isDigitsOnlyP1")
	public void isDigitsOnly1(String param) {
		assertEquals(StrUtil.isDigitsOnly(param), true);
	}
	
	@Test(enabled=false, dataProvider="isDigitsOnlyP2")
	public void isDigitsOnly2(String param) {
		assertEquals(StrUtil.isDigitsOnly(param), false);
	}
	
	@DataProvider
    public Object[][] containsAplhaOrDigitP1(){
        Object[][] result = {{"as"},{"1 12d"},{"!s@##"},{"!d!@#"},{"M<3M<+"}};
        return result;
    }
    
	@DataProvider
    public Object[][] containsNoAplhaOrDigitP2(){
        Object[][] result = {{null},{""},{" "},{"!!@#"}};
        return result;
    }

	@Test(enabled=false, dataProvider="containsAplhaOrDigitP1")
	public void containsNoAplhaOrDigit1(String param) {
		assertEquals(StrUtil.containsNoneOfAplhanumeric(param), false);
	}
	
	@Test(enabled=false, dataProvider="containsNoAplhaOrDigitP2")
	public void containsNoAplhaOrDigit2(String param) {
		assertEquals(StrUtil.containsNoneOfAplhanumeric(param), true);
	}
	
	@DataProvider
    public Object[][] containsDigitP1(){
        Object[][] result = {{"as2s"},{"1 12d"},{"!s@2##"},{"!2d1!@#"},{"M<3M<+"}};
        return result;
    }
    
	@DataProvider
    public Object[][] containsNoDigitP2(){
        Object[][] result = {{null},{""},{" "},{"!!@#"}};
        return result;
    }

	@Test(enabled=false, dataProvider="containsDigitP1")
	public void containsDigit1(String param) {
		assertEquals(StrUtil.containsDigit(param), true);
	}
	
	@Test(enabled=false, dataProvider="containsNoDigitP2")
	public void containsDigit2(String param) {
		assertEquals(StrUtil.containsDigit(param), false);
	}

	@Test(enabled=false, dataProvider="containsDigitP1")
	public void containsNoDigit1(String param) {
		assertEquals(StrUtil.containsNoneOfDigit(param), false);
	}
	
	@Test(enabled=false, dataProvider="containsNoDigitP2")
	public void containsNoDigit2(String param) {
		assertEquals(StrUtil.containsNoneOfDigit(param), true);
	}
	
	@DataProvider
    public Object[][] extractFirstIntegerP1(){
        Object[][] result = {{null, null},{"", null},{" ", null},{"!!@#", null},
        		{"n02io", 2},{"0010", 10},{"n23fe", 23},{"9f2w3e", 9}};
        return result;
    }
	
	@Test(enabled=false, dataProvider="extractFirstIntegerP1")
	public void extractFirstInteger(String param, Integer result) {
		assertEquals(StrUtil.extractFirstInteger(param), result);
	}
	
	@DataProvider
    public Object[][] extractIntegersP1(){
        Object[][] result = {{null, empList},{"", empList},{" ", empList},{"!!@#", empList},
        		{"m123sa", gen(123)},{"n1112io", gen(1112)},{"03010", gen(3010)},
        		{"n23fe", gen(23)},{"9f2w3e", gen(9,2,3)}};
        return result;
    }
	
	private <E extends Object> List<E> gen(E... arr) {
		List<E> list = new ArrayList<E>();
		for(int i = 0; i < arr.length; i++) {
			list.add(arr[i]);
		}
		
		return list;
	}
	
	@Test(enabled=false, dataProvider="extractIntegersP1")
	public void extractIntegers(String param, List<Integer> list) {
		assertEquals(StrUtil.extractIntegers(param), list);
	}

	@DataProvider
    public Object[][] parseIntegerP1(){
        Object[][] result = {{"0123", 123},{"000000", 0},{"0000001", 1},{"3123121", 3123121}};
        return result;
    }
	@DataProvider
    public Object[][] parseIntegerP2(){
		Integer max = Integer.MAX_VALUE;
		Integer min = Integer.MIN_VALUE;
		long g1 = (long)max + 2;
		long g2 = (long)min - 2;
		D.pl(max, g1, min, g2);
        Object[][] result = {{null},{""},{" "},{"!!@#"},
        		{g1 + ""},{max + "0"},{g2 + ""},{min + "0"}};
        return result;
    }
	
	@Test(enabled=false, dataProvider="parseIntegerP1")
	public void parseInt(String param, int result) {
//		assertEquals(StrUtil.parseInteger(param), result);
		assertEquals(Integer.parseInt(param), result);
	}

	@Test(enabled=false, expectedExceptions={NumberFormatException.class}, dataProvider="parseIntegerP2")
	public void toInteger1(String param) {
		Integer.parseInt(param);
	}

	@Test(enabled=false, dataProvider="parseIntegerP2")
	public void toInteger2(String param) {
		assertEquals(MathUtil.toInteger(param), null);
	}

	@DataProvider
    public Object[][] splitToList(){
        Object[][] result = {{null, empList},{"\\", gen("\\")},{"	", empList},{"\\", gen("\\")},{"!!@#", gen("!!@#")},
        		{"1,2",gen("1","2")},{"0,1,,2", gen("0","1","","2")},{"12d,d3f,wf3,21,32",gen("12d","d3f","wf3","21","32")}};
        return result;
    }

	@Test(enabled=false, dataProvider="splitToList")
	public void splitToList(String param, List<String> list) {
		assertEquals(StrUtil.split(param), list);
	}

	@DataProvider
    public Object[][] contains(){
        Object[][] result = {{gen("FOR")},{gen("cli","fore", "japan")}};
        return result;
    }

	@Test(enabled=false, dataProvider="contains")
	public void containsIgnoreCase(List<String> keyWords) {
		assertEquals(StrUtil.containsIgnoreCase("Eclipse forever.", keyWords), true);
	}

	@Test(enabled=false, dataProvider="contains")
	public void containsIgnoreCase2(List<String> keyWords) {
		assertEquals(StrUtil.containsIgnoreCase(null, keyWords), false);
		assertEquals(StrUtil.containsIgnoreCase(" ", keyWords), false);
		assertEquals(StrUtil.containsIgnoreCase("  	", keyWords), false);
		assertEquals(StrUtil.containsIgnoreCase("zzzzzzzzzzz", keyWords), false);
	}

	@Test(enabled=false, dataProvider="contains")
	public void containsIgnoreCase3(List<String> keyWords) {
		assertEquals(StrUtil.contains(null, keyWords), false);
		assertEquals(StrUtil.containsIgnoreCase(" ", keyWords), false);
		assertEquals(StrUtil.containsIgnoreCase("  	", keyWords), false);
		assertEquals(StrUtil.containsIgnoreCase("zzzzzzzzzzz", keyWords), false);
	}

}
