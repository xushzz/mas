package com.sirap.common.framework.command;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.StrUtil;

public class FileSizeInputAnalyzer extends InputAnalyzer {
	
	public FileSizeInputAnalyzer(String input) {
		super(input);
	}
	
	@Override
	protected boolean isMatchedEscape(String source, int indexOfGT) {
		boolean flagA = super.isMatchedEscape(source, indexOfGT);
		if(flagA) {
			return true;
		}
		
		boolean flagB = isMatchedFileSizeOpoeartor(source, indexOfGT);
		return flagB;
	}
	
	public static boolean isMatchedFileSizeOpoeartor(String source, int indexOfGT) {
		String temp = source.substring(indexOfGT + 1);
		
		String regex = "\\s*" + Konstants.REGEX_FLOAT + "[" + Konstants.FILE_SIZE_UNIT + "]((\\s.*|&|\\|).*|>.*|$)";
		boolean flag = StrUtil.isRegexMatched(regex, temp);
		
		return flag;
	}
}
