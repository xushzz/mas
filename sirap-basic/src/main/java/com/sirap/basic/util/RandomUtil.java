package com.sirap.basic.util;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.data.CityData;
import com.sirap.basic.domain.NameRankItem;
import com.sirap.basic.domain.ValuesItem;

public class RandomUtil {

	public static String digits(int countOfChars) {
		String result = chars(countOfChars, StrUtil.DIGITS);
		
		return result;
	}

	public static Long digitsStartWithNoZero(int countOfChars) {
		String start = chars(1, "123456798");
		String others = chars(countOfChars - 1, StrUtil.DIGITS);
		
		return Long.parseLong(start + others);
	}

	public static String letters(int countOfChars) {
		String result = chars(countOfChars, StrUtil.LETTERS);
		
		return result;
	}

	public static boolean bool() {
		Random r = new Random();
		int va = r.nextInt(10);
		return va > 5;
	}

	public static String LETTERS(int countOfChars) {
		String result = chars(countOfChars, StrUtil.LETTERS_UPPERCASED);
		
		return result;
	}
	public static String alphanumeric(int countOfChars) {
		String result = chars(countOfChars, StrUtil.ALPHANUMERIC);
		
		return result;
	}
	
	public static String chars(int countOfChars, String source) {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		String temp = source;
		
		while(sb.length() < countOfChars) {
			int index = r.nextInt(temp.length());
			sb.append(temp.charAt(index));
		}
		
		return sb.toString();
	}

	public static int number(int minValue, int maxValue) {
		Random xian = new Random();
		int range = Math.abs(minValue - maxValue) + 1;
		int result = xian.nextInt(range) + Math.min(minValue, maxValue);
		
		return result;
	}
	
	public static int number(int maxValue) {
		Random xian = new Random();
		int result = xian.nextInt(maxValue);
		
		return result;
	}
	
	public static ValuesItem city() {
		return cities(1).get(0);
	}
	
	public static List<ValuesItem> cities(int size) {
		Random xian = new Random();
		List<ValuesItem> sample = Lists.newArrayList(CityData.EGGS.values());
		int samplesize = sample.size();
		List<ValuesItem> items = Lists.newArrayList();
		if(samplesize == 0) {
			return items;
		}
		while(items.size() < size) {
			int index = xian.nextInt(samplesize);
			items.add(sample.get(index));
		}
		
		return items;
	}
	
	public static String name() {
		return names(1).get(0);
	}
	
	public static String name(int rankTopK) {
		return names(1, rankTopK).get(0);
	}
	
	public static List<String> names(int size) {
		return names(size, 500);
	}
	
	public static List<String> names(int size, int rankTopK) {
		return names(size, "", rankTopK);
	}
	
	public static List<String> names(int size, String criteria, int rankTopK) {
		List<NameRankItem> data = fetchEnglishNames(rankTopK);
		if(data.isEmpty()) {
			XXXUtil.alert("No names to choose at all.");
		}
		
		List<NameRankItem> sample = Colls.filter(data, criteria);
		if(sample.isEmpty()) {
			XXXUtil.alert("No such names matching criteria: {0}", criteria);
		}
		
		Random xian = new Random();
		int samplesize = sample.size();
		List<String> items = Lists.newArrayList();
		while(items.size() < size) {
			int index = xian.nextInt(samplesize);
			items.add(sample.get(index).getName());
		}
		
		return items;
	}
	
	public static List<NameRankItem> fetchEnglishNames(int rankTopK) {
		List<NameRankItem> items = Lists.newArrayList();
		items.addAll(fetchEnglishNamesByGender(true, rankTopK));
		items.addAll(fetchEnglishNamesByGender(false, rankTopK));
		
		return items;
	}
	
	public static List<NameRankItem> fetchEnglishNamesByGender(boolean isMale, int rankTopK) {
		Extractor<NameRankItem> neymar = new Extractor<NameRankItem>() {

			public String getUrl() {
				useList();//.showFetching();
				String key = isMale ? "" : "fe";
				return StrUtil.occupy("#data/{0}males.txt", key);
			}
			
			@Override
			protected void parse() {
				for(String line : sourceList) {
					List<String> items = StrUtil.split(line);
					if(items != null && items.size() > 1) {
						NameRankItem item = new NameRankItem();
						String temp = items.get(0);
						String name = temp.substring(0, 1).toUpperCase() + temp.substring(1).toLowerCase();
						item.setName(name);
						item.setGender(isMale ? "M" : "F");
						int rank = Integer.parseInt(items.get(1));
						if(rankTopK <= 0 || rank <= rankTopK) {
							item.setRank(items.get(1));
							mexItems.add(item);
						}
					}
				}
			}
		};
		
		return neymar.process().getItems();
	}
}
