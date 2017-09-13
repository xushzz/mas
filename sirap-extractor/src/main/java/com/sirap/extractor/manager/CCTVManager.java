package com.sirap.extractor.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.impl.CCTVProgramExtractor;

public class CCTVManager {
	private static CCTVManager instance;

	public static final String CH5P_1 = "5p";
	
	public static final HashMap<String, String> BOOKS = new HashMap<>();
	static {
		BOOKS.put("9", "jilu");
		BOOKS.put("14", "child");
		BOOKS.put(CH5P_1, "5plus");
	}
	
	public static CCTVManager g() {
		if(instance == null) {
			instance = new CCTVManager();
		}
		
		return instance;
	}
	
	public String findApiIdByName(String name) {
		String value = BOOKS.get(name);
		if(value == null) {
			value = name;
		}
		
		return value;
	}
	
	public List<String> allChannels() {
		int size = 15;
		List<String> items = new ArrayList<>();
		for(int i = 1; i <= size; i++) {
			items.add(i + "");
		}
		items.add(5, CH5P_1);

		return items;
	}
	
	public List<String> currentProgrammesInAllChannels() {
		List<String> channels = allChannels();
		String date = DateUtil.displayNow(DateUtil.DATE_TIGHT);
		List<String> items = new ArrayList<>();
		for(String channel : channels) {
			String apiId = CCTVManager.g().findApiIdByName(channel);
			Extractor<MexObject> mike = new CCTVProgramExtractor(apiId, date, false);
			mike.process();
			String lead = "CCTV" + StrUtil.extend(channel, 2, " ");
			items.add(lead + " " + mike.getMexItem());
		}
		
		return items;
	}
}
