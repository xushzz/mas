package com.sirap.extractor.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import com.google.common.collect.Maps;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.domain.SportsMatchItem;

public class Extractors {

	public static List<SportsMatchItem> fetchUefaChampionsSchedule() {
		Extractor<SportsMatchItem> neymar = new Extractor<SportsMatchItem>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String url = "https://soccer.hupu.com/uefa/schedule.php";
				return url;
			}
			
			@Override
			protected void parseContent() {
				String regex = "<tr align=\"center\">\\s*";
				regex += "<td>([^<>]+)</td>\\s*";
				regex += "<td>([^<>]+)</td>\\s*";
				regex += "<td>([^<>]+)</td>\\s*";
				regex += "<td>([^<>]+)</td>\\s*";
				regex += "<td><[^<>]+>([^<>]+)</a></td>\\s*";
				regex += "<td><[^<>]+>([^<>]+)</a></td>\\s*";
				regex += "<td><[^<>]+>([^<>]+)</a></td>\\s*";
				Matcher ma = createMatcher(regex, source);
				Set<String> numbers = new HashSet<>();
				while(ma.find()) {
					String gameNumber = ma.group(1);
					if(numbers.contains(gameNumber)) {
						continue;
					} else {
						numbers.add(gameNumber);
					}
					SportsMatchItem item = new SportsMatchItem();
					item.setOrder(gameNumber);
					String time = ma.group(3).replaceAll(":\\d+$", "").trim();
					item.setDatetime(ma.group(2).trim() + " " + time);
					item.setGroup(ma.group(4).trim());
					item.setHomeTeam(getPrettyText(ma.group(5)));
					item.setStatus(getPrettyText(ma.group(6)).replace(" ", ""));
					item.setAwayTeam(getPrettyText(ma.group(7)));
					mexItems.add(item);
				}
			}
		};
		
		return neymar.process().getMexItems();
	}
	
	public static List<MexObject> fetchAllNobelPrizes() {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String url = "https://www.nobelprize.org/nobel_prizes/lists/all/index.html";
				return url;
			}
			
			@Override
			protected void parseContent() {
				String fixed = source.replace("<div class=\"by_year_clear\"></div>", "");
				String regex = "<div class=\"by_year(.+?)</div>";
				Matcher ma = createMatcher(regex, fixed);
				while(ma.find()) {
					String temp = ma.group(0).replace("</a></h3>", ", ");
					temp = temp.replaceAll("</h6>\\s*<p>", ", ");
					temp = temp.replace("The Sveriges Riksbank Prize in Economic Sciences in Memory of Alfred Nobel", "Economic Sciences");
					temp = temp.replace("The Nobel Prize in", "");
					temp = temp.replace("The Nobel", "");
					temp = getPrettyText(temp);
					temp = temp.replaceAll(",\\s*$", "");
					mexItems.add(new MexObject(temp));
				}
			}
		};
		
		return neymar.process().getMexItems();
	}
	
	public static List<MexObject> fetchHtmlEntities() {
		Extractor<MexObject> nikita = new Extractor<MexObject>() {

			@Override
			protected void readSource() {
				String temp = "";
				temp += IOUtil.readURL("http://www.w3school.com.cn/tags/html_ref_entities.html");
				temp += IOUtil.readURL("http://www.w3school.com.cn/tags/html_ref_symbols.html");
				
				source = temp;
			}
			
			@Override
			protected void parseContent() {
				String regex = "<td>([^<>]+)</td>\\s*<td>&amp;([a-z]{1,99});</td>\\s*<td>&amp;#(\\d{1,7});</td>";

				Matcher ma = createMatcher(regex);
				String guys = "EGGS.put(\"{0}\", new HtmlEntity(\"{0}\", {1}, \"{2}\"));";
				while(ma.find()) {
					String temp = StrUtil.occupy(guys, ma.group(2), ma.group(3), ma.group(1).trim());
					mexItems.add(new MexObject(temp));
				}
			}
		};
		
		return nikita.process().getMexItems();
	}
	
	/**
	 * 
	 * @param year 1917, bc626, 756
	 * @return
	 */
	public static List<MexObject> fetchHistoryEventsByYear(final String yearInfo) {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String url = StrUtil.occupy("http://jintian.cidianwang.com/{0}.htm", yearInfo);
				return url;
			}
			
			@Override
			protected void parseContent() {
				String regex = "<li>\\[<a[^<>]+>[^<>]+</a>[^<>]+<a href=\"http://jintian.cidianwang.com/([^/]+)/[^<>]+>([^<>]+)</a></li>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String temp = yearInfo.toUpperCase() + "/" + ma.group(1).replace('-', '/') + " " + getPrettyText(ma.group(2));
					mexItems.add(new MexObject(temp));
				}
			}
		};
		
		return neymar.process().getMexItems();
	}

	/***
	 * @param param 01-16
	 * @return
	 */
	public static List<MexObject> fetchHistoryEventsByDay(final String day) {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String url = StrUtil.occupy("http://jintian.cidianwang.com/{0}", day);
				return url;
			}
			
			@Override
			protected void parseContent() {
				String regex = "</h2><ul>(.+?)</ul>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String temp = ma.group(1);
					parseSection(temp);
				}
			}

			protected void parseSection(String section) {
				String regexSectioin = "<li>(.+?)</li>";
				Matcher ma = createMatcher(regexSectioin, section);
				while(ma.find()) {
					String rawItem = ma.group(1);
					String temp;
					String regexItem = "\\[<a href=\"http://jintian.cidianwang.com/([^\"/]+).htm\".+?target=\"_blank\"\\s*>(.+?)</a>";
					Matcher ma2 = createMatcher(regexItem, rawItem);
					if(ma2.matches()) {
						temp = ma2.group(1).toUpperCase() + "/" + day.replace('-', '/') + " " + getPrettyText(ma2.group(2));
					} else {
						temp = day.replace('-', '/') + " " + getPrettyText(rawItem);
					}
					mexItems.add(new MexObject(temp));
				}
			}
		};
		
		return neymar.process().getMexItems();
	}

	/***
	 * @param param 1.16
	 * @return
	 */
	public static List<MexObject> fetchHistoryEventsByDay2(final String urlParam, final String monthDay) {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {
			
			@Override
			public String getUrl() {
				printFetching = true;
				String url = StrUtil.occupy("http://www.historynet.com/today-in-history/{0}", urlParam);
				return url;
			}
			
			@Override
			protected void parseContent() {
				String regex = "<tr>\\s*<td align=\"left\" valign=\"top\">([^\"]+)</td>";
				regex += "\\s*<td>[^<>]*</td>";
				regex += "\\s*<td align=\"left\" valign=\"top\">(.+?)</td>\\s*</tr>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String temp = getPrettyText(ma.group(1)) + "/" + monthDay + " " + getPrettyText(ma.group(2));
					mexItems.add(new MexObject(temp.trim()));
				}
			}
		};
		
		return neymar.process().getMexItems();
	}

	public static List<MexObject> fetchCarList() {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				return "http://www.ip138.com/carlist.htm";
			}
			
			@Override
			protected void parseContent() {
				Map<String, String> sea = shortAndFullProvinceName();
				String regex = "<td>(.)([A-Z])\\s*</td><td>([^<>]+)</td>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String temp = sea.get(ma.group(1)) + " " + ma.group(1) + ma.group(2) + " " + ma.group(3);
					mexItems.add(new MexObject(temp));
				}
			}
			
			private Map<String, String> shortAndFullProvinceName() {
				String regex = "<th colspan=\"2\">([^<>]+?)[^<>]([^<>])[^<>]</th>";
				Matcher ma = createMatcher(regex);
				Map<String, String> sea = Maps.newHashMap();
				while(ma.find()) {
					sea.put(ma.group(2), ma.group(1));
				}
				
				return sea;
			}
		};
		
		return neymar.process().getMexItems();
	}
}
