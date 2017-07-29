package com.sirap.basic.exception;

import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MexException extends RuntimeException {
	
	private Throwable origin;
	
	public Throwable getOrigin() {
		return origin;
	}

	public void setOrigin(Throwable origin) {
		this.origin = origin;
	}

	public MexException(Throwable ex) {
		super(ex);
		origin = ex;
	}
	
	public MexException() {
		super("mex exception");
	}
	
	public MexException(String msg) {
		super(msg);
	}
	
	public MexException(String msgTemplate, Object... params) {
		super(StrUtil.occupy(msgTemplate, params));
	}
}
