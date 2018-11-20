package com.sirap.extractor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.domain.Album;
import com.sirap.extractor.images.Netease163Fetcher;
import com.sirap.extractor.images.SinaFetcher;
import com.sirap.extractor.images.SuperiorFetcher;
import com.sirap.extractor.images.WebsiteImageLinksFetcher;
import com.sirap.extractor.images.WeixinFetcher;

public class LinksFetchers {

	public static final String QIHU360_HTML_QUERY = "http://image.so.com/i?q={0}";
	public static final String QIHU360_JSON_QUERY = "http://image.so.com/j?q={0}&pn={1}&sn={2}";
	public static final String HOMEPAGE_SOGOU = "http://pic.sogou.com";
	public static final String HOMEPAGE_WEIBO = "https://s.weibo.com/ajax_pic/list?q={0}&page={1}";
	
	public static final Map<String, WebsiteImageLinksFetcher> MY_FETCHERS = Maps.newLinkedHashMap();
	static {
		MY_FETCHERS.put("163.com", new Netease163Fetcher());
		MY_FETCHERS.put("sina.c", new SinaFetcher());
		MY_FETCHERS.put("weixin.qq.com", new WeixinFetcher());
		MY_FETCHERS.put("superiorpics.com", new SuperiorFetcher());
	}
	
	public static Album fetchAlbum(final String albumUrl) {
		Iterator<String> it = MY_FETCHERS.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(StrUtil.contains(albumUrl, key)) {
				return MY_FETCHERS.get(key).fetch(albumUrl);
			}
		}
		
		XXXUtil.alerto("Not yet supported website: ", albumUrl);
		return null;
	}

	public static Album superAlbumLinksOfIga() {
		String url = "https://www.superiorpics.com/c/Iga_Wyrwal/";
		return fetchAlbum(url);
	}

	public static List<String> sogouImageLinks(final String keyword) {
		Extractor<String> frank = new Extractor<String>() {
			
			public static final String URL = "http://pic.sogou.com/pics?query=";
			
			@Override
			public String getUrl() {
				printFetching = true;
				String param = encodeURLParam(keyword);
				return StrUtil.occupy(URL + param);
			}
			
			@Override
			protected void parse() {
				String regex = "\"pic_url\":\"(.*?)\"";
				Matcher m = createMatcher(regex);
				while(m.find()) {
					String imageUrl = m.group(1);
					mexItems.add(imageUrl);
				}
			}
		};
		
		frank.process();
		
		return frank.getItems();
	}
	
	public static List<String> qihu360ImageLinksx(final String keyword) {
		Extractor<String> frank = new Extractor<String>() {
			
			public static final String URL = "http://image.so.com/i?q=";
			
			@Override
			public String getUrl() {
				printFetching = true;
				String param = encodeURLParam(keyword);
				return StrUtil.occupy(URL + param);
			}
			
			@Override
			protected void parse() {
				String regex = "\"img\":\"(http[^\"]+)\"";
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
	
	private static Extractor<String> createQihuExtractor(String url) {
		Extractor<String> frank = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching();
				return url;
			}
			
			@Override
			protected void parse() {
				String regex = "\"img\":\"(http[^\"]+)\"";
				Matcher m = createMatcher(regex);
				while(m.find()) {
					String temp = m.group(1);
					String item = temp.replace("\\", "");
					mexItems.add(item);
				}
				
				if(url.contains("/i?")) {
					item = StrUtil.findFirstMatchedItem("\"lastindex\"\\s*:\\s*(\\d+)", source);
					D.pl("Contains no lastindex from " + url);
				}
			}
		};
		
		return frank;
	}
	
	public static List<String> qihu360ImageLinks(String keyword, int maxPage) {
		String query = XCodeUtil.urlEncodeUTF8(keyword);
		int lastindex = 60;
		int pagesize = 60;
		List<String> links = Lists.newArrayList();
		for(int k = 0; k < maxPage; k++) {
			String url;
			List<String> current;
			if(k == 0) {
				url = StrUtil.occupy(QIHU360_HTML_QUERY, query);
				Extractor<String> frank = createQihuExtractor(url).process();
				String lastindexInfo = frank.getItem();
				if(lastindexInfo != null) {
					lastindex = Integer.parseInt(lastindexInfo);
				}
				current = frank.getItems();
			} else {
				url = StrUtil.occupy(QIHU360_JSON_QUERY, query, pagesize, lastindex);
				lastindex += pagesize;
				current = createQihuExtractor(url).process().getItems();
			}
			
			if(current.isEmpty()) {
				C.pl2("Break when it comes to page {0} with {1}", k, url);
				break;
			}
			
			links.addAll(current);
		}
		
		return Colls.distinctOf(links);
	}
	
	public static List<String> weiboImageLinks(String keyword) {
		return weiboImageLinks(keyword, 1);
	}
	
	public static List<String> weiboImageLinks(String keyword, int page) {
		Extractor<String> frank = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching();
				String query = encodeURLParam(keyword);
				return StrUtil.occupy(HOMEPAGE_WEIBO, query, page);
			}
			
			@Override
			protected void parse() {
				//"original_pic": "\/\/wx4.sinaimg.cn\/large\/9fdf5b9bly1fwuwn0a6qoj20fa0aw400.jpg",
				String regex = JsonUtil.createRegexKey("original_pic");
				Matcher m = createMatcher(regex);
				while(m.find()) {
					String temp = m.group(1);
					String item = temp.replace("\\", "");
					mexItems.add("https:" + item);
				}
			}
		};
		
		frank.process();
		
		return frank.getItems();
	}
}
