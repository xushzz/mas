package com.sirap.basic.exception;

import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MexException extends RuntimeException {
	
	private Exception origin;
	
	public Exception getOrigin() {
		return origin;
	}

	public void setOrigin(Exception origin) {
		this.origin = origin;
	}

	public MexException(Exception ex) {
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
