package com.sirap.geek.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.geek.domain.DistrictItem;

public class GaodeUtils {
	
	public static final String GITEE_URL_CHINA_DISTRICTS = "https://gitee.com/thewire/stamina/raw/master/china/amapChinaDistricts.txt";
	public static final String LEVEL_PROVINCE = "province";
	public static final String API_KEY = "02a37b38c4b33a671f1cd1584b54adc8";
	public static final String TEMPLATE_DISTRICT = "http://restapi.amap.com/v3/config/district?extensions=base&subdistrict={0}&keywords={1}&key={2}";
	public static final String TEMPLATE_GEOCODE = "http://restapi.amap.com/v3/geocode/geo?address={0}&city={1}&key={2}";
	public static final String TEMPLATE_REGEOCODE = "http://restapi.amap.com/v3/geocode/regeo?location={0}&radius={1}&key={2}";

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
				return url;
			}
			
			@Override
			protected void parse() {
				item = JsonUtil.getPrettyText(source);
//				item = source;
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
	public static String geocodeOf(String address, String city) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_GEOCODE, encodeURLParam(address), encodeURLParam(city), API_KEY);
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
	 * http://lbs.amap.com/api/webservice/guide/api/georegeo
	 * @param location
	 * @param radius
	 * @return
	 */
	public static String regeocodeOf(String location, String radius) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_REGEOCODE, location.replace(" ", ""), radius, API_KEY);
				return url;
			}
			
			@Override
			protected void parse() {
				item = JsonUtil.getPrettyText(source);
			}
		};
		
		return neymar.process().getItem();
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
}
