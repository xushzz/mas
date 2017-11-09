package com.sirap.extractor.manager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import com.google.common.collect.Maps;
import com.sirap.basic.domain.KeyValuesItem;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.Link;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.domain.SportsMatchItem;

public class Extractors {

	public static Link fetchHangyangLocation(String plateNo) {
		Extractor<Link> neymar = new Extractor<Link>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String param = encodeURLParam(plateNo);
				String temp = "http://m.gwwg.com/wap/keytop/getParkingPaymentInfo_APP.htm?plateNo=" + param;
				return temp;
			}
			
			@Override
			protected void parseContent() {
				String fields = "floorName,area,spaceNo";
				List<String> keys = StrUtil.split(fields);
				String connector = ", ";
				StringBuffer sb = StrUtil.sb();
				for(String key : keys) {
					String item = JsonUtil.getFirstStringValueByKey(source, key);
					if(!EmptyUtil.isNullOrEmptyOrBlank(item)) {
						sb.append(item.trim()).append(connector);
					}
				}
				String href = JsonUtil.getFirstStringValueByKey(source, "carImage");
				String temp = sb.toString().replaceAll(connector + "$", "");
				
				if(EmptyUtil.isNullOrEmpty(href) && EmptyUtil.isNullOrEmpty(href)) {
					return;
				}
				
				mexItem = new Link(temp, href);
			}
		};
		
		return neymar.process().getMexItem();
	}

	public static List<Link> fetchHangyangPlates(String fuzzy) {
		Extractor<Link> neymar = new Extractor<Link>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String temp = "http://m.gwwg.com/wap/keytop/getFuzzyCarInfo.htm?plateNo=" + fuzzy;
				return temp;
			}
			
			@Override
			protected void parseContent() {
				String regex = "\\{(.+?)\\}";
				Matcher ma = createMatcher(regex, source);
				while(ma.find()) {
					String section = ma.group(1);
					String href = JsonUtil.getFirstStringValueByKey(section, "imgName");
					String plate = JsonUtil.getFirstStringValueByKey(section, "plateNo");
					String when = JsonUtil.getFirstStringValueByKey(section, "entryTime");
					mexItems.add(new Link(plate + ", " + when, href));
				}
			}
		};
		
		return neymar.process().getMexItems();
	}
	
	public static List<MexObject> fetchNanningPolice() {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String temp = "http://www.nngaj.gov.cn/Ajax/PoliceCoordinate.ashx?HandleType=GetPoliceCoordinateList";
				return temp;
			}
			
			@Override
			protected void parseContent() {
				String regex = "\\{(.+?)\\}";
				Matcher ma = createMatcher(regex, source);
				List<String> keys = StrUtil.split("Name,ADDRESS,PHONE1,PHONE2,Lon,Lat");
				String connector = ", ";
				while(ma.find()) {
					String section = ma.group(1);
					StringBuffer sb = StrUtil.sb();
					for(String key : keys) {
						String item = JsonUtil.getFirstStringValueByKey(section, key);
						if(!EmptyUtil.isNullOrEmptyOrBlank(item)) {
							sb.append(item.trim()).append(connector);
						}
					}
					String temp = sb.toString().replaceAll(connector + "$", "");
					mexItems.add(new MexObject(temp));
				}
			}
		};
		
		return neymar.process().getMexItems();
	}

	public static List<MexObject> fetchAnjukeCities() {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String temp = "https://www.anjuke.com/sy-city.html";
				return temp;
			}
			
			@Override
			protected void parseContent() {
				String regexDiv = "<label class=\"label_letter\">[a-z]</label>\\s*<div class=\"city_list\">(.+?)</div>";
				List<String> divs = StrUtil.findAllMatchedItems(regexDiv, source);

				String regex = "<a href=\"https://([^\"]+).anjuke.com\"([^<>]*)>([^<>]+)</a>";
				for(String div : divs) {
					Matcher ma = createMatcher(regex, div);
					while(ma.find()) {
						String code = ma.group(1);
						String name = ma.group(3);
						mexItems.add(new MexObject(code + " " + name));
					}
				}
			}
		};
		
		return neymar.process().getMexItems();
	}
	
	public static List<MexObject> fetchAnjukeHouse(String city, String town, int pageNumber) {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String temp = "https://{0}.anjuke.com/community/{1}p{2}";
				String townInfo = EmptyUtil.isNullOrEmpty(town) ? "": (town + "/");
				String url = StrUtil.occupy(temp, city, townInfo, pageNumber);
				return url;
			}
			
			@Override
			protected void parseContent() {
				String regex = "<div _soj=\"xqlb\"[^<>]+>\\s*";
				regex += "<a.+?/a>\\s*";
				regex = "<div class=\"li-info\">(.*?)</div>\\s*";
				regex += "<div class=\"li-side\">(.*?)</div>\\s";
				Matcher ma = createMatcher(regex, source);
				
				while(ma.find()) {
					String location = getPrettyText(ma.group(1));
					location = location.replaceAll("\\)[^\\)\\(]+", ")");
					String price = getPrettyText(ma.group(2));
					String temp = location + " " + price;
					mexItems.add(new MexObject(temp));
				}
			}
		};
		
		return neymar.process().getMexItems();
	}
	
	public static List<MexObject> fetchHupuFootballChinaTable() {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String url = "https://soccer.hupu.com/table/csl.html";
				return url;
			}
			
			private String[] columnNames(int len) {
				String regexTH = "";
				for(int k = 0; k < len; k++) {
					regexTH += "<th[^<>]*>(.+?)</th>\\s*";
				}
				String regexTR ="<tr>\\s*" + regexTH;
				String[] items = new String[len];
				Matcher ma = createMatcher(regexTR, source);
				if(ma.find()) {
					for(int k = 0; k < len; k++) {
						items[k] = ma.group(k + 1);
					}
				}
				
				return items;
			}

			@Override
			protected void parseContent() {
				int[] lens = {4, 12, 4, 2, 2, 2, 4, 4, 6, 4};
				String template = "";
				{
					for(int i = 0; i < lens.length; i++) {
						template += "{" + i + "}    ";
					}
					template = template.trim();
				}
				String[] header = columnNames(lens.length);
				{
					int i = 0, k = 0;
					String order = StrUtil.padRightAscii(header[i++], lens[k++]);
					String team = StrUtil.padRightAscii(header[i++], lens[k++]);
					String games = StrUtil.padLeftAscii(header[i++], lens[k++]);
					String win = StrUtil.padLeftAscii(header[i++], lens[k++]);
					String draw = StrUtil.padLeftAscii(header[i++], lens[k++]);
					String lose = StrUtil.padLeftAscii(header[i++], lens[k++]);
					String goals = StrUtil.padLeftAscii(header[i++], lens[k++]);
					String goals2 = StrUtil.padLeftAscii(header[i++], lens[k++]);
					String net = StrUtil.padLeftAscii(header[i++], lens[k++]);
					String points = StrUtil.padLeftAscii(header[i++], lens[k++]);
					String temp = StrUtil.occupy(template, order, team, games, win, draw, lose, goals, goals2, net, points);
					mexItems.add(new MexObject(temp));
				}

				String regexTD = "";
				for(int i = 0; i < lens.length; i++) {
					regexTD += "<td>([^<>]+)</td>\\s*";
				}
				String regex = "<tr[^<>]*>\\s*" + regexTD + "</tr>";
				Matcher ma = createMatcher(regex, source);
				
				while(ma.find()) {
					int i = 1, k = 0;
					String order = StrUtil.padRight("#" + getPrettyText(ma.group(i++)), lens[k++]);
					String team = StrUtil.padRightAscii(getPrettyText(ma.group(i++)), lens[k++]);
					String games = StrUtil.padLeft(ma.group(i++), lens[k++]);
					String win = StrUtil.padLeft(ma.group(i++), lens[k++]);
					String draw = StrUtil.padLeft(ma.group(i++), lens[k++]);
					String lose = StrUtil.padLeft(ma.group(i++), lens[k++]);
					String goals = StrUtil.padLeft(ma.group(i++), lens[k++]);
					String goals2 = StrUtil.padLeft(ma.group(i++), lens[k++]);
					String net = StrUtil.padLeft(ma.group(i++), lens[k++]);
					String points = StrUtil.padLeft(ma.group(i++), lens[k++]);
					String temp = StrUtil.occupy(template, order, team, games, win, draw, lose, goals, goals2, net, points);
					mexItems.add(new MexObject(temp));
				}
			}
		};
		
		return neymar.process().getMexItems();
	}
	
	public static List<MexObject> fetchUefaChampionsTable() {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String url = "https://soccer.hupu.com/uefa/table.php";
				return url;
			}
			
			private Object[] columnNames() {
				int len = 10;
				String regexTH = "";
				for(int k = 0; k < len; k++) {
					regexTH += "<th[^<>]*>([^<>]+)</th>\\s*";
				}
				String regexTR ="<tr[^<>]+>\\s*" + regexTH;
				String[] items = new String[len];
				Matcher ma = createMatcher(regexTR, source);
				if(ma.find()) {
					for(int k = 0; k < len; k++) {
						items[k] = ma.group(k + 1);
					}
				}
				
				return items;
			}

			@Override
			protected void parseContent() {
				String template = "{0}  {1}  {2}   {3}   {4}   {5}   {6}   {7}   {8}   {9}";
				String temp = StrUtil.occupy(template, columnNames());
				mexItems.add(new MexObject(temp));

				String regexTR = "<tr[^<>]*>";
				regexTR += "<td>([^<>]+)</td>\\s*";
				regexTR += "<td><a[^<>]+>([^<>]+)</a></td>\\s*";
				regexTR += "<td>([^<>]+)</td>\\s*";
				regexTR += "<td>([^<>]+)</td>\\s*";
				regexTR += "<td>([^<>]+)</td>\\s*";
				regexTR += "<td>([^<>]+)</td>\\s*";
				regexTR += "<td>([^<>]+)</td>\\s*";
				regexTR += "<td>([^<>]+)</td>\\s*";
				regexTR += "<td>([^<>]+)</td>\\s*";
				regexTR += "<td>([^<>]+)</td>\\s*";
				regexTR += "</tr>";
				Matcher ma = createMatcher(regexTR, source);
				
				int k = 0;
				char lastGroup = 'A';
				while(ma.find()) {
					int groupIndex = k / 4;
					k++;
					char car = (char)((int)'A' + groupIndex);
					if(car != lastGroup) {
						mexItems.add(new MexObject(""));
						lastGroup = car;
					}
					String order = car + ma.group(1);
					String team = StrUtil.padRightAscii(getPrettyText(ma.group(2)), 16);
					String games = ma.group(3);
					String win = ma.group(4);
					String draw = ma.group(5);
					String lose = ma.group(6);
					String goals = StrUtil.padLeft(ma.group(7), 3);
					String goals2 = StrUtil.padLeft(ma.group(8), 3);
					String net = StrUtil.padLeft(ma.group(9), 3);
					String points = StrUtil.padLeft(ma.group(10), 3);
					temp = StrUtil.occupy(template, order, team, games, win, draw, lose, goals, goals2, net, points);
					mexItems.add(new MexObject(temp));
				}
			}
		};
		
		return neymar.process().getMexItems();
	}

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
	
	public static List<MexObject> fetchHupuFootballScorers(int leagueId) {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String url = "https://soccer.hupu.com/scorers/{0}";
				return StrUtil.occupy(url, leagueId);
			}
			
			private Object[] columnNames(int len) {
				String regexTH = "";
				for(int k = 0; k < len; k++) {
					regexTH += "<th>(.+?)</th>\\s*";
				}
				String regexTR ="<tr[^<>]*>\\s*" + regexTH + "</tr>";
				String[] items = new String[len];
				Matcher ma = createMatcher(regexTR, source);
				if(ma.find()) {
					for(int k = 0; k < len; k++) {
						items[k] = ma.group(k + 1);
					}
				}
				
				return items;
			}
			
			private void conciseScorers() {
				int len = 4;
				String template = "{0}  {1}  {2}   {3}";
				String temp = StrUtil.occupy(template, columnNames(len));
				mexItems.add(new MexObject(temp));

				String regexTD = "";
				for(int k = 0; k < len; k++) {
					regexTD += "<td>(.+?)</td>\\s*";
				}
				String regexTR ="<tr[^<>]*>\\s*" + regexTD + "</tr>";
				Matcher ma = createMatcher(regexTR, source);
				
				while(ma.find()) {
					int i = 1;
					String order = "#" + StrUtil.padRight(getPrettyText(ma.group(i++)), 3);
					String playerCN = StrUtil.padRightAscii(getPrettyText(ma.group(i++)), 22);
					String team = StrUtil.padRightAscii(getPrettyText(ma.group(i++)), 16);
					String goals = StrUtil.padRight(getPrettyText(ma.group(i++)), 6);
					temp = StrUtil.occupy(template, order, playerCN, team, goals);
					mexItems.add(new MexObject(temp));
				}
			}

			@Override
			protected void parseContent() {
				if(leagueId == 128) {
					conciseScorers();
					return;
				}
				int len = 7;
				String template = "{0}  {1}  {2}   {3}   {4}   {5}   {6}";
				String temp = StrUtil.occupy(template, columnNames(len));
				mexItems.add(new MexObject(temp));

				String regexTD = "";
				for(int k = 0; k < len; k++) {
					regexTD += "<td>(.+?)</td>\\s*";
				}
				String regexTR ="<tr[^<>]*>\\s*" + regexTD + "</tr>";
				Matcher ma = createMatcher(regexTR, source);
				
				while(ma.find()) {
					int i = 1;
					String order = "#" + StrUtil.padRight(getPrettyText(ma.group(i++)), 3);
					String playerCN = StrUtil.padRightAscii(getPrettyText(ma.group(i++)), 22);
					String player = StrUtil.padRight(getPrettyText(ma.group(i++)), 26);
					String team = StrUtil.padRightAscii(getPrettyText(ma.group(i++)), 16);
					String position = StrUtil.padRightAscii(getPrettyText(ma.group(i++)), 4);
					String goals = StrUtil.padLeft(getPrettyText(ma.group(i++)), 3);
					String penalty = StrUtil.padRight(getPrettyText(ma.group(i++)), 3);
					temp = StrUtil.occupy(template, order, playerCN, player, team, position, goals, penalty);
					mexItems.add(new MexObject(temp));
				}
			}
		};
		
		return neymar.process().getMexItems();
	}

	public static List<SportsMatchItem> fetchHupuFootballSchedule(int leagueId) {
		Extractor<SportsMatchItem> neymar = new Extractor<SportsMatchItem>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String url = "https://soccer.hupu.com/schedule/schedule.server.php";
				return url;
			}

			@Override
			protected void parseContent() {
				String regex = "<tr[^<>]*>\\s*";
				regex += "<td class=\"hui\" title=\"([^\"]+)\">[^<>]+\\s([^<>]+)</td>\\s*";
				regex += "<td[^<>]*>(.+?)</td>\\s*";
				regex += "<td[^<>]*>(.+?)</td>\\s*";
				regex += "<td[^<>]*>(.+?)</td>\\s*";
				regex += "<td[^<>]*>(.+?)</td>\\s*";
				regex += "<td[^<>]*>(.+?)(|</a>)</span>\\s*</td>";
				
				Matcher ma = createMatcher(regex, source);
				int count = 0;
				while(ma.find()) {
					count++;
					SportsMatchItem item = new SportsMatchItem();
					item.setOrder(count + "");
					item.setDatetime(ma.group(1) + " " + getPrettyText(ma.group(2)));
					item.setWeekday(getPrettyText(ma.group(3)));
					item.setRound(getPrettyText(ma.group(4)));
					item.setHomeTeam(getPrettyText(ma.group(5)));
					item.setStatus(getPrettyText(ma.group(6)).replace(" ", ""));
					item.setAwayTeam(getPrettyText(ma.group(7)));
					mexItems.add(item);
				}
			}
		};
		
		neymar.setMethodPost(true);
		neymar.setRequestParams("league_id=" + leagueId);
		
		return neymar.process().getMexItems();
	}
	
	public static List<MexObject> fetchHupuFootballBig5Table(String leagueName) {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String url = "https://soccer.hupu.com/table/{0}.html";
				return StrUtil.occupy(url, leagueName);
			}
			
			private String[] columnNames() {
				int len = 15;
				String regexTH = "";
				for(int k = 0; k < len; k++) {
					regexTH += "<th width=[^<>]*>([^<>]+)</th>\\s*";
				}
				String regexTR ="<tr>\\s*" + regexTH;
				String[] items = new String[len];
				Matcher ma = createMatcher(regexTR, source);
				if(ma.find()) {
					for(int k = 0; k < len; k++) {
						items[k] = ma.group(k + 1);
					}
				}
				
				return items;
			}

			@Override
			protected void parseContent() {
				String space2 = "  ";
				List<String> list = Arrays.asList(columnNames());
				mexItems.add(new MexObject(StrUtil.connect(list, space2)));

				int len = 12;
				String regexTR = "<tr class=[^<>]+>\\s*";
				regexTR += "<td>([^<>]+)</td>\\s*";
				regexTR += "<td><img[^<>]+/></td>\\s*";
				regexTR += "<td><a[^<>]+>([^<>]+)</a></td>\\s*";
				for(int k = 0; k < len; k++) {
					regexTR += "<td>([^<>]+)</td>\\s*";
				}
				regexTR += "</tr>";
				Matcher ma = createMatcher(regexTR, source);

				while(ma.find()) {
					StringBuffer sb = StrUtil.sb();
					String goodOrder = "#" + StrUtil.padRight(ma.group(1), 2);
					sb.append(goodOrder).append(space2);
					String team = StrUtil.padRightAscii(getPrettyText(ma.group(2)), 12);
					sb.append(team).append(space2);
					for(int k = 0; k < len; k++) {
						String item = StrUtil.padLeft(ma.group(k + 3), 5);
						sb.append(item).append(space2);
					}
					mexItems.add(new MexObject(sb.toString()));
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

	public static List<MexObject> fetchCarNoList() {
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

	public static List<MexItem> fetchCarList() {
		Extractor<MexItem> neymar = new Extractor<MexItem>() {

			@Override
			public String getUrl() {
				useGBK();
				printFetching = true;
				return "http://data.ecar168.cn";
			}
			
			@Override
			protected void parseContent() {
				String regex = "\"/selectcar/([^\"]+)\">([^<>]+)</a></dd";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					KeyValuesItem item = new KeyValuesItem("id", ma.group(1));
					item.add("name", ma.group(2));
					mexItems.add(item);
				}
			}
		};
		
		return neymar.process().getMexItems();
	}

	public static List<MexItem> fetchCarDetail(Object id) {
		Extractor<MexItem> neymar = new Extractor<MexItem>() {

			@Override
			public String getUrl() {
				useGBK();
				printFetching = true;
				String temp = "http://data.ecar168.cn/car/" + id;
				return temp;
			}
			
			@Override
			protected void parseContent() {
				ValuesItem vi = new ValuesItem();
				
				String name = StrUtil.findFirstMatchedItem(">([^<>]+)</a></h3>", source);
				vi.add(name);

				String price = StrUtil.findFirstMatchedItem("<span class=\"jiage\">([^<>]+)</span>", source);
				vi.add(price);
				
				String regex = "</label>([^<>]+)<";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					vi.add(getPrettyText(ma.group(1)));
				}
				
				mexItems.add(vi);
			}
		};
		
		return neymar.process().getMexItems();
	}
}
