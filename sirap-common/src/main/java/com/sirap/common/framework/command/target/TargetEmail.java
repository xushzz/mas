package com.sirap.common.framework.command.target;

import java.util.List;

import com.sirap.basic.email.EmailCenter;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.common.framework.SimpleKonfig;

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

	@Override
	public void export(List records, String options, boolean withTimestamp) {
		boolean useNewThread = SimpleKonfig.g().isYes("email.send.newthread");
		EmailCenter.g().sendEmail(records, toList, subject, !useNewThread);
		C.pl();
	}
	
	@Override
	public String toString() {
		return D.jst(this);
	}
}
