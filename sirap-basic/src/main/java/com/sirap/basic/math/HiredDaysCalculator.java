package com.sirap.basic.math;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.sirap.basic.domain.HiredInfo;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.XXXUtil;

public class HiredDaysCalculator {

	private int sumOfHiredDays;
	private int sumOfSpentDays;
	
	private List<HiredInfo> items = new ArrayList<>();
	
	public HiredDaysCalculator(List<String> records) {
		init(records);
		process();
	}

	private void process() {
		XXXUtil.nullOrEmptyCheck(items, ":no employment records.");

		Date minStartDate = null;
		Date maxEndDate = null;
		
		for(HiredInfo item : items) {
			Date startDate = item.getDateStart();
			Date endDate = item.getDateEnd();
			
			int daysDiff = DateUtil.dayDiff(endDate, startDate);
			item.setDays(Math.abs(daysDiff));

			if(minStartDate == null) {
				minStartDate = startDate;
			} else {
				if(startDate.compareTo(minStartDate) < 0) {
					minStartDate = startDate;
				}
			}
			
			if(maxEndDate == null) {
				maxEndDate = endDate;
			}
			
			if(daysDiff > 0) {
				if(endDate.compareTo(maxEndDate) > 0) {
					maxEndDate = endDate;
				}
			}
			
			sumOfHiredDays += Math.abs(daysDiff);
		}
		
		Date currentDate = new Date();
		if(minStartDate != null) {
			sumOfSpentDays = DateUtil.dayDiff(currentDate, minStartDate);
		}
		
//		if(maxEndDate != null && minStartDate != null) {
//			sumOfSpentDays = DateUtil.dayDiff(maxEndDate, minStartDate);
//		}
	}
	
	public List<String> orderByHiredDays() {
		return orderByHiredDays(false);
	}
	
	public List<String> orderByHiredDays(boolean descend) {
		Collections.sort(items, new Comparator<HiredInfo>(){

			@Override
			public int compare(HiredInfo a, HiredInfo b) {
				int value = a.getDays() - b.getDays();
				if(descend) {
					value *= -1;
				}
				
				return value;
			}
						
		});
		
		return display();
	}
	
	public List<String> orderByHiredDate() {
		return orderByHiredDate(false);
	}
	
	public List<String> orderByHiredDate(boolean descend) {
		Collections.sort(items, new Comparator<HiredInfo>(){
			@Override
			public int compare(HiredInfo a, HiredInfo b) {
				int value = a.getDateStart().compareTo(b.getDateStart());
				if(descend) {
					value *= -1;
				}
				
				return value;
			}
		});
		
		return display();
	}
	
	private List<String> display() {
		List<String> list = new ArrayList<>();
		
		int count = 0;		
		for(HiredInfo item : items) {
			count++;
			item.setPseudoOrder(count);
			list.add(item.toPrint());
		}

		BigDecimal spentYears = MathUtil.divide(sumOfSpentDays, 365, 2);
		BigDecimal hiredYears = MathUtil.divide(sumOfHiredDays, 365, 2);
		BigDecimal perc = MathUtil.divide(sumOfHiredDays * 100, sumOfSpentDays, 1); 
		list.add("");
		list.add("DAYS SPENT: " + sumOfSpentDays + ", " + spentYears + " years");
		list.add("DAYS HIRED: " + sumOfHiredDays + ", " + hiredYears + " years");
		list.add("HIRED PERC: " + perc + "%");

		return list;
	}
	
	public void init(List<String> records) {
		for(String record : records) {
			HiredInfo item = new HiredInfo();
			if(item.parse(record)) {
				items.add(item);
			}
		}
	}
}
