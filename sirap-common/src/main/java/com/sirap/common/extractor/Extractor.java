package com.sirap.common.extractor;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.tool.C;
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

	private static String[] PARAM_PLAIN = {"+"," ","/","?","#","&","=",":","'"};
	private static String[] PARAM_ENCODED = {"%2B","+","%2F","%3F","%23","%26","%3D","%3A","%27"};
	
	public static String encodeURLParam(String param) {
		if(param == null) {
			return null;
		}

		String temp = param;
		for(int i = 0; i < PARAM_PLAIN.length; i++) {
			temp = temp.replace(PARAM_PLAIN[i], PARAM_ENCODED[i]);
		}
		
		return temp;
	}
	
	public static String decodeURLParam(String param) {
		if(param == null) {
			return null;
		}
		
		String temp = param;
		for(int i = 0; i < PARAM_PLAIN.length; i++) {
			temp = temp.replace(PARAM_ENCODED[i], PARAM_PLAIN[i]);
		}
		
		return temp;
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
}
