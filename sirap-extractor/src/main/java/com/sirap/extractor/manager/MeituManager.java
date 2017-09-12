package com.sirap.extractor.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.thread.MasterMexItemsOriented;
import com.sirap.basic.thread.WorkerMexItemsOritented;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.domain.MeituLassItem;
import com.sirap.extractor.domain.MeituOrgItem;
import com.sirap.extractor.impl.MeituImageLinksExtractor;
import com.sirap.extractor.impl.MeituLassExtractor;
import com.sirap.extractor.impl.MeituLassIntroExtractor;
import com.sirap.extractor.impl.MeituOrgsExtractor;
import com.sirap.extractor.konstants.MeituKonstants;

public class MeituManager {
	private static MeituManager instance;
	
	public static MeituManager g() {
		if(instance == null) {
			instance = new MeituManager();
		}
		
		return instance;
	}
	
	public List<MeituOrgItem> getAllOrgItems(boolean toExplode) {
		Extractor<MeituOrgItem> justin = new MeituOrgsExtractor(toExplode);
		justin.process();
		
		return justin.getMexItems();
	}
	
	public List<MexObject> explode(String base, int count) {
		List<MexObject> items = new ArrayList<>();
		int mod = count / MeituKonstants.ALBUMS_PER_PAGE;
		int remain = count % MeituKonstants.ALBUMS_PER_PAGE;
		int pages = remain > 0 ? mod + 1 : mod;
		String temp = "index_{0}.html";
		for(int i = 1; i < pages; i++) {
			String path = StrUtil.useSlash(base, StrUtil.occupy(temp, i));
			items.add(new MexObject(path));
		}
		
		return items;
	}
	
	public List<MeituLassItem> getAllLassItems() {
		List<MeituOrgItem> items = getAllOrgItems(true);
		MasterMexItemsOriented<MeituOrgItem, MeituLassItem> master = new MasterMexItemsOriented<MeituOrgItem, MeituLassItem>(items, new WorkerMexItemsOritented<MeituOrgItem, MeituLassItem>() {

			@Override
			public List<MeituLassItem> process(MeituOrgItem orgItem) {
				
				Extractor<MeituLassItem> justin = new MeituLassExtractor(orgItem);
				
				int count = countOfTasks - tasks.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", orgItem);
				justin.process();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetched.", "");
				
				return justin.getMexItems();
			}
			
		});
		
		master.sitAndWait();
		
		return master.getAllMexItems();
	}
	
	public List<MexObject> getImageLinks(List<MexObject> morePages) {
		MasterMexItemsOriented<MexObject, MexObject> master = new MasterMexItemsOriented<MexObject, MexObject>(morePages, new WorkerMexItemsOritented<MexObject, MexObject>() {

			@Override
			public List<MexObject> process(MexObject orgItem) {
				
				Extractor<MexObject> justin = new MeituImageLinksExtractor(orgItem.getString());
				
				int count = countOfTasks - tasks.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", orgItem);
				justin.process();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetched.", "");
				
				return justin.getMexItems();
			}
			
		});
		
		master.sitAndWait();
		
		return master.getAllMexItems();
	}
	
	public List<MeituLassItem> getAllLassIntros(List<MexObject> morePages) {
		MasterMexItemsOriented<MexObject, MeituLassItem> master = new MasterMexItemsOriented<MexObject, MeituLassItem>(morePages, new WorkerMexItemsOritented<MexObject, MeituLassItem>() {

			@Override
			public List<MeituLassItem> process(MexObject obj) {
				Extractor<MeituLassItem> justin = new MeituLassIntroExtractor(obj.toString());
				
				int count = countOfTasks - tasks.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", obj);
				justin.process();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetched.", "");
				
				return justin.getMexItems();
			}
			
		});
		
		master.sitAndWait();
		
		return master.getAllMexItems();
	}
	
	public List<MexObject> readLassPath(List<String> records) {
		List<MexObject> items = new ArrayList<>();
		Pattern p = Pattern.compile("(^t/\\d+)\\s");
		for(String record : records) {
			Matcher ma = p.matcher(record);
			if(ma.find()) {
				String path = ma.group(1);
				items.add(new MexObject(path));
			}
		}
		
		return items;
	}
}
