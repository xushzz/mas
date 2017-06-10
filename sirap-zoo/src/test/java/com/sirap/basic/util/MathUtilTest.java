package com.sirap.basic.util;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.MathUtil;

public class MathUtilTest {
	
	@Test
	public void regex() {
		//32767
		StringBuffer regex = new StringBuffer();
		regex.append("0");
		regex.append("|").append("[1-9]\\d{0,3}");
		regex.append("|").append("[1-2]\\d{4}");
		regex.append("|").append("3[0-1]\\d{3}");
		regex.append("|").append("32[0-6]\\d{2}");
		regex.append("|").append("327[0-5]\\d");
		regex.append("|").append("3276[0-7]");
		//String shortReg = "0|[1-9]\\d{0,3}|3276[0-7]|327[0-5]\\d|32[0-6]\\d{2}|3[0-1]\\d{3}|[1-2]\\d{4}";
		String shortReg = regex.toString();
		for(int k = 0;k <= Short.MAX_VALUE + 0; k++) {
			int i = k;
			boolean flag = StrUtil.isRegexMatched(shortReg, i + "");
			if(!flag) {
				D.sink(i);
				break;
			}
		}

		C.pl(StrUtil.isRegexMatched(shortReg, "0"));
		C.pl(StrUtil.isRegexMatched(shortReg, "00"));
	}
	public void toBD() {
		C.pl(MathUtil.toBigDecimal("00001.120051"));	
	}
	
	public void permu1() {
		C.list(MathUtil.permutation("ABCD", 11));
	}
	
	@DataProvider
    public Object[][] zeroes(){
        Object[][] result = {{"",""},{".0", "0"}, {"10.0", "10"}, {"10.140", "10.14"},
        		 {"1.290", "1.29"}, {"19.0000", "19"}, {"190000", "190000"}
        };
        
        return result;
    }

	@Test(enabled=false, dataProvider="zeroes")
	public void removeZeros(String input, String expected) {
		StrUtil.removePointZeroes("");
		assertEquals(StrUtil.removePointZeroes(input), expected);
	}

	@Test(enabled=false, dataProvider="zeroes")
	public void removeZeros2(String input, String expected) {
//		assertEquals(MathUtil.removeExtraZeroesInDecimal(input), expected);
	}
	
	@DataProvider
    public Object[][] numbers(){
        Object[][] result = {{"10098.189","10,098.19"},{"5.479988", "5.48"}, {"100.234", "100.23"}, {"ewe", "ewe"}};
        return result;
    }
	
	@Test(enabled=false, dataProvider="numbers")
	public void setScale(String input, String expected) {
		assertEquals(MathUtil.formatNumber(input, 2), expected);
	}
	
	@Test(enabled=false)
	public void exchange() {
		String dec = "1212";
		int i = 255;
		String hex = "FF";
		D.pl(Integer.parseInt(hex, 2));
		D.pl(Integer.parseInt(hex, 8));
		D.pl(Integer.parseInt(hex, 10));
		D.pl(Integer.parseInt(hex, 16));
		D.pl(Integer.toBinaryString(i));
		D.pl(Integer.toOctalString(i));
		D.pl(Integer.toHexString(i));
	}
	
	@Test(enabled=false)
	public void permutationLimit() {
		String source = "ABCDEFGHIJL";
//		String source = "1234567890A"; 
		int count = 7;
		long t1 = System.currentTimeMillis();
		assertEquals(MathUtil.permutation(source, count).size() == 0, true);
		long t2 = System.currentTimeMillis();
		D.sink(t2 -t1);
		long t3 = System.currentTimeMillis();
		D.sink(t3 -t2);
	}

	@DataProvider
    public Object[][] permutationP(){
//        Object[][] result = {{"abc", 2},{"abcd", 2},{"abcde", 3},{"", 0}};
		Object[][] result = {{11,8,"6652800"},{1,1,"1"},{5,3,"60"},{3,2,"6"}};
        return result;
    }
	
	@Test(enabled=false, dataProvider="permutationP")
	public void permutation(int numberOfSamples, int targetSize, String result) {
		assertEquals(MathUtil.permutation(numberOfSamples, targetSize), result);
	}
	@DataProvider
    public Object[][] factorialP(){
//        Object[][] result = {{"abc", 2},{"abcd", 2},{"abcde", 3},{"", 0}};
		Object[][] result = {{null, 0}, {null, -1}, {"120", 5}, {"3628800", 10}
		,{"39916800",11}};
        return result;
    }
	
	@Test(enabled=false, dataProvider="factorialP")
	public void permutation(String result, int count) {
		assertEquals(MathUtil.permutation(count), result);
	}
	
	@DataProvider
    public Object[][] arrangementP() {
        Object[][] result = {{"", 0},{"as", 0}};
//		Object[][] result = {{"abc", -2}};
        return result;
    }
	
	@Test(enabled=false, dataProvider="arrangementP")
	public void arrangement(String source, int count) {
		assertEquals(MathUtil.permutation(source, count), Collections.EMPTY_LIST);
	}
	
	@Test(enabled=false)
	public void test() {
		List list = new ArrayList();
		list.add("A");
		list.add("B");
		List list2 = new ArrayList();
		list2.addAll(list);
		D.pl(list);
		D.pl(list2);
		list.clear();
		D.pl(list);
		D.pl(list2);
	}
	
	@DataProvider
    public Object[][] isHeavierP(){
        Object[][] result = {{"x"},{"X"},{"*"},{"/"}};
        return result;
    }
	
	@Test(enabled=false,dataProvider="isHeavierP")
	public void isHeavier(String str) {
//		assertEquals(SimpleMathCalculator.isHeavierOperator(str), true);
	}
	
	@Test(enabled=false)
	public void divide() {
		BigDecimal bd = new BigDecimal("");
		BigDecimal bd2 = new BigDecimal("0.000");
		D.pl(bd.divide(bd2, RoundingMode.HALF_UP));
	}
}
