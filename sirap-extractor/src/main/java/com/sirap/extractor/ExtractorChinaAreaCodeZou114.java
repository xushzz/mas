package com.sirap.extractor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.thread.MasterMexItemsOriented;
import com.sirap.basic.thread.WorkerMexItemsOritented;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.common.extractor.Extractor;

public class ExtractorChinaAreaCodeZou114 {
	
	private static final String HOMEPAGE = "http://www.zou114.com/qh";

	public static List<MexedObject> getAllAreaCodes() {
		
		List<MexedObject> areaLinks = getAllAreaLinks();
		MasterMexItemsOriented<MexedObject, MexedObject> master = new MasterMexItemsOriented<MexedObject, MexedObject>(areaLinks, new WorkerMexItemsOritented<MexedObject, MexedObject>() {

			@Override
			public List<MexedObject> process(MexedObject areaLink) {
				
				String url = HOMEPAGE + "/" + areaLink;
				
				Extractor<MexedObject> frank = new Extractor<MexedObject>() {
					
					@Override
					public String getUrl() {
						return url;
					}
					
					/***
					 * <font color=green face="Arial">区号&nbsp;0771&nbsp;&nbsp;广西&nbsp;南宁</font> <font color=#000000 size=2>(市辖区、马山县、)</font>
					 */
					@Override
					protected void parseContent() {
						String regex = "face=\"Arial\">[^\\d]+([\\d]{1,9}[^\"]+)</font></font><br>";
						Matcher mat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
						while(mat.find()) {
							String temp = mat.group(1);
							temp = HtmlUtil.removeHttpTag(temp);
							temp = temp.replace("、)", ")").trim();
							temp = temp.replaceAll("&nbsp;&nbsp;", " ");
							temp = temp.replaceAll("&nbsp;", "");
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
	
	/***
	 * http://www.zou114.com/qh
	 * @param
	 * @return gd.html
	 */
	public static List<MexedObject> getAllAreaLinks() {
		
		Extractor<MexedObject> frank = new Extractor<MexedObject>() {
			
			@Override
			public String getUrl() {
				return HOMEPAGE;
			}
			
			/***
			 *  target="_blank" href="ah.htm"
			 */
			@Override
			protected void parseContent() {
				String regex = "target=\"_blank\" href=\"([^\"]+.htm)\"";
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
