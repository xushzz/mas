package com.sirap.basic.component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.HttpUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.WebReader;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.XXXUtil;

public abstract class Extractor<T extends Object> {

	private String url;
	private String charset = Konstants.CODE_UTF8;
	
	protected boolean printFetching;
	private boolean printExceptionIfNeeded = true;
	private boolean allBeingWell = true;
	private boolean intoList;
	private boolean methodPost;

	protected String source = "";
	protected List<String> sourceList = Lists.newArrayList();
	protected T item;
	protected List<T> mexItems = new ArrayList<>();
	
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
		fetch();
		parse();
		
		return this;
	}
	
	protected void fetch() {
		List<String> urls = getUrls();
		if(EmptyUtil.isNullOrEmpty(urls)) {
			urls = Lists.newArrayList();
			urls.add(getUrl());
		}
		
		for(String url: urls) {
			fetch(url);
		}
	}
	
	private void fetch(String url) {
		String target = url;
		
		XXXUtil.nullCheck(target, "url");
		if(!HttpUtil.isHttp(target)) {
			if(printFetching) {
				C.pl("Reading... " + target);
			}
			if(intoList) {
				sourceList.addAll(IOUtil.readLines(target, charset));
			} else {
				source += IOUtil.readString(target, charset);
			}
			
			return;
		}
		
		if(printFetching) {
			String temp = target;
			if(methodPost) {
				temp += " $+post"; 
			}
			
			C.pl("Fetching... " + temp);
		}
		
		WebReader xiu = new WebReader(target, charset);
		xiu.setMethodPost(methodPost);

		try {
			if(intoList) {
				sourceList.addAll(xiu.readIntoList());
			} else {
				source += xiu.readIntoString();
			}
		} catch (MexException ex) {
			if(printExceptionIfNeeded) {
				C.pl(ex);
			} else {
				throw ex;
			}
		}
	}
	
	public Extractor<T> setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public String getUrl() {
		return url;
	}
	
	public List<String> getUrls() {
		return null;
	}
	
	public Extractor<T> setCharset(String charset) {
		this.charset = charset;
		return this;
	}
	
	public Extractor<T> useUTF8() {
		this.charset = Konstants.CODE_UTF8;
		return this;
	}
	
	public Extractor<T> useList() {
		this.intoList = true;
		return this;
	}
	
	public Extractor<T> showFetching() {
		this.printFetching = true;
		return this;
	}
	
	public String useHttps(String webUrl) {
		return webUrl.replaceAll("^http:", "https:");
	}
	
	public Extractor<T> useGBK() {
		this.charset = Konstants.CODE_GBK;
		return this;
	}

	public List<T> getItems() {
		return mexItems;
	}

	public T getItem() {
		return item;
	}

	protected abstract void parse();
		
	public boolean isAllBeingWell() {
		return allBeingWell;
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
		return methodPost;
	}

	public Extractor<T> usePost() {
		this.methodPost = true;
		return this;
	}

	public Extractor<T> setPrintExceptionIfNeeded(boolean flag) {
		this.printExceptionIfNeeded = flag;
		return this;
	}

	public Extractor<T> setAllBeingWell(boolean flag) {
		this.allBeingWell = flag;
		return this;
	}
}
