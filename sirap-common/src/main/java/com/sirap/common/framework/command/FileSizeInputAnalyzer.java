package com.sirap.common.framework.command;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.StrUtil;

public class FileSizeInputAnalyzer extends SizeInputAnalyzer {
	
	public FileSizeInputAnalyzer(String input) {
		super(input);
	}
	
	public boolean isMatchedOpoeartor(String source, int indexOfGT) {
		String temp = source.substring(indexOfGT + 1);
		
		String regex = "\\s*" + Konstants.REGEX_FLOAT + "[" + Konstants.FILE_SIZE_UNIT + "]((\\s.*|&|\\|).*|>.*|$)";
		boolean flag = StrUtil.isRegexMatched(regex, temp);
		
		return flag;
	}
}
