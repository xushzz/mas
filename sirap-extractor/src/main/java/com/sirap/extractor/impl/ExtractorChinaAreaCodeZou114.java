package com.sirap.extractor.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.thread.MasterItemsOriented;
import com.sirap.basic.thread.WorkerItemsOriented;
import com.sirap.basic.util.HtmlUtil;

public class ExtractorChinaAreaCodeZou114 {
	
	public static final String HOMEPAGE = "http://www.zou114.com/qh";

	public static List<MexObject> getAllAreaCodes() {
		
		List<MexObject> areaLinks = getAllAreaLinks();
		MasterItemsOriented<MexObject, MexObject> master = new MasterItemsOriented<MexObject, MexObject>(areaLinks, new WorkerItemsOriented<MexObject, MexObject>() {

			@Override
			public List<MexObject> process(MexObject areaLink) {
				
				String url = HOMEPAGE + "/" + areaLink;
				
				Extractor<MexObject> frank = new Extractor<MexObject>() {
					
					@Override
					public String getUrl() {
						return url;
					}
					
					/***
					 * <font color=green face="Arial">区号&nbsp;0771&nbsp;&nbsp;广西&nbsp;南宁</font> <font color=#000000 size=2>(市辖区、马山县、)</font>
					 */
					@Override
					protected void parse() {
						String regex = "face=\"Arial\">[^\\d]+([\\d]{1,9}[^\"]+)</font></font><br>";
						Matcher mat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
						while(mat.find()) {
							String temp = mat.group(1);
							temp = HtmlUtil.removeHttpTag(temp);
							temp = temp.replace("、)", ")").trim();
							temp = temp.replaceAll("&nbsp;&nbsp;", " ");
							temp = temp.replaceAll("&nbsp;", "");
							mexItems.add(new MexObject(temp));
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
	
	/***
	 * http://www.zou114.com/qh
	 * @param
	 * @return gd.html
	 */
	public static List<MexObject> getAllAreaLinks() {
		
		Extractor<MexObject> frank = new Extractor<MexObject>() {
			
			@Override
			public String getUrl() {
				return HOMEPAGE;
			}
			
			/***
			 *  target="_blank" href="ah.htm"
			 */
			@Override
			protected void parse() {
				String regex = "target=\"_blank\" href=\"([^\"]+.htm)\"";
				Matcher mat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
				while(mat.find()) {
					String temp = mat.group(1);
					mexItems.add(new MexObject(temp));
				}
			}
		};
		
		frank.process();

		return frank.getItems();
	}
}
