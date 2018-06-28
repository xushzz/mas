package com.sirap.basic.component.html;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sirap.basic.output.HtmlParams;
import com.sirap.basic.util.DateUtil;

public class HtmlExporter {

	private static final String KEY_TOPINFO = "thisIsTopInfo";
	private static final String KEY_NICERECORD = "thisIsNiceRecord";
	private static final String KEY_NICETITLE = "thisIsNiceTitle";
	
	public static List<String> generateHtmlContent(List<String> htmlTemplate, List<?> objList, HtmlParams params) {
		List<String> items = new ArrayList<>();
		
		String[] targets = {whatToReplace(KEY_TOPINFO), whatToReplace(KEY_NICERECORD), whatToReplace(KEY_NICETITLE)};
		boolean[] hasBeenHandled = new boolean[targets.length];
		for(String item : htmlTemplate) {
			int index = 0;
			if(!hasBeenHandled[index]) {
				String temp = targets[index];
				if(item.contains(temp)) {
					hasBeenHandled[index] = true;
					String newItem = item.replace(temp, params.getTopInfo());
					items.add(newItem);
					continue;
				}
			}
			
			index = 1;
			if(!hasBeenHandled[index]) {
				String temp = targets[index];
				if(item.contains(temp)) {
					hasBeenHandled[index] = true;
					for(Object obj : objList) {
						String newItem = item.replace(temp, obj + "");
						items.add(newItem);
					}
					continue;
				}
			}
			
			index = 2;
			if(!hasBeenHandled[index]) {
				String temp = targets[index];
				if(item.contains(temp)) {
					hasBeenHandled[index] = true;
					String header = DateUtil.strOf(new Date(), DateUtil.GMT);
					String newItem = item.replace(temp, header);
					items.add(newItem);
					continue;
				}
			}
			
			items.add(item);
		}
		
		return items;
	}
	
	private static String whatToReplace(String key) {
		String target = "${" + key + "}";
		
		return target;
	}
}
