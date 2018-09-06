package com.sirap.basic.db.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.StrUtil;

public class SchemaNameParser {
	
	/***
	 * jdbc:db2://localhost:5000/testDB
	 * jdbc:mysql://localhost:3306/?useUnicode=true&amp;characterEncoding=UTF-8
	 * @param url
	 * @return
	 */
	public String parseSchema(String url) {
		String regex = getRegex();
		String result = StrUtil.findFirstMatchedItem(regex, url);
		
		return result;
	}
	
	public String addSchema(String url, String schema) {
		String questionMark = "?";
		char slash = '/';
		int indexOfQ = url.indexOf(questionMark);
		if(indexOfQ >  0) {
			char previousChar = url.charAt(indexOfQ - 1);
			String temp = url;
			if(previousChar == slash) {
				temp = url.replace("?", schema + "?");
			} else {
				temp = url.replace("?", slash + schema + "?");
			}
			
			return temp;
		}
		
		if(url.endsWith(slash + "")) {
			String temp = url + schema;
			return temp;
		} else {
			String temp = url + slash + schema;
			return temp;
		}
	}
	
	public String fixUrlByChangingSchema(String url, String schema) {
		String regex = getRegex();
		
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(url);
		if(m.find()) {
			String tempA = m.group();
			String tempB = m.group(1);
			String tempC = tempA.replace(tempB, schema);
			String temp = url.replace(tempA, tempC);
			
			
			return temp;
		} else {
			return addSchema(url, schema);
		}
	}
	
	protected String getRegex() {
		String regex = "[^/]/([^/?]{1,99})";
		
		return regex;
	}
}
