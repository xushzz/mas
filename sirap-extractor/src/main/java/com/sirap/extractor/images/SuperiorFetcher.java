package com.sirap.extractor.images;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.thread.MasterItemsOriented;
import com.sirap.basic.thread.WorkerItemsOriented;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.domain.Album;

public class SuperiorFetcher extends WebsiteImageLinksFetcher {

	@Override
	public Album fetch(String weburl) {
		return superAlbumLinks(weburl);
	}
	
	//https://www.superiorpics.com/c/Iga_Wyrwal/
	//https://www.superiorpics.com/c/Iga_Wyrwal/index1.html
	public static Album superAlbumLinks(String personUrl) {
		int maxPage = maxPageOfSuperPersons(personUrl);
		List<Integer> pages = Colls.listOfInts(1, maxPage);
		MasterItemsOriented<Integer, String> master = new MasterItemsOriented<>(pages, new WorkerItemsOriented<Integer, String>() {

			@Override
			public List<String> process(Integer page) {
				String pageUrl = urlOfPage(page);
				int count = countOfTasks - queue.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", pageUrl);
				List<String> links = superAlbumLinksByPage(pageUrl);
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetched.", "");
				
				return links;
			}
			
			private String urlOfPage(int page) {
				String pageUrl = personUrl;
				if(page > 1) {
					String indexInfo = StrUtil.occupy("index{0}.html", page);
					pageUrl = StrUtil.useDelimiter("/", personUrl, indexInfo);
				}
				
				return pageUrl;
			}
			
		});
		
//		D.list(linksA);
		List<String> albumLinks = master.getAllMexItems();
		List<String> linksB = superImageLinks(albumLinks);
//		D.list(linksB);
		
		String personName = StrUtil.findFirstMatchedItem("/([^/]+)/?$", personUrl);
		Album al = new Album(personName, linksB);
		al.setTag("super");
		
		return al;
	}
	
	public static int maxPageOfSuperPersons(String personUrl) {
		Extractor<Integer> frank = new Extractor<Integer>() {
			
			@Override
			public String getUrl() {
				showFetching();
				return personUrl;
			}
			
			@Override
			protected void parse() {
				String regex = ">(\\d{1,3})</a>&nbsp;&nbsp;<a href=\"[^\"]+\">&raquo;";
				String max = StrUtil.findFirstMatchedItem(regex, source);
				XXXUtil.shouldBeNotnull(max);
				item = Integer.parseInt(max);
			}
		};
		
		return frank.process().getItem();
	}
	
	public static int maxPageOfSuperAlbums(String albumUrl) {
		Extractor<Integer> frank = new Extractor<Integer>() {
			
			@Override
			public String getUrl() {
				showFetching();
				return albumUrl;
			}
			
			@Override
			protected void parse() {
				String regex = "<div class=\"box-paging\">(.*?)</div>";
				String maxInfo = StrUtil.findFirstMatchedItem(regex, source);
				XXXUtil.shouldBeNotnull(maxInfo);
				String temp = maxInfo.trim();
				if(temp.isEmpty()) {
					item = 1;
				} else {
					regex = ">(\\d+)</a>$";
					String max2 = StrUtil.findFirstMatchedItem(regex, temp);
					XXXUtil.shouldBeNotnull(max2);
					item = Integer.parseInt(max2);
				}
//				D.pl(maxInfo);
			}
		};
		
		return frank.process().getItem();
	}
	
	private static List<String> superAlbumLinksByPage(String url) {
		Extractor<String> frank = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching();
				return url;
			}
			
			@Override
			protected void parse() {
				//<div class="box135-thumb-wrapper"><a href="https://www.hotflick.net/u/g/?d=3194107"
				String regex = "<div class=\"box135-thumb-wrapper\"><a href=\"(https://www.hotflick[^\"]+)\"";
				Matcher m = createMatcher(regex);
				while(m.find()) {
					String temp = m.group(1);
					mexItems.add(temp);
				}
				
			}
		};
		
		return frank.process().getItems();
	}
	
	//https://www.hotflick.net/u/g/index.php?d=2732862&p=2.page
	private static Extractor<String> createSuperImageLinksExtractor(String url) {
		Extractor<String> frank = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching();
				return url;
			}
			
			@Override
			protected void parse() {
				String regex = "src=\"([^\"]+)\" class=\"img-100pr\">";
				Matcher m = createMatcher(regex);
				while(m.find()) {
					String temp = m.group(1);
					String href = temp.replace("/tn/", "/");
					mexItems.add(href);
				}
			}
		};
		
		return frank;
	}
	
	//https://www.hotflick.net/u/g/?d=3194386
	public static List<String> superImageLinks(List<String> albumsLinks) {
		MasterItemsOriented<String, String> master = new MasterItemsOriented<>(albumsLinks, new WorkerItemsOriented<String, String>() {

			@Override
			public List<String> process(String albumLink) {
				List<String> links = Lists.newArrayList();
				int count = countOfTasks - queue.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", albumLink);
				String href = albumLink.replace("/?", "/index.php?");
				int page = 1;
				while(page < 99) {
					String url = StrUtil.occupy("{0}&p={1}.page", href, page++);
					Extractor<String> frank = createSuperImageLinksExtractor(url);
					List<String> current = frank.process().getItems();
					if(current.isEmpty()) {
						break;
					}
					links.addAll(current);
				}
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetched.", "");
				
				return links;
			}
		});
		
		return Colls.distinctOf(master.getAllMexItems());
	}
	
}
