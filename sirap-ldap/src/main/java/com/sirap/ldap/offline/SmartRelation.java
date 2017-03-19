package com.sirap.ldap.offline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.EmptyUtil;

@SuppressWarnings("serial")
public class SmartRelation extends MexItem {
	private String who;
	private String supers;
	private String unders;
	
	private static Pattern PATTERN_WHO = Pattern.compile("([^\\$#]+)", Pattern.CASE_INSENSITIVE);
	private static Pattern PATTERN_SUPERS = Pattern.compile("\\$\\[(.+?)\\]", Pattern.CASE_INSENSITIVE);
	private static Pattern PATTERN_UNDERS = Pattern.compile("#\\[(.+?)\\]", Pattern.CASE_INSENSITIVE);
	
	@Override
	public boolean parse(String record) {
		if(EmptyUtil.isNullOrEmptyOrBlank(record)) {
			return false;
		}
		
		who =  findFirstMatchedItem(PATTERN_WHO, record);
		supers = findFirstMatchedItem(PATTERN_SUPERS, record);
		unders = findFirstMatchedItem(PATTERN_UNDERS, record);
		
		return true;
	}
	
	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public String getSupers() {
		return supers;
	}

	public void setSupers(String supers) {
		this.supers = supers;
	}

	public String getUnders() {
		return unders;
	}

	public void setUnders(String unders) {
		this.unders = unders;
	}

	private String findFirstMatchedItem(Pattern pat, String source) {
		Matcher m = pat.matcher(source);
		if(m.find()) {
			return m.group(1).trim();
		}
		
		return null;
	}

	@Override
	public String toString() {
		return "SmartItem [who=" + who + ", supers=" + supers + ", unders="
				+ unders + "]";
	}
	
	
}