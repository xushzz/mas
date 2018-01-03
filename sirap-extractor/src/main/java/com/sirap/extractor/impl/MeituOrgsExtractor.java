package com.sirap.extractor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.util.StrUtil;
import com.sirap.extractor.domain.MeituOrgItem;
import com.sirap.extractor.konstants.MeituKonstants;

public class MeituOrgsExtractor extends Extractor<MeituOrgItem> {

	private boolean toExplode;
	public MeituOrgsExtractor(boolean toExplode) {
		this.toExplode = toExplode;
	}
	private List<MeituOrgItem> explodeItems = new ArrayList<>();
	
	@Override
	public String getUrl() {
		useUTF8();
		printFetching = true;
		String fullUrl = StrUtil.useSlash(MeituKonstants.HOME_PAGE, "jigou");
		return fullUrl;
	}
	
	public List<MeituOrgItem> getExplodeItems() {
		return explodeItems;
	}

	@Override
	protected void parse() {
		String temp = "<li><a href=\"{0}/([^/]+/[^/]+)/\" target=\"_blank\">([^<>]+)</a><br/><span>(\\d+)[^<>]+</span>\\s*</li>";
		String regex = StrUtil.occupy(temp, MeituKonstants.HOME_PAGE);
		Matcher m = createMatcher(regex, source);
		while(m.find()) {
			String base = m.group(1);
			String name = m.group(2);
			int count = Integer.parseInt(m.group(3));
			if(toExplode) {
				mexItems.addAll(explode(name, base, count));
			} else {
				mexItems.add(new MeituOrgItem(name, base));
			}
		}
	}
	
	public List<MeituOrgItem> explode(String name, String base, int count) {
		List<MeituOrgItem> items = new ArrayList<>();
		items.add(new MeituOrgItem(name, base));
		int mod = count / MeituKonstants.ALBUMS_PER_PAGE;
		int remain = count % MeituKonstants.ALBUMS_PER_PAGE;
		int pages = remain > 0 ? mod + 1 : mod;
		String temp = "index_{0}.html";
		for(int i = 1; i < pages; i++) {
			String path = StrUtil.useSlash(base, StrUtil.occupy(temp, i));
			items.add(new MeituOrgItem(name, path));
		}
		
		return items;
	}
}