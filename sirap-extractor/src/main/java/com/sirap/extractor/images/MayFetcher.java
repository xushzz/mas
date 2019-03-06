package com.sirap.extractor.images;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.thread.MasterItemsOriented;
import com.sirap.basic.thread.WorkerItemsOriented;
import com.sirap.basic.util.Amaps;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.Album;

public class MayFetcher extends WebsiteImageLinksFetcher {

	@Override
	public Album fetch(String weburl) {
		return of(weburl);
	}
	
	public static Map<String, Object> countAndTitleOf(String albumurl) {
		Extractor<Map<String, Object>> frank = new Extractor<Map<String, Object>>() {
    		
			@Override
			public String getUrl() {
				showFetching();
				return albumurl;
			}
			
			@Override
			protected void parse() {
				item = Amaps.newConcurrentHashMap();
				String regex = "<span id=\"allnum\">(\\d+)</span>";
				item.put("count", StrUtil.findFirstMatchedItem(regex, source));

				regex = "alt=\"([^\"]+)\"";
				item.put("title", StrUtil.findFirstMatchedItem(regex, source));
			}
		};
		
		return frank.process().getItem();
	}
	
	public static Album of(String albumurl) {
		Map<String, Object> info = countAndTitleOf(albumurl);
		List<String> links = Lists.newArrayList(albumurl);
		String name = info.get("title") + "";
		int maxpage = Integer.parseInt(info.get("count") + "");
//		D.pl(maxpage);
//		maxpage = 3;
//		D.pl(maxpage);
		
		for(int page = 1; page <= maxpage; page++) {
			links.add(albumurl.replace(".html", "_" + page + ".html"));
//			break;
		}
//		D.list(links);
		MasterItemsOriented<String, String> master = new MasterItemsOriented<>(links, new WorkerItemsOriented<String, String>() {

			@Override
			public List<String> process(String href) {
				return imagesInPage(href);
			}

		});
		
		List<String> list = master.getAllMexItems();
		Album item = Album.of(name, list).setUrl(albumurl).setTag("may").setListObj(list);
		
		return item;
	}
	
	public static List<String> imagesInPage(String url) {
		Extractor<String> frank = new Extractor<String>() {
    		
			@Override
			public String getUrl() {
				useGBK().showFetching();
				return url;
			}
			@Override
			protected void parse() {
				String r1 = "<p><center>(.+?)</center></p>";
				String kid = StrUtil.findFirstMatchedItem(r1, source);
				String regex = "lazysrc=\\s*(\\S+)";
				mexItems = StrUtil.findAllMatchedItems(regex, kid, 1);
				item = StrUtil.findFirstMatchedItem(regex, source);
			}
		};
		
		return frank.process().getItems();
	}
}
