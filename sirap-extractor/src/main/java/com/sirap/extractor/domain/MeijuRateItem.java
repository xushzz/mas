package com.sirap.extractor.domain;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.search.SizeCriteria;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MeijuRateItem extends MexItem implements Comparable<MeijuRateItem> {
	private String name;
	private String rate;
	private String href;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rank) {
		this.rate = rank;
	}
	
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public boolean isMatched(String keyWord) {
		if(StrUtil.contains(name, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(name, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(rate, keyWord)) {
			return true;
		}

		SizeCriteria quinn = getSizeCriteria(keyWord);
		if(quinn != null && quinn.isGood(rankOf())) {
			return true;
		}
		
		return false;
	}
	
	public List toList(String options) {
		List items = Lists.newArrayList(name, rate, href);

		return items;
	}

	@Override
	public String toString() {
		return StrUtil.connectWithSpace(toList());
	}

	@Override
	public int compareTo(MeijuRateItem another) {
		double result = rankOf() - another.rankOf();
		if(result < 0) {
			return -1;
		} else if(result == 0) {
			return 0;
		} else {
			return 1;
		}
	}
	
	private double rankOf() {
		Double va = MathUtil.toDouble(rate);
		if(va != null) {
			return va;
		} else {
			return 0;
		}
	}

}
