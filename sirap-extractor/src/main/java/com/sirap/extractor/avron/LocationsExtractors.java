package com.sirap.extractor.avron;

import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.StrUtil;

public class LocationsExtractors {
	
	public static void cities() {
		Extractor<String> neymar = new Extractor<String>() {
			@Override
			public String getUrl() {
				useList();
				String pathA = "E:/KDB/practice/amap/allcities.txt";
				return pathA;
			}
			
			@Override
			protected void parseContent() {
				String regex = Konstants.REGEX_FLOAT + "\\s*,\\s*" + Konstants.REGEX_FLOAT + "\\s+(.+)";
				String temp = "{\"name\": \"{0}\",\"center\": \"{1},{2}\"}";
				//222	119.929575843,28.4562995521	浙江省-丽水市
				for(String line : sourceList) {
					Matcher ma = StrUtil.createMatcher(regex, line);
					while(ma.find()) {
						mexItems.add(StrUtil.occupy(temp, ma.group(3), ma.group(1), ma.group(2)));
					}
					continue;
				}
				String allStars = "[" + StrUtil.connectWithComma(mexItems) + "]";
				C.pl(allStars);
			}
		};
		
		neymar.process();
	}
}
