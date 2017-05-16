package com.sirap.basic.math;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sirap.basic.math.FormulaCalculator;

public class FormulaTest {
	
	@DataProvider
	public Object[][] s1(){
		//(3x+1)/2-2=(3x-2)/10-(2x+3)/5
		Object[][] data = {{"+1","1"},{"-1","-1"},{"+_1","-1"},{"-_1","1"},
				{"+","+"},{"-","-"},{"1","1"},{"",""},
				{"+10","10"},{"-10","-10"},{"+_10","-10"},{"-_10","10"}
				};
		
		return data;
	}
	
	@Test(dataProvider="s1")
	public void trimSigns(String input, String expected) {
		String actual = FormulaCalculator.trimSigns(input);
		assertEquals(actual, expected);
	}
	
	@DataProvider
    public Object[][] f1(){
		//12:05 PM 10/22/2014
		//Object[][] result = {{"8w+12-4.8w-9=2w+2-5.8w-90","w=5"},{"3.4w-1.4w-6=14","w=10"}};
		Object[][] result = {{"-10+_0.1+0+3x+30=2x+90","70.1"}, {"8x-4.8x=1.6","0.5"},{"3.4x-1.4x-6=14","10"},
				{"x+4x=20","4"}};
        return result;
    }
	
	@Test(enabled=false, dataProvider="f1")
	public void formula(String expression, String expected) {
		String actual = FormulaCalculator.evaluate(expression);
		assertEquals(actual, expected);
	}
}
