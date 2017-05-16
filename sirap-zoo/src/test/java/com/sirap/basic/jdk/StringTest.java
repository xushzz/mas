package com.sirap.basic.jdk;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sirap.basic.tool.D;

public class StringTest {

	@DataProvider
    public Object[][] splitP1(){
        Object[][] result = {{null}, {"\\"},{"\t"},{","},{" "},{"!!@#"},
        		{"1,2"},{"0.1..2"},{"12d,d3f,wf3,21,32"}};
        return result;
    }
	
	@Test(enabled=false, dataProvider="splitP1")
	public void split(String param) {
		if(param == null) {
			D.pl(param, "shit");
		} else {
			D.pl(param, param.split(",").length);
		}
	}
	@DataProvider
    public Object[][] splitP2(){
        Object[][] result = {{null}, {"\\"},{"\t"},{","},{" "},{"!!@#"},
        		{"1,2"},{"0.1..2"},{"12d d3f wf3 21 32"}};
        return result;
    }
	
	@Test(dataProvider="splitP2")
	public void split2(String param) {
		if(param == null) {
			D.pl(param, "shit");
		} else {
			D.pl(param, param.split(" ").length);
		}
	}
}
