package com.sirap.basic.http;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.WebReader;

public class HttpRequestTest {
	
	@Test
	public void post() {
		String url = "https://gonglinongli.51240.com";
    	String params = "gongli_nian=2017&gongli_yue=06&gongli_ri=01";
    	//String params = "nongli_nian=2017&nongli_yue=06&nongli_ri=01";
    	WebReader xiu = new WebReader(url, null, true);
    	//xiu.setMethodPost(true);
    	xiu.setRequestParams(params);
    	String result = xiu.readIntoString();
    	C.pl(result);
	}
}
