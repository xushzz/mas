package com.sirap.db;

import com.sirap.basic.util.StrUtil;
import com.sirap.common.framework.command.InputAnalyzer;

public class DBInputAnalyzer extends InputAnalyzer {
	
	public DBInputAnalyzer(String input) {
		super(input);
	}
	
	public int whereToSplit() {
		String source = input;
		int indexOfGT = source.indexOf(EXPORT_FLAG);
		while(indexOfGT >= 0) {
			String temp = source.substring(indexOfGT + 1);
			boolean flag = isSqlOpoeartor(temp);
			if(flag) {
				indexOfGT = source.indexOf(EXPORT_FLAG, indexOfGT + 1);
			} else {
				break;
			}
		}
		
		return indexOfGT;
	}
	
	public static boolean isSqlOpoeartor(String source) {
		String regex = "\\s*=?\\s*(\\d{1,}|'[^']*')(\\s.*|>.*|$)";
		boolean flag = StrUtil.isRegexMatched(regex, source);
		
		return flag;
	}
}
