package com.sirap.basic.util;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.Lists;
import com.sirap.basic.component.map.AlinkMap;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexLocale;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.domain.ValuesItem;

public class LocaleUtil {
	
	public static final List<String> TOP_CURRENCIES;
	static {
		TOP_CURRENCIES = StrUtil.split("CNY,GBP,EUR,USD,JPY,AUD,CAD,CHF,HKD,TWD,SGD,NZD");
	}
	
	/***
	 	Currency.getAvailableCurrencies().size() 225
		Locale.getAvailableLocales().length 160
		Locale: 160
		Currency: 114 by locale
		Currency: 75 different by locale
	 */

	public static final List<Locale> LOCALES = Lists.newArrayList();
	public static final List<MexLocale> MEX_LOCALES = new ArrayList<>();
	public static final AlinkMap<String, Locale> MAP_LOCALES = Amaps.newLinkHashMap();
	public static final Map<String, String> MAP_CURRENCY_CODE_SYMBOL = new TreeMap<>();
	
	static {
		for(Locale item : Locale.getAvailableLocales()) {
			if(!item.toString().isEmpty()) {
				LOCALES.add(item);
			}
		}
		Collections.sort(LOCALES, comparatorOfLocale());
		
		for(Locale item : LOCALES) {
			MEX_LOCALES.add(new MexLocale(item));
			MAP_LOCALES.put(item.toString(), item);
			try {
				Currency ccy = Currency.getInstance(item);
				MAP_CURRENCY_CODE_SYMBOL.put(ccy.getCurrencyCode(), ccy.getSymbol(item));
			} catch (Exception ex) {
//				do nothing.
			}
		}
	}

	public static Locale of(String localeStr) {
		XXXUtil.shouldBeNotnull(localeStr);
		
		return MAP_LOCALES.get(localeStr);
	}
	
	public static List<Locale> listOf(String locales) {
		List<Locale> lots = new ArrayList<>();
		if(EmptyUtil.isNullOrEmpty(locales)) {
			return lots;
		}
		
		List<String> items = StrUtil.splitByRegex(locales, ",|\\+");
		for(String item : items) {
			Locale lot = MAP_LOCALES.get(item);
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
	
	public static List<Locale> getAvailableLocales() {
		List<Locale> list = Arrays.asList(Locale.getAvailableLocales());
		
		return list;
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
		List<Locale> extraLocales = listOf(multipleLocalesString);
		
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
	
	public static List<MexItem> getAllCurrencies(String locales) {
		List<Currency> items = new ArrayList<>(Currency.getAvailableCurrencies());
		List<Locale> others = listOf(locales);
		
		List<ValuesItem> list = Lists.newArrayList();
		for(Currency item : items) {
			String code = item.getCurrencyCode();
			String symbol = MAP_CURRENCY_CODE_SYMBOL.get(code);
			if(symbol == null) {
				symbol = "";
			}
			
			ValuesItem vi = ValuesItem.of();
			vi.add(code);
			vi.add(symbol);
			vi.add(item.getDisplayName(Locale.ENGLISH));
			vi.add(item.getDisplayName());
			for(Locale other : others) {
        		vi.add(item.getDisplayName(other));
        	}
			list.add(vi);
		}
		
		Collections.sort(list, comparatorOfCurrencyCode());
		
		return Lists.newArrayList(list);
	}
	
	public static Comparator<ValuesItem> comparatorOfCurrencyCode() {
		return new Comparator<ValuesItem>() {

			@Override
			public int compare(ValuesItem a, ValuesItem b) {
				int diff = valueOf(a) - valueOf(b);
				if(diff != 0) {
					return diff;
				}
				return a.toString().compareTo(b.toString());
			}
			
			private int valueOf(ValuesItem vi) {
				String code = vi.getByIndex(0) + "";
				int index = TOP_CURRENCIES.indexOf(code);
//				D.pl(index);
				if(index < 0) {
					index = TOP_CURRENCIES.size();
				}
//				D.pl(index);
//				D.pl();
				return index;
			}
		};
	}
	
	public static Comparator<Locale> comparatorOfLocale() {
		return new Comparator<Locale>() {
			@Override
			public int compare(Locale a, Locale b) {
				int diff = valueOf(a) - valueOf(b);
				if(diff != 0) {
					return diff;
				}
				return a.toString().compareTo(b.toString());
			}
			
			private int valueOf(Locale loc) {
				if(StrUtil.equals(loc.getLanguage(), Locale.CHINESE.getLanguage())) {
					return 1;
				} else if(StrUtil.equals(loc.getLanguage(), Locale.ENGLISH.getLanguage())) {
					return 2;
				} else if(StrUtil.equals(loc.getDisplayLanguage(Locale.ENGLISH), "Spanish")) {
					return 3;
				} else if(StrUtil.equals(loc.getLanguage(), Locale.FRENCH.getLanguage())) {
					return 4;
				} else if(StrUtil.equals(loc.getLanguage(), Locale.GERMAN.getLanguage())) {
					return 5;
				} else if(StrUtil.equals(loc.getLanguage(), Locale.ITALIAN.getLanguage())) {
					return 6;
				} else if(StrUtil.equals(loc.getDisplayLanguage(Locale.ENGLISH), "Serbian")) {
					return 7;
				} else {
					return 99;
				}
			}
		};
	}
	
	public static List<MexItem> getMonths(List<Locale> locs) {
		List<MexItem> items = new ArrayList<>();
		Locale must = Locale.ENGLISH;
		boolean useDefault = !Locale.getDefault().equals(must);
		for(Locale other : locs) {
			String lang = other.getDisplayLanguage(must);
			if(useDefault) {
				lang += " " + other.getDisplayLanguage(Locale.getDefault());
			}
			
			DateFormatSymbols jack = new DateFormatSymbols(other);
			items.add(ValuesItem.of(lang).addAll(listOf(jack.getShortMonths())));
			items.add(ValuesItem.of(lang).addAll(listOf(jack.getMonths())));
		}
		
		return items;
	}
	
	public static List<MexItem> getWeeks(List<Locale> locs) {
		List<MexItem> items = new ArrayList<>();
		Locale must = Locale.ENGLISH;
		boolean useDefault = !Locale.getDefault().equals(must);
		for(Locale other : locs) {
			String lang = other.getDisplayLanguage(must);
			if(useDefault) {
				lang += " " + other.getDisplayLanguage(Locale.getDefault());
			}
			
			DateFormatSymbols jack = new DateFormatSymbols(other);
			items.add(ValuesItem.of(lang).addAll(listOf(jack.getShortWeekdays())));
			items.add(ValuesItem.of(lang).addAll(listOf(jack.getWeekdays())));
		}
		
		return items;
	}
	
	public static List<String> listOf(String[] items) {
		List<String> list = Lists.newArrayList();
		for(String item : items) {
			if(!item.isEmpty()) {
				list.add(item);
			}
		}

		return list;
	}
}
