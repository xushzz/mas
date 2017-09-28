package com.sirap.extractor.manager;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.google.common.collect.Maps;
import com.sirap.basic.domain.MexObject;
import com.sirap.common.extractor.Extractor;

public class Extractors {

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
				String regex = "<td>(.)([A-Z])</td><td>([^<>]+)</td>";
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
