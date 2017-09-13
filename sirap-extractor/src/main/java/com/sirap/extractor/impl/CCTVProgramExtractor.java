package com.sirap.extractor.impl;

import java.util.regex.Matcher;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.common.extractor.Extractor;

public class CCTVProgramExtractor extends Extractor<MexObject> {
	public static final String TEMP = "http://api.cntv.cn/epg/epginfo?serviceId=tvcctv&c=cctv{0}&d={1}";
	
	public CCTVProgramExtractor(String apiId, String yyyyhhmmDate, boolean printFetching) {
		this.printFetching = printFetching;
		String url = StrUtil.occupy(TEMP, apiId, yyyyhhmmDate);
		setUrl(url);
	}
	
	public CCTVProgramExtractor(String apiId, String yyyyhhmmDate) {
		printFetching = true;
		String url = StrUtil.occupy(TEMP, apiId, yyyyhhmmDate);
		setUrl(url);
	}
	
	@Override
	protected void parseContent() {
		String regexAliveStart = "\"liveSt\":(\\d+)";
		String aliveStart = StrUtil.findFirstMatchedItem(regexAliveStart, source);
		
		String regex = "\\{\"t\":\"([^\"]+)\",\"st\":(\\d+),[^\\{\\}]+,\"showTime\":\"([^\"]+)\",[^\\{\\}]+\\}";
		Matcher ma = createMatcher(regex);
		while(ma.find()) {
			String temp = ma.group(1);
			String what = XCodeUtil.replaceHexChars(temp, Konstants.CODE_UNICODE).replace("\\", "");
			String start = ma.group(2);
			String time = ma.group(3);
			String prefix = " ";
			boolean isAlive = StrUtil.equals(start, aliveStart);
			if(isAlive) {
				prefix = "*";
			}
			
			MexObject mo = new MexObject(prefix + time + " " + what);
			if(isAlive) {
				mexItem = mo;
			}
			mexItems.add(mo);
		}
	}
}
