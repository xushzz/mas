package com.sirap.extractor;

import java.util.List;

import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.extractor.domain.SportsMatchItem;
import com.sirap.extractor.manager.Extractors;

public class CommandSports extends CommandBase {
	private static final String KEY_UEFA = "uefa";

	public boolean handle() {
		if(is(KEY_UEFA)) {
			List<SportsMatchItem> items = Extractors.fetchUefaChampionsSchedule();
			String today = DateUtil.displayNow("yyyy-MM-dd");
			List<SportsMatchItem> todayItems = CollectionUtil.filter(items, ">=" + today);
			export(CollectionUtil.top(todayItems, 16));
			
			return true;
		}
		
		singleParam = parseParam(KEY_UEFA + "\\s+(.+)");
		if(singleParam != null) {
			List<SportsMatchItem> items = Extractors.fetchUefaChampionsSchedule();
			export(CollectionUtil.filter(items, singleParam));
			return true;
		}
		
		if(is(KEY_UEFA + KEY_2DOTS)) {
			export(Extractors.fetchUefaChampionsSchedule());
			
			return true;
		}
		
		return false;
	}
}
