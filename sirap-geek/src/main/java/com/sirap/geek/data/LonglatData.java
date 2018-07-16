package com.sirap.geek.data;

import java.util.List;

import com.sirap.basic.component.map.AlinkMap;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.util.Amaps;

public class LonglatData {

	public static final AlinkMap<String, String> EGGS = Amaps.newLinkHashMap();
	static {
		EGGS.put("deli", "108.392544,22.828986");
		EGGS.put("tam", "116.397573,39.908743");
		EGGS.put("laojia", "108.904706,24.777411");
		EGGS.put("jgc", "108.388262,22.696636");
		EGGS.put("laiyin", "108.413603,22.811284");
		EGGS.put("sf", "113.937987,22.523392");
		EGGS.put("yayuan", "108.374956,22.829735");
		EGGS.put("xida", "108.288135,22.839381");
		EGGS.put("nyc", "-73.916666,40.7333333");
//		EGGS.put("", "");
//		EGGS.put("", "");
//		EGGS.put("", "");
//		EGGS.put("", "");
//		EGGS.put("", "");
//		EGGS.put("", "");
//		EGGS.put("", "");
//		EGGS.put("", "");
//		EGGS.put("", "");
//		EGGS.put("", "");
//		EGGS.put("", "");
//		EGGS.put("", "");
//		EGGS.put("", "");
//		EGGS.put("", "");
	}
	
	public static List<String> eggs() {
		return Amaps.listOf(EGGS);
	}
	
	public static String getXY(String key) {
		String longlat = EGGS.get(key.toLowerCase());
		if(longlat != null) {
			return longlat;
		}
		
		ValuesItem vi = CityData.EGGS.get(key.toLowerCase());
		if(vi != null) {
			return vi.getByIndex(CityData.INDEX_LONGLAT).toString();
		}
		
		return null;
	}
}
