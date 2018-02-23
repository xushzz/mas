package com.sirap.basic.search;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class FileSizeCriteria extends SizeCriteria {
	
	private static final long serialVersionUID = 1L;
	
	private char unit;

	public FileSizeCriteria() {}
	
	public FileSizeCriteria(String record) {
		if(!parse(record)) {
			XXXUtil.alert("Not able to parse: " + record);
		}
	}

	@Override
	public boolean parse(String record) {
		String regex = "([=><~])" + Konstants.REGEX_FLOAT + "([" + Konstants.FILE_SIZE_UNIT + "])";
		String[] params = StrUtil.parseParams(regex, record);
		if(params != null) {
			operator = params[0];
			String baseValue = params[1];
			char unit = params[2].toUpperCase().charAt(0);
			value = FileUtil.parseSize(baseValue, unit);
			
			return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		return "SizeOperative [operator=" + operator + ", value=" + value + ", unit=" + unit + "]";
	}
}
