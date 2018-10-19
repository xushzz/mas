package com.sirap.basic.domain;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class NameRankItem extends MexItem implements Comparable<NameRankItem> {
	private String name;
	private String gender;
	private String rank;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}
	
	public String eggOf() {
		String temp = "EGGS.put(\"{0}{1}\", \"{2}\");";
		String nice = name.toUpperCase().charAt(0) + name.substring(1).toLowerCase();
		return StrUtil.occupy(temp, gender, rank, nice);
	}
	
	@Override
	public boolean isMatched(String keyWord) {
		if(StrUtil.contains(name, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(name, keyWord)) {
			return true;
		}
		
		if(StrUtil.equals("#" + gender, keyWord)) {
			return true;
		}
		
		if(StrUtil.equals("#" + rank, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(rank, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(rank, keyWord)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public List toList(String options) {
		return Lists.newArrayList(rank, gender, name);
	}

	@Override
	public String toString() {
		return toPrint();
	}

	@Override
	public int compareTo(NameRankItem another) {
		return rankOf() - another.rankOf();
	}
	
	private int rankOf() {
		String va = StrUtil.findFirstMatchedItem("(\\d+)", rank);
		if(va != null) {
			return Integer.parseInt(va);
		} else {
			return 0;
		}
	}

}
