package com.sirap.basic.component;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;

public class MatrixCalendar {
	
	public static enum MatrixMode {

		THREE(3), FOUR(4);
		
	    final int oldMode;

	    private MatrixMode(int oldMode) {
	        this.oldMode = oldMode;
	    }
	}
	
	private List<List<String>> grandList;
	private List<List<List<String>>> totalList;
	private List<String> results = new ArrayList<String>();;
	
	public MatrixCalendar(List<List<String>> grandList, MatrixMode mode) {
		this.grandList = grandList;
		process(mode);
	}
	
	private void process(MatrixMode mode) {
		int sectionsPerLine = mode.oldMode;
		if(EmptyUtil.isNullOrEmpty(grandList)) {
			return;
		}
		
		splitGrandList(sectionsPerLine);
		
		for(int i = 0; i < totalList.size(); i++) {
			List<List<String>> combinedSections = totalList.get(i);
			int[] maxInfo = max(combinedSections);
			adjust(combinedSections, maxInfo);
			
			List<String> combinedList = combine(combinedSections, maxInfo[0]);
			results.addAll(combinedList);
			results.add("");
		}
	}
	
	public List<String> getResults() {
		return results;
	}

	private int[] max(List<List<String>> combinedSections) {
		int maxLines = 0;
		int maxLen = 0;
		for(List<String> list:combinedSections) {
			int lines = list.size();
			if(lines > maxLines) {
				maxLines = lines;
			}
			
			for(String record:list) {
				int len = record.length();
				if(len > maxLen) {
					maxLen = len;
				}
			}
		}
		
		return new int[] {maxLines, maxLen};
	}
	
	private void adjust(List<List<String>> combinedSections, int[] maxInfo) {
		int tempMaxLines = maxInfo[0];
		int tempMaxLen = maxInfo[1];
		
		for(List<String> list:combinedSections) {

			int linesDiff = tempMaxLines - list.size();
			for(int i = 0; i < linesDiff; i++) {
				list.add("");
			}
			
			for(int i = 0; i < list.size(); i++) {
				String record = list.get(i);
				String temp = StrUtil.extend(record, tempMaxLen + 2);
				list.set(i, temp);
			}
		}
	}
	
	private List<String> combine(List<List<String>> tempGrandList, int maxLines) {
		List<String> combinedList = new ArrayList<String>();
		for(int i = 0; i < maxLines; i++) {
			StringBuffer sb = new StringBuffer();
			for(int m = 0; m < tempGrandList.size(); m++) {
				List<String> list = tempGrandList.get(m);
				String record = list.get(i);
				sb.append(record);
			}
			combinedList.add(sb.toString());
		}
		
		return combinedList;
	}
	
	private List<List<List<String>>> splitGrandList(int sectionsPerLine) {
		totalList = new ArrayList<List<List<String>>>();
		List<List<String>> tempGrandList = new ArrayList<List<String>>();
		for(int m = 0; m < grandList.size(); m++) {
			tempGrandList.add(grandList.get(m));
			if((m+1)%sectionsPerLine == 0) {
				totalList.add(tempGrandList);
				tempGrandList = new ArrayList<List<String>>();
			}
		}
		if(!tempGrandList.isEmpty()) {
			totalList.add(tempGrandList);
		}
		
		return totalList;
	}
}
