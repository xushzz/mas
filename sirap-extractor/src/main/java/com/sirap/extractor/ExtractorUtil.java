package com.sirap.extractor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.Code;
import com.sirap.common.domain.Link;
import com.sirap.common.extractor.Extractor;

public class ExtractorUtil {

	public static final String HOMEPAGE_SOGOU = "http://pic.sogou.com";
	public static final String HOMEPAGE_YOUDAO = "http://image.youdao.com";

	public static List<Code> getDisplayCountryCodes(Locale inLocale) {
		List<Code> codes = new ArrayList<Code>();
		
		Locale[] allLocales = Locale.getAvailableLocales();
		for(int i = 0; i < allLocales.length; i++) {
			Locale temp = allLocales[i];
			String name = null;
			try {
				name = temp.getISO3Country();
			} catch (Exception ex) {
				C.pl(ex);
			}
			if(EmptyUtil.isNullOrEmpty(name)) {
				continue;
			}
			String meaning = temp.getDisplayCountry(inLocale);
			Code code = new Code(name, "Country(ISO)", meaning);
			codes.add(code);			
		}
		
		Collections.sort(codes);
		return codes;
	}
	
	public static List<MexedObject> youdaoImageLinks(final String keyword, final int start) {
		Extractor<MexedObject> frank = new Extractor<MexedObject>() {
			
			public static final String URL_TEMPLATE = HOMEPAGE_YOUDAO + "/search?q={0}&start={1}";
			
			@Override
			public String getUrl() {
				printFetching = true;
				String param = encodeURLParam(keyword);
				return StrUtil.occupy(URL_TEMPLATE, param, start);
			}
			
			@Override
			protected void parseContent() {
				String regex = "logci\\('.*?',\\s*'(.*?)',\\s*'.*?'\\)";
				Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
				while(m.find()) {
					String temp = m.group(1);
					String imageUrl = decodeURLParam(temp);
					if(imageUrl.endsWith("loading40.gif")) {
						continue;
					}
					
					mexItems.add(new MexedObject(imageUrl));
				}
			}
		};
		
		frank.process();
		
		return frank.getMexItems();
	}
	
	public static List<MexedObject> sogouImageLinks(final String keyword) {
		Extractor<MexedObject> frank = new Extractor<MexedObject>() {
			
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
					mexItems.add(new MexedObject(imageUrl));
				}
			}
		};
		
		frank.process();
		
		return frank.getMexItems();
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
		
		return ExtractorUtil.items2Links(frank.getMexItems());
	}
}
