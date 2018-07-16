package com.sirap.geek.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.NetworkUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.basic.util.XmlUtil;
import com.sirap.geek.data.LonglatData;
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
	public static final String TEMPLATE_IP_NOPARAM = "http://restapi.amap.com/v3/ip?key={0}";
	public static final String TEMPLATE_IP = "http://restapi.amap.com/v3/ip?key={0}&ip={1}";
	public static final String METER_CHINESE_MI = XCodeUtil.urlDecodeUTF8("%E7%B1%B3");
	public static final String CHINESE_KILOMETER_GONGLI = XCodeUtil.urlDecodeUTF8("%E5%85%AC%E9%87%8C");

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
	public static List<ValuesItem> searchPlaceText(String keywords, String types, String city) {
		Extractor<ValuesItem> neymar = new Extractor<ValuesItem>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_PLACE_TEXT, API_KEY, encodeURLParam(keywords), encodeURLParam(types), encodeURLParam(city));
				C.pl(lax() + StrUtil.occupy(TEMPLATE_PLACE_TEXT, API_KEY, keywords, types, city));
				return url;
			}
			
			@Override
			protected void parse() {
				String regex = "<poi>(.+?)</poi>";
				List<String> keys = StrUtil.split("cityname,adname,name,type,address,tel,location");
				Matcher ma = createMatcher(regex);
				List<String> templist = Lists.newArrayList();
				while(ma.find()) {
					String xmlText = ma.group(1);
					List<String> items = Lists.newArrayList();
					for(String key : keys) {
						String value = XmlUtil.readValue(xmlText, key);
						if(EmptyUtil.isNullOrEmpty(value)) {
							continue;
						}
						if(StrUtil.equals(key, "location")) {
							items.add(tieLocation(value));
						} else {
							items.add(value);
						}
					}
					templist.add(StrUtil.connect(items, " "));
				}
				Collections.sort(templist);
				int count = 0;
				for(String item : templist) {
					ValuesItem vi = new ValuesItem(item);
					vi.setPseudoOrder(++count);
					mexItems.add(vi);
				}
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
	public static List<ValuesItem> searchPlaceAround(String location, String keywords, String types, String radius) {
		Extractor<ValuesItem> neymar = new Extractor<ValuesItem>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_PLACE_AROUND, API_KEY, encodeURLParam(keywords), encodeURLParam(types), location.replace(" ", "0"), encodeURLParam(radius));
				C.pl(lax() + StrUtil.occupy(TEMPLATE_PLACE_AROUND, API_KEY, keywords, types, location, radius));
				return url;
			}
			
			@Override
			protected void parse() {
				String regex = "<poi>(.+?)</poi>";
				List<String> keys = StrUtil.split("distance,cityname,adname,name,type,address,tel,location");
				Matcher ma = createMatcher(regex);
				int count = 0;
				while(ma.find()) {
					String xmlText = ma.group(1);
					ValuesItem vi = new ValuesItem();
					vi.setPseudoOrder(++count);
					
					for(String key : keys) {
						String value = XmlUtil.readValue(xmlText, key);
						if(EmptyUtil.isNullOrEmpty(value)) {
							continue;
						}
						if(StrUtil.equals(key, "distance")) {
							vi.add(niceDistance(MathUtil.toInteger(value, 0)));
						} else if(StrUtil.equals(key, "location")) {
							vi.add(tieLocation(value));
						} else {
							vi.add(value);
						}
					}
					
					mexItems.add(vi);
				}
			}
		};
		
		return neymar.process().getItems();
	}
	
	/****
	 * https://lbs.amap.com/api/webservice/guide/api/direction
	 * @param originLocation
	 * @param destLocation
	 * @return
	 */
	public static int[] distanceAndDuration(String originLocation, String destLocation) {
		Extractor<int[]> neymar = new Extractor<int[]>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_DISTANCE, originLocation, destLocation, API_KEY);
				return url;
			}
			
			@Override
			protected void parse() {
				String distanceInMeter = XmlUtil.readValue(source, "distance");
				Integer distance = MathUtil.toInteger(distanceInMeter);
				if(distance == null || distance <= 0) {
					String info = StrUtil.findFirstMatchedItem("<info>([^a-z]+)</info>", source);
					XXXUtil.alert(info);
				}
				
				String durationInSecond = XmlUtil.readValue(source, "duration");
				Integer duration = MathUtil.toInteger(durationInSecond, 1);
				
				item = new int[]{distance, duration};
			}
		};
		
		return neymar.process().getItem();
	}
	
	/****
	 * 1000m
	 * @param distanceInMeter
	 * @return
	 */
	public static String niceDistance(long distanceInMeter) {
		if(distanceInMeter < 1000) {
			return distanceInMeter + METER_CHINESE_MI;
		} else {
			String temp = MathUtil.setDoubleScale(distanceInMeter/1000.0, 1);
			temp = StrUtil.removePointZeroes(temp) + CHINESE_KILOMETER_GONGLI;
			
			return temp;
		}
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
	 * http://lbs.amap.com/api/webservice/guide/api/ipconfig/
	 * IP定位是一个简单的HTTP接口，根据用户输入的IP地址，能够快速的帮用户定位IP的所在位置。
	 * @param address
	 * @param city
	 * @return
	 */
	public static List<String> locationOfIp(String ipAddress) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = "";
				if(NetworkUtil.isLegalIP(ipAddress)) {
					url = StrUtil.occupy(TEMPLATE_IP, API_KEY, ipAddress);
				} else {
					XXXUtil.alert("Not a valid ip: ", ipAddress);
				}
				return url;
			}
			
			@Override
			protected void parse() {
				mexItems = JsonUtil.getPrettyTextInLines(source);
			}
		};
		
		return neymar.process().getItems();
	}
	
	public static List<String> locationOfIp() {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_IP_NOPARAM, API_KEY);
				return url;
			}
			
			@Override
			protected void parse() {
				mexItems = JsonUtil.getPrettyTextInLines(source);
			}
		};
		
		return neymar.process().getItems();
	}

	public static String locationOf(String address, String city) {
		List<String> lines = geocodeOf(address, city);
		String regex = "\"location\"\\s*:\\s*\"([^\"]+)\"";
		for(String line : lines) {
			Matcher ma = StrUtil.createMatcher(regex, line);
			if(ma.find()) {
				String value = ma.group(1);
				return value;
			}
		}

		return null;
	}

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
		int count = 0;
		while(ma.find()) {
			ValuesItem item = new ValuesItem();
			item.setPseudoOrder(++count);
//			item.add(ma.group(3));
			item.add(ma.group(1));
			item.add(ma.group(5));
			item.add(ma.group(2));
			item.add(tieLocation(ma.group(4)));
			
			mexItems.add(item);
		}
		
		return mexItems;
	}
	
	public static boolean isLongLat(String longAndLat) {
		String regex = Konstants.REGEX_SIGN_FLOAT + "\\s*,\\s*" + Konstants.REGEX_SIGN_FLOAT;
		return StrUtil.isRegexMatched(regex, longAndLat);
	}
	
	public static String getIfLonglat(String longAndLat) {
		String regex = Konstants.REGEX_SIGN_FLOAT + "\\s*,\\s*" + Konstants.REGEX_SIGN_FLOAT;
		if(StrUtil.isRegexMatched(regex, longAndLat))  {
			return longAndLat.replaceAll("\\s+", "");
		} else {
			return null;
		}
	}
	
	public static String reverseLongAndLat(String longAndLat) {
		String regex = Konstants.REGEX_SIGN_FLOAT + "\\s*,\\s*" + Konstants.REGEX_SIGN_FLOAT;
		String[] params = StrUtil.parseParams(regex, longAndLat);
		if(params == null) {
			XXXUtil.alert("Invalid coordination: " + longAndLat);
		}
		
		return params[1] + "," + params[0]; 
	}
	
	public static String tieLocation(String location) {
		String value = StrUtil.occupy("[{0}]", location.replaceAll("\\s+", ""));
		
		return value;
	}
	
	private static String lax() {
		String head = "###";
		String temp = StrUtil.padRight(head, "fetching...".length() + 1);

		return temp;
	}
	
	/****
	 * #ABC => split the pie
	 * 108.0737436,22.62314202 => Good
	 * deli => if in dictionary
	 * @param placeInfo
	 * @return
	 */
	public static String fetchLonglatx(String placeInfo) {
		return fetchLonglat(placeInfo, "");
	}

	public static String fetchLonglat(String placeInfo, String city) {
		return fetchLonglat(placeInfo, city, false);
	}
	
	public static String fetchLonglat(String placeInfo, String city, boolean mandatory) {
		String location = GaodeUtils.getIfLonglat(placeInfo);
		if(location != null) {
			return location;
		}
		
		String value = LonglatData.getXY(placeInfo);
		if(value != null) {
			return value;
		}
		
		String[] params = StrUtil.parseParams("(#?)(.+?)", placeInfo);
		boolean toConvert = !params[0].isEmpty();
		String place2 = params[1];
		location = GaodeUtils.getIfLonglat(place2);
		if(location != null) {
			return location;
		}
		
		value = LonglatData.EGGS.getIgnorecase(place2);
		if(value != null) {
			return value;
		}
		
		if(toConvert || mandatory) {
			location = GaodeUtils.locationOf(place2, city);
			return location;
		}
		
		return null;
	}
}
