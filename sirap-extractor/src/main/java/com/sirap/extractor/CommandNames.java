package com.sirap.extractor;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.extractor.domain.NameRankItem;
import com.sirap.extractor.manager.Extractors;

public class CommandNames extends CommandBase {

	private static final String KEY_JAPANESE_NAME = "ono";
	private static final String KEY_NAME_MALE = "bbb";
	private static final String KEY_NAME_FEMALE = "ggg";

	@Override
	public boolean handle() throws Exception {

		solo = parseSoloParam(KEY_JAPANESE_NAME + "\\s(.+)");
		if(solo != null) {
			export2(Extractors.fetchJapaneseNames(), solo);
			
			return true;
		}
		
		params = parseParams("(" + KEY_NAME_MALE + "|" + KEY_NAME_FEMALE + ")(|\\s.+)");
		if(params != null) {
			Boolean byRank = OptionUtil.readBoolean(options, "r");
			String criteria = params[1];
			Integer rank = null;
			int diff = 2;
			String[] rankAndDiff = StrUtil.parseParams("(\\d+)(|\\s\\d+)", criteria);
			if(rankAndDiff != null) {
				rank = Integer.parseInt(rankAndDiff[0]);
				if(!rankAndDiff[1].isEmpty()) {
					diff  = Integer.parseInt(rankAndDiff[1]);
				}
			}
			if(rank != null) {
				byRank = true;
				List<String> ranks = adjacentOf(rank, diff);
				criteria = StrUtil.connect(ranks, "|");
			}
			
			boolean isMale = StrUtil.equals(KEY_NAME_MALE, params[0]);
			List<NameRankItem> list = Extractors.fetchEnglishNames(isMale, criteria);
			if(byRank != null) {
				Collections.sort(list);
				if(!byRank) {
					Collections.reverse(list);
				}
			}
			
			export(list);
			
			return true;
		}
		
		return false;
	}
	
	private List<String> adjacentOf(int rank, int diff) {
		List<String> items = Lists.newArrayList();
		int start = rank - diff;
		if(start < 0) {
			start = 0;
		}
		int end = rank + diff;
		for(int i = start; i <= end; i++) {
			items.add(i + "");
		}
		
		return items;
	}
}