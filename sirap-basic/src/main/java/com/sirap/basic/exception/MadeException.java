package com.sirap.basic.exception;

import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MadeException extends MexException {
	
	public MadeException() {
		super("made exception");
	}
	
	public MadeException(String msg) {
		super(msg);
	}
	
	public MadeException(String msgTemplate, Object... params) {
		super(StrUtil.occupy(msgTemplate, params));
	}

	public MadeException(Throwable ex) {
		super(ex);
	}
}
