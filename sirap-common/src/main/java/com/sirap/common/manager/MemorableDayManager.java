package com.sirap.common.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.sirap.basic.search.MexFilter;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.common.domain.MemoryRecord;
import com.sirap.common.framework.SimpleKonfig;

public class MemorableDayManager {
	
	private static MemorableDayManager instance;
	private List<MemoryRecord> ALL_RECORDS;
	private String filePath;
	
	public static MemorableDayManager g(String filePath) {
		instance = new MemorableDayManager(filePath);
		
		return instance;
	}
	
	private MemorableDayManager(String filePath) {
		this.filePath = filePath;
	}
	
	@SuppressWarnings("unchecked")
	public List<MemoryRecord> getMemoryRecords(int latestCount) {
		if(latestCount <= 0) {
			return Collections.EMPTY_LIST;
		}
		
		List<MemoryRecord> allRecords = getAllRecords();
		int size = allRecords.size();
		int startIndex = size - latestCount;
		if(startIndex < 0) {
			startIndex = 0;
		}
		
		List<MemoryRecord> records = new ArrayList<MemoryRecord>();
		for(int i = startIndex; i < size; i++) {
			records.add(allRecords.get(i));
		}
		
		return records;
	}
	
	public synchronized List<MemoryRecord> getAllRecords(boolean isForcibly) {
		if(EmptyUtil.isNullOrEmpty(ALL_RECORDS) || isForcibly) {
			String charset = SimpleKonfig.g().getCharsetInUse();
			ALL_RECORDS = MexUtil.readMexItemsViaExplicitClass(filePath, MemoryRecord.class, charset);
		}
		
		return ALL_RECORDS;
	}
	
	public List<MemoryRecord> getAllRecords() {
		return getAllRecords(true);
	}
	
	public List<MemoryRecord> search(String keyWord) {
		List<MemoryRecord> records = new ArrayList<MemoryRecord>(getAllRecords(false));
		MexFilter<MemoryRecord> filter = new MexFilter<MemoryRecord>(keyWord, records);
		List<MemoryRecord> list = filter.process();
		
		return list;
	}
	
	public List<MemoryRecord> searchWithNDays(int ndays) {
		List<MemoryRecord> records = new ArrayList<MemoryRecord>(getAllRecords(false));
		List<MemoryRecord> list = new ArrayList<>();
		Date currentDate = new Date();
		for(MemoryRecord record: records) {
			Date date = record.getDate();
			int dayOfYearMemory = DateUtil.getDay(date);
			int dayOfYearNow = DateUtil.getDay(currentDate);
			int diff = dayOfYearMemory - dayOfYearNow;
			if(Math.abs(diff) < ndays) {
				list.add(record);
			}
		}
		
		return list;
	}
}
