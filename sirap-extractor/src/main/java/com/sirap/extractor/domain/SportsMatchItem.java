package com.sirap.extractor.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class SportsMatchItem extends MexItem {
	private String order;
	private String datetime;
	private String group;
	private String homeTeam;
	private String status;
	private String awayTeam;
	
	public void setOrder(String order) {
		this.order = order;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}
	
	public boolean isMatched(String key) {
		if(StrUtil.equals("#" + order, key)) {
			return true;
		}
		
		if(isRegexMatched("#" + order, key)) {
			return true;
		}
		
		String dateFlag = StrUtil.parseParam(">=(\\d{4}-\\d{2}-\\d{2})", key);
		if(dateFlag != null) {
			if(datetime.startsWith(dateFlag)) {
				return true;
			}
			if(dateFlag.compareTo(datetime) < 0) {
				return true;
			}
		}
		
		if(isRegexMatched(datetime, key)) {
			return true;
		}

		if(StrUtil.contains(datetime, key)) {
			return true;
		}
		
		if(StrUtil.equals("#" + group, key)) {
			return true;
		}
		
		if(isRegexMatched("#" + group, key)) {
			return true;
		}

		if(isRegexMatched(homeTeam, key)) {
			return true;
		}

		if(StrUtil.contains(homeTeam, key)) {
			return true;
		}

		if(isRegexMatched(status, key)) {
			return true;
		}

		if(StrUtil.contains(status, key)) {
			return true;
		}

		if(isRegexMatched(awayTeam, key)) {
			return true;
		}

		if(StrUtil.contains(awayTeam, key)) {
			return true;
		}
		
		return false;
	}
	
	public String toString() {
		String goodOrder = StrUtil.extend(order, 3);
		String temp = StrUtil.occupy("#{0} {1}  {2}  {3}  {4}  {5}", goodOrder, datetime, group, homeTeam, status, awayTeam);
		return temp;
	}
	
}
