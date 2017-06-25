package com.sirap.extractor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.thread.MasterMexItemsOriented;
import com.sirap.basic.thread.WorkerMexItemsOritented;
import com.sirap.common.extractor.Extractor;

public class ExtractorChinaPostCodeToolcncn {
	
	public static final String HOMEPAGE = "http://tool.cncn.com";

	public static List<MexedObject> getAllVillageCodes() {
		
		List<MexedObject> cityNamePostCodes = new ArrayList<>();
		List<MexedObject> countyLinks = getAllCountyLinks(cityNamePostCodes);
		
		MasterMexItemsOriented<MexedObject, MexedObject> master = new MasterMexItemsOriented<MexedObject, MexedObject>(countyLinks, new WorkerMexItemsOritented<MexedObject, MexedObject>() {

			/***
			 * /youbian/hechi-jinchengjiang
			 */
			@Override
			public List<MexedObject> process(MexedObject countyLink) {
				final String url = HOMEPAGE + countyLink;
				
				Extractor<MexedObject> frank = new Extractor<MexedObject>() {
					
					@Override
					public String getUrl() {
						return url;
					}
					
					/***
					 * new PCAS('location_p', 'location_c', 'location_a', '四川省', '阿坝藏族羌族自治州', '汶川县');
\					 * <p>古南镇桥坝村,古南镇清水村,古南镇白鹤村,古南镇白庙村<a href="/youbian/401420">[更多]</a></p>
					 */
					@Override
					protected void parseContent() {
						String regex = "'location_a', '([^']+)', '([^']+)', '([^']+)'\\);";
						String townName = null;
						Matcher mat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
						if(mat.find()) {
							String temp = mat.group(1) + mat.group(2) + mat.group(3);
							townName = temp;
						}
						
						regex = "<p>([^\"]+)<a href=\"/youbian/(\\d+)\">\\[";
						mat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
						while(mat.find()) {
							String code = mat.group(2);
							String locations = mat.group(1);
							String temp = townName + " " +  code + " " + locations;
							
							mexItems.add(new MexedObject(temp));
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
		List<MexedObject> allItems = master.getAllMexItems();
		allItems.addAll(cityNamePostCodes);
		
		return allItems;
	}

	/***
	 * 3090 counties
	 * @return /youbian/hechi-jinchengjiang
	 */
	public static List<MexedObject> getAllCountyLinks(List<MexedObject> cityNamePostCodes) {
		
		List<MexedObject> cityLinks = getAllCityLinks();
		
		MasterMexItemsOriented<MexedObject, MexedObject> master = new MasterMexItemsOriented<MexedObject, MexedObject>(cityLinks, new WorkerMexItemsOritented<MexedObject, MexedObject>() {

			/***
			 * /zibo-youbian
			 */
			@Override
			public List<MexedObject> process(MexedObject cityLink) {
				
				String url = HOMEPAGE + cityLink;
				
				Extractor<MexedObject> frank = new Extractor<MexedObject>() {
					
					@Override
					public String getUrl() {
						return url;
					}
					
					/***
					 * <p>深圳地区通用邮编：<em><a href ="/youbian/518000">518000</a></em>
					 * 
					 * a href="/youbian/hechi-jinchengjiang"
					 */
					@Override
					protected void parseContent() {
						String regex = "'location_a', '([^']+)', '([^']+)', ''\\);";
						String cityName = null;
						Matcher mat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
						if(mat.find()) {
							String temp = mat.group(1) + mat.group(2);
							cityName = temp;
						}
						
						regex = "<p>[^<>]+：<em><a href =\"/youbian/([\\d]{1,9})\">";
						mat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
						if(mat.find()) {
							String cityPostCode = mat.group(1);
							String temp = cityName + "通用邮编 " + cityPostCode;
							cityNamePostCodes.add(new MexedObject(temp));
						}

						regex = "href=\"(/youbian/[^\"]+-[^\"]+)\"";
						mat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
						while(mat.find()) {
							String temp = mat.group(1);
							mexItems.add(new MexedObject(temp));
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
		
		return master.getAllMexItems();
	}
	
	/**
	 * 343 cities
	 * @return aba-youbian
	 */
	public static List<MexedObject> getAllCityLinks() {
		
		Extractor<MexedObject> frank = new Extractor<MexedObject>() {
			
			@Override
			public String getUrl() {
				return HOMEPAGE + "/youbian";
			}
			
			/***
			 *  <a href="/aba-youbian">阿坝</a>
			 */
			@Override
			protected void parseContent() {
				String regex = "href=\"(/[^\"]+-youbian)\"";
				Matcher mat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
				while(mat.find()) {
					String temp = mat.group(1);
					mexItems.add(new MexedObject(temp));
				}
			}
		};
		
		frank.process();

		return frank.getMexItems();
	}
}
