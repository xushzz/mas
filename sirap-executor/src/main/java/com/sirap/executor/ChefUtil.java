package com.sirap.executor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.StrUtil;

public class ChefUtil {
	
	/***
	 * test::time
	 * @param records
	 * @param cookbook
	 * @return
	 */
	public static List<String> extractParams(List<String> records, String cookbook) {
		Set<String> items = new HashSet<>();
		
		String tempRegex = "node\\s*\\['{0}'\\]\\['(.+?)'\\]";
		String regex = StrUtil.occupy(tempRegex, cookbook);
		for(String record : records) {
			boolean ignore = record.trim().startsWith("#");
			if(ignore) {
				continue;
			}
			Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(record);
			while(m.find()) {
				String item = m.group(1);
				items.add(item);
			}
		}
		
		List<String> params = new ArrayList<>(items);
		return params;
	}
}
