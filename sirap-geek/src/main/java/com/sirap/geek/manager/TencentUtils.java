package com.sirap.geek.manager;

import java.util.List;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.component.Mist;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.MistUtil;
import com.sirap.basic.util.StrUtil;

public class TencentUtils {

	public static final String API_KEY = "RZQBZ-OZ5RR-VAGWQ-W7G6E-DGA3Z-RFBT2";
	public static final String TEMPLATE_REGEOCODE = "http://apis.map.qq.com/ws/geocoder/v1/?location={0}&key={1}&get_poi=0";
	public static final String TEMPLATE_GEOCODE = "http://apis.map.qq.com/ws/geocoder/v1/?address={0}&key={1}";
	
	/***
	 * http://lbs.qq.com/webservice_v1/guide-gcoder.html
	 * https://www.cnblogs.com/haibin-zhang/p/4955880.html
	 * @param location
	 * @param radius
	 * @return
	 */
	public static String regeocodeOfRaw(String latCommaLong) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_REGEOCODE, latCommaLong.replace(" ", ""), API_KEY);
				return url;
			}
			
			@Override
			protected void parse() {
				item = source;
			}
		};
		
		return neymar.process().getItem();
	}
	
	public static List<String> regeocodeOf(String latCommaLong) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_REGEOCODE, latCommaLong.replace(" ", ""), API_KEY);
				return url;
			}
			
			@Override
			protected void parse() {
				mexItems = JsonUtil.getPrettyTextInLines(source);
			}
		};
		
		return neymar.process().getItems();
	}
	
	public static boolean isInGreatDetail(String jsonOneline) {
		String recommend = MistUtil.ofJsonText(jsonOneline).asIs().findStringBy("recommend");
		return StrUtil.isPositive(recommend);
	}

	public static String niceAddressByRawJson(String source) {
		Mist mars = MistUtil.ofJsonText(source).asIs();
		
		String nice;
		String recommend = mars.findStringBy("recommend");
		if(recommend != null) {
			//in China.
			String address = mars.findStringBy("address");
			String temp = address.replace(recommend, "");
			temp = recommend + " " + temp; 
			nice = temp.trim();
		} else {
			D.sink();
			//outside China
			String nation = mars.findStringBy("nation");
			String levelA = mars.findStringBy("ad_level_1");
			String levelB = mars.findStringBy("ad_level_2");
			String levelC = mars.findStringBy("ad_level_3");
			String street = mars.findStringBy("street");
			D.pla(nation, levelA);
			StringBuffer sb = StrUtil.sb(nation);
			if(StrUtil.isPositive(levelA)) {
				sb.append(" ").append(levelA);
			}
			if(StrUtil.isPositive(levelB)) {
				sb.append(" ").append(levelB);
			}
			if(StrUtil.isPositive(levelC)) {
				sb.append(" ").append(levelC);
			}
			if(StrUtil.isPositive(street)) {
				sb.append(" ").append(street);
			}
			
			nice = sb.toString().trim();
		}
		
		return nice;
	}
}
