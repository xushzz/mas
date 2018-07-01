package com.sirap.extractor;


import java.util.Collections;
import java.util.List;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.common.framework.command.SizeInputAnalyzer;
import com.sirap.extractor.domain.MeijuRateItem;
import com.sirap.extractor.impl.CCTVProgramExtractor;
import com.sirap.extractor.impl.MMKProgramExtractor;
import com.sirap.extractor.manager.CCTVManager;
import com.sirap.extractor.manager.Extractors;
import com.sirap.extractor.manager.MMKManager;

public class CommandTV extends CommandBase {

	private static final String KEY_CCTV = "cctv";
	private static final String KEY_MANMANKAN = "kan";
	private static final String KEY_MEIJU = "meiju";

	{
//		helpMeanings.put("nongli.url", ChinaCalendarExtractor.URL_TEMPLATE);
	}
	
	public boolean handle() {
		if(is(KEY_CCTV)) {
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
			export(mike.getItems());
			
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
			export(mike.getItems());
			
			return true;
		}
		
		InputAnalyzer sean = new SizeInputAnalyzer(input);
		solo = StrUtil.parseParam(KEY_MEIJU + "(|\\s.+)", sean.getCommand());
		if(solo != null) {
			this.command = sean.getCommand();
			this.target = sean.getTarget();
			this.options = sean.getOptions();
			
			List<MeijuRateItem> list = Extractors.fetchTopMeijus();
			if(!solo.isEmpty()) {
				list = Colls.filter(list, solo);
			}

			Boolean byRate = OptionUtil.readBoolean(options, "r");
			if(byRate != null) {
				Collections.sort(list);
				if(!byRate) {
					Collections.reverse(list);
				}
			}
			
			String temp = "maxLen=" + maxLenOfName(list) + ",space=2";
			String mergedOptions = OptionUtil.mergeOptions(options, temp);
			export(list, mergedOptions);
			
			return true;
		}
		
		solo = parseParam("meis\\s(.+)");
		if(solo != null) {
			String param = solo;
			List<String> list = Extractors.fetchMeiju(param);
			export(list);
			
			return true;
		}
		
		return false;
	}
	
	private int maxLenOfName(List<MeijuRateItem> list) {
		int max = 0;
		for(MeijuRateItem item : list) {
			int len = StrUtil.countOfAscii(item.getName());
			if(len > max) {
				max = len;
			}
		}
		
		return max;
	}
}
