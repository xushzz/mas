package com.sirap.common.test;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sirap.basic.email.EmailCenter;
import com.sirap.basic.email.EmailServerItem;
import com.sirap.basic.search.CriteriaFilter;
import com.sirap.basic.util.StrUtil;

public class EmailTest {
	
	private static Map<String, EmailServerItem> servers = new HashMap<String, EmailServerItem>();
	{
		servers.put("qq.com", new EmailServerItem("qq.com", "smtp.qq.com", "25"));
		servers.put("aol.com", new EmailServerItem("aol.com", "smtp.aol.com", "25"));
	}
	
	@Test
	public void sendViaTencent() {
		EmailCenter baoan = EmailCenter.g();
		baoan.config("584407421@qq.com", "XXXXX", "piratewithoutsea@163.com");
		baoan.config("584407421@aols.com", "XXXXXXXXXXXXXXXX", "piratewithoutsea@163.com", servers);
//		baoan.config("aol007@163.com", "xxxxxxx", "piratewithoutsea@163.com");
//		baoan.config("aol001@126.com", "xxxxxxx", "piratewithoutsea@163.com");
		List<Object> items = new ArrayList<Object>();
		items.add(123456798);
		items.add("ABCDEFGH");
		
		List<String> toList = new ArrayList<String>();
		toList.add(EmailCenter.DEF_RECEIVER);
		baoan.sendEmail(items, toList, "Love can couquer everything.", true);
	}
	
	
	
	@DataProvider
	public Object[][] censor() {
		Object[][] arr = {{"hi james>a@a.com;aol007@163.com;b@b.com", "hi james>a@a.com;b@b.com"},
				{"a@a.com;b@b.com", "a@a.com;b@b.com"},
				{"aol007@163.com;b@b.com", "b@b.com"},
				{"b@b.com;aol007@163.com", "b@b.com"},
				{"aol007@163.com", ""},
			};
		
		return arr;
	}
	
	@Test(enabled=false, dataProvider="censor")
	public void criteriaFilter(String input, String output) {
		String target = "aol007@163.com";
		CriteriaFilter fk = new CriteriaFilter(target, input, ";");
		assertEquals(fk.getFixedCommand(), output);
	}
	
	@DataProvider
	public Object[][] to() {
		Object[][] arr = {{"yuzhending <yuzhending@163.com>", "yuzhending@163.com"},
				{"yu@153.cosm", "yu@153.com"}};
		
		return arr;
	}
	
	@Test(enabled=false, dataProvider="to")
	public void replyTo(String input, String output) {
		assertEquals(parseEmailAddress(input), output);
	}
	
	public String parseEmailAddress(String input) {
		String param = StrUtil.parseParam(".*?<(.+?)>.*?", input);
		if(param != null) {
			return param;
		} else {
			return input.trim();
		}
	}
}
