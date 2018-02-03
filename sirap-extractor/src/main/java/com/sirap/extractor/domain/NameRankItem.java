package com.sirap.extractor.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class NameRankItem extends MexItem implements Comparable<NameRankItem> {
	private String name;
	private String rank;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}
	
	@Override
	public boolean isMatched(String keyWord) {
		if(StrUtil.contains(name, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(name, keyWord)) {
			return true;
		}
		
		if(StrUtil.equals(rank, keyWord)) {
			return true;
		}
		
		if(StrUtil.equals("#" + rank, keyWord)) {
			return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(StrUtil.padRight(rank, 4));
		sb.append("  ");
		sb.append(name);
		
		return sb.toString();
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
