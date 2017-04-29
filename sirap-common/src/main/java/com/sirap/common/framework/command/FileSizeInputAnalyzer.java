package com.sirap.common.framework.command;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.StrUtil;

public class FileSizeInputAnalyzer extends InputAnalyzer {
	
	public FileSizeInputAnalyzer(String input) {
		super(input);
	}
	
	public int whereToSplit() {
		String source = input;
		int indexOfGT = source.indexOf(EXPORT_FLAG);
		while(indexOfGT >= 0) {
			String temp = source.substring(indexOfGT + 1);
			boolean flag = isFileSizeOpoeartor(temp);
			if(flag) {
				indexOfGT = source.indexOf(EXPORT_FLAG, indexOfGT + 1);
			} else {
				break;
			}
		}
		
		return indexOfGT;
	}
	
	public static boolean isFileSizeOpoeartor(String source) {
		String regex = "\\s*" + Konstants.REGEX_FLOAT + "[" + Konstants.FILE_SIZE_UNIT + "]((\\s.*|&|\\|).*|>.*|$)";
		boolean flag = StrUtil.isRegexMatched(regex, source);
		
		return flag;
	}
}
