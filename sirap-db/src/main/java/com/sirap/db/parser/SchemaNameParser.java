package com.sirap.db.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.StrUtil;

public class SchemaNameParser {
	/***
	 * jdbc:db2://localhost:5000/testDB
	 * jdbc:mysql://localhost:3306/email?useUnicode=true&amp;characterEncoding=UTF-8
	 * @param url
	 * @return
	 */
	public String parseSchema(String url) {
		String regex = getRegex();
		String result = StrUtil.findFirstMatchedItem(regex, url);
		
		return result;
	}
	
	public String changeSchema(String url, String schema) {
		String regex = getRegex();
		
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(url);
		if(m.find()) {
			String tempA = m.group();
			String tempB = m.group(1);
			String tempC = tempA.replace(tempB, schema);
			String temp = url.replace(tempA, tempC);
			
			return temp;
		}
		
		throw new MexException("No valid schema found in url => " + url);
	}
	
	protected String getRegex() {
		String regex = "[^/]/([^/?]{1,99})";
		
		return regex;
	}
}
