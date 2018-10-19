package com.sirap.basic.util;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sirap.basic.component.map.AlinkMap;
import com.sirap.basic.domain.MexItem;
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
	
	public static List<Locale> langsOf(String locales) {
		return langsOf(locales, true);
	}
	
	public static List<Locale> langsOf(String locales, boolean withDefault) {
		Set<Locale> set = Sets.newLinkedHashSet();
		if(withDefault) {
			set.add(Locale.getDefault());
			set.add(Locale.ENGLISH);
		}
		
		if(EmptyUtil.isNullOrEmpty(locales)) {
			return Lists.newArrayList(set);
		}

		List<String> items = StrUtil.splitByRegex(locales, ",|\\+");
		for(String item : items) {
			Locale kid = searchLocaleByLang(item);
			if(kid == null) {
				XXXUtil.alert("Not a valid locale: " + item);
			}
			
			set.add(kid);
		}
		
		return Lists.newArrayList(set);
	}
	
	public static Locale searchLocaleByLang(String target) {
		for(Locale item : LOCALES) {
			String langAndCountry = item.toString();
			String lang = item.getLanguage();
			String english = item.getDisplayLanguage(Locale.ENGLISH);
			String chinese = item.getDisplayLanguage(Locale.CHINESE);
			if(StrUtil.isIn(target, Lists.newArrayList(langAndCountry, lang, english, chinese))) {
				return item;
			}
		}
		
		return null;
	}

	/**
	 * 
	 * @param locales 1) en_US, en_GB, zh_TW
	 *                2) en_US
	 *                3) en
	 *                4) xx
	 * @return
	 */
	
	public static String iso3CountryOf(Locale locale) {
		try {
        	String iso = locale.getISO3Country();
        	return iso;
        } catch (Exception ex) {
        	//D.pl(ex.getMessage());
        }
		
		return null;
	}

	public static List<ValuesItem> getLocaleRecords(List<Locale> localesInColumns) {
		List<ValuesItem> list = Lists.newArrayList();
		for(Locale locale : LOCALES) {
			if(locale.toString().isEmpty()) {
				continue;
			}
			
			ValuesItem vi = ValuesItem.of(locale.toString(), iso3CountryOf(locale));
			for(Locale kid : localesInColumns) {
				vi.add(locale.getDisplayLanguage(kid));
				vi.add(locale.getDisplayCountry(kid));
	    	}
			list.add(vi);
		}
		
		return Lists.newArrayList(list);
	}

	public static Map<String, Set<String>> groupByLangs() {
		Map<String, Set<String>> mars = new LinkedHashMap<>();
		
		for(Locale locale : LOCALES) {
			String lang = locale.getDisplayLanguage(Locale.ENGLISH);
			String country = locale.getDisplayCountry(Locale.ENGLISH);
			if(country.isEmpty()) {
				continue;
			}
			
			Set<String> vi = mars.get(lang);
			if(vi == null) {
				vi = Sets.newTreeSet();
				vi.add(country);
				mars.put(lang, vi);
			} else {
				vi.add(country);
			}
		}
		
		return mars;
	}

	public static Map<String, Set<String>> groupByCountries() {
		Map<String, Set<String>> mars = new LinkedHashMap<>();
		
		for(Locale locale : LOCALES) {
			String lang = locale.getDisplayLanguage(Locale.ENGLISH);
			String country = locale.getDisplayCountry(Locale.ENGLISH);
			if(country.isEmpty()) {
				continue;
			}
			
			Set<String> vi = mars.get(country);
			if(vi == null) {
				vi = Sets.newTreeSet();
				vi.add(lang);
				mars.put(country, vi);
			} else {
				vi.add(lang);
			}
		}
		
		return mars;
	}
	
	public static List<ValuesItem> getAllCurrencies(List<Locale> localesInColumns) {
		List<Currency> items = new ArrayList<>(Currency.getAvailableCurrencies());
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
			for(Locale locale : localesInColumns) {
        		vi.add(item.getDisplayName(locale));
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
		Locale forSearch = Locale.ENGLISH;
		boolean isCurrentEnglish = StrUtil.equals(Locale.getDefault().getLanguage(), forSearch.getLanguage());
		for(Locale other : locs) {
			String prefix = "";
			if(isCurrentEnglish) {
				prefix = other.getDisplayName(Locale.getDefault());
			} else {
				prefix = other.getDisplayLanguage(forSearch) + " " + other.getDisplayName(Locale.getDefault());
			}
			
			DateFormatSymbols jack = new DateFormatSymbols(other);
			items.add(ValuesItem.of(prefix).addAll(listOf(jack.getShortMonths())));
			items.add(ValuesItem.of(prefix).addAll(listOf(jack.getMonths())));
		}
		
		return items;
	}
	
	public static List<MexItem> getWeeks(List<Locale> locs) {
		List<MexItem> items = new ArrayList<>();
		Locale forSearch = Locale.ENGLISH;
		boolean isCurrentEnglish = StrUtil.equals(Locale.getDefault().getLanguage(), forSearch.getLanguage());
		for(Locale other : locs) {
			String prefix = "";
			if(isCurrentEnglish) {
				prefix = other.getDisplayName(Locale.getDefault());
			} else {
				prefix = other.getDisplayLanguage(forSearch) + " " + other.getDisplayName(Locale.getDefault());
			}
			
			DateFormatSymbols jack = new DateFormatSymbols(other);
			items.add(ValuesItem.of(prefix).addAll(listOf(jack.getShortWeekdays())));
			items.add(ValuesItem.of(prefix).addAll(listOf(jack.getWeekdays())));
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
