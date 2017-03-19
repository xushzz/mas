package com.sirap.basic.exception;

@SuppressWarnings("serial")
public class MexException extends RuntimeException {
	
	public MexException() {
		super("mex exception");
	}
	
	public MexException(String msg) {
		super(msg);
	}
	
	public MexException(Exception ex) {
		super(ex);
	}
}
