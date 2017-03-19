package com.sirap.common.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class SiteSearchEngine extends MexItem {
	private String prefix;
	private String urlTemplate;
	private String motto = "";
	
	public static final String REGEX_HTTP_QUERY = "https?://.{4,}.*?\\{0\\}.*?";
	
	public SiteSearchEngine() {
		
	}
	
	public SiteSearchEngine(String prefix, String urlTemplate, String motto) {
		this.prefix = prefix;
		this.urlTemplate = urlTemplate;
		this.motto = motto;
	}
	
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getUrlTemplate() {
		return urlTemplate;
	}
	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}
	public String getMotto() {
		return motto;
	}
	public void setMotto(String motto) {
		this.motto = motto;
	}
	
	public boolean parse(String source) {
		String[] info = source.split(">");
		if(info.length < 2) {
			return false;
		}
		
		String url = info[1].trim();
		if(!StrUtil.isRegexMatched(REGEX_HTTP_QUERY, url)) {
			return false;
		}
		
		setPrefix(info[0].trim());
		setUrlTemplate(url);
		if(info.length > 2) {
			setMotto(info[2].trim());
		}
		
		return true;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(prefix).append("\t");
		sb.append(urlTemplate).append("\t");
		sb.append(motto);
		
		return sb.toString(); 
	}
}
