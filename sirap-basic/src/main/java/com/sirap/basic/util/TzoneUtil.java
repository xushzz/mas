package com.sirap.basic.util;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.ValuesItem;

public class TzoneUtil {
	
	public static List<List> namesAndMore(String criteria, String localestrs, boolean showOffset, boolean showTime) {
		List<Locale> lots = LocaleUtil.ofs(localestrs);
		String[] ids = TimeZone.getAvailableIDs();
		List<List> data = Lists.newArrayList();
		Date date = new Date();
		for(String id : ids) {
			ValuesItem vi = new ValuesItem();
			TimeZone zone = TimeZone.getTimeZone(id);
			vi.add(id);
			for(Locale lot : lots) {
				vi.add(zone.getDisplayName(lot));
			}
			if(showOffset) {
				vi.add(DateUtil.tzoneOffsetInHour(id));
			}
			if(showTime) {
				vi.add(DateUtil.strOf(date, DateUtil.HOUR_Min_AM_WEEK_DATE, id, null));
			}
			if(EmptyUtil.isNullOrEmpty(criteria) || vi.isMexMatched(criteria)) {
				data.add(vi.getValues());
			}
		}

		return data;
	}
}
