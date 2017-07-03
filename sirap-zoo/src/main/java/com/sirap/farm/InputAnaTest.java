package com.sirap.farm;

import java.util.regex.Matcher;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.StrUtil;
import com.sirap.db.SqlInputAnalyzer;

public class InputAnaTest {
	
	@Test
	public void insideQuote() {
		String sa = "dept_'manager' wh'1994'";
		int idx = 10;
		C.pl(SqlInputAnalyzer.insideQuote(sa, idx));
	}
	public void baobao() {
		String source = "!>no##!>re!>ant#!>cation.>";
		String escape = "!";
		String EXPORT_FLAG = ">";
		int indexOfGT = source.indexOf(EXPORT_FLAG);
		while(indexOfGT >= 0) {
			int previousIndex = indexOfGT - escape.length();
			if(previousIndex < 0) {
				break;
			}

			String left = source.substring(0, indexOfGT);
			boolean isEscape = StrUtil.endsWith(left, escape);
			if(isEscape) {
				indexOfGT = source.indexOf(EXPORT_FLAG, indexOfGT + 1);
			} else {
				break;
			}
		}
		
		C.pl(indexOfGT);
	}
}
