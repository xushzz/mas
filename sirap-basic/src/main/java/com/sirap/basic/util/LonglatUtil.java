package com.sirap.basic.util;

import java.util.Iterator;
import java.util.Map;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.map.AlinkMap;
import com.sirap.basic.domain.LocationItem;
import com.sirap.basic.domain.LongOrLat;
import com.sirap.basic.tool.D;

public class LonglatUtil {
	public static final double[] RANGE_CHINA_LONG = {73.55, 135.0833333};
	public static final double[] RANGE_CHINA_LAT = {3.85, 53.55};

	/***
	 * 
	 * @param source
	 * 1) 73.55, 135.0833333
	 * 2) 73.55 135.0833333
	 * @return
	 */
	public static double[] longlatOf(String source) {
		String regex = Konstants.REGEX_SIGN_FLOAT + "\\s*,\\s*" + Konstants.REGEX_SIGN_FLOAT;
		String[] params = StrUtil.parseParams(regex, source);
		if(params == null) {
			regex = Konstants.REGEX_SIGN_FLOAT + "\\s+" + Konstants.REGEX_SIGN_FLOAT;
			params = StrUtil.parseParams(regex, source);
		}
		if(params != null) {
			double lng = Double.parseDouble(params[0]);
			double lat = Double.parseDouble(params[1]);
			double[] location = {lng, lat};
			return location;
		}
		
		return null;
	}
	
	public static boolean isInsideOf(double[] location, double[] rangOfLong, double[] rangeOfLat) {
		XXXUtil.shouldBeTrue(location.length == 2);
		XXXUtil.shouldBeTrue(rangOfLong.length == 2);
		XXXUtil.shouldBeTrue(rangeOfLat.length == 2);
		boolean flag = MathUtil.isBetween(location[0], rangOfLong[0], rangOfLong[1]);
		
		if(!flag) {
			return false;
		}
		flag = MathUtil.isBetween(location[1], rangeOfLat[0], rangeOfLat[1]);
		return flag;
	}
	
	/****
	 * 
	 * @param degreeMinuteSecond
	 * @return
	 */
	public static LocationItem longAndlatOfDMS(String jackCommaJones) {
		LocationItem item = new LocationItem();
		if(item.parse(jackCommaJones)) {
			return item;
		}
		
		return null;
	}
	
	/***
	 * 43 45 19.10N => lat 43.7553056
	 * 2.1217333E => lon 2.1217333
	 * @param source 
	 * @return
	 */
	public static LongOrLat longOrLatOfDMS(String degreeMinuteSecondNEWS) {
		LongOrLat item = new LongOrLat();
		if(item.parse(degreeMinuteSecondNEWS)) {
			return item;
		}
		
		return null;
	}	
	
	public static boolean isLongLat(String longAndLat) {
		String regex = Konstants.REGEX_SIGN_FLOAT + "\\s*,\\s*" + Konstants.REGEX_SIGN_FLOAT;
		return StrUtil.isRegexMatched(regex, longAndLat);
	}
	
	public static Map<String, String> getPois(String filepath) {
		Map<String, String> map = Amaps.newLinkHashMap();
		if(!FileUtil.exists(filepath)) {
			D.pl("File not fould: " + filepath);
			return map;
		}
		AlinkMap<String, String> props = Amaps.ofProperties(IOUtil.readLines(filepath, Konstants.CODE_UTF8));
		Iterator<String> it = props.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String value = props.get(key);
			LocationItem item = new LocationItem();
			if(item.parse(value)) {
				map.put(key, item.longCommaLat());
			} else {
				D.pl("bad: " + key + "=" + value);
			}
//			D.pla(key, value);
		}
		
		return map;
	}

}
