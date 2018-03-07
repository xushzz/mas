package com.sirap.geek.manager;

import java.util.List;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.json.JsonUtil;
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
	public static List<String> regeocodeOf(String location) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				showFetching().useUTF8();
				String url = StrUtil.occupy(TEMPLATE_REGEOCODE, location.replace(" ", ""), API_KEY);
				return url;
			}
			
			@Override
			protected void parse() {
				mexItems = JsonUtil.getPrettyTextInLines(source);
			}
		};
		
		return neymar.process().getItems();
	}


}
