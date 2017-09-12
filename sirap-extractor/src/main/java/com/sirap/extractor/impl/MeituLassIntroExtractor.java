package com.sirap.extractor.impl;

import java.util.regex.Matcher;

import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.domain.MeituLassItem;
import com.sirap.extractor.konstants.MeituKonstants;

public class MeituLassIntroExtractor extends Extractor<MeituLassItem> {

	private String lassPath;
	public MeituLassIntroExtractor(String lassPath) {
		this.lassPath = lassPath;
	}
	
	@Override
	public String getUrl() {
		useUTF8();
		printFetching = true;
		String fullUrl = StrUtil.useSlash(MeituKonstants.HOME_PAGE, lassPath);
		return fullUrl;
	}
	
	@Override
	protected void parseContent() {
		String regex = "</div>\\s*<h1>([^<>]+)</h1>(.*?)</p>\\s*</div>";
		Matcher m = createMatcher(regex);
		
		if(m.find()) {
			StringBuffer sb = new StringBuffer();
			sb.append(m.group(1).replaceAll("\\s", "")).append(" ");
			String what = m.group(2);
			String regex2 = "<span>[^<>]+</span>([^<>]+)";
			Matcher m2 = createMatcher(regex2, what);
			while(m2.find()) {
				sb.append(m2.group(1).replaceAll("\\s", "")).append(" ");
			}
			MeituLassItem item = new MeituLassItem();
			item.setPath(lassPath);
			item.setIntro(sb.toString());
			mexItems.add(item);
		}
	}
}