package com.sirap.basic.search;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;

public class CriteriaFilter {
	private String[] params;
	private String fixedCriterias;
	
	private String target;
	private String mixedCriterias;
	private String separater = "&";
	
	public CriteriaFilter(String target, String command) {
		this.target = target;
		this.mixedCriterias = command;
		
		distill();
	}
	
	public CriteriaFilter(String target, String command, String separater) {
		this.target = target;
		this.mixedCriterias = command;
		this.separater = separater;
		
		distill();
	}

	public String[] getParams() {
		return params;
	}

	public String getFixedCommand() {
		return fixedCriterias;
	}

	private void distill() {
		fixedCriterias = mixedCriterias;

		if(EmptyUtil.isNullOrEmpty(mixedCriterias) || EmptyUtil.isNullOrEmpty(target)) {
			return;
		}
		
		
		String key = separater;
		String[] arr = mixedCriterias.split(key);
		for(int i = 0; i < arr.length; i++) {
			String criteria = arr[i];
			String[] paramArr = StrUtil.parseParams(target, criteria);
			if(paramArr == null) {
				continue;
			}
			
			String temp = null;
			if(arr.length == 1) {
				temp = "";
			} else if(i != arr.length - 1) {
				temp = mixedCriterias.replace(criteria + key, "");
			} else {
				temp = mixedCriterias.replace(key + criteria, "");
			}
			
			params = paramArr; 
			fixedCriterias = temp;
			
			return;
		}
	}
}
