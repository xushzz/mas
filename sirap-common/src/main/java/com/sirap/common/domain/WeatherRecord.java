package com.sirap.common.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class WeatherRecord extends MexItem {
	
	private String cityPY;
	private String city;
	private String weather;
	private String celsius;
	
	public String getCityPY() {
		return cityPY;
	}

	public void setCityPY(String cityPY) {
		this.cityPY = cityPY;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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
		
		String digits = StrUtil.findFirstMatchedItem("(\\d+)", celsius);
		
		return digits;
	}

	public void setCelsius(String celsius) {
		this.celsius = celsius;
	}

	public boolean isMatched(String keyWord) {
		if(isRegexMatched(cityPY, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(cityPY, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(city, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(city, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(weather, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(weather, keyWord)) {
			return true;
		}
		
		String temper = getCelsiusDigits();
		if(isRegexMatched(temper, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(temper, keyWord)) {
			return true;
		}
		
		return false;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(StrUtil.extend(city, 6));
		sb.append(StrUtil.extend(weather, 10));
		sb.append(celsius);
		
		return sb.toString();
	}
}
