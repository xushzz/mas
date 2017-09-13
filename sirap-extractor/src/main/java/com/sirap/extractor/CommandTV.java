package com.sirap.extractor;


import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.impl.CCTVProgramExtractor;
import com.sirap.extractor.impl.MMKProgramExtractor;
import com.sirap.extractor.manager.CCTVManager;
import com.sirap.extractor.manager.MMKManager;

public class CommandTV extends CommandBase {

	private static final String KEY_CCTV = "cctv";
	private static final String KEY_MANMANKAN = "kan";

	{
//		helpMeanings.put("nongli.url", ChinaCalendarExtractor.URL_TEMPLATE);
	}
	
	public boolean handle() {
		if(is(KEY_CCTV)) {
			export(CCTVManager.g().allChannels());
			
			return true;
		}

		if(is(KEY_CCTV + KEY_2DOTS)) {
			export(CCTVManager.g().currentProgrammesInAllChannels());
			
			return true;
		}
		
		regex = KEY_CCTV + "(" + StrUtil.connect(CCTVManager.g().allChannels(), "|") + ")(|\\s+\\d{1,8})";
		params = parseParams(regex);
		if(params != null) {
			String channel = params[0];
			String date = params[1];
			if(date.isEmpty()) {
				date = DateUtil.displayNow(DateUtil.DATE_TIGHT);
			} else {
				date = DateUtil.wrapTightYMD(date);
			}
			
			String apiId = CCTVManager.g().findApiIdByName(channel);
			Extractor<MexObject> mike = new CCTVProgramExtractor(apiId, date);
			mike.process();
			export(mike.getMexItems());
			
			return true;
		}

		if(is(KEY_MANMANKAN + KEY_2DOTS)) {
			export(MMKManager.g().allChannels());
			
			return true;
		}
		
		regex = KEY_MANMANKAN + "(\\d{1,3})(|\\s+[1-7])";
		params = parseParams(regex);
		if(params != null) {
			int channel = Integer.parseInt(params[0]);
			XXXUtil.shouldBeTrue(channel >= 1 && channel <= 230);
			
			int dayOfWeek = params[1].isEmpty() ? DateUtil.getDayOfWeek() : Integer.parseInt(params[1]);
			String cnDayOfWeek = StrUtil.split("yi,er,san,si,wu,liu,ri").get(dayOfWeek - 1);
				
			Extractor<MexObject> mike = new MMKProgramExtractor(channel, cnDayOfWeek);
			mike.process();
			export(mike.getMexItems());
			
			return true;
		}
		
		return false;
	}
}
