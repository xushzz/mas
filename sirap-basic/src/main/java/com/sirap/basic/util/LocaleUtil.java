package com.sirap.basic.util;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexLocale;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.search.MexFilter;

public class LocaleUtil {

	public static final List<Locale> AA_LOCALES = Arrays.asList(Locale.getAvailableLocales());
	public static final List<MexLocale> AAM_LOCALES = new ArrayList<>();
	static {
		for(Locale item : AA_LOCALES) {
			if(EmptyUtil.isNullOrEmpty(item.getLanguage())) {
				continue;
			}
			
			if(StrUtil.contains(item.toString(), "#")) {
				continue;
			}
			AAM_LOCALES.add(new MexLocale(item));
		}
		Collections.sort(AAM_LOCALES);
	}

	public static Locale of(String localeStr) {
		XXXUtil.nullCheck(localeStr, "localeStr");

		String regex = "([a-z]{2})(_([A-Z]{2})|)";
		String[] params = StrUtil.parseParams(regex, localeStr);
		if(params != null) {
			Locale lo = null;
			String lang = params[0].toLowerCase();
			String area = params[2];
			if(area != null) {
				lo = new Locale(lang, area.toUpperCase());
			} else {
				lo = new Locale(lang);
			}

			return lo;
		}
		
		return null;
	}
	
	public static List<Locale> ofs(String multipleLocalesString) {
		List<Locale> lots = new ArrayList<>();
		if(EmptyUtil.isNullOrEmpty(multipleLocalesString)) {
			return lots;
		}
		List<String> items = StrUtil.splitByRegex(multipleLocalesString, ",|\\+");
		for(String item : items) {
			Locale lot = of(item);
			if(lot == null) {
				XXXUtil.alert("Not a valid locale: " + item);
			}
			
			lots.add(lot);
		}
		
		return lots;
	}
	
	public static boolean doesExist(Locale locale) {
		List<Locale> list = getAvailableLocales();
		boolean flag = list.indexOf(locale) >= 0;

		return flag;
	}
	
	public static List<MexLocale> searchSimilars(String criteria) {
		MexFilter<MexLocale> filter = new MexFilter<MexLocale>(criteria, AAM_LOCALES);
		List<MexLocale> mexItems = filter.process();	
		
		return mexItems;
	}
	
	public static List<Locale> getAvailableLocales() {
		List<Locale> list = Arrays.asList(Locale.getAvailableLocales());
		
		return list;
	}
	
	public static String getIso3Header(String extraLocales) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("[");
		sb.append("locale").append(", ");
		sb.append("code").append(", ");
		sb.append("lang").append(", ");
		sb.append("country").append(", ");
		sb.append("local");
		if(!EmptyUtil.isNullOrEmpty(extraLocales)) {
			sb.append(", ").append(extraLocales);
		}
		sb.append("]");
		
		return sb.toString();
	}


	/**
	 * 
	 * @param locales 1) en_US, en_GB, zh_TW
	 *                2) en_US
	 *                3) en
	 *                4) xx
	 * @return
	 */
	public static List<MexObject> getIso3Countries() {
		return getIso3Countries(null);
	}
	
	public static String getIso3CountryInfo(Locale countryLocale, String multipleLocalesString) {
		List<Locale> extraLocales = ofs(multipleLocalesString);
		
		StringBuffer sb = new StringBuffer();
		Locale temp = countryLocale;
		String iso = "SHIT";
        try {
        	iso = temp.getISO3Country();
        } catch (Exception ex) {
        	//D.pl(ex.getMessage());
        }
        sb.append(temp).append(", ");
        sb.append(iso).append(", ");
        sb.append(temp.getDisplayLanguage(Locale.ENGLISH)).append(", ");
        sb.append(temp.getDisplayCountry(Locale.ENGLISH)).append(", ");
        sb.append(temp.getDisplayCountry()).append(", ");
        if(!EmptyUtil.isNullOrEmpty(extraLocales)) {
        	for(Locale extra : extraLocales) {
                sb.append(temp.getDisplayCountry(extra)).append(", ");
        	}
        }
        String item = sb.toString().replaceAll(",\\s*$", "");
        
        return item;
	}
	
	public static List<MexObject> getIso3Countries(String multipleLocalesString) {
		Locale[] allLocales = Locale.getAvailableLocales();
		List<MexObject> records = new ArrayList<>();
		for(int i = 0; i < allLocales.length; i++) {
			String item = getIso3CountryInfo(allLocales[i], multipleLocalesString);
            records.add(new MexObject(item));
		}
		
		return records;
	}
	
	public static List<MexObject> getAllCurrencies(String multipleLocalesString) {
		List<Currency> items = new ArrayList<>(Currency.getAvailableCurrencies());
		List<MexObject> records = new ArrayList<>();
		List<Locale> extraLocales = ofs(multipleLocalesString);
		Map<String, String> ma = getCurrencyCodeAndSymbol();
		
		for(Currency item : items) {
			String code = item.getCurrencyCode();
			String symbol = ma.get(code);
			if(symbol == null) {
				symbol = item.getSymbol();
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append(code).append(", ");
			sb.append(symbol).append(", ");
			sb.append(item.getDisplayName(Locale.ENGLISH)).append(", ");
			sb.append(item.getDisplayName()).append(", ");
            if(!EmptyUtil.isNullOrEmpty(extraLocales)) {
            	for(Locale extra : extraLocales) {
            		sb.append(item.getDisplayName(extra)).append(", ");
            	}
            }
            String temp = sb.toString().replaceAll(",\\s*$", "");
            records.add(new MexObject(temp));
		}
		
		return records;
	}
	
	public static Map<String, String> getCurrencyCodeAndSymbol() {
		Locale[] allLocales = Locale.getAvailableLocales();
		Map<String, String> ma = new HashMap<>();
		for(int i = 0; i < allLocales.length; i++) {
			Locale lo = allLocales[i];
			try {
				Currency ccy = Currency.getInstance(lo);
				String code = ccy.getCurrencyCode();
				String symbol = ccy.getSymbol(lo);
				
				ma.put(code, symbol);
			} catch (Exception ex) {
				//do nothing.
			}
		}
		
		return ma;
	}
	
	public static List<String> getAllMonthWeekdays() {
		Locale[] allLocales = Locale.getAvailableLocales();
		List<String> items = new ArrayList<>();
		for(int i = 0; i < allLocales.length; i++) {
			Locale lo = allLocales[i];
			DateFormatSymbols bol = new DateFormatSymbols(lo);
			items.add(lo + ", part month: " + trimStringArray(bol.getShortMonths()));
			items.add(lo + ", full month: " + trimStringArray(bol.getMonths()));
			items.add(lo + ", part weeks: " + trimStringArray(bol.getShortWeekdays()));
			items.add(lo + ", full weeks: " + trimStringArray(bol.getWeekdays()));
		}
		
		return items;
	}
	
	private static String trimStringArray(String[] arr) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < arr.length; i++) {
			String item = arr[i];
			if(EmptyUtil.isNullOrEmpty(item)) {
				continue;
			}
			
			sb.append(item.trim()).append(", ");
		}
		
		String temp = sb.toString().replaceAll(",\\s*$", "");
		return temp;
	}
}
