package com.sirap.extractor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.Link;
import com.sirap.common.extractor.Extractor;

public class ExtractorUtil {

	public static final String HOMEPAGE_QIHU360 = "http://image.so.com";
	public static final String HOMEPAGE_SOGOU = "http://pic.sogou.com";

	public static List<String> sogouImageLinks(final String keyword) {
		Extractor<String> frank = new Extractor<String>() {
			
			public static final String URL = HOMEPAGE_SOGOU + "/pics?query=";
			
			@Override
			public String getUrl() {
				printFetching = true;
				String param = encodeURLParam(keyword);
				return StrUtil.occupy(URL + param);
			}
			
			@Override
			protected void parseContent() {
				String regex = "\"pic_url\":\"(.*?)\"";
				Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
				while(m.find()) {
					String imageUrl = m.group(1);
					mexItems.add(imageUrl);
				}
			}
		};
		
		frank.process();
		
		return frank.getItems();
	}
	
	public static List<String> qihu360ImageLinks(final String keyword) {
		Extractor<String> frank = new Extractor<String>() {
			
			public static final String URL = HOMEPAGE_QIHU360 + "/i?q=";
			
			@Override
			public String getUrl() {
				printFetching = true;
				String param = encodeURLParam(keyword);
				return StrUtil.occupy(URL + param);
			}
			
			@Override
			protected void parseContent() {
				String regex = "\"img\":\"(.*?)\"";
				Matcher m = createMatcher(regex);
				while(m.find()) {
					String temp = m.group(1);
					String item = temp.replace("\\", "");
					mexItems.add(item);
				}
			}
		};
		
		frank.process();
		
		return frank.getItems();
	}
	
	public static List<String> items2Links(List<Link> links) {
		List<String> records = new ArrayList<String>();
		for(Link link:links) {
			records.add(link.getHref());
		}
		
		return records;
	}
	
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
	
	public static List<String> imageLinks(final String pageUrl, final String imageUrlExp) {
		
		Extractor<Link> frank = new Extractor<Link>() {
			
			@Override
			public String getUrl() {
				return pageUrl;
			}
			
			@Override
			protected void parseContent() {
				String regex = imageUrlExp;
				Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
				int count = m.groupCount();
				while(m.find()) {
					if(count < 1) {
						break;
					}
					String temp = m.group(1);
					mexItems.add(new Link(temp));
				}
			}
		};
		
		frank.process();
		
		return ExtractorUtil.items2Links(frank.getItems());
	}
}
