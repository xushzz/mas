package com.sirap.extractor.impl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.thread.MasterItemOriented;
import com.sirap.basic.thread.WorkerItemOriented;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.StrUtil;

public class May699Utils {
	
	public static void main(String[] args) {
		Object oa = null;
		String sa = null;
		int ia = 1948;
		oa = maxSearchId(ia);
		sa = "http://www.699mm.com/search-0-2250.html";
//		sa = "http://www.699mm.com/search-0-250.html"; //net
//		oa = keywordOf(sa);
		oa = getIdAndWords(100);
		D.pjsp(oa);
	}
	
	public static Map<Integer, String> getIdAndWords(int high) {
		return getIdAndWords(1, high);
	}
	
	public static Map<Integer, String> getIdAndWords(int low, int high) {
		List<Integer> ids = Lists.newArrayList();
		for(int aid = low; aid <= high; aid++) {
			ids.add(aid);
//			break;
		}
//		D.list(ids);
		MasterItemOriented<Integer, String> master = new MasterItemOriented<>(ids, new WorkerItemOriented<Integer, String>() {

			@Override
			public String process(Integer aid) {
				int count = countOfTasks - queue.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", aid);
				String word = keywordOf(aid, false);
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetched", aid + " " + word);
				return word;
			}

		});
		
		return new TreeMap<Integer, String>(master.getResults());
	}

	public static int maxSearchId(int limit) {
		int low = 1, high = limit;
		int count = 0;
		int lastid = 1;
		String lastword = null;
		String status = "#{4}  low: {0} high: {1} middle: {2}, word: {3}";
		while(low <= high) {
			if(count++ == 100) {
				break;
			}
			int middle = (low + high) / 2;
			String word = keywordOf(middle, true);
			boolean good = word != null;
			D.pl(StrUtil.occupy(status, low, high, middle, word, count));
			if(good) {
//				D.pla(low, high, middle, word);
				low = middle + 1;
				lastword = word;
				lastid = middle;
			} else {
				high = middle - 1;
			}
		}
		D.pla(count, lastid, lastword);
		return lastid;
	}

	public static List<String> traceMaxSearchId(int limit) {
		int low = 1, high = limit;
		int count = 0;
		int lastid = 1;
		String lastword = null;
		String status = "#{0} low:{1} middle:{2} high:{3} word:{4}";
		List<String> items = Lists.newArrayList();
		while(low <= high) {
			if(count++ == 100) {
				break;
			}
			int middle = (low + high) / 2;
			String word = keywordOf(middle, true);
			boolean good = word != null;
			String msg = StrUtil.occupy(status, count, low, middle, high, word);
			D.pl(msg);
			items.add(msg);
			if(good) {
//				D.pla(low, high, middle, word);
				low = middle + 1;
				lastword = word;
				lastid = middle;
			} else {
				high = middle - 1;
			}
		}
		D.pla(count, lastid, lastword);
		String msg = StrUtil.occupy("#FINAL count:{0} id:{1} word:{2}", count, lastid, lastword);
		D.pl(msg);
		items.add(msg);
		return items;
	}
	
	public static String keywordOf(int aid, boolean showProgress) {
		Extractor<String> frank = new Extractor<String>() {
    		
			@Override
			public String getUrl() {
				String temp = "http://www.699mm.com/search-0-{0}.html";
				if(showProgress) {
					showFetching();
				}
				return StrUtil.occupy(temp, aid);
			}
			
			@Override
			protected void parse() {
				String ra = "<font color='#cc3399'>\"([^<>]+)\"</font>";
				item = StrUtil.findFirstMatchedItem(ra, source);
			}
		};
		
		return frank.process().getItem();
	}
}