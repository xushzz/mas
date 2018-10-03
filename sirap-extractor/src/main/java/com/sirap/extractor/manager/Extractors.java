package com.sirap.extractor.manager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.component.Mist;
import com.sirap.basic.domain.KeyValuesItem;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.thread.MasterItemsOriented;
import com.sirap.basic.thread.WorkerItemsOriented;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.MistUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.domain.Link;
import com.sirap.extractor.domain.MeijuRateItem;
import com.sirap.extractor.domain.NameRankItem;
import com.sirap.extractor.domain.SportsMatchItem;

public class Extractors {
	
	public static List<String> topPasswordsSqls(String filepath) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				useList().showFetching();
				return filepath;
			}
			
			@Override
			protected void parse() {
				String max = "";
				String sqlTemp = "insert into top_pwd values({0}, '{1}', '{2}');";
				for(String line : sourceList) {
					List<String> items = StrUtil.split(line);
					if(items.size() != 3) {
						continue;
					}
					String pwd = items.get(1);
					if(pwd.length() > max.length()) {
						max = pwd;
					}
					mexItems.add(StrUtil.occupy(sqlTemp, items.get(0), items.get(1), items.get(2)));
				}
				C.pl(max);
			}
		};
		
		return neymar.process().getItems();
	}
	
	public static List<ValuesItem> topPasswordsAll(int maxPageNumber) {
		List<Integer> pages = Lists.newArrayList();
		for(int i = 1; i <= maxPageNumber; i++) {
			pages.add(i);
		}
		MasterItemsOriented<Integer, ValuesItem> george = new MasterItemsOriented<>(pages, new WorkerItemsOriented<Integer, ValuesItem>() {

			@Override
			public List<ValuesItem> process(Integer page) {
				int count = queue.size() + 1;
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "async dealing with page ", page);
				List<ValuesItem> items = topPasswords(page);
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "async done with page", page);
				return items;
			}
			
		});
		
		List<ValuesItem> values = george.getAllMexItems();
		Collections.sort(values, new Comparator<ValuesItem>(){

			@Override
			public int compare(ValuesItem va, ValuesItem vb) {
				return orderOf(va) - orderOf(vb);
			}
			
			private int orderOf(ValuesItem item) {
				String obj = item.getByIndex(0) + "";
				return MathUtil.toInteger(obj, 0);
			}
			
		});

		return values;
	}
	
	public static List<ValuesItem> topPasswords(int pageNumber) {
		Extractor<ValuesItem> neymar = new Extractor<ValuesItem>() {

			public String getUrl() {
				showFetching().useGBK();
				String template = "http://www.passwordrandom.com/most-popular-passwords/page/{0}";
				return StrUtil.occupy(template, pageNumber);
			}
			
			@Override
			protected void parse() {
				//<tr><td>1</td><td>password</td><td>5f4dcc3b5aa765d61d8327deb882cf99</td>
				String regex = "<tr>\\s+<td>(\\d{1,5})</td><td>([^<>]+)</td><td>([a-z\\d]{32})</td>";
				Matcher ma = createMatcher(regex);
				
				while(ma.find()) {
					ValuesItem item = new ValuesItem();
					item.add(ma.group(1));
					item.add(ma.group(2));
					item.add(ma.group(3));
					mexItems.add(item);
				}
			}
		};
		
		return neymar.process().getItems();
	}
	
	public static List<MeijuRateItem> fetchTopMeijus() {
		Extractor<MeijuRateItem> neymar = new Extractor<MeijuRateItem>() {

			public String getUrl() {
				showFetching().useGBK();
				String template = "http://www.meijutt.com/alltop_hit.html";
				return template;
			}
			
			@Override
			protected void parse() {
				String regex = "<h5><a href=\".+?</div></li>";
				Matcher ma = createMatcher(regex);
				
				while(ma.find()) {
					String raw = ma.group().replace("<strong class=\"average-big\">", "XXXX");
					String[] arce = raw.split("XXXX");
					String name = getPrettyText(arce[0]);
					String rate = getPrettyText(arce[1]);
					String href = "http://www.meijutt.com" + StrUtil.findFirstMatchedItem("href=\"([^\"]+)\"", arce[0]);
					MeijuRateItem item = new MeijuRateItem();
					item.setHref(href);
					item.setName(name);
					item.setRate(rate);
					mexItems.add(item);
				}
			}
		};
		
		return neymar.process().getItems();
	}
	
	public static List<String> fetchMeiju(String param) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().usePost().useGBK();
				String template = "http://www.meijutt.com/search.asp?searchword=";
				return template + param.replace(' ', '+');
			}
			
			@Override
			protected void parse() {
				String regex = "<div class=\"bor_img3_right\">(.+?)</font></li></ul></div>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String raw = ma.group(1).replace("</li><li>", " ");
					mexItems.add(getPrettyText(raw));
				}
			}
		};
		
		return neymar.process().getItems();
	}

	public static List<NameRankItem> fetchEnglishNames() {
		return fetchEnglishNames(true, null);
	}
	
	public static List<NameRankItem> fetchEnglishNames(boolean isMale, String criteria) {
		Extractor<NameRankItem> neymar = new Extractor<NameRankItem>() {

			public String getUrl() {
				useList().showFetching();
				String template = "https://gitee.com/thewire/stamina/raw/master/names/{0}males.txt";
				template = "#data/{0}males.txt";
				String key = isMale ? "" : "fe";
				return StrUtil.occupy(template, key);
			}
			
			@Override
			protected void parse() {
				for(String line : sourceList) {
					List<String> items = StrUtil.split(line);
					if(items != null && items.size() > 1) {
						NameRankItem item = new NameRankItem();
						item.setName(items.get(0));
						item.setRank(items.get(1));
						if(EmptyUtil.isNullOrEmpty(criteria) || item.isMexMatched(criteria)) {
							mexItems.add(item);
						}
					}
				}
			}
		};
		
		return neymar.process().getItems();
	}

	public static List<NameRankItem> fetchEnglishNamesRaw(boolean isMale, String criteria) {
		Extractor<NameRankItem> neymar = new Extractor<NameRankItem>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String template = "https://names.mongabay.com/{0}male_names_alpha.htm";
				String key = isMale ? "" : "fe";
				return StrUtil.occupy(template, key);
			}
			
			@Override
			protected void parse() {
				//<tr><td>AARON</td><td>0.002</td><td> 3,036 </td><td>2701</td></tr>
				String regex = "<tr><td>([^<>]+)</td><td>[^<>]+</td><td>[^<>]+</td><td>([^<>]+)</td></tr>";
				Matcher ma = createMatcher(regex, source);
				while(ma.find()) {
					NameRankItem item = new NameRankItem();
					item.setName(ma.group(1));
					item.setRank(ma.group(2));
					if(EmptyUtil.isNullOrEmpty(criteria) || item.isMexMatched(criteria)) {
						mexItems.add(item);
					}
				}
			}
		};
		
		return neymar.process().getItems();
	}

	public static List<String> fetchJapaneseNames() {
		Extractor<String> neymar = new Extractor<String>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String temp = "http://www.for68.com/new/201006/li1871181518601022640.htm";
				return temp;
			}
			
			@Override
			protected void parse() {
				String regex = "</p><p>(.+?)</p></div>";
				String names = StrUtil.findFirstMatchedItem(regex, source);
				List<String> list = StrUtil.splitByRegex(names, "<br\\s*/>");
				for(String item : list) {
					String temp = item.replaceAll("^([^a-zA-Z]+)", "").trim();
					mexItems.add(temp);
				}
			}
		};
		
		return neymar.process().getItems();
	}

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
			protected void parse() {
				String fields = "floorName,area,spaceNo";
				Mist mist = MistUtil.ofJsonText(source);
				List<String> keys = StrUtil.split(fields);
				String connector = ", ";
				StringBuffer sb = StrUtil.sb();
				for(String key : keys) {
					String item = mist.findBy(key) + "";
					if(!EmptyUtil.isNullOrEmptyOrBlank(item)) {
						sb.append(item.trim()).append(connector);
					}
				}
				String href =  mist.findBy("carImage") + "";
				String temp = sb.toString().replaceAll(connector + "$", "");
				
				if(EmptyUtil.isNullOrEmpty(href)) {
					return;
				}
				
				item = new Link(temp, href);
			}
		};
		
		return neymar.process().getItem();
	}

	public static List<KeyValuesItem> fetchHangyangPlates(String fuzzy) {
		Extractor<KeyValuesItem> neymar = new Extractor<KeyValuesItem>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String temp = "http://m.gwwg.com/wap/keytop/getFuzzyCarInfo.htm?plateNo=" + fuzzy;
				return temp;
			}
			
			@Override
			protected void parse() {
				Object sections = MistUtil.ofJsonText(source).findBy("carinfos");
				if(!List.class.isInstance(sections)) {
					XXXUtil.alerto("Uncanny, section should be List:\n{0}", sections);
					return;
				}
				
				List list = (List)sections;
				for(Object section : list) {
					String image = MistUtil.ofMapOrList(section).findBy("imgName") + "";
					String plate = MistUtil.ofMapOrList(section).findBy("plateNo") + "";
					String when = MistUtil.ofMapOrList(section).findBy("entryTime") + "";
					KeyValuesItem item = new KeyValuesItem();
					item.add("plate", plate);
					item.add("when", when);
					item.add("image", image);
					mexItems.add(item);
				}
			}
		};
		
		return neymar.process().getItems();
	}
	
	public static List<List<String>> fetchNanningPolice() {
		Extractor<List<String>> neymar = new Extractor<List<String>>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String temp = "http://www.nngaj.gov.cn/Ajax/PoliceCoordinate.ashx?HandleType=GetPoliceCoordinateList";
				return temp;
			}
			
			@Override
			protected void parse() {
				String regex = "\\{(.+?)\\}";
				Matcher ma = createMatcher(regex, source);
				while(ma.find()) {
					String section = ma.group(1);
					String name = JsonUtil.getFirstStringValueByKey(section, "Name");
					String address = JsonUtil.getFirstStringValueByKey(section, "ADDRESS");
					String phone = JsonUtil.getFirstStringValueByKey(section, "PHONE1");
					phone += " " + JsonUtil.getFirstStringValueByKey(section, "PHONE2");
					phone = phone.replace("null", "").trim();
					String location = JsonUtil.getFirstStringValueByKey(section, "Lon");
					location += "," + JsonUtil.getFirstStringValueByKey(section, "Lat");
					location = location.replace("null", "").trim();
					
					List<String> lines = Lists.newArrayList();
					lines.add(name);
					lines.add(phone);
					lines.add(address);
					lines.add(location);
					mexItems.add(lines);
				}
			}
		};
		
		return neymar.process().getItems();
	}

	public static List<String> fetchAnjukeCities() {
		Extractor<String> neymar = new Extractor<String>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String temp = "https://www.anjuke.com/sy-city.html";
				return temp;
			}
			
			@Override
			protected void parse() {
				String regexDiv = "<label class=\"label_letter\">[a-z]</label>\\s*<div class=\"city_list\">(.+?)</div>";
				List<String> divs = StrUtil.findAllMatchedItems(regexDiv, source);

				String regex = "<a href=\"https://([^\"]+).anjuke.com\"([^<>]*)>([^<>]+)</a>";
				for(String div : divs) {
					Matcher ma = createMatcher(regex, div);
					while(ma.find()) {
						String code = ma.group(1);
						String name = ma.group(3);
						mexItems.add(code + " " + name);
					}
				}
			}
		};
		
		return neymar.process().getItems();
	}
	
	public static List<String> fetchAnjukeHouse(String city, String town, int pageNumber) {
		Extractor<String> neymar = new Extractor<String>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String temp = "https://{0}.anjuke.com/community/{1}p{2}";
				String townInfo = EmptyUtil.isNullOrEmpty(town) ? "": (town + "/");
				String url = StrUtil.occupy(temp, city, townInfo, pageNumber);
				return url;
			}
			
			@Override
			protected void parse() {
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
					mexItems.add(temp);
				}
			}
		};
		
		return neymar.process().getItems();
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
			protected void parse() {
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
		
		return neymar.process().getItems();
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
			protected void parse() {
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
		
		return neymar.process().getItems();
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
			protected void parse() {
				String regex = "<tr align=\"center\">\\s*";
				regex += "<td>([^<>]+)</td>\\s*";
				regex += "<td>([^<>]+)</td>\\s*";
				regex += "<td>([^<>]+)</td>\\s*";
				regex += "<td>([^<>]*)</td>\\s*";
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
					String group = ma.group(4).trim();
					if(group.isEmpty()) {
						group = " ";
					}
					item.setGroup(group);
					item.setHomeTeam(getPrettyText(ma.group(5)));
					item.setStatus(getPrettyText(ma.group(6)).replace(" ", ""));
					item.setAwayTeam(getPrettyText(ma.group(7)));
					mexItems.add(item);
				}
			}
		};
		
		return neymar.process().getItems();
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
			protected void parse() {
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
		
		return neymar.process().getItems();
	}

	public static List<SportsMatchItem> fetchHupuFootballSchedule(int leagueId) {
		Extractor<SportsMatchItem> neymar = new Extractor<SportsMatchItem>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String url = "https://soccer.hupu.com/schedule/schedule.server.php?league_id=" + leagueId;
				return url;
			}

			@Override
			protected void parse() {
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
		
		neymar.usePost();
		
		return neymar.process().getItems();
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
			protected void parse() {
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
		
		return neymar.process().getItems();
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
			protected void parse() {
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
		
		return neymar.process().getItems();
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
			protected void parse() {
				String regex = "<li>\\[<a[^<>]+>[^<>]+</a>[^<>]+<a href=\"http://jintian.cidianwang.com/([^/]+)/[^<>]+>([^<>]+)</a></li>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String temp = yearInfo.toUpperCase() + "/" + ma.group(1).replace('-', '/') + " " + getPrettyText(ma.group(2));
					mexItems.add(new MexObject(temp));
				}
			}
		};
		
		return neymar.process().getItems();
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
			protected void parse() {
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
		
		return neymar.process().getItems();
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
			protected void parse() {
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
		
		return neymar.process().getItems();
	}

	public static List<MexObject> fetchCarNoList() {
		Extractor<MexObject> neymar = new Extractor<MexObject>() {

			@Override
			public String getUrl() {
				printFetching = true;
				return "http://www.ip138.com/carlist.htm";
			}
			
			@Override
			protected void parse() {
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
		
		return neymar.process().getItems();
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
			protected void parse() {
				String regex = "\"/selectcar/([^\"]+)\">([^<>]+)</a></dd";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					KeyValuesItem item = new KeyValuesItem("id", ma.group(1));
					item.add("name", ma.group(2));
					mexItems.add(item);
				}
			}
		};
		
		return neymar.process().getItems();
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
			protected void parse() {
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
		
		return neymar.process().getItems();
	}
}
