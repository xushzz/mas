package com.sirap.common.test;

import static org.testng.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sirap.basic.tool.C;
import com.sirap.common.manager.FileManager;

public class RegexTest {

	@DataProvider
    public Object[][] criteria(){
        Object[][] result = {{null, null},{"", null}, {"5.0s10", null}, {"05.03", "S05E03"},  
        		{"05.00", null}, {"0.03", null}, {"1.2", "S01E02"}, {"10.2", "S10E02"}};
        return result;
    }

	@Test(dataProvider="criteria")
	public void fixCriteria(String param, String result) {
		assertEquals(FileManager.fixCriteria(param), result);
	}
	
	@DataProvider
    public Object[][] criteria2(){
        Object[][] result = {{null, null},{"", null}, {"5.01A", "S05E01A"}, {"05.03", "S05E03"},  
        		{"Home&05.00", null}, {"Home&0.03", null}, {"Home|1.2", "Home|S01E02"}, {"Home|10.2", "Home|S10E02"}};
        return result;
    }

	@Test(dataProvider="criteria2")
	public void fixCriteria2(String param, String result) {
		assertEquals(FileManager.fixCriteria(param), result);
	}
	
	public void matches() {
		C.pl("1332".matches("\\d{4}"));
		C.pl("1921a97".matches("\\d"));
		C.pl("1997a".matches("\\d"));
		C.pl("a1997".matches("\\d"));
		
		Matcher m = Pattern.compile("\\d+").matcher("tic");
		C.pl(m.find());
	}
}
