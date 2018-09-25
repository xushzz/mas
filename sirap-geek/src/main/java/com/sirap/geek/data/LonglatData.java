package com.sirap.geek.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.component.map.AlinkMap;
import com.sirap.basic.data.CityData;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.Amaps;
import com.sirap.basic.util.LonglatUtil;
import com.sirap.basic.util.StrUtil;

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
		EGGS.put("tw", "120.914716,23.192564");
		EGGS.put("moscow", "37.620539,55.753592");
		EGGS.put("nurem", "11.124185,49.42595");
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
	
	public static List<ValuesItem> online() {
		Extractor<ValuesItem> neymar = new Extractor<ValuesItem>() {
			
			@Override
			public String getUrl() {
				String href = "https://en.wikipedia.org/wiki/List_of_population_centers_by_latitude";
//				href = "E:/KDB/tasks/0714_Longlat/wiki.txt";
				showFetching();
				
				return href;
			}

			@Override
			protected void parse() {
				StringBuffer sb = StrUtil.sb();
				sb.append("<tr>");
				sb.append("<td data-sort-value=\"(.+?)\">.+?</td>");
				sb.append("<td data-sort-value=\"(.+?)\">.+?</td>");
				sb.append("<td.*?>(.*?)</td>");
				sb.append("<td.*?>(.*?)</td>");
				sb.append("<td.*?>(.*?)</td>");
				sb.append("</tr>");
				String regex = sb.toString();
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String location = ma.group(2) + "," + ma.group(1);
					String city = getPrettyText(ma.group(3));
					String province = getPrettyText(ma.group(4));
					String country = getPrettyText(ma.group(5));
					if(StrUtil.equals("People's Republic of China", country)) {
						country = "China";
					}
					if(StrUtil.equals("Republic of China (Taiwan)", country)) {
						country = "Taiwan";
					}
					if(StrUtil.equals("N/A", province)) {
						province = city;
					}
					if(LonglatUtil.isLongLat(location)) {
						mexItems.add(new ValuesItem(city, province, country, location));
					} else {
						D.pla("Bad location:", location, city, province, country);
					}
				}
				
				Collections.sort(mexItems, new Comparator<ValuesItem>() {

					@Override
					public int compare(ValuesItem jack, ValuesItem kate) {
						String ca = jack.getByIndex(2) + "";
						String cb = kate.getByIndex(2) + "";
						return ca.toLowerCase().compareTo(cb.toLowerCase());
					}
				});
			}
		};
		
		return neymar.process().getItems();	
	}
	
}
