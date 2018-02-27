package com.sirap.geek.manager;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.geek.domain.DistrictItem;

public class GaodeManager {
	
	private static GaodeManager instance;
	
	public static final List<String> LEVELS = StrUtil.split("country,province,city,district,street");
	private static List<DistrictItem> ALL_DISTRICTS;
	
	private GaodeManager() {}
	
	public static GaodeManager g() {
		if(instance == null) {
			instance = new GaodeManager();
		}
		
		return instance;
	}
	
	private void readData(boolean mandatory) {
		if(EmptyUtil.isNullOrEmpty(ALL_DISTRICTS) || mandatory) {
			String location = SimpleKonfig.g().getUserValueOf("gaode.districts", GaodeUtils.GITEE_URL_CHINA_DISTRICTS);
			Extractor<DistrictItem> neymar = new Extractor<DistrictItem>() {

				public String getUrl() {
					showFetching().useUTF8().useList();
					return location;
				}
				
				@Override
				protected void parse() {
					int count = 0;
					for(String line : sourceList) {
						DistrictItem item = new DistrictItem();
						if(item.parse(line)) {
							mexItems.add(item);
						} else {
							if(count > 0) {
								C.pl("Unable to parse to DistrictItem: " + line);
							}
						}
						count++;
					}
				}
			};
				
			ALL_DISTRICTS = neymar.process().getItems();
			if(EmptyUtil.isNullOrEmpty(ALL_DISTRICTS)) {
				C.pl("Uncanny, still no valid by " + location);
			}
		}
	}

	public List<DistrictItem> getAllDistricts(boolean toRefresh) {
		readData(toRefresh);
		return ALL_DISTRICTS;
	}

	public List<DistrictItem> getLowerDistrictsOf(DistrictItem current, int nextKLevel) {
		List<DistrictItem> items = Lists.newArrayList(current);
		List<String> validLevels = current.nextKLevels(nextKLevel);
		if(EmptyUtil.isNullOrEmpty(validLevels)) {
			return items;
		}
		List<DistrictItem> kids = lowerDistrictsOf(current, validLevels);
		if(kids != null) {
			items.addAll(kids);
		}
		
		return items;
	}

	public List<DistrictItem> getUpperDistrictsOf(DistrictItem current) {
		List<DistrictItem> items = Lists.newArrayList(current);
		DistrictItem item = upperDistrictOf(current);
		while(item != null) {
			items.add(item);
			item = upperDistrictOf(item);
		}
		
		return items;
	}

	private DistrictItem upperDistrictOf(DistrictItem current) {
		int index = ALL_DISTRICTS.indexOf(current);
		String target = current.previousLevel();
		if(target == null) {
			return null;
		}
		
		int start = index - 1;
		for(int i = start; i >= 0; i--) {
			DistrictItem item = ALL_DISTRICTS.get(i);
			if(StrUtil.equals(item.getLevel(), target)) {
				return item;
			}
		}
		
		return null;
	}

	private List<DistrictItem> lowerDistrictsOf(DistrictItem current, List<String> validLevels) {
		int index = ALL_DISTRICTS.indexOf(current);
		if(current.isBottomLevel()) {
			return null;
		}
		
		List<DistrictItem> kids = Lists.newArrayList();
		int start = index + 1;
		for(int i = start; i < ALL_DISTRICTS.size(); i++) {
			DistrictItem item = ALL_DISTRICTS.get(i);
			if(item.sameLevelAs(current)) {
				break;
			}
			
			if(validLevels.indexOf(item.getLevel()) >= 0) {
				kids.add(item);
			}
		}
		
		return kids;
	}
}
