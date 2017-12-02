package com.sirap.extractor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.thread.MasterItemsOriented;
import com.sirap.basic.thread.WorkerItemsOriented;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.Link;
import com.sirap.extractor.ExtractorUtil;

public class ExtractorNetease {
	
	public static final Map<String, String> TYPE_METHOD = new HashMap<String, String>();
	static {
		TYPE_METHOD.put("social", "socialPhotos");
		TYPE_METHOD.put("bundes", "bundesPhotos");
		TYPE_METHOD.put("laliga", "laligaPhotos");
		TYPE_METHOD.put("premier", "premierPhotos");
		TYPE_METHOD.put("seriea", "serieAPhotos");
		TYPE_METHOD.put("uefa", "uefaPhotos");
		TYPE_METHOD.put("china", "chinaPhotos");
	}
	
	public static List<String> socialPhotos() {
		List<Link> events = socialEvents();
		List<String> allLinks = getAllLinksInEvents(events);
		
		return allLinks;
	}

	public static List<Link> socialEvents() {
		String[] types = {"Ranking"};
		
		List<Link> links = new ArrayList<Link>();
		for(int i = 0; i < types.length; i++) {
			String temp = "http://news.163.com/photo/#" + types[i];
			final String url = temp;
			Extractor<Link> frank = new Extractor<Link>() {
				@Override
				public String getUrl() {
					printFetching = true;
					return url;
				}
				
				@Override
				protected void parseContent() {
					String regex = "\"seturl\":\"(http://.*?)\"";
					Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
					while(m.find()) {
						String temp = m.group(1);
						mexItems.add(new Link(temp));
					}
				}
			};
			frank.process();
			links.addAll(frank.getItems());
		}

		return links;
	}

	public static List<String> uefaPhotos() {
		String urlTemp = "http://sports.163.com/special/00051K89/gjbtpklm{0}.html";
		List<Link> events = eventLinks(urlTemp, 20);
		List<String> allLinks = getAllLinksInEvents(events);
		
		return allLinks;
	}

	public static List<String> chinaPhotos() {
		String urlTemp = "http://sports.163.com/special/00051K89/zctpkhz{0}.html";
		List<Link> events = eventLinks(urlTemp, 20);
		List<String> allLinks = getAllLinksInEvents(events);
		
		return allLinks;
	}

	public static List<String>  bundesPhotos() {
		String urlTemp = "http://sports.163.com/special/00051K89/djtpklm{0}.html";
		List<Link> events = eventLinks(urlTemp, 20);
		List<String> allLinks = getAllLinksInEvents(events);
		
		return allLinks;
	}

	public static List<String>  premierPhotos() {
		String urlTemp = "http://sports.163.com/special/00051K89/yctpklm{0}.html";
		List<Link> events = eventLinks(urlTemp, 20);
		List<String> allLinks = getAllLinksInEvents(events);

		return allLinks;
	}

	public static List<String> serieAPhotos() {
		String urlTemp = "http://sports.163.com/special/00051K89/yjtpklm{0}.html";
		List<Link> events = eventLinks(urlTemp, 10);
		List<String> allLinks = getAllLinksInEvents(events);	

		return allLinks;
	}

	public static List<String> laligaPhotos() {
		String urlTemp = "http://sports.163.com/special/00051K89/xjtpklm{0}.html";
		List<Link> events = eventLinks(urlTemp, 20);
		List<String> allLinks = getAllLinksInEvents(events);

		return allLinks;
	}
	
	private static List<String> getAllLinksInEvents(List<Link> events) {
		
		MasterItemsOriented<Link, Link> master = new MasterItemsOriented<Link, Link>(events, new WorkerItemsOriented<Link, Link>() {

			@Override
			public List<Link> process(Link link) {
				
				final String url = link.getHref();
				
				Extractor<Link> frank = new Extractor<Link>() {
					
					@Override
					public String getUrl() {
						return url;
					}
					
					@Override
					protected void parseContent() {
						String regex = "\"oimg\":\\s+\"(.*?)\"";
						Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
						while(m.find()) {
							String temp = m.group(1);
							mexItems.add(new Link(temp));
						}
					}
				};
				
				int count = countOfTasks - queue.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", url);
				frank.process();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetched.", "");
				
				return frank.getItems();
			}
			
		});
		
		return ExtractorUtil.items2Links(master.getAllMexItems());
	}
	
	private static List<Link> eventLinks(String urlTemplate, int max) {
		List<Link> links = new ArrayList<Link>();
		for(int i = 1; i <= max; i++) {
			String page = "";
			if(i > 1) {
				String pageTemp = i + "";
				page = "_" + StrUtil.padLeft(pageTemp, 2, "0");
			}
			String url = StrUtil.occupy(urlTemplate, page);
			links.add(new Link(url));
		}

		MasterItemsOriented<Link, Link> master = new MasterItemsOriented<Link, Link>(links, new WorkerItemsOriented<Link, Link>() {

			@Override
			public List<Link> process(Link link) {
				final String url = link.getHref();
				
				Extractor<Link> frank = new Extractor<Link>() {
					
					@Override
					public String getUrl() {
						return url;
					}
					
					@Override
					protected void parseContent() {
						String regex = "<li><a\\shref=\"(http://sports.163.com/photoview.*?html)\">";
						Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
						while(m.find()) {
							String temp = m.group(1);
							mexItems.add(new Link(temp));
						}
					}
				};
				
				int count = countOfTasks - queue.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", url);
				frank.process();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetched.", "");
				
				return frank.getItems();
			}
			
		});
		
		return master.getAllMexItems();
	}
}
