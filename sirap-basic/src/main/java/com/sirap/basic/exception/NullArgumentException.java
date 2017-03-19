package com.sirap.basic.exception;

import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class NullArgumentException extends MexException {
	
	private static final String MSG_TEMPLATE = "argument [{0}] can't be null.";
	private static final String MSG_SIMPLE = "argument can't be null.";
	
	public NullArgumentException() {
		super(MSG_SIMPLE);
	}
	
	public NullArgumentException(String info) {
		super(readClosely(info));
	}
	
	private static String readClosely(String info) {
		if(info == null) {
			return MSG_SIMPLE;
		}
		String param = StrUtil.parseParam(":(.+)", info);
		if(param != null) {
			return param;
		} else {
			String temp = StrUtil.occupy(MSG_TEMPLATE, info);
			return temp;
		}
	}
}
