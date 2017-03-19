package com.sirap.ldap.offline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.EmptyUtil;

@SuppressWarnings("serial")
public class SmartDetail extends MexItem implements Comparable<SmartDetail> {
	private String who;
	private String detail;

	private static Pattern PATTERN_WHO = Pattern.compile("\\((\\S+)\\)", Pattern.CASE_INSENSITIVE);
	private static Pattern PATTERN_WHEN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})", Pattern.CASE_INSENSITIVE);
	
	public SmartDetail() {
		
	}
	
	public SmartDetail(String who) {
		this.who = who;
	}
	
	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public String getDetail() {
		return detail;
	}

	public String wrapDetail() {
		String detail = getDetail();
		if(EmptyUtil.isNullOrEmpty(detail)) {
			detail = getWho() + ", no detail.";
		}
		
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Override
	public boolean parse(String record) {
		if(EmptyUtil.isNullOrEmptyOrBlank(record)) {
			return false;
		}
		
		who =  findFirstMatchedItem(PATTERN_WHO, record);
		detail = record; 
		
		return true;
	}
	
	private String findFirstMatchedItem(Pattern pat, String source) {
		Matcher m = pat.matcher(source);
		if(m.find()) {
			return m.group(1).trim();
		}
		
		return null;
	}
	
	private String whenCreated() {
		if(EmptyUtil.isNullOrEmpty(detail)) {
			return null;
		}
		
		String when = findFirstMatchedItem(PATTERN_WHEN, detail);
		
		return when;
	}
	
	@Override
	public int compareTo(SmartDetail who) {
		String whenA = whenCreated();
		String whenB = who.whenCreated();
		if(whenA == null && whenB == null) {
			return 0;
		}
		
		if(whenA != null && whenB == null) {
			return -1;
		}
		
		if(whenA == null && whenB != null) {
			return 1;
		}
		
		return whenA.compareTo(whenB);
	}

	@Override
	public String toString() {
		return "SmartDetail [who=" + who + ", detail=" + detail + "]";
	}
}