package com.sirap.extractor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sirap.basic.domain.MexObject;
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
			Set<MexObject> allItems = new LinkedHashSet<MexObject>();
			for(int k = 1; k <= maxPage; k++) {
				List<MexObject> items = Extractors.fetchAnjukeHouse(city, town, k);
				if(EmptyUtil.isNullOrEmpty(items)) {
					break;
				}
				
				int before = allItems.size();
				allItems.addAll(items);
				int after = allItems.size();
				if(after <= before) {
					break;
				}
			}
			
			export(new ArrayList<>(allItems));
			
			return true;
		}

		return false;
	}
}
