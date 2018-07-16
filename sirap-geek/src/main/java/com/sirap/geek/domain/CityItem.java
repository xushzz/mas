package com.sirap.geek.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.ValuesItem;

@SuppressWarnings("serial")
public class CityItem extends MexItem {
	private String english;
	private String chinese;
	private String country;
	private String longlat;
	
	public CityItem() {}
	
	public CityItem(String english, String chinese, String country, String longlat) {
		this.english = english;
		this.chinese = chinese;
		this.country = country;
		this.longlat = longlat;
	}
	
	public String getId() {
		return english.replaceAll("\\s+", "");
	}
	
	public String getEnglish() {
		return english;
	}
	public void setEnglish(String english) {
		this.english = english;
	}
	public String getChinese() {
		return chinese;
	}
	public void setChinese(String chinese) {
		this.chinese = chinese;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLonglat() {
		return longlat;
	}
	public void setLonglat(String longlat) {
		this.longlat = longlat;
	}
	
	public String toString() {
		ValuesItem vi = new ValuesItem(getId());
		vi.add(english);
		vi.add(chinese);
		vi.add(country);
		vi.add(longlat);
		
		return vi.toPrint(" ");
	}
}
