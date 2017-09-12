package com.sirap.extractor.impl;

import java.util.regex.Matcher;

import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.domain.MeituLassItem;
import com.sirap.extractor.domain.MeituOrgItem;
import com.sirap.extractor.konstants.MeituKonstants;

public class MeituLassExtractor extends Extractor<MeituLassItem> {

	private MeituOrgItem orgItem;
	
	public MeituLassExtractor(MeituOrgItem orgItem) {
		this.orgItem = orgItem;
	}
	
	public MeituLassExtractor(String orgPath) {
		orgItem = new MeituOrgItem(orgPath);
	}
	
	@Override
	public String getUrl() {
		useUTF8();
		printFetching = true;
		XXXUtil.nullCheckOnly(orgItem.getPath());
		String fullUrl = StrUtil.useSlash(MeituKonstants.HOME_PAGE, orgItem.getPath());
		return fullUrl;
	}
	
	@Override
	protected void parseContent() {
		String keyWord = XCodeUtil.urlDecodeUTF8("%E6%A8%A1%E7%89%B9%EF%BC%9A");
		String regex = "<p>" + keyWord + "(.+?)</p>";
		Matcher m = createMatcher(regex);
		while(m.find()) {
			String what = m.group(1);
			String temp = "<a href=\"{0}/([^/]+/[^/]+)/\" target=\"_blank\">([^<>]+)</a>";
			String regex2 = StrUtil.occupy(temp, MeituKonstants.HOME_PAGE);
			Matcher m2 = createMatcher(regex2, what);
			MeituLassItem lazy = new MeituLassItem(what);
			if(m2.find()) {
				lazy.setPath(m2.group(1));
				lazy.setName(m2.group(2));
			}
			lazy.setOrg(orgItem);
			mexItems.add(lazy);
		}
	}
}