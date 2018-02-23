package com.sirap.common.framework.command;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.StrUtil;

public class SizeInputAnalyzer extends InputAnalyzer {
	
	public SizeInputAnalyzer(String input) {
		super(input);
	}
	
	@Override
	protected boolean isMatchedEscape(String source, int indexOfGT) {
		boolean flagA = super.isMatchedEscape(source, indexOfGT);
		if(flagA) {
			return true;
		}
		
		boolean flagB = isMatchedOpoeartor(source, indexOfGT);
		return flagB;
	}
	
	public boolean isMatchedOpoeartor(String source, int indexOfGT) {
		String temp = source.substring(indexOfGT + 1);
		
		String regex = "\\s*" + Konstants.REGEX_FLOAT + "((\\s.*|&|\\|).*|>.*|$)";
		boolean flag = StrUtil.isRegexMatched(regex, temp);
		
		return flag;
	}
}
