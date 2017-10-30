package com.sirap.extractor.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class SportsMatchItem extends MexItem {
	private String order;
	private String datetime;
	private String weekday;
	private String group;
	private String round;
	private String homeTeam;
	private String status;
	private String awayTeam;
	
	public void setOrder(String order) {
		this.order = order;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public void setRound(String round) {
		this.round = round;
	}
	public String getRoundNumber() {
		if(round == null) {
			return null;
		} else {
			return round.replaceAll("\\D", "");
		}
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
	
	public void setWeekday(String weekday) {
		this.weekday = weekday;
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
		

		dateFlag = StrUtil.parseParam("<=(\\d{4}-\\d{2}-\\d{2})", key);
		if(dateFlag != null) {
			if(datetime.startsWith(dateFlag)) {
				return true;
			}
			if(datetime.compareTo(dateFlag) < 0) {
				return true;
			}
		}
		
		if(isRegexMatched(datetime, key)) {
			return true;
		}

		if(StrUtil.contains(datetime, key)) {
			return true;
		}
		
		if(isRegexMatched(weekday, key)) {
			return true;
		}
		
		if(StrUtil.contains(weekday, key)) {
			return true;
		}
		
		if(StrUtil.equals("#" + group, key)) {
			return true;
		}
		
		if(isRegexMatched("#" + group, key)) {
			return true;
		}

		String roundNumber = getRoundNumber();
		if(!EmptyUtil.isNullOrEmpty(roundNumber)) {
			if(StrUtil.equals("R" + roundNumber, key)) {
				return true;
			}
			
			if(isRegexMatched("R" + roundNumber, key)) {
				return true;
			}
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
	
	@Override
	public String toPrint(String options) {
		int spaceNumber = OptionUtil.readIntegerPRI(options, "space", 3);
		String spaceK = StrUtil.repeat(' ', spaceNumber);
		String goodOrder = "#" + StrUtil.padRight(order, 3);
		int len = 14;
		String teamA = StrUtil.padRightAscii(homeTeam, len);
		String teamB = StrUtil.padLeftAscii(awayTeam, len);
		StringBuffer sb = StrUtil.sb();
		sb.append(goodOrder).append(spaceK);
		sb.append(datetime).append(spaceK);
		if(weekday != null) {
			sb.append(weekday).append(spaceK);
		}
		if(group != null) {
			sb.append(group).append(spaceK);
		}
		if(round != null) {
			sb.append(StrUtil.padRightAscii(round, 6)).append(spaceK);
		}
		sb.append(teamA).append(spaceK);
		String goodStatus = StrUtil.padRight(status, 3);
		sb.append(goodStatus).append(spaceK);
		sb.append(teamB).append(spaceK);
		
		String temp = sb.toString();
		boolean markToday = OptionUtil.readBooleanPRI(options, "mark", true);
		if(markToday) {
			String today = DateUtil.displayNow("yyyy-MM-dd");
			if(StrUtil.startsWith(datetime, today)) {
				temp = temp.replace(" " + today, "*" + today);
			}
		}
		
		return temp;
	}

	public String toString() {
		String space2 = "  ";
		String goodOrder = "#" + StrUtil.padRight(order, 3);
		int len = 14;
		String teamA = StrUtil.padRightAscii(homeTeam, len);
		String teamB = StrUtil.padLeftAscii(awayTeam, len);
		StringBuffer sb = StrUtil.sb();
		sb.append(goodOrder).append(space2);
		sb.append(datetime).append(space2);
		if(weekday != null) {
			sb.append(weekday).append(space2);
		}
		if(group != null) {
			sb.append(group).append(space2);
		}
		if(round != null) {
			sb.append(StrUtil.padRightAscii(round, 6, " ")).append(space2);
		}
		sb.append(teamA).append(space2);
		String goodStatus = StrUtil.padRight(status, 3);
		sb.append(goodStatus).append(space2);
		sb.append(teamB).append(space2);
		
		return sb.toString();
	}
	
}
