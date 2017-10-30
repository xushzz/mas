package com.sirap.basic.component;

import java.util.Map;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.StrUtil;

public class DistributionAssistant {

	public static final String TBD = Konstants.DISTRIBUTION_KEYS_TBD[0];
	
	public static void print(Map<String, Integer> typeValueMap, String prefix, int lenOfKey, char repeatedChar, int maxLenToDisplay) {
		int sum = 0;
		for(Map.Entry<String, Integer> entry: typeValueMap.entrySet()) {
			String key = entry.getKey();
			Integer number = entry.getValue();
			sum += number;
			
			String display;
			if(!TBD.equals(key)) {
				display = StrUtil.repeatNicely(repeatedChar, number, maxLenToDisplay);
			} else {
				display = number + "";
			}
			
			String record = prefix + StrUtil.padRight(key, lenOfKey + 2) + display;
			C.pl(record);
		}
		
		C.total(sum);
		C.pl();
	}
	
}
