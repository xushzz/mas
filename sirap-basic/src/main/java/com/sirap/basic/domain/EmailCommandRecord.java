package com.sirap.basic.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class EmailCommandRecord extends MexItem implements Comparable<EmailCommandRecord> {

	public static final String DOING = "DOING";
	public static final String UNKNOWN = "UNKNOWN";
	public static final String DONE = "DONE";
	public static final String EXPIRED = "EXPIRED";
	public static final String IGNORED = "IGNORED";
	
	private Date sentDate;
	private String status = UNKNOWN;
	private String subject;
	private Set<String> msgFrom = new HashSet<String>();
	private Set<String> msgTo = new HashSet<String>();
	
	public EmailCommandRecord() {
		
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}
	
	public boolean isExpired(int mins2live) {
		int minDiff = DateUtil.minDiff(new Date(), getSentDate());
		
		return minDiff > mins2live;
	}
	
	public boolean isIgnorable() {
		return EmptyUtil.isNullOrEmpty(subject) || subject.startsWith("#");
	}

	public void setStatus(String value) {
		status = value;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setMsgFrom(List<String> msgFrom) {
		this.msgFrom = new HashSet<String>(msgFrom);
	}
	
	public String getMsgFromString() {
		return StrUtil.connect(new ArrayList<String>(msgFrom), ";");
	}

	public String getCommandBasicInfo() {
		String template = "remote command[{0}] from[{1}] to[{2}] date[{3}]";
		String dateStr = DateUtil.displayDate(sentDate);
		
		String info = StrUtil.occupy(template, subject, getMsgFromString(), getMsgToString(), dateStr);
		
		return info;
	}

	public void setMsgTo(List<String> msgTo) {
		this.msgTo = new HashSet<String>(msgTo);
	}

	public String getMsgToString() {
		return StrUtil.connect(new ArrayList<String>(msgTo), ";");
	}

	public String getReplyToString(String receiverToExclude) {
		Set<String> replyTo = new HashSet<String>();
		replyTo.addAll(msgFrom);
		replyTo.addAll(msgTo);
		
		
		if(!EmptyUtil.isNullOrEmpty(receiverToExclude)) {
			replyTo.remove(receiverToExclude);
		}

		return StrUtil.connect(new ArrayList<String>(replyTo), ";");
	}
	
	@Override
	public int compareTo(EmailCommandRecord o) {
		Date d1 = sentDate;
		Date d2 = o.getSentDate();
		if(d1 == null || d2 == null) {
			return 0;
		}
		
		return d1.compareTo(d2);
	}
	
	@Override
	public boolean parse(String source) {
		String regex = "(\\d{8}_\\d{6})_([a-z]{1,10})_\\[(.+?)\\]\\[(.+?)\\]\\[(.+?)\\]";
		String[] params = StrUtil.parseParams(regex, source);
		if(params != null) {
			sentDate = DateUtil.parse(DateUtil.DATETIME_SPACE_TIGHT, params[0]);
			status = params[1];
			subject = params[2];
			msgFrom = new HashSet<String>(StrUtil.split(params[3], ";"));
			msgTo = new HashSet<String>(StrUtil.split(params[4], ";"));
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sentDate == null) ? 0 : sentDate.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailCommandRecord other = (EmailCommandRecord) obj;
		if (sentDate == null) {
			if (other.sentDate != null)
				return false;
		} else if (!sentDate.equals(other.sentDate))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		String template = "{0}_{1}_[{2}][{3}][{4}]";
    	String dateStr = DateUtil.displayDate(sentDate, DateUtil.DATETIME_SPACE_TIGHT);
    	String msgFromStr = getMsgFromString(); 
    	String msgToStr = getMsgToString();
    	String record = StrUtil.occupy(template, dateStr, status, subject, msgFromStr, msgToStr);
    	return record;
	}
}
