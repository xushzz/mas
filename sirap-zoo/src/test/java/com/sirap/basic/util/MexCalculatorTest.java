package com.sirap.basic.util;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sirap.basic.math.MexCalculator;
import com.sirap.basic.math.SimpleCalculator;
import com.sirap.basic.tool.C;

public class MexCalculatorTest {
	@DataProvider
	public Object[][] mex2() {
		Object[][] obj = {{"-5+6", "1"}, {"5-6", "-1"},
				{"/", null},{"-5", "-5"},{"(((((1+2)))))", "3"},
				{"45+(1x(2+3)*4+8)x7+(99-78.3X(45-12/(14-9)))", "-2995.58"},
				{"45 +	(1x(2 +	3)*4 + 	8)x7 +	(99-78.3X(45-12/(14-9)))", "-2995.58"},
		};
		
		return obj;
	}
	
	@DataProvider
	public Object[][] mex3() {
		Object[][] obj = {{"((1+2))", "3"}
		};
		
		return obj;
	}

	@DataProvider
	public Object[][] simple1() {
		Object[][] obj = {{"-5+6", "1"}, {"-5", "-5"}, {"x5", null}
		};
		
		return obj;
	}
	
	@Test(enabled=true, dataProvider="mex3")
	public void mex(String source, String result) {
		assertEquals(MexCalculator.evaluate(source), result);
	}
	
	@Test(enabled=false, dataProvider="simple1")
	public void simple(String source, String result) {
		C.pl("james");
		assertEquals(SimpleCalculator.evaluate(source), result);
	}
}
