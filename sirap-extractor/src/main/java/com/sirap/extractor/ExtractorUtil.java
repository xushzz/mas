package com.sirap.extractor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.domain.Album;
import com.sirap.common.domain.Link;

public class ExtractorUtil {

	@SuppressWarnings("unchecked")
	public static List<String> photos(String method, Class<?> clazz) {
		
		try {
			Method m = clazz.getMethod(method, new Class[]{});
			List<String> links = (List<String>)m.invoke(null, new Object[]{});
			
			return links;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Collections.EMPTY_LIST; 
	}
	
	public static List<String> items2Links(List<Link> links) {
		List<String> records = new ArrayList<String>();
		for(Link link:links) {
			records.add(link.getHref());
		}
		
		return records;
	}
}
