package com.sirap.extractor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.thread.MasterMexItemsOriented;
import com.sirap.basic.thread.WorkerMexItemsOritented;
import com.sirap.common.domain.Link;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.ExtractorUtil;

public class ExtractorPhoenix {
	
	public static List<String> photos() {
		List<Link> events = eventLinks();
		List<String> allLinks = getAllLinksInEvents(events);
		
		return allLinks;
	}
	
	private static List<String> getAllLinksInEvents(List<Link> events) {
		MasterMexItemsOriented<Link, Link> master = new MasterMexItemsOriented<Link, Link>(events, new WorkerMexItemsOritented<Link, Link>() {

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
				
				int count = countOfTasks - tasks.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", url);
				frank.process();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetched.", "");
				
				return frank.getMexItems();
			}
			
		});
		
		master.sitAndWait();
		
		return ExtractorUtil.items2Links(master.getAllMexItems());
	}
	
	public static List<Link> eventLinks() {
		String[] events = {
				"http://news.ifeng.com/photo/list_0/0.shtml",
				"http://news.ifeng.com/photo/weiguan/list_0/0.shtml",
				"http://news.ifeng.com/photo/tekan/list_0/0.shtml",
				"http://news.ifeng.com/photo/zairenjian/list_0/0.shtml",
				"http://news.ifeng.com/photo/dashijian/list_0/0.shtml",
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
					//href="http://news.ifeng.com/a/20140905/41882756_0.shtml"
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
