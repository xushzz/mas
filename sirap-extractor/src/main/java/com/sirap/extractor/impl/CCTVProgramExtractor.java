package com.sirap.extractor.impl;

import java.util.regex.Matcher;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.common.extractor.Extractor;

public class CCTVProgramExtractor extends Extractor<MexObject> {
	public static final String TEMP = "http://api.cntv.cn/epg/epginfo?serviceId=tvcctv&c=cctv{0}&d={1}";
	
	public CCTVProgramExtractor(String channel, String yyyyhhmmDate) {
		printFetching = true;
		String url = StrUtil.occupy(TEMP, channel, yyyyhhmmDate);
		setUrl(url);
	}

	@Override
	protected void parseContent() {
		String regex = "\\{\"t\":\"([^\"]+)\",[^\\{\\}]+,\"showTime\":\"([^\"]+)\",[^\\{\\}]+\\}";
		Matcher ma = createMatcher(regex);
		while(ma.find()) {
			String time = ma.group(2);
			String what = ma.group(1);
			String temp = XCodeUtil.replaceHexChars(what, Konstants.CODE_UNICODE).replace("\\", "");
			MexObject mo = new MexObject(time + " " + temp);
			mexItems.add(mo);
		}
	}
}
