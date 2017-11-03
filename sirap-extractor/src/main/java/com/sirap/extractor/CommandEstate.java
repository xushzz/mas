package com.sirap.extractor;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.extractor.manager.Extractors;

public class CommandEstate extends CommandBase {

	private static final String KEY_ANJUKE = "anju";

	public boolean handle() {
		regex = KEY_ANJUKE + "\\s([^\\.]*)\\.([^\\.\\d]*)\\.(|\\d+)";
		params = parseParams(regex);
		if(params != null) {
			String city = params[0];
			if(EmptyUtil.isNullOrEmpty(city)) {
				city = g().getUserValueOf("anju.city", "nanning");
			}
			String town = params[1];
			int maxPage = MathUtil.toInteger(params[2], 1);
			List<MexItem> allItems = Lists.newArrayList();
			for(int k = 1; k <= maxPage; k++) {
				List<MexItem> items = Extractors.fetchAnjukeHouse(city, town, k);
				if(EmptyUtil.isNullOrEmpty(items)) {
					break;
				}
				allItems.addAll(items);
			}
			
			export(allItems);
			
			return true;
		}

		return false;
	}
}
