package com.sirap.extractor;


import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.impl.China24JieqiExtractor;
import com.sirap.extractor.impl.ChinaCalendarExtractor;

public class CommandNongli extends CommandBase {

	private static final String KEY_IN_GONGLI_NONGLI = "(g|n)\\d{8}";
	private static final String KEY_IN_GONGLI_TODAY = "gnow";
	private static final String KEY_24_JIEQI = "jieqi";

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
			Extractor<MexedObject> mike = new China24JieqiExtractor(year);
			mike.process();
			export(mike.getMexItems());
			
			return true;
		}
		
		if(is(KEY_24_JIEQI)) {
			Extractor<MexedObject> mike = new China24JieqiExtractor();
			mike.process();
			export(mike.getMexItems());
			
			return true;
		}
		
		return false;
	}
}
