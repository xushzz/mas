package com.sirap.common.domain;

import java.util.Map;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class WeatherRecord extends MexItem {
	
	public static final String KEY_MAX_LEN_CITY = "maxLenCity";
	
	private String city;
	private String time;
	private String weather;
	private String celsius;
	private String link;
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getCelsius() {
		return celsius;
	}
	
	public String getCelsiusDigits() {
		if(celsius == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer(celsius);
		sb.setCharAt(sb.length() - 2, '^');
		return sb.toString();
		
//		List<Integer> temp = StringUtil.extractIntegers(celsius);
//		String value = StringUtil.connect(temp, "");
//		return StringUtil.extendLeftward(value, 2, " ");
	}

	public void setCelsius(String celsius) {
		this.celsius = celsius;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isMatched(String keyWord) {
		
		return false;
	}
	
	public String toPrint(Map<String, Object> params) {
		Integer maxCityLen = MathUtil.toInteger(String.valueOf(params.get(KEY_MAX_LEN_CITY)), 20);
		
		StringBuffer sb = new StringBuffer();
		sb.append(StrUtil.extend(city, maxCityLen + 2));
		sb.append(getCelsiusDigits() + "  ");
		sb.append(time + "  ");
		sb.append(StrUtil.extend(weather, 30));
		sb.append(link);
		
		return sb.toString();
	}
}
