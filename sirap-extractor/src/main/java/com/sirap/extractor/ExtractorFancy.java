package com.sirap.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.thread.MasterMexItemsOriented;
import com.sirap.basic.thread.WorkerMexItemsOritented;
import com.sirap.common.domain.Link;
import com.sirap.common.extractor.Extractor;
import com.sirap.security.MrTrump;

public class ExtractorFancy {

	public static final String SITE_PINDER = MrTrump.decodeBySIRAP("7544210D0DD0010AC4F0E37BB1CB3DF12D304A14B59CA9D06DB89B573AC450D4", "fancy", true);
	public static final String SITE_IGA = MrTrump.decodeBySIRAP("CE40B4E17C53FA37FC66A1B4850EE87618C4DF31B104022E642F033E686399D6", "fancy", true);
	public static final String SITE_CARVER = MrTrump.decodeBySIRAP("9B0E2757D3723842C2301835277D6A0A", "fancy", true);
	
	public static final Map<String, String> TYPE_METHOD = new HashMap<String, String>();
	static {
		TYPE_METHOD.put("iga", "igaPhotos");
		TYPE_METHOD.put("pinder", "pinderPhotos");
		TYPE_METHOD.put("carver", "carverPhotos");
	}
	
	public static List<String> pinderPhotos() {
		int max = 573;
		
		List<Link> events = new ArrayList<Link>();
		for(int i = 1; i <= max; i++) {
			String url = "http://" + SITE_PINDER + "/gallery.php?r=&p=" + i;
			events.add(new Link(url));
		}
		
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
						String regex = "href=\"(/forum/images/.*?)\"";
						Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
						while(m.find()) {
							String temp = "http://" + SITE_PINDER + "/" + m.group(1);
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

	public static List<String> igaPhotos() {
		int max = 19;
		
		List<Link> events = new ArrayList<Link>();
		for(int i = 1; i <= max; i++) {
			String url = "http://" + SITE_IGA + "/page" + i;
			events.add(new Link(url));
		}
		
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
						String regex = "<a href=\"(http://" + SITE_IGA + "/nude/.*?)\"";
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

	public static List<String> carverPhotos() {
		int max = 172;
		
		List<Link> events = new ArrayList<Link>();
		for(int i = 1; i <= max; i++) {
			String url = "http://" + SITE_CARVER + "/jordan-carver/pictures/";
			if(i != 1) {
				url += "/" + i;
			}
			events.add(new Link(url));
		}
		
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
						String regex = "<a href='(http://" + SITE_CARVER + "/viewimage/.*?)'>";
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
		
		return carverFullPhotos(master.getAllMexItems());
	}

	private static List<String> carverFullPhotos(List<Link> events) {
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
						String regex = "<center>.*?<img src='(.*?)'.*?</center>";
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
}
