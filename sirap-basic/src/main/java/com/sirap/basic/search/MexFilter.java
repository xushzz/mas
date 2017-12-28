package com.sirap.basic.search;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class MexFilter<T extends MexItem> {
	
	public static final String LOGIC_AND = "and";
	public static final String LOGIC_OR = "or";

	public static final char ESCAPE = '\\';
	public static final String SYMBOL_AND = "&";
	public static final String SYMBOL_OR = "|";
	
	private boolean caseSensitive;
	private String criteria;
	private List<T> source;
	private boolean stayCriteria = true;
	
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public void setSource(List<T> source) {
		this.source = source;
	}

	public void setStayCriteria(boolean stay) {
		this.stayCriteria = stay;
	}

	public MexFilter() {
		
	}
	
	public MexFilter(String criteria, List<T> source) {
		this.criteria = criteria;
		this.source = source;
	}
	
	public MexFilter(String criteria, List<T> source, boolean caseSensitive) {
		this.criteria = criteria;
		this.source = source;
		this.caseSensitive = caseSensitive;
	}
	
	public List<T> process() {
		XXXUtil.nullCheck(source, "source");
		XXXUtil.nullCheck(criteria, "criteria");
		
		List<T> matchedList = new ArrayList<T>();
		
		MexCriteria mex = createMexCriteria();
		if(mex == null) {
			throw new MexException("'{0}' is not a valid criteria.", criteria);
		}
		
		String logic = mex.getLogic();
		List<String> criterias = mex.getCriterias();
		
		if(LOGIC_AND.equalsIgnoreCase(logic)) {
			int count = 0;
			Set<T> set = new LinkedHashSet<T>();
			for(T item:source) {
				count++;
				if(item == null) {
					continue;
				}
				boolean isAllMatched = true;
				for(String keyWord:criterias) {
					if(!item.isMatched(keyWord, caseSensitive)) {
						isAllMatched = false;
						break;
					}
				}
				if(isAllMatched) {
					item.setPseudoOrder(count);
					set.add(item);
				}
			}
			matchedList.addAll(set);
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
					if(item.isMatched(keyWord, caseSensitive)) {
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
	
	public MexCriteria createMexCriteria() {
		List<String> list = new ArrayList<String>();
		if(stayCriteria || StrUtil.isRegexMatched("[" + SYMBOL_AND + SYMBOL_OR + "]+", criteria)) {
			list.add(criteria);
			return new MexCriteria(list);
		}
		
		if(criteria.indexOf(SYMBOL_AND) != -1) {
			List<String> whats = snort(criteria, SYMBOL_AND, ESCAPE);
			
			for(String what : whats) {
				String temp = what.trim();
				if(temp.length() != 0) {
					list.add(temp);
				}
			}
			
			if(list.size() > 0) {
				return new MexCriteria(LOGIC_AND, list);
			}
		} else if(criteria.indexOf(SYMBOL_OR) != -1) {
			List<String> whats = snort(criteria, SYMBOL_OR, ESCAPE);
			
			for(String what : whats) {
				String temp = what.trim();
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
	
	public static List<String> snort(String source, String by, char escape) {
		String random = RandomUtil.digits(99);
		String temp = source.replace(escape + by, random);
		List<String> items = StrUtil.split(temp, by);
		List<String> recover = Lists.newArrayList();
		for(String item : items) {
			recover.add(item.replace(random, by));
		}
		
		return recover;
	}
}
