package com.sirap.extractor.manager;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.impl.IcibaTranslationExtractor;

public class IcibaManager {

	private static IcibaManager instance;
	
	public static IcibaManager g() {
		if(instance == null) {
			instance = new IcibaManager();
		}
		
		return instance;
	}
	
	public synchronized boolean saveToDatabase(ValuesItem item, String location, String charset) {
		List<String> items = null;
		if(FileUtil.exists(location)) {
			items = IOUtil.readFileIntoList(location, charset);
		} else {
			items = Lists.newArrayList();
		}
		items.add(item.toPrint("conn=|"));

		return IOUtil.saveAsTxtWithCharset(items, location, charset);
	}
	
	public synchronized List<ValuesItem> readFromDatabase(String word, String location, String charset, boolean caseSensitive) {
		if(!FileUtil.exists(location)) {
			return null;
		}
		List<String> items = IOUtil.readFileIntoList(location, charset);
		List<ValuesItem> list = Lists.newArrayList();
		for(String item : items) {
			ValuesItem vi = new ValuesItem();
			vi.parse(item);
			if(vi.inRange(0)) {
				String target = vi.getByIndex(0) + "";
				boolean isMatched = false;
				if(caseSensitive) {
					isMatched = StrUtil.equalsCaseSensitive(word, target);
				} else {
					isMatched = StrUtil.equals(word, target);
				}
				if(isMatched) {
					list.add(vi);
				}
			}
		}
		
		return list;
	}
	
	public ValuesItem fetchFromWebsite(String word) {
		Extractor<ValuesItem> frank = new IcibaTranslationExtractor(word);
		return frank.process().getItem();
	}
}
