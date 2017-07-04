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

import com.sirap.basic.domain.MexedLocale;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.search.MexFilter;

public class LocaleUtil {

	public static final List<Locale> AA_LOCALES = Arrays.asList(Locale.getAvailableLocales());
	public static final List<MexedLocale> AAM_LOCALES = new ArrayList<>();
	static {
		for(Locale item : AA_LOCALES) {
			if(EmptyUtil.isNullOrEmpty(item.getLanguage())) {
				continue;		
			}
			
			if(StrUtil.contains(item.toString(), "#")) {
				continue;
			}
			AAM_LOCALES.add(new MexedLocale(item));
		}
		Collections.sort(AAM_LOCALES);
	}

	public static Locale parseLocale(String localeStr) {
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
	
	public static boolean doesExist(Locale locale) {
		List<Locale> list = getAvailableLocales();
		boolean flag = list.indexOf(locale) >= 0;

		return flag;
	}
	
	public static List<MexedLocale> searchSimilars(String criteria) {
		MexFilter<MexedLocale> filter = new MexFilter<MexedLocale>(criteria, AAM_LOCALES);
		List<MexedLocale> mexItems = filter.process();	
		
		return mexItems;
	}
	
	public static List<Locale> getAvailableLocales() {
		List<Locale> list = Arrays.asList(Locale.getAvailableLocales());
		
		return list;
	}
	
	public static List<Locale> parseLocales(String multipleLocalesString) {
		List<Locale> extraLocales = new ArrayList<>();
		if(!EmptyUtil.isNullOrEmpty(multipleLocalesString)) {
			List<String> items = StrUtil.split(multipleLocalesString);
			for(String item : items) {
				Locale extraLocale = parseLocale(item);
				
				if(extraLocale != null) {
					extraLocales.add(extraLocale);
				}
			}
		}
		
		return extraLocales;
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
	public static List<MexedObject> getIso3Countries() {
		return getIso3Countries(null);
	}
	
	public static String getIso3CountryInfo(Locale countryLocale, String multipleLocalesString) {
		List<Locale> extraLocales = parseLocales(multipleLocalesString);
		
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
	
	public static List<MexedObject> getIso3Countries(String multipleLocalesString) {
		Locale[] allLocales = Locale.getAvailableLocales();
		List<MexedObject> records = new ArrayList<>();
		for(int i = 0; i < allLocales.length; i++) {
			String item = getIso3CountryInfo(allLocales[i], multipleLocalesString);
            records.add(new MexedObject(item));
		}
		
		return records;
	}
	
	public static List<MexedObject> getAllCurrencies(String multipleLocalesString) {
		List<Currency> items = new ArrayList<>(Currency.getAvailableCurrencies());
		List<MexedObject> records = new ArrayList<>();
		List<Locale> extraLocales = parseLocales(multipleLocalesString);
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
            records.add(new MexedObject(temp));
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
