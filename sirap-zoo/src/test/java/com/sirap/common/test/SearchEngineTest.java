package com.sirap.common.test;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sirap.common.domain.SiteSearchEngine;

public class SearchEngineTest {
	
	@Test(enabled=false, dataProvider="searchengines")
	public void searchQuery(String url, boolean flag) {
		SiteSearchEngine se = new SiteSearchEngine();
		assertEquals(se.parse(url), flag);
	}
	
	@DataProvider(name="searchengines")
	public Object[][] provideEngines() {
		return new Object[][]{
				{"a>http://search.aol.com/aol/search?q={0}>AOL.com, enhanced by Google.", true},
				{"b>http://www.baidu.com/s?wd={0}>Baidu.com, do evil.", true},
				{"c>https://www.google.com.hk/#q={0}>Google.com, do no evil.", true},
				{"c>https://www.google.com.hk/#q=>Google.com, do no evil.", false},
		};
	}
	

}
