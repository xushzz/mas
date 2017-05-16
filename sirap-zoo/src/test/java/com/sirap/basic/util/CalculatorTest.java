package com.sirap.basic.util;

import static org.testng.Assert.assertEquals;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sirap.basic.math.MexCalculator;
import com.sirap.basic.math.SimpleCalculator;
import com.sirap.basic.tool.D;

public class CalculatorTest {

	@DataProvider
	public Object[][] mex1() {
		Object[][] obj = {{"+", null}, {"-", null}, {"x", null}, {"x", null},  {"/", null}, 
				{"0", null}, {"0000", null}, {"000000", null}, {"0000000", null},  {"00000000", null}, 
				{"+0", null}, {"-0", null}, {"x0", null}, {"X0", null},  {"/0", null}, 
				{"+1", null}, {"-1", null}, {"x1", null}, {"X1", null},  {"/1", null},
				{"+5.0", null}, {"-5.0", null}, {"x5.0", null}, {"X5.0", null},  {"/5.0", null},
				{"5.0", null}, {"5.0", null}, {"5.0", null}, {"5.0", null},  {"5.0", null},
				{"0+1", "1"}, {"0-1", "-1"}, {"0x1", "0"}, {"0X1", "0"},  {"0/1", "0"},
				{"1+1", "2"}, {"1-1", "0"}, {"1x1", "1"}, {"1X1", "1"},  {"1/1", "1"},
				{"1+(1)", "2"}, {"1-(1)", "0"}, {"1x(1)", "1"},  {"1/(1)", "1"},
				{"1+(_1)", "0"}, {"1-(_1)", "2"}, {"1x(_1)", "-1"},  {"1/(_1)", "-1"},
				{"1+1*2", "3"}, {"1-1*2", "-1"}, {"1x1*2", "2"}, {"1X1*2", "2"},  {"1/1*2", "2"},
				{"0", null}, {"1-1*2", "-1"}, {"1x1*2", "2"}, {"1X1*2", "2"},  {"1/1*2", "2"}
				};
		
		return obj;
	}
	
	@Test(enabled=false, dataProvider="mex1")
	public void mex1(String source, String result) {
		assertEquals(MexCalculator.evaluate(source), result);
	}
	
	@DataProvider
	public Object[][] mex2() {
		Object[][] obj = {
				{"(1+1)", "2"}, {"1*((2+8))", "10"},{"(1-2)x7", "-7"},{"(15/5-(4+3))", "-4"},
				{"((1)+1)", "2"}, {"(1)*((2+8))", "10"},{"((1)-2)x7", "-7"},{"((1)5/5-(4+3))", "-4"},
				{"((((((1)))))+1)", "2"}, {"(1)*((2+(((8)))))", "10"},{"(((((((1))))))x-2)x7", null},
				{"((1hhj)5/5-(4+3))", null},{"(((((((1))))))x_2)x7", "-14"},
		};
		
		return obj;
	}
	
	@Test(enabled=true, dataProvider="mex2")
	public void mex2(String source, String result) {
		assertEquals(MexCalculator.evaluate(source), result);
	}
	
	
	@DataProvider
	public Object[][] alone() {
		Object[][] obj = {{"-6", null},{"_6", null}, {"6", null},{"X", null}, {"/", null}};
		
		return obj;
	}
	
	@Test(enabled=false, dataProvider="alone")
	public void alone(String source, String result) {
		assertEquals(MexCalculator.evaluate(source), result);
	}
	
	@DataProvider
	public Object[][] case1() {
		Object[][] obj = {{"1-(AXB)+(B+C)", null},{"9/7 - ( 2/7 s- 10/21)", null}, {"1+6x(5-(6))/2", "-2"},{"1+6x(5-(4x3)-(6))/2", "-38"},{"1+6x(5+(4x3)-6)/2", "34"},{"1+3x5-6/2", "13"},{"(1+6)x(5-1)/2", "14"}};
		
		return obj;
	}
	
	@Test(enabled=false, dataProvider="case1")
	public void advance(String source, String result) {
		assertEquals(MexCalculator.evaluate(source), result);
	}

	@DataProvider
	public Object[][] a2() {
		Object[][] obj = {{"7*",null},{"-6","-6"},{"_7","-7"},{"-6","-6"},{"6","6"},{"1-2-3", "-4"}, {"1-_2-3", "0"},{"-1-2", "-3"}};
		
		return obj;
	}
	
	@Test(enabled=false, dataProvider="a2")
	public void simple(String source, String result) {
		assertEquals(SimpleCalculator.evaluate(source), result);
	}
	
	@DataProvider
	public Object[][] simpleAlone() {
		Object[][] obj = {{"+", null}, {"-", null}, {"x", null}, {"x", null},  {"/", null}, 
				{"+0", null}, {"-0", null}, {"x0", null}, {"X0", null},  {"/0", null}, 
				{"+1", null}, {"-1", null}, {"x1", null}, {"X1", null},  {"/1", null},
				{"0", "0"}, {"0000", "0"}, {"000000", "0"}, {"0000000", "0"},  {"00000000", "0"},
				{"5.0", "5"}, {"5.0", "5"}, {"5.0", "5"}, {"5.0", "5"},  {"5.0", "5"},
				{"0+1", "1"}, {"0-1", "-1"}, {"0x1", "0"}, {"0X1", "0"},  {"0/1", "0"},
				{"1+1", "2"}, {"1-1", "0"}, {"1x1", "1"}, {"1X1", "1"},  {"1/1", "1"},
				{"1+1*2", "3"}, {"1-1*2", "-1"}, {"1x1*2", "2"}, {"1X1*2", "2"},  {"1/1*2", "2"},
				};
		
		return obj;
	}
	
	@Test(enabled=false, dataProvider="simpleAlone")
	public void simpleAlone(String source, String result) {
		assertEquals(SimpleCalculator.evaluate(source), result);
	}
	
	@Test(enabled=false, dataProvider="a2")
	private void js(String source, String result) {
//		String ex = "0.5+2*4-2.5*4/5";
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        try {
        	String realResult = String.valueOf(engine.eval(source));
        	D.sink(realResult);
			assertEquals(realResult, result);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
	@DataProvider
    public Object[][] case2(){
        Object[][] result = {
        {"3/7 × 49/9 - 4/3", null}
        ,{"35 / 7  x  49 / 5  -  9   / 	3", "46"}
        ,{"1+2*3-4+99/3.0+12", "48"}
        ,{"1.00/0.00", null}
        ,{"_1+2x3+40/5-8", "5"}
        ,{"_1+2+3+4", "8"}
        ,{"1+2+3+4", "10"}
        ,{"10+20+30+40", "100"}
        ,{"1.1+2.2+3.3+4.44", "11.04"}
        ,{"1+2+3+4", "10"}
        ,{"10+20+30+40", "100"}
        ,{"1-2-3-4", "-8"}};
        
        return result;
    }

	@Test(enabled=true,dataProvider="case2")
	public void case2(String exp, String result) {
		assertEquals(MexCalculator.evaluate(exp), result);
//		D.pl(MexCalculator.evaluate(exp));
	}
	
	@DataProvider
    public Object[][] case3(){
        Object[][] result = {
        		{"021/10", "2.1"},{"1+as12", null},{"21-12", "9"},
        		{"21X12", "252"}
        };
        
        return result;
	}
	
	@Test(enabled=true, dataProvider="case3")
	public void case3(String exp, String result) {
		assertEquals(MexCalculator.evaluate(exp), result);
	}
	
	@DataProvider
    public Object[][] case4(){
        Object[][] result = {
        		{"15199.56 +		13807.31", "29006.87"},{"15199.56- 13807.31", "1392.25"}
        		,{"15199.56 x 13807.31", "209865036.7836"},{"15199.56   /13807.31", "1.10083426822458538267"},
        		{"15199+	13807", "29006"},{"15199-13807", "1392"},{"15199x13807", "209852593"},
        		{"15199/13807", "1.10081842543637285435"},{"x",null}
        };
        
        return result;
	}
	
	@Test(enabled=true, dataProvider="case4")
	public void case4(String exp, String result) {
		assertEquals(MexCalculator.evaluate(exp), result);
	}
	
	public void extraPrecison() {
		MexCalculator.evaluate("15199.56+13807.31");
		MexCalculator.evaluate("15199.56-13807.31");
		MexCalculator.evaluate("15199.56x13807.31");
		MexCalculator.evaluate("15199.56/13807.31");
		MexCalculator.evaluate("15199+13807");
		MexCalculator.evaluate("15199-13807");
		MexCalculator.evaluate("15199x13807");
		MexCalculator.evaluate("15199/13807");
	}

	public void doubleMath() {
		MexCalculator.evaluate("0.21/10");
		MexCalculator.evaluate(null);
		MexCalculator.evaluate("1+as12");
		MexCalculator.evaluate("..1+12");
		MexCalculator.evaluate("21.123456798789798789498498498-12.0011111111111111100");
		MexCalculator.evaluate("21.3652/12.87");
		MexCalculator.evaluate("21x12");
		MexCalculator.evaluate("21/12");
	}
}
