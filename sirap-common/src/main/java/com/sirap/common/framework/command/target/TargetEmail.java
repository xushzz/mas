package com.sirap.common.framework.command.target;

import java.util.List;

public class TargetEmail extends Target {

	private String subject;
	private List<String> toList;
	
	public TargetEmail(String subject, List<String> toList) {
		this.subject = subject;
		this.toList = toList;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<String> getToList() {
		return toList;
	}

	public void setToList(List<String> toList) {
		this.toList = toList;
	}
}
