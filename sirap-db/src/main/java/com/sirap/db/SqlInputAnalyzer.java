package com.sirap.db;

import java.util.regex.Matcher;

import com.sirap.basic.util.StrUtil;
import com.sirap.common.framework.command.InputAnalyzer;

public class SqlInputAnalyzer extends InputAnalyzer {
	
	public SqlInputAnalyzer(String input) {
		super(input);
	}
	
	@Override
	protected boolean isMatchedEscape(String source, int indexOfGT) {
		boolean flagA = super.isMatchedEscape(source, indexOfGT);
		if(flagA) {
			return true;
		}
		
		boolean flagB = isMatchedLiteralOrSqlOpoeartor(source, indexOfGT);
		return flagB;
	}
	
	public static boolean isMatchedLiteralOrSqlOpoeartor(String source, int indexOfGT) {
		boolean asLiteral = insideQuote(source, indexOfGT);
		if(asLiteral) {
			return true;
		}
		
		String temp = source.substring(indexOfGT + 1);
		String regex = "\\s*=?\\s*(\\d{1,}|'[^']*')(\\s.*|>.*|$)";
		boolean flag = StrUtil.isRegexMatched(regex, temp);
		
		return flag;
	}
	
	public static boolean insideQuote(String source, int index) {
		String regex = "'([^']*)'";
		Matcher ma = StrUtil.createMatcher(regex, source);
		while(ma.find()) {
			int start = ma.start(1);
			int end = ma.end(1);
			boolean galf = index >= start && index <= end;
			if(galf) {
				return true;
			}
		}
		
		return false;
	}
}
