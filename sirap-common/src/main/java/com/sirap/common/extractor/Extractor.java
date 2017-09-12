package com.sirap.common.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.WebReader;
import com.sirap.basic.util.XCodeUtil;

public abstract class Extractor<T extends MexItem> {

	private String url;
	private String charset = Konstants.CODE_UTF8;
	
	protected boolean printFetching = false;
	protected boolean printExceptionIfNeeded = true;
	protected boolean isAllBeingWell = true;

	protected String source;
	protected List<String> sourceList;
	protected T mexItem;
	protected List<T> mexItems = new ArrayList<>();
	protected List<Object> objItems = new ArrayList<>();
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
		if(printFetching) {
			String temp = "";
			if(isMethodPost) {
				temp = ", POST: " + (requestParams != null ? requestParams : "zero param."); 
			}
			
			C.pl("Fetching... " + target + temp);
		}
		
		WebReader xiu = new WebReader(target, charset, true);
		xiu.setMethodPost(isMethodPost);
		xiu.setRequestParams(requestParams);

		source = xiu.readIntoString();
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

	public List<T> getMexItems() {
		return mexItems;
	}

	public T getMexItem() {
		return mexItem;
	}
	
	public List<Object> getObjectItems() {
		return objItems;
	}

	protected abstract void parseContent();

	public void list() {
		if(!EmptyUtil.isNullOrEmpty(mexItems)) {
			for(Object obj:mexItems) {
				C.pl(obj);
			}
			C.pl("size:" + mexItems.size());
		} else {
			listObjectItems();
		}
	}
	
	public void listObjectItems() {
		for(Object obj:objItems) {
			C.pl(obj);
		}
		C.pl("size:" + objItems.size());
	}
	
	public boolean isAllBeingWell() {
		return isAllBeingWell;
	}
	
	public String removeHttpStuff(String source) {
		String temp = HtmlUtil.removeHttpTag(source);
		temp = temp.replaceAll("&nbsp;", "");
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
