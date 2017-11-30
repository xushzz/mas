package com.sirap.common.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.WebReader;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.XXXUtil;

public abstract class Extractor<T extends Object> {

	private String url;
	private String charset = Konstants.CODE_UTF8;
	
	protected boolean printFetching = false;
	protected boolean printExceptionIfNeeded = true;
	protected boolean isAllBeingWell = true;
	protected boolean readIntoSourceList = false;

	protected String source;
	protected List<String> sourceList;
	protected T item;
	protected List<T> mexItems = new ArrayList<>();
	private boolean isMethodPost;
	private String requestParams;
	
	public static String encodeURLParam(String param) {
		return XCodeUtil.urlEncodeUTF8(param);
	}
	
	public static String decodeURLParam(String param) {
		return XCodeUtil.urlDecodeUTF8(param);
	}
	
	protected final Matcher createMatcher(String regex) {
		return createMatcher(regex, source);
	}
	
	protected final Matcher createMatcher(String regex, String content) {
		return StrUtil.createMatcher(regex, content);
	}
	
	public Extractor<T> process() {
		readSource();
		
		if(source != null || sourceList != null) {
			parseContent();
			if(!isAllBeingWell) {
				C.pl2("Not cool, wrong web content");
			}
		} else {
			isAllBeingWell = false;
		}
		
		return this;
	}
	
	protected void readSource() {
		String target = getUrl();
		
		XXXUtil.nullCheck(target, "url");
		if(!StrUtil.startsWith(target, "http")) {
			if(printFetching) {
				C.pl("Reading... " + target);
			}
			if(readIntoSourceList) {
				sourceList = IOUtil.readFileIntoList(target, charset);
			} else {
				source = IOUtil.readFileWithLineSeparator(target, "", charset);
			}
			
			return;
		}
		
		if(printFetching) {
			String temp = target;
			if(requestParams != null) {
				if(StrUtil.contains(temp, "?")) {
					temp += "&" + requestParams;
				} else {
					temp += "?" + requestParams;
				}
			}
			
			if(isMethodPost) {
				temp += " $+post"; 
			}
			
			C.pl("Fetching... " + temp);
		}
		
		WebReader xiu = new WebReader(target, charset);
		xiu.setMethodPost(isMethodPost);
		xiu.setRequestParams(requestParams);

		try {
			if(readIntoSourceList) {
				sourceList = xiu.readIntoList();
			} else {
				source = xiu.readIntoString();
			}
		} catch (MexException ex) {
			if(printExceptionIfNeeded) {
				C.pl(ex);
			} else {
				throw ex;
			}
		}
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void useCharset(String charset) {
		this.charset = charset;
	}
	
	public void useUTF8() {
		this.charset = Konstants.CODE_UTF8;
	}
	
	public void useGBK() {
		this.charset = Konstants.CODE_GBK;
	}

	public boolean isReady() {
		return getUrl() != null && getUrl().length() > 10;
	}

	public List<T> getItems() {
		return mexItems;
	}

	public T getItem() {
		return item;
	}

	protected abstract void parseContent();
		
	public boolean isAllBeingWell() {
		return isAllBeingWell;
	}
	
	public static String getPrettyText(String source) {
		if(EmptyUtil.isNullOrEmpty(source)) {
			return source;
		}
		
		String temp = source;
		temp = HtmlUtil.removeComment(temp);
		temp = HtmlUtil.removeHttpTag(temp);
		temp = HtmlUtil.replaceRawUnicode(temp);
		temp = HtmlUtil.replaceHtmlEntities(temp);
		temp = StrUtil.reduceMultipleSpacesToOne(temp);
		temp = temp.trim();
		
		return temp;
	}

	public boolean isMethodPost() {
		return isMethodPost;
	}

	public void setMethodPost(boolean isMethodPost) {
		this.isMethodPost = isMethodPost;
	}

	public String getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(String requestParams) {
		this.requestParams = requestParams;
	}
}
