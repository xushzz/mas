package com.sirap.extractor.manager;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.google.common.collect.Maps;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class Extractors {
	
	/**
	 * 
	 * @param 
	 * 	year 1917.htm, bc626.htm, 756.htm
	 * 	month 01-16
	 * @return
	 */
	public static List<MexObject> fetchHistoryEvents(final String param) {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String url = StrUtil.occupy("http://jintian.cidianwang.com/{0}", param);
				return url;
			}
			
			@Override
			protected void parseContent() {
				String regex = "<a href=\"http://jintian.cidianwang.com/([^/]+)/(\\d{4})[^<>]+>([^<>]+)</a></li>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String temp = ma.group(2) + "/" + ma.group(1).replace('-', '/') + " " + ma.group(3);
					mexItems.add(new MexObject(temp));
				}
			}
		};
		
		return neymar.process().getMexItems();
	}

	public static List<MexObject> fetchCarList() {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				return "http://www.ip138.com/carlist.htm";
			}
			
			@Override
			protected void parseContent() {
				Map<String, String> sea = shortAndFullProvinceName();
				String regex = "<td>(.)([A-Z])\\s*</td><td>([^<>]+)</td>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String temp = sea.get(ma.group(1)) + " " + ma.group(1) + ma.group(2) + " " + ma.group(3);
					mexItems.add(new MexObject(temp));
				}
			}
			
			private Map<String, String> shortAndFullProvinceName() {
				String regex = "<th colspan=\"2\">([^<>]+?)[^<>]([^<>])[^<>]</th>";
				Matcher ma = createMatcher(regex);
				Map<String, String> sea = Maps.newHashMap();
				while(ma.find()) {
					sea.put(ma.group(2), ma.group(1));
				}
				
				return sea;
			}
		};
		
		return neymar.process().getMexItems();
	}
}
