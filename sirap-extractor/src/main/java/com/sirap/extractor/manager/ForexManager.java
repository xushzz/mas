package com.sirap.extractor.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.ForexRateRecord;
import com.sirap.extractor.impl.XRatesForexRateExtractor;

public class ForexManager {

	public static final String KEY_ALL = "/";
	public static final List<String> TOP_CURRENCIES = new ArrayList<String>();
	static {
		TOP_CURRENCIES.add("GBP");
		TOP_CURRENCIES.add("EUR");
		TOP_CURRENCIES.add("CHF");
		TOP_CURRENCIES.add("USD");
		TOP_CURRENCIES.add("CAD");
		TOP_CURRENCIES.add("AUD");
		TOP_CURRENCIES.add("SGD");
		TOP_CURRENCIES.add("NZD");
		TOP_CURRENCIES.add("CNY");
		TOP_CURRENCIES.add("HKD");
		TOP_CURRENCIES.add("TWD");
		TOP_CURRENCIES.add("JPY");
	}
	
	private static ForexManager instance;
	
	public static ForexManager g() {
		if(instance == null) {
			instance = new ForexManager();
		}
		
		return instance;
	}
	
	public List<String> convert(String ccyCode, String amount) {
		return convert(ccyCode, amount, null);
	}
	
	private List<String> filterUnwantedCurrency(String baseCurrency, String currencies) {
		Set<String> set = new HashSet<String>();
		List<String> items = StrUtil.splitByRegex(currencies);
		for(String item:items) {
			if(baseCurrency.equalsIgnoreCase(item)) {
				continue;
			}
			
			set.add(item.toLowerCase());
		}
		
		
		return new ArrayList<String>(set);
	}
	
	public List<List<String>> convert4PDF(String baseCurrency, String amount, String criteria) {
		List<ForexRateRecord> mexItems = fetchItems(baseCurrency, amount, criteria); 
		return CollectionUtil.items2PDFRecords(mexItems);
	}
	
	public List<String> convert(String baseCurrency, String amount, String criteria) {
		List<ForexRateRecord> mexItems = fetchItems(baseCurrency, amount, criteria); 

		int[] maxArr = maxLenOfFields(mexItems);
		StringBuilder sb = new StringBuilder();
		sb.append("maxLenOfCurrency=").append(maxArr[0]);
		sb.append(",");
		sb.append("maxLenOfAmount=").append(maxArr[1]);
		
		return CollectionUtil.items2PrintRecords(mexItems, sb.toString());
	}
	
	@SuppressWarnings("unchecked")
	public List<ForexRateRecord> fetchItems(String baseCurrency, String amount, String criteria) {
		List<String> legalCurrencies = null;
		if(!EmptyUtil.isNullOrEmpty(criteria) && !StrUtil.equals(KEY_ALL, criteria)) {
			legalCurrencies = filterUnwantedCurrency(baseCurrency, criteria);
			if(EmptyUtil.isNullOrEmpty(legalCurrencies)) {
				return Collections.EMPTY_LIST;
			}
		}
		
		XRatesForexRateExtractor zhang = new XRatesForexRateExtractor(baseCurrency, amount);
		zhang.process();
		List<ForexRateRecord> mexItems = zhang.getMexItems();
		
		if(EmptyUtil.isNullOrEmpty(criteria)) {
			String temp = StrUtil.connect(TOP_CURRENCIES, MexFilter.SYMBOL_OR);
			MexFilter<ForexRateRecord> filter = new MexFilter<ForexRateRecord>(temp, mexItems);
			mexItems = filter.process();
		} else if(StrUtil.equals(KEY_ALL, criteria)) {
			//show all
		} else {
			String temp = StrUtil.connect(legalCurrencies, MexFilter.SYMBOL_OR);
			MexFilter<ForexRateRecord> filter = new MexFilter<ForexRateRecord>(temp, mexItems);
			mexItems = filter.process();
		}
		
		if(EmptyUtil.isNullOrEmpty(mexItems)) {
			return Collections.EMPTY_LIST;
		}
		
		Collections.sort(mexItems);

		ForexRateRecord base = zhang.getBaseRecord();
		if(base != null) {
			base.setValue(amount);
			mexItems.add(0, base);
		}
		
		return mexItems;
	}
	
	public int[] maxLenOfFields(List<ForexRateRecord> records) {
		int[] maxArr = new int[2];
		for(MexItem item:records) {
			ForexRateRecord record = (ForexRateRecord)item;
			if(record == null) {
				continue;
			}
			int len = record.getDisplayName().length();
			if(len > maxArr[0]) {
				maxArr[0] = len;
			}
			len = record.getDisplayAmount().length();
			if(len > maxArr[1]) {
				maxArr[1] = len;
			}
		}
		
		return maxArr;
	}
}
