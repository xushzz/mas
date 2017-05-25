package com.sirap.common.extractor;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;

public abstract class Extractor<T extends MexItem> {

	private String url;
	private String charset = Konstants.CODE_UTF8;
	
	protected boolean printFetching = false;
	protected boolean printExceptionIfNeeded = true;
	protected boolean isAllBeingWell = true;
	protected List<String> params = new ArrayList<String>();

	protected String source;
	protected List<String> sourceList;
	protected T mexItem;
	protected List<T> mexItems = new ArrayList<T>();
	protected List<Object> objItems = new ArrayList<Object>();
	
	public static String encodeURLParam(String param) {
		return XCodeUtil.urlEncodeUTF8(param);
	}
	
	public static String decodeURLParam(String param) {
		return XCodeUtil.urlDecodeUTF8(param);
	}
	
	public void process() {
		readSource();
		
		if(source != null || sourceList != null) {
			parseContent();
			if(!isAllBeingWell) {
				C.pl2("Not cool, wrong web content");
			}
		} else {
			isAllBeingWell = false;
		}
	}
	
	protected void readSource() {
		String target = getUrl();
		if(printFetching) {
			C.pl("Fetching... " + target);
		}
		source = IOUtil.readURL(target, charset, printExceptionIfNeeded);
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

	public void setParam(String param) {
		params.add(param);
	}

	public void setParams(List<String> params) {
		params.addAll(params);
	}
	
	public List<String> getParams() {
		return params;
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
}
