package com.sirap.common.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.sirap.basic.component.DistributionKeyComparator;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.MexedTimer;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.LoginRecord;
import com.sirap.common.framework.SimpleKonfig;

public class LoginHistoryManager extends MexedTimer {
	
	private static LoginHistoryManager instance;
	private List<LoginRecord> ALL_RECORDS;
	
	private LoginRecord currentLogin;
	private String filePath;
	
	public static LoginHistoryManager g() {
		if(instance == null) {
			instance = new LoginHistoryManager();
			instance.start();
		}
		
		return instance;
	}
	
	private void start() {
		if(!SimpleKonfig.g().isHistoryEnabled()) {
			return;
		}
		
		String fileName = "L.txt";
    	String location = SimpleKonfig.g().pathWithSeparator("storage.history", Konstants.FOLDER_HISTORY);
    	
		filePath = location + fileName;
		List<LoginRecord> allRecords = getAllRecords(true);
		currentLogin = new LoginRecord(DateUtil.displayNow(DateUtil.DATETIME));
		allRecords.add(currentLogin);
		setDelaySeconds(60);
		setPeriodSeconds(60);
		startTimer();
	}
	
	private void increaseAndWrite() {
		currentLogin.increase();
		MexUtil.saveAsNew(getAllRecords(false), filePath);
	}
	
	@Override
	protected void timerAction() {
		increaseAndWrite();
	}
	
	@SuppressWarnings("unchecked")
	public List<LoginRecord> getLoginRecords(int latestCount) {
		if(latestCount <= 0) {
			return Collections.EMPTY_LIST;
		}
		
		List<LoginRecord> allRecords = getAllRecords(false);
		int size = allRecords.size();
		int startIndex = size - latestCount;
		if(startIndex < 0) {
			startIndex = 0;
		}
		
		List<LoginRecord> records = new ArrayList<LoginRecord>();
		for(int i = startIndex; i < size; i++) {
			records.add(allRecords.get(i));
		}
		
		return records;
	}
	
	public synchronized List<LoginRecord> getAllRecords(boolean isForcibly) {
		if(EmptyUtil.isNullOrEmpty(ALL_RECORDS) || isForcibly) {
			ALL_RECORDS = MexUtil.readMexItemsViaExplicitClass(filePath, LoginRecord.class);
		}
		
		return ALL_RECORDS;
	}
	
	public List<LoginRecord> getAllInputRecords() {
		return getAllRecords(false);
	}
	
	public List<LoginRecord> search(String keyWord) {
		List<LoginRecord> records = new ArrayList<LoginRecord>(getAllRecords(false));
		MexFilter<LoginRecord> filter = new MexFilter<LoginRecord>(keyWord, records);
		List<LoginRecord> list = filter.process();
		
		return list;
	}
	
	public List<String> displayDistribution() {
		return displayDistribution(getAllRecords(false), Integer.MAX_VALUE);
	}
	
	public List<String> displayDistribution(List<LoginRecord> records) {
		return displayDistribution(records, Integer.MAX_VALUE);
	}
	
	public List<String> displayDistribution(List<LoginRecord> records, int maxCount) {
		Map<String, Integer> typeValueMap = new TreeMap<String, Integer>(new DistributionKeyComparator());
		for(LoginRecord p : records) {
			String[] arr = p.getDatetimeLogin().split(" ");
			String type = arr[0];
			Integer totalMins = typeValueMap.get(type);
			if(totalMins == null) {
				typeValueMap.put(type, p.getMins());
			} else {
				typeValueMap.put(type, totalMins + p.getMins());
			}
		}
		
		return print(typeValueMap, 'T', 100, maxCount);
	}
	
	public static List<String> print(Map<String, Integer> typeValueMap, char repeatedChar, int maxLenToDisplay, int maxCount) {
		int sum = 0;
		int count = 0;
		List<String> list = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry: typeValueMap.entrySet()) {
			String key = entry.getKey();
			Integer number = entry.getValue();
			sum += number;
			
			int cells = number/10;
			if(cells == 0) {
				cells = 1;
			}
			
			String display = StrUtil.repeatNicely(repeatedChar, cells, maxLenToDisplay);
			
			String record = key + " " + display;
			list.add(record);
			count++;
			if(count >= maxCount) {
				break;
			}
		}
		
		if(!typeValueMap.isEmpty()) {
			list.add("Total " + sum + " minutes.");
		}
		
		return list;
	}
	
	public void exitGracefully() {
		cancelTimer();
		if(currentLogin != null) {
			currentLogin.setDatetimeExit(DateUtil.displayNow(DateUtil.DATETIME));
			MexUtil.saveAsNew(ALL_RECORDS, filePath);
		}
	}
}
