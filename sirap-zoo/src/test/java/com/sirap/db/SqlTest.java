package com.sirap.db;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.StrUtil;

public class SqlTest {
	
	@Test
	public void location() {
		String va = "from iams_menu where id>=202 and name != 'chen'";
		va = "i>202";
		va = "e>'a' limit 20";
		va = "e>'a'>D:";
		va = "i>202 and a>10 and c>3";
		va = "i>202 and a>10 and c>=3";
		va = "e>'a fwfwfwe'";
		String source = va;
		int indexOfGT = source.indexOf('>');
		while(indexOfGT >= 0) {
			String v1 = source.substring(indexOfGT + 1);
			String regex = "\\s*^=?\\s*(\\d{1,}|'[^']*')(\\s.*|>.*|$)";
			boolean isSqlOperator = StrUtil.isRegexMatched(regex, v1);
			D.pl(v1, isSqlOperator);
			if(isSqlOperator) {
				indexOfGT = source.indexOf('>', indexOfGT + 1);
			} else {
				break;
			}
		}
		
		C.pl(indexOfGT);
	}
}
