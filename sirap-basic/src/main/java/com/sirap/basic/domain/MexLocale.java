package com.sirap.basic.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.sirap.basic.util.LocaleUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MexLocale extends MexItem implements Comparable<MexLocale> {

	protected Locale locale;
	
	public MexLocale(Locale locale) {
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
	public int compareTo(MexLocale o) {
		return locale.toString().compareTo(o.locale.toString());
	}

	public List<String> toList(String options) {
		List<String> items = new ArrayList<>();
		
		items.add(locale.toString());
		items.add(getISO3Country());
		items.add(locale.getDisplayLanguage(Locale.ENGLISH));
		items.add(locale.getDisplayCountry(Locale.ENGLISH));
		items.add(locale.getDisplayCountry());
		
		String locales = OptionUtil.readString(options, "locs");
		List<Locale> kids = LocaleUtil.listOf(locales);
//		D.list(kids);
		for(Locale kid : kids) {
			items.add(locale.getDisplayCountry(kid));
    	}
		
		return items;
	}
	
	public static ValuesItem getHeader(String options) {
		ValuesItem vi = ValuesItem.of("Locale", "Code", "Lang", "Country", "Current");
		String locales = OptionUtil.readString(options, "locs");
		List<Locale> kids = LocaleUtil.listOf(locales);
//		D.list(kids);
		for(Locale kid : kids) {
			vi.add(kid.toString());
    	}
		
		return vi;
	}
	
	@Override
	public boolean isMatched(String keyWord) {
		List<String> items = toList();
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
		return StrUtil.connectWithCommaSpace(toList());
	}
	
	public String toPrint(String options) {
		return StrUtil.connectWithCommaSpace(toList(options));
	}
}
