package com.sirap.basic.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;

public class MexColorConverter {

	private String expression;
	private List<String> sourceList = new ArrayList<String>();
	private List<String> results = new ArrayList<String>();

	public MexColorConverter(String expression) {
		this.expression = expression;
		sourceList = StrUtil.splitByRegex(expression, ",|\\s");
		boolean hasBeenHandled = decimal2hex();
		if(!hasBeenHandled) {
			hex2decimal();
		}
	}
	
	public List<String> getResult() {
		return results;
	}
	
	private boolean decimal2hex() {
		if(sourceList.size() != 3) {
			return false;
		}
		
		StringBuffer sb = new StringBuffer(expression + "=#");
		for(int i = 0; i < sourceList.size(); i++) {
			String item = sourceList.get(i);
			if(EmptyUtil.isNullOrEmpty(item)) {
				return false;
			}
			
			String exp = item.trim();
			Integer dec = MathUtil.toInteger(exp);
			if(dec == null || dec < 0 || dec > 255) {
				return false;
			}
			
			String hex = Integer.toHexString(dec).toUpperCase();
			if(hex.length() == 2) {
				sb.append(hex);
			} else if(hex.length() == 1) {
				sb.append("0").append(hex);
			}
		}
		
		results.add(sb.toString());
		
		return true;
	}
	
	private void hex2decimal() {
		for(String item : sourceList) {
			if(EmptyUtil.isNullOrEmpty(item)) {
				continue;
			}
			
			String[] params = StrUtil.parseParams("(#|)([a-f|\\d]{3,6})", item.trim());
			if(params == null) {
				continue;
			}
			String exp = params[1];
			
			StringBuffer sb = new StringBuffer("#" + exp.toUpperCase() + "=");
			List<Integer> values = parseColorHex(exp);
			for(int i = 0; i < values.size(); i++) {
				Integer dec = values.get(i);
				if(dec == null) {
					continue;
				}
				
				sb.append(Integer.toString(dec));
				if(i != values.size() - 1) {
					sb.append(",");
				}
			}
			if(values.size() > 0) {
				results.add(sb.toString());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> parseColorHex(String source) {
		int len = source.length();
		if(len != 6) {
			return Collections.EMPTY_LIST;
		}
		
		int mod = len / 3;
		if(mod == 0) {
			return Collections.EMPTY_LIST;
		}
		
		List<Integer> values = new ArrayList<Integer>();
		String p1 = source.substring(0, mod);
		String p2 = source.substring(mod, mod*2);
		String p3 = source.substring(mod*2);
		values.add(MathUtil.toIntegerByRadius(p1, 16));
		values.add(MathUtil.toIntegerByRadius(p2, 16));
		values.add(MathUtil.toIntegerByRadius(p3, 16));
		
		return values;
	}
}
