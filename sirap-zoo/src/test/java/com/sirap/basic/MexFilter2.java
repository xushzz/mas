package com.sirap.basic.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class MexFilter2<T extends MexItem> {
	
	public static final String LOGIC_AND = "and";
	public static final String LOGIC_OR = "or";
	
	public static final String SYMBOL_AND = "&";
	public static final String SYMBOL_OR = "|";
	
	private String criteria;
	private List<T> source;
	
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public void setSource(List<T> source) {
		this.source = Collections.unmodifiableList(source);
	}

	public MexFilter2() {
		
	}
	
	public MexFilter2(String criteria, List<T> source) {
		this.criteria = criteria;
		this.source = Collections.unmodifiableList(source);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> process() {
		if(source == null) return Collections.EMPTY_LIST;
		List<T> tempList = filter(new ArrayList<T>(source), criteria);
		
		return tempList;
	}
	
	public List<T> filter(List<T> tempList, String criteria) {
		FilterPoint fp = whatNext(criteria);
		if(fp == null) {
			return tempList;
		}
		
		D.pl(fp);
		String op = fp.operator;
		String target = fp.target;
		int endIndex = fp.endIndex;
		
		if(fp.isEmptyCrteria()) {
			String newCriteria = criteria.substring(endIndex);
			return filter(tempList, newCriteria);
		}
		
		List<T> rightList = null;
		if(fp.targetAsExpression) {
			rightList = filter(source, target);
		} else {
			rightList = filterSingleCriteria(source, target);
		}
		if(StrUtil.equals(SYMBOL_AND, op)) {
			tempList = intersection(tempList, rightList);
		} else if(StrUtil.equals(SYMBOL_OR, op)) {
			tempList = merge(tempList, rightList);
		} else {
			XXXUtil.alert("Shit happens, invalid op [" + op + "].");
		}

		String newCriteria = criteria.substring(endIndex);
		return filter(tempList, newCriteria);
	}
	
	public FilterPoint whatNext(String criteria) {
		D.sink(criteria);
		if(EmptyUtil.isNullOrEmpty(criteria)) {
			return null;
		}
		
		FilterPoint fp = null;
		
		String regexA = "^([&\\|]|)\\((.*)\\)";
		Matcher ma = StrUtil.createMatcher(regexA, criteria);
		if(ma.find()) {
			fp = createFP(ma);
			fp.targetAsExpression = true;
			fp.endIndex = fp.endIndex + 1;
			return fp;
		}

		String regexB = "^([&\\|]|)([^&\\|\\(]*)";
		ma = StrUtil.createMatcher(regexB, criteria);
		if(ma.find()) {
			fp = createFP(ma);
			return fp;
		}
		
		fp = new FilterPoint();
		fp.endIndex = 1;
		
		return fp;
	}
	
	private FilterPoint createFP(Matcher ma) {
		FilterPoint fil = new FilterPoint();
		String op = ma.group(1);
		if(EmptyUtil.isNullOrEmpty(op)) {
			op = "&";
		}
		String target = ma.group(2);
		int endIndex = ma.end(2);
		fil.operator = op;
		fil.target = target;
		fil.endIndex = endIndex;

		return fil;
	}
	
	private List<T> filterSingleCriteria(List<T> items, String singleCriteria) {
		int count = 0;
		List<T> matchedList = new ArrayList<T>();
		for(T item:source) {
			count++;
			if(item == null) {
				continue;
			}
			
			if(item.isMatched(singleCriteria)) {
				item.setPseudoOrder(count);
				matchedList.add(item);
			}
		}
		
		return matchedList;
	}
	
	public List<T> intersection(List<T> listA, List<T> listB) {
		List<T> listC = new ArrayList<>();
		for(T item : listB) {
			if(listA.indexOf(item) >= 0) {
				listC.add(item);
			}
		}
		
		return listC;
	}
	
	public List<T> merge(List<T> listA, List<T> listB) {
		Set<T> set = new LinkedHashSet<T>();
		set.addAll(listA);
		set.addAll(listB);
		
		return new ArrayList<>(set);
	}
}

class FilterPoint {
	public String operator;
	public String target;
	public boolean targetAsExpression;
	public int endIndex;
	
	public String toString() {
		String temp = operator + ", " + target + ", " + targetAsExpression + ", " + endIndex;
		return temp;
	}
	
	public boolean isEmptyCrteria() {
		return EmptyUtil.isNullOrEmpty(target);
	}
}
