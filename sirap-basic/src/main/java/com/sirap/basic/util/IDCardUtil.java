package com.sirap.basic.util;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class IDCardUtil {
	
	public static char checkCodeChina(String id17or18) {
		int len = 17;
		String regex = "(\\d{" + len + "})(|\\d|X)";

		String[] params = StrUtil.parseParams(regex, id17or18);
		if(params == null) {
			throw new MexException("illegal ID number: " + id17or18);
		}
		
		String bulk = params[0];
		
		int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
		
		XXXUtil.shouldBeEqual(len, weight.length);

		int sum = 0;
		for(int i = 0; i < len; i++) {
			int digit = Integer.parseInt(bulk.charAt(i) + "");
			sum += digit * weight[i];
		}
		
		char[] validate = { '1','0','X','9','8','7','6','5','4','3','2'};
		int mode = sum % validate.length;
		char code = validate[mode];
		
		return code;
	}
}
