package com.sirap.common.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.MexedMap;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.common.CommonHelper;
import com.sirap.common.domain.TZRecord;
import com.sirap.common.framework.SimpleKonfig;

public class TimeZoneManager {

	private static TimeZoneManager instance;
	private static List<TZRecord> ALL_RECORDS;
	
	public static TimeZoneManager g() {
		if(instance == null) {
			instance = new TimeZoneManager();
		}
		init();
		
		return instance;
	}
	
	private static void init() {
		ALL_RECORDS = new ArrayList<TZRecord>();
		String[] idArr = TimeZone.getAvailableIDs();
		for(int i = 0; i <idArr.length; i++) {
			String id = idArr[i];
			int diff = DateUtil.getTimeZoneDiff(id);
			ALL_RECORDS.add(new TZRecord(id, diff));
		}
		
		List<TZRecord> extras = readExtraTimezones();
		ALL_RECORDS.addAll(extras);
	}
	
	public List<TZRecord> getTZRecordsById(String criteria) {
		if(Konstants.KEY_DOT_AS_CRITERIA.equals(criteria)) {
			return ALL_RECORDS;
		}
		
		MexFilter<TZRecord> filter = new MexFilter<TZRecord>(criteria, ALL_RECORDS);
		List<TZRecord> items = filter.process();
		
		return items;
	}
	
	public List<TZRecord> getTimeZones(String criteria, Locale locale) {
		return getTimeZones(criteria, locale, true);
	}
	
	public List<TZRecord> getTimeZones(String criteria, Locale locale, boolean displayTime) {
		List<TZRecord> items = new ArrayList<>();
		if(Konstants.KEY_DOT_AS_CRITERIA.equals(criteria)) {
			items = ALL_RECORDS;
		} else {
			MexFilter<TZRecord> filter = new MexFilter<TZRecord>(criteria, ALL_RECORDS);
			items = filter.process();
		}
		
		if(items.isEmpty()) {
			return null;
		}

		Date worldTime = null;
		
		if(displayTime) {
			worldTime = CommonHelper.getWorldTime();
			if(worldTime == null) {
				C.pl("UTC unavailable");
			}
		}

		for(TZRecord item : items) {
			if(worldTime != null) {
				item.setDatetime(DateUtil.hourDiff(worldTime, item.getDiff()));
			}
		}
		
		return items;
	}
	
	@SuppressWarnings("unchecked")
	private static List<TZRecord> readExtraTimezones() {
		MexedMap mm = SimpleKonfig.g().getProps();
		if(mm == null) {
			return Collections.EMPTY_LIST;
		}
		
		List<TZRecord> extras = new ArrayList<TZRecord>();
		List<String> items = SimpleKonfig.g().getValuesByKeyword("tz.");
		for(String item : items) {
			String[] arr= item.split(",");
			if(arr.length != 2) {
				continue;
			}
			String zone = arr[0].trim();
			Integer value = MathUtil.toInteger(arr[1].trim());
			if(value == null) {
				continue;
			}
			
			extras.add(new TZRecord(zone, value));
		}

		return extras;
	}
	
	public int maxLenOfId(List<TZRecord> records) {
		int max = 0;
		for(TZRecord item:records) {
			if(item == null) {
				continue;
			}
			int len = item.getId().length();
			if(len > max) {
				max = len;
			}
		}
		
		return max;
	}
}
