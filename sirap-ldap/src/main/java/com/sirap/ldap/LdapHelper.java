package com.sirap.ldap;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.ldap.online.StaffInfo;

public class LdapHelper {
	
	public static String ignoreTypoInAccount(String source) {
		String regex = "\\W";
		String result = source.replaceAll(regex, "");
		
		return result;
	}
	
	public static boolean containsWordCommaSpaceOnly(String source) {
		String regex = "[\\w\\s,]+";
		boolean good = StrUtil.isRegexMatched(regex, source);
		
		return good;
	}
	
	public static String constructAccountExpression(List<String> accountList) {
		if(EmptyUtil.isNullOrEmpty(accountList)) {
			return null;
		}
		
		StringBuffer bf = new StringBuffer("(|");
		for(String item : accountList) {
			String temp = ignoreTypoInAccount(item);
			bf.append("(").append("sAMAccountName=").append(temp).append(")");
		}
		bf.append(")");
		
		String filterExpressioin = bf.toString();
		
		return filterExpressioin;
	}

	public static List<String> parseWorkerNumbersOfDirectReports(String source) {
		List<String> folksWorkerNumber = new ArrayList<>();;
		List<String> folks = StrUtil.split(source, "CN=", true);
		for(String folk : folks) {
			if(EmptyUtil.isNullOrEmpty(folk)) {
				continue;
			}
			String workerNumber = parseWorkerNumber(folk);
			//String workerNumber = StrUtil.findFirstMatchedItem(regex, folk);
			if(workerNumber != null) {
				folksWorkerNumber.add(workerNumber);
				continue;
			}
		}

		return folksWorkerNumber;
	}
	
	public static String parseWorkerNumber(String source) {
		String regex = "\\((\\w+)\\)";
		String workerNumber = StrUtil.findFirstMatchedItem(regex, source);

		return workerNumber;
	}
	
	public static List<String> toStaffStringList(List<StaffInfo> items) {
		List<String> list = new ArrayList<>();
		
		for(StaffInfo item : items) {
			list.add(item.getDetail());
		}
		
		return list;
	}
	
	public static String ignoreTypoAccount(String source) {
		String regex = "\\W";
		String result = source.replaceAll(regex, "");
		
		return result;
	}
}
