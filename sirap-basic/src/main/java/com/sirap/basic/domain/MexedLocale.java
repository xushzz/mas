package com.sirap.basic.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MexedLocale extends MexItem implements Comparable<MexedLocale> {

	protected Locale locale;
	
	public MexedLocale(Locale locale) {
		this.locale = locale;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public String getLang() {
		return locale.getLanguage();
	}
	
	public String getArea() {
		return locale.getCountry();
	}
	
	public String getISO3Country() {
		try {
        	String iso = locale.getISO3Country();
        	return iso;
        } catch (Exception ex) {
        	//D.pl(ex.getMessage());
        }
		
		return null;
	}

	@Override
	public int compareTo(MexedLocale o) {
		return locale.toString().compareTo(o.locale.toString());
	}

	public List<String> keyItems() {
		List<String> items = new ArrayList<>();
		
		items.add(locale.toString());
		items.add(getISO3Country());
		items.add(locale.getDisplayLanguage(Locale.ENGLISH));
		items.add(locale.getDisplayCountry(Locale.ENGLISH));
		items.add(locale.getDisplayCountry());
		
		return items;
	}
	
	@Override
	public boolean isMatched(String keyWord) {
		List<String> items = keyItems();
		for(String item : items) {
			if(isRegexMatched(item, keyWord)) {
				return true;
			}
			
			if(StrUtil.contains(item, keyWord)) {
				return true;
			}
				
		}
		
		return false;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		List<String> items = keyItems();
		for(String item : items) {
			sb.append(item).append(", ");
		}
		
		return sb.toString();
	}
	
	public String toPrint(Map<String, Object> params) {
		Object stuff = params.get("localesInDisplay");
		List<Locale> localesInDisplay = null;
		if(stuff instanceof List) {
			localesInDisplay = (List<Locale>)stuff;
		}

		StringBuffer sb = new StringBuffer();
		List<String> items = keyItems();
		for(String item : items) {
			sb.append(item).append(", ");
		}
        if(!EmptyUtil.isNullOrEmpty(localesInDisplay)) {
        	for(Locale extra : localesInDisplay) {
                sb.append(locale.getDisplayCountry(extra)).append(", ");
        	}
        }
        
        String value = sb.toString().replaceAll("[\\s,]+$", "");
        
        return value;
	}
}
