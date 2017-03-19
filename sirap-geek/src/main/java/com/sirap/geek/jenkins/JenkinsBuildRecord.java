package com.sirap.geek.jenkins;

import com.sirap.basic.domain.MexItem;

@SuppressWarnings("serial")
public class JenkinsBuildRecord extends MexItem {
	
	private String number;
	private String status;
	private String duration;
	private String dateStr;
	private String extra;
	
	public JenkinsBuildRecord() {
		
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
	
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("#").append(number);
		sb.append(" ").append(status);
		sb.append(" ").append(duration);
		sb.append(", ").append(dateStr);
		if(extra != null) {
			sb.append(" ").append(extra);
		}
		
		return sb.toString();
	}
}
