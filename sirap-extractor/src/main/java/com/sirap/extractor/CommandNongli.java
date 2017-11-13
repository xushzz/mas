package com.sirap.extractor;


import java.util.List;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.impl.China24JieqiExtractor;
import com.sirap.extractor.impl.ChinaCalendarExtractor;
import com.sirap.extractor.impl.ChinaJieriExtractor;

public class CommandNongli extends CommandBase {

	private static final String KEY_IN_GONGLI_NONGLI = "(g|n)\\d{8}";
	private static final String KEY_IN_GONGLI_TODAY = "gnow";
	private static final String KEY_24_JIEQI = "jieqi";
	private static final String KEY_CHINA_JIERI = "jieri";

	{
		helpMeanings.put("nongli.url", ChinaCalendarExtractor.URL_TEMPLATE);
		helpMeanings.put("24jieqi.url", China24JieqiExtractor.HOMEPAGE);
	}
	
	public boolean handle() {
		if(StrUtil.isRegexMatched(KEY_IN_GONGLI_NONGLI, command)) {
			ChinaCalendarExtractor mike = new ChinaCalendarExtractor(command);
			mike.process();
			export(mike.getCalendarInfo());
			
			return true;
		}
		
		if(isIn(KEY_IN_GONGLI_TODAY)) {
			String param = "g" + DateUtil.displayNow(DateUtil.DATE_TIGHT);
			ChinaCalendarExtractor mike = new ChinaCalendarExtractor(param);
			mike.process();
			export(mike.getCalendarInfo());

			return true;
		}
		
		String regex = KEY_24_JIEQI + "(20(17|18|19|20))";
		params = parseParams(regex);
		if(params != null) {
			String year = params[0];
			Extractor<MexObject> mike = new China24JieqiExtractor(year);
			mike.process();
			export(mike.getItems());
			
			return true;
		}
		
		if(is(KEY_24_JIEQI)) {
			Extractor<MexObject> mike = new China24JieqiExtractor();
			export(mike.process().getItems());
			
			return true;
		}
		
		params = parseParams(KEY_CHINA_JIERI + "(|\\s+(.+))");
		if(params != null) {
			Extractor<MexObject> mike = new ChinaJieriExtractor();
			List<MexObject> items = mike.process().getItems();
			boolean showAll = OptionUtil.readBooleanPRI(options, "all", false);
			if(showAll) {
				export(items);
			} else {
				String criteria = EmptyUtil.isNullOrEmpty(params[0]) ? DateUtil.displayNow("MM/") : params[1];
				export(CollectionUtil.filter(items, criteria));
			}
			
			return true;
		}
		
		return false;
	}
}
