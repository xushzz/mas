package com.sirap.extractor;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.thread.MasterItemOriented;
import com.sirap.basic.thread.WorkerItemOriented;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.extractor.impl.China24JieqiExtractor;
import com.sirap.extractor.impl.ChinaCalendarExtractor;
import com.sirap.extractor.impl.ChinaCalendarTextExtractor;
import com.sirap.extractor.impl.ChinaJieriExtractor;

public class CommandNongli extends CommandBase {

	private static final String KEY_IN_GONGLI_TODAY = "gnow";
	private static final String KEY_24_JIEQI = "jieqi";
	private static final String KEY_CHINA_JIERI = "jieri";
	private static final String KEY_DAYS_OF_YEAR = "days";

	{
		helpMeanings.put("nongli.url", ChinaCalendarExtractor.URL_TEMPLATE);
		helpMeanings.put("24jieqi.url", China24JieqiExtractor.HOMEPAGE);
	}
	
	public boolean handle() {
		params = parseParams(KEY_DAYS_OF_YEAR + "(\\d{4})(|-(\\d{4}))");
		if(params != null) {
			int start = Integer.parseInt(params[0]);
			int end = start;
			if(params[2] != null) {
				end = Integer.parseInt(params[2]);
			}
			List<String> regularDays = Lists.newArrayList();
			boolean showNongli = OptionUtil.readBooleanPRI(options, "nong", false);
			for(int k = start; k <= end; k++) {
				regularDays.addAll(DateUtil.getAllDaysByYear(k));
			}
			if(showNongli) {
				List<String> nongliDays = fetchAllChinaCalendarDays(regularDays);
				export(nongliDays);
			} else {
				export(regularDays);
			}
		}
		
		String gong = ChinaCalendarTextExtractor.FLAG_GONGLI;
		String nong = ChinaCalendarTextExtractor.FLAG_NONGLI;
		String leap = ChinaCalendarTextExtractor.FLAG_LEAP;
		regex = "(" + gong + "|" + nong + ")(\\d{4})(\\d{0,2})(\\d{0,2})(" + leap + "?)";
		params = parseParams(regex);
		if(params != null) {
			if(OptionUtil.readBooleanPRI(options, "old", false)) {
				ChinaCalendarExtractor mike = new ChinaCalendarExtractor(command);
				export(mike.process().getItem());
			} else {
				boolean isGongli = StrUtil.equals(gong, params[0]);
				String year = params[1];
				String month = params[2];
				String day = params[3];
				boolean isLeap = !EmptyUtil.isNullOrEmpty(params[4]);

				String source = g().getUserValueOf("nongli.source");
				if(StrUtil.equals(params[0], gong)) {
					Extractor<MexItem> mike = new ChinaCalendarTextExtractor(source, year, isGongli, year, month, day, isLeap);
					export(mike.process().getItems());
				} else {
					List<MexItem> items = Lists.newArrayList();
					Extractor<MexItem> mike = new ChinaCalendarTextExtractor(source, year, isGongli, year, month, day, isLeap);
					items.addAll(mike.process().getItems());
					String nextYear = Integer.parseInt(params[1]) + 1 + "";
					mike = new ChinaCalendarTextExtractor(source, nextYear, isGongli, year, month, day, isLeap);
					items.addAll(mike.process().getItems());
					
					export(items);
				}
			}
			
			return true;
		}
		
		if(isIn(KEY_IN_GONGLI_TODAY)) {
			Date now = new Date();
			String year = DateUtil.displayDate(now, "yyyy");
			String month = DateUtil.displayDate(now, "MM");
			String day = DateUtil.displayDate(now, "dd");
			boolean fetchOld = OptionUtil.readBooleanPRI(options, "old", false);
			if(fetchOld) {
				ChinaCalendarExtractor mike = new ChinaCalendarExtractor("g" + year + month + day);
				export(mike.process().getItem());
			} else {
				String source = g().getUserValueOf("nongli.source");
				Extractor<MexItem> mike = new ChinaCalendarTextExtractor(source, year, true, year, month, day, false);
				export(mike.process().getItems());
			}
			
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
				export(CollUtil.filter(items, criteria));
			}
			
			return true;
		}
		
		return false;
	}
	
	/***
	 * 
	 * @param regularDays, like "20101012"
	 * @return
	 */
	private List<String> fetchAllChinaCalendarDays(List<String> regularDays) {
		MasterItemOriented<String> george = new MasterItemOriented<>(regularDays, new WorkerItemOriented<String>() {

			@Override
			public String process(String yyyyMMdd) {
				int count = queue.size() + 1;
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "async dealing...", yyyyMMdd);
				ChinaCalendarExtractor mike = new ChinaCalendarExtractor("g" + yyyyMMdd);
				String value = mike.process().getItem().toString();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "async done", yyyyMMdd);
				return value;
			}
			
		});
		
		List<String> values = george.getValidStringResults();
		Collections.sort(values);

		return values;
	}

}
