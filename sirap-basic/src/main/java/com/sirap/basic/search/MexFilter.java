package com.sirap.basic.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sirap.basic.domain.MexItem;

public class MexFilter<T extends MexItem> {
	
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
		this.source = source;
	}

	public MexFilter() {
		
	}
	
	public MexFilter(String criteria, List<T> source) {
		this.criteria = criteria;
		this.source = source;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> process() {
		if(source == null) return Collections.EMPTY_LIST;

		List<T> matchedList = new ArrayList<T>();
		
		MexCriteria mex = parseCriteria();
		if(mex == null) return Collections.EMPTY_LIST;
		
		String logic = mex.getLogic();
		List<String> criterias = mex.getCriterias();
		
		if(LOGIC_AND.equalsIgnoreCase(logic)) {
			int count = 0;
			for(T item:source) {
				count++;
				if(item == null) {
					continue;
				}
				boolean isAllMatched = true;
				for(String keyWord:criterias) {
					if(!item.isMatched(keyWord)) {
						isAllMatched = false;
						break;
					}
				}
				if(isAllMatched) {
					item.setPseudoOrder(count);
					matchedList.add(item);
				}
			}
		} else if(LOGIC_OR.equalsIgnoreCase(logic)) {
			int count = 0;
			Set<T> set = new LinkedHashSet<T>();
			for(T item:source) {
				count++;
				if(item == null) {
					continue;
				}
				boolean isAnyMatched = false;
				for(String keyWord:criterias) {
					if(item.isMatched(keyWord)) {
						isAnyMatched = true;
						break;
					}
				}
				if(isAnyMatched) {
					item.setPseudoOrder(count);
					set.add(item);
				}
			}
			matchedList.addAll(set);
		}
		
		return matchedList;
	}
	
	public MexCriteria parseCriteria() {
		if(criteria == null) {
			return null;
		}
		
		List<String> list = new ArrayList<String>(); 
		
		if(criteria.indexOf(SYMBOL_AND) != -1) {
			String[] criteriaArr = criteria.split(SYMBOL_AND);
			
			for(int i = 0; i < criteriaArr.length; i++) {
				String temp = criteriaArr[i].trim();
				if(temp.length() != 0) {
					list.add(temp);
				}
			}
			
			if(list.size() > 0) {
				return new MexCriteria(LOGIC_AND, list);
			}
		} else if(criteria.indexOf(SYMBOL_OR) != -1) {
			String[] criteriaArr2 = criteria.split("\\" + SYMBOL_OR);
			
			for(int i = 0; i < criteriaArr2.length; i++) {
				String temp = criteriaArr2[i].trim();
				if(temp.length() != 0) {
					list.add(temp);
				}
			}
			
			if(list.size() > 0) {
				return new MexCriteria(LOGIC_OR, list);
			}
		} else {
			list.add(criteria);		
			return new MexCriteria(list);
		}

		return null;
	}
}
