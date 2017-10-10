package com.sirap.basic.exception;

@SuppressWarnings("serial")
public class DuplicationException extends MexException {
	
	public DuplicationException(String msg) {
		super(msg);
	}

	public DuplicationException(String msgTemplate, String params) {
		super(msgTemplate, params);
	}
}
