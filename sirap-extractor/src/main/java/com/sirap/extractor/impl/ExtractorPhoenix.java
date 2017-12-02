package com.sirap.extractor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.thread.MasterItemsOriented;
import com.sirap.basic.thread.WorkerItemsOriented;
import com.sirap.common.domain.Link;
import com.sirap.extractor.ExtractorUtil;

public class ExtractorPhoenix {
	
	public static final String HOMEPAGE = "http://news.ifeng.com";
	
	public static List<String> photos() {
		List<Link> events = eventLinks();
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
						String regex = "\\stimg: '(.*?)'";
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
	
	public static List<Link> eventLinks() {
		String[] events = {
				HOMEPAGE + "/photo/list_0/0.shtml",
				HOMEPAGE + "/photo/weiguan/list_0/0.shtml",
				HOMEPAGE + "/photo/tekan/list_0/0.shtml",
				HOMEPAGE + "/photo/zairenjian/list_0/0.shtml",
				HOMEPAGE + "/photo/dashijian/list_0/0.shtml",
				};
		
		final List<Link> items = new ArrayList<Link>();
		for(int i = 0; i < events.length; i++) {
			final String url = events[i];
			Extractor<Link> frank = new Extractor<Link>() {
				@Override
				public String getUrl() {
					return url;
				}
				
				@Override
				protected void parseContent() {
					String regex = "href=\"(http://news.ifeng.com/a/.*?shtml)\"";
					Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
					while(m.find()) {
						String temp = m.group(1);
						items.add(new Link(temp));
					}
				}
			};
			
			frank.process();
		}

		return items;
	}
	
	public static List<String> linksInEvent(String eventLink) {
		final List<String> items = new ArrayList<String>();
		
		final String url = eventLink;
		Extractor<MexItem> frank = new Extractor<MexItem>() {
			
			@Override
			public String getUrl() {
				return url;
			}
			
			@Override
			protected void parseContent() {
				String regex = "\\stimg: '(.*?)'";
				Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
				while(m.find()) {
					String temp = m.group(1);
					items.add(temp);
				}
			}
		};
		
		frank.process();
		
		return items;
	}
}
