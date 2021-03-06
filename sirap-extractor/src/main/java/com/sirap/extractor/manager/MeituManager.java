package com.sirap.extractor.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.thread.MasterItemsOriented;
import com.sirap.basic.thread.WorkerItemsOriented;
import com.sirap.basic.util.StrUtil;
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
		
		return justin.getItems();
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
		MasterItemsOriented<MeituOrgItem, MeituLassItem> master = new MasterItemsOriented<MeituOrgItem, MeituLassItem>(items, new WorkerItemsOriented<MeituOrgItem, MeituLassItem>() {

			@Override
			public List<MeituLassItem> process(MeituOrgItem orgItem) {
				
				Extractor<MeituLassItem> justin = new MeituLassExtractor(orgItem);
				
				int count = countOfTasks - queue.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", orgItem);
				justin.process();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetched.", "");
				
				return justin.getItems();
			}
			
		});
		
		return master.getAllMexItems();
	}
	
	public List<MexObject> getImageLinks(List<MexObject> morePages) {
		MasterItemsOriented<MexObject, MexObject> master = new MasterItemsOriented<MexObject, MexObject>(morePages, new WorkerItemsOriented<MexObject, MexObject>() {

			@Override
			public List<MexObject> process(MexObject orgItem) {
				
				Extractor<MexObject> justin = new MeituImageLinksExtractor(orgItem.getString());
				
				int count = countOfTasks - queue.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", orgItem);
				justin.process();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetched.", "");
				
				return justin.getItems();
			}
			
		});
		
		return master.getAllMexItems();
	}
	
	public List<MeituLassItem> getAllLassIntros(List<MexObject> morePages) {
		MasterItemsOriented<MexObject, MeituLassItem> master = new MasterItemsOriented<MexObject, MeituLassItem>(morePages, new WorkerItemsOriented<MexObject, MeituLassItem>() {

			@Override
			public List<MeituLassItem> process(MexObject obj) {
				Extractor<MeituLassItem> justin = new MeituLassIntroExtractor(obj.toString());
				
				int count = countOfTasks - queue.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", obj);
				justin.process();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetched.", "");
				
				return justin.getItems();
			}
			
		});
		
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
