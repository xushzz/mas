package com.sirap.geek.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.basic.util.XmlUtil;
import com.sirap.geek.domain.DistrictItem;

public class GaodeUtils {
	
	public static final String GITEE_URL_CHINA_DISTRICTS = "https://gitee.com/thewire/stamina/raw/master/china/amapChinaDistricts.txt";
	public static final String LEVEL_PROVINCE = "province";
	public static final String API_KEY = "02a37b38c4b33a671f1cd1584b54adc8";
	public static final String TEMPLATE_DISTRICT = "http://restapi.amap.com/v3/config/district?extensions=base&subdistrict={0}&keywords={1}&key={2}";
	public static final String TEMPLATE_GEOCODE = "http://restapi.amap.com/v3/geocode/geo?address={0}&city={1}&key={2}";
	public static final String TEMPLATE_REGEOCODE = "http://restapi.amap.com/v3/geocode/regeo?location={0}&radius={1}&key={2}";
	public static final String TEMPLATE_INPUTTIPS = "http://restapi.amap.com/v3/assistant/inputtips?key={0}&keywords={1}";
	public static final String TEMPLATE_PLACE_TEXT = "http://restapi.amap.com/v3/place/text?output=xml&offset=1000&key={0}&keywords={1}&types={2}&city={3}";
	public static final String TEMPLATE_PLACE_AROUND = "http://restapi.amap.com/v3/place/around?output=xml&offset=1000&key={0}&keywords={1}&types={2}&location={3}&radius={4}";
	public static final String TEMPLATE_DISTANCE = "http://restapi.amap.com/v3/distance?origins={0}&destination={1}&output=xml&key={2}";

	public static List<DistrictItem> provincesOfChina() {
		Extractor<DistrictItem> neymar = new Extractor<DistrictItem>() {

			public String getUrl() {
				showFetching().useUTF8();
				String zhongguo = "%E4%B8%AD%E5%9B%BD";
				String url = StrUtil.occupy(TEMPLATE_DISTRICT, 1, zhongguo, API_KEY);
				return url;
			}
			
			@Override
			protected void parse() {
				mexItems = parseDistrictItems(source);
			}
		};
		
		return neymar.process().getItems();
	}
	
	public static List<DistrictItem> fetchAllDistricts() {
		Extractor<DistrictItem> neymar = new Extractor<DistrictItem>() {

			public String getUrl() {
				showFetching().useUTF8();
				String zhongguo = "%E4%B8%AD%E5%9B%BD";
				String url = StrUtil.occupy(TEMPLATE_DISTRICT, 1, zhongguo, API_KEY);
				return url;
			}
			
			@Override
			protected void parse() {
				List<DistrictItem> items = parseDistrictItems(source);
				int count = 0;
				for(DistrictItem item : items) {
					String level = item.getLevel();
					if(!StrUtil.equals(level, LEVEL_PROVINCE)) {
						continue;
					}
					if(count++ > 1) {
						break;
					}
					String raw = districtsOf(item.getAdcode(), "3");
					List<DistrictItem> posterity = parseDistrictItems(raw);
					mexItems.addAll(posterity);
				}
			}
		};
		
		return neymar.process().getItems();
	}
	
	/****
	 * http://lbs.amap.com/api/webservice/guide/api/district
	 * 行政区域查询是一类简单的HTTP接口，根据用户输入的搜索条件可以帮助用户快速的查找特定的行政区域信息。
	 * @param keyword
	 * @param level
	 * @return
	 */
	public static String districtsOf(String keyword, String level) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_DISTRICT, level, encodeURLParam(keyword), API_KEY);
				C.pl(lax() + StrUtil.occupy(TEMPLATE_DISTRICT, level, keyword, API_KEY));
				return url;
			}
			
			@Override
			protected void parse() {
				item = JsonUtil.getPrettyText(source);
			}
		};
		
		return neymar.process().getItem();
	}

	/***
	 * http://lbs.amap.com/api/webservice/guide/api/search
	 * http://restapi.amap.com/v3/place/text
	 * @param keyword
	 * @param level
	 * @return
	 */
	public static List<String> searchPlaceText(String keywords, String types, String city) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_PLACE_TEXT, API_KEY, encodeURLParam(keywords), encodeURLParam(types), encodeURLParam(city));
				C.pl(lax() + StrUtil.occupy(TEMPLATE_PLACE_TEXT, API_KEY, keywords, types, city));
				return url;
			}
			
			@Override
			protected void parse() {
				String regex = "<poi>(.+?)</poi>";
				List<String> keys = StrUtil.split("pname,cityname,adname,name,type,address,tel,location");
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String xmlText = ma.group(1);
					List<String> items = Lists.newArrayList();
					for(String key : keys) {
						String value = XmlUtil.readValue(xmlText, key);
						if(EmptyUtil.isNullOrEmpty(value)) {
							continue;
						}
						items.add(value);
					}
					mexItems.add(StrUtil.connect(items, " "));
				}
				Collections.sort(mexItems);
			}
		};
		
		return neymar.process().getItems();
	}

	/***
	 * http://lbs.amap.com/api/webservice/guide/api/search
	 * http://restapi.amap.com/v3/place/text
	 * @param keyword
	 * @param level
	 * @return
	 */
	public static List<String> searchPlaceAround(String location, String keywords, String types, String radius) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_PLACE_AROUND, API_KEY, encodeURLParam(keywords), encodeURLParam(types), location.replace(" ", "0"), encodeURLParam(radius));
				C.pl(lax() + StrUtil.occupy(TEMPLATE_PLACE_AROUND, API_KEY, keywords, types, location, radius));
				return url;
			}
			
			@Override
			protected void parse() {
				TreeMap<Integer, String> ken = new TreeMap<>();
				String regex = "<poi>(.+?)</poi>";
				List<String> keys = StrUtil.split("distance,pname,cityname,adname,name,type,address,tel,location");
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String xmlText = ma.group(1);
					List<String> items = Lists.newArrayList();
					for(String key : keys) {
						String value = XmlUtil.readValue(xmlText, key);
						if(EmptyUtil.isNullOrEmpty(value)) {
							continue;
						}
						items.add(value);
					}
					String distance = XmlUtil.readValue(xmlText, "distance");
					int ace = MathUtil.toInteger(distance, 0);
					ken.put(ace, StrUtil.connect(items, " "));
				}
				mexItems.addAll(ken.values());
			}
		};
		
		return neymar.process().getItems();
	}
	
	public static String distance(String originLocation, String destLocation) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_DISTANCE, originLocation, destLocation, API_KEY);
				return url;
			}
			
			@Override
			protected void parse() {
				String distance = XmlUtil.readValue(source, "distance");
				item = distance + " M";
			}
		};
		
		return neymar.process().getItem();
	}

	public static List<ValuesItem> allDistricts() {
		Extractor<ValuesItem> neymar = new Extractor<ValuesItem>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = "E:/Mas/exp/beijing.txt";
				return url;
			}
			
			@Override
			protected void parse() {
				StringBuffer sb = StrUtil.sb();
				String temp = "\"{0}\":\"([^\"]+)\",\\s*";
				sb.append(StrUtil.occupy(temp, "adcode"));
				sb.append(StrUtil.occupy(temp, "name"));
				sb.append(StrUtil.occupy(temp, "center"));
				sb.append(StrUtil.occupy(temp, "level"));
				String regex = sb.toString();
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					ValuesItem item = new ValuesItem();
					item.add(ma.group(1));
					item.add(ma.group(2));
					item.add(ma.group(3));
					item.add(ma.group(4));
					mexItems.add(item);
				}
			}
		};
		
		return neymar.process().getItems();
	}
	
	/***
	 * http://lbs.amap.com/api/webservice/guide/api/georegeo
	 * 地理编码/逆地理编码 API 是通过 HTTP/HTTPS 协议访问远程服务的接口，提供结构化地址与经纬度之间的相互转化的能力。
	 * @param address
	 * @param city
	 * @return
	 */
	public static List<String> geocodeOf(String address, String city) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_GEOCODE, encodeURLParam(address), encodeURLParam(city), API_KEY);
				C.pl(lax() + StrUtil.occupy(TEMPLATE_GEOCODE, address, city, API_KEY));
				return url;
			}
			
			@Override
			protected void parse() {
				mexItems = JsonUtil.getPrettyTextInLines(source);
			}
		};
		
		return neymar.process().getItems();
	}
	
	/***
	 * http://lbs.amap.com/api/webservice/guide/api/georegeo
	 * @param location
	 * @param radius
	 * @return
	 */
	public static List<String> regeocodeOf(String location, String radius) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_REGEOCODE, location.replace(" ", ""), radius, API_KEY);
				return url;
			}
			
			@Override
			protected void parse() {
				mexItems = JsonUtil.getPrettyTextInLines(source);
			}
		};
		
		return neymar.process().getItems();
	}

	/***
	 * http://lbs.amap.com/api/javascript-api/example/poi-search/input-prompt
	 * @param location
	 * @param radius
	 * @return
	 */
	public static List<ValuesItem> tipsOf(String input) {
		Extractor<ValuesItem> neymar = new Extractor<ValuesItem>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_INPUTTIPS, API_KEY, encodeURLParam(input));
				C.pl(lax() + StrUtil.occupy(TEMPLATE_INPUTTIPS, API_KEY, input));
				return url;
			}
			
			@Override
			protected void parse() {
				mexItems = parseInputTips(source);
			}
		};
		
		return neymar.process().getItems();
	}
	
	public static List<DistrictItem> parseDistrictItems(String json) {
		StringBuffer sb = StrUtil.sb();
		String temp = "\"{0}\":\"([^\"]+)\",\\s*";
		sb.append(StrUtil.occupy(temp, "adcode"));
		sb.append(StrUtil.occupy(temp, "name"));
		sb.append(StrUtil.occupy(temp, "center"));
		sb.append(StrUtil.occupy(temp, "level"));
		String regex = sb.toString();
		
		List<DistrictItem> mexItems = new ArrayList<>();
		Matcher ma = StrUtil.createMatcher(regex, json);
		while(ma.find()) {
			DistrictItem item = new DistrictItem();
			item.setAdcode(ma.group(1));
			item.setName(ma.group(2));
			item.setCenter(ma.group(3));
			item.setLevel(ma.group(4));
			
			mexItems.add(item);
		}
		
		Collections.sort(mexItems);
		
		return mexItems;
	}
	
	public static List<ValuesItem> parseInputTips(String json) {
		StringBuffer sb = StrUtil.sb();
		String temp = "\"{0}\":\"([^\"]+)\",\\s*";
		sb.append(StrUtil.occupy(temp, "name"));
		sb.append(StrUtil.occupy(temp, "district"));
		sb.append(StrUtil.occupy(temp, "adcode"));
		sb.append(StrUtil.occupy(temp, "location"));
		sb.append(StrUtil.occupy(temp, "address"));
		String regex = sb.toString();
		
		List<ValuesItem> mexItems = new ArrayList<>();
		Matcher ma = StrUtil.createMatcher(regex, json);
		while(ma.find()) {
			ValuesItem item = new ValuesItem();
			item.add(ma.group(3));
			item.add(ma.group(4));
			item.add(ma.group(1));
			item.add(ma.group(2));
			item.add(ma.group(5));
			
			mexItems.add(item);
		}
		
		return mexItems;
	}
	
	public static boolean isCoordination(String longAndLat) {
		String regex = Konstants.REGEX_SIGN_FLOAT + "\\s*,\\s*" + Konstants.REGEX_SIGN_FLOAT;
		return StrUtil.isRegexMatched(regex, longAndLat);
	}
	
	public static String reverseLongAndLat(String longAndLat) {
		String regex = Konstants.REGEX_SIGN_FLOAT + "\\s*,\\s*" + Konstants.REGEX_SIGN_FLOAT;
		String[] params = StrUtil.parseParams(regex, longAndLat);
		if(params == null) {
			XXXUtil.alert("Invalid coordination: " + longAndLat);
		}
		
		return params[1] + "," + params[0]; 
	}
	
	private static String lax() {
		String head = "###";
		String temp = StrUtil.padRight(head, "fetching...".length() + 1);

		return temp;
	}
}
