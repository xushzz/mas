package com.sirap.extractor;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.NameRankItem;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.RandomUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.MexItemsFetcher;
import com.sirap.extractor.manager.Extractors;

public class CommandNames extends CommandBase {

	private static final String KEY_JAPANESE_NAME = "ono";
	private static final String KEY_RANDOM_NAME = "name";
	private static final String KEY_NAME_SEARCH = "nas";

	@Override
	public boolean handle() throws Exception {

		flag = searchAndProcess(KEY_JAPANESE_NAME, new MexItemsFetcher<MexItem>() {
			@Override
			public void handle(List<MexItem> items) {
				exportMatrix(items);
			}
			
			@Override
			public List<MexItem> body() {
				return toMexItems(Extractors.fetchJapaneseNames());
			}
		});
		if(flag) return true;
		
		params = parseParams(KEY_RANDOM_NAME + "(|\\d{1,3})(|\\s[a-z&\\|]+)");
		if(params != null) {
			int size = params[0].isEmpty() ? 1 : Integer.parseInt(params[0]);
			String criteria = params[1];
			int topK = OptionUtil.readIntegerPRI(options, "k", 100);
			if(criteria.isEmpty()) {
				export(RandomUtil.names(size, topK));
			} else {
				export(RandomUtil.names(size, criteria, topK));
			}
			
			return true;
		}
		
		flag = searchAndProcess(KEY_NAME_SEARCH, new MexItemsFetcher<MexItem>() {
			@Override
			public void handle(List<MexItem> items) {
				exportMatrix(items);
			}
			
			@Override
			public List<MexItem> body() {
				List<NameRankItem> list = Lists.newArrayList();
				if(OptionUtil.readBooleanPRI(options, "m", true)) {
					list.addAll(RandomUtil.fetchEnglishNamesByGender(true, 0));
				}
				
				if(OptionUtil.readBooleanPRI(options, "f", true)) {
					list.addAll(RandomUtil.fetchEnglishNamesByGender(false, 0));
				}

				Boolean byRank = OptionUtil.readBoolean(options, "r");
				if(byRank != null) {
					Collections.sort(list);
					if(!byRank) {
						Collections.reverse(list);
					}
				}

				return toMexItems(list);
			}
		});
		if(flag) return true;
		
		return false;
	}
}