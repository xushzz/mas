package com.sirap.common.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;


@SuppressWarnings("serial")
public class Player extends MexItem implements Cloneable, Comparable<Player> {
	protected String team;
	protected String name;
	protected String number;
	protected String position;
	protected String dob;
	protected String country;
	protected String height;
	protected String weight;
	
	public String getDob() {
		return dob;
	}
	
	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPosition() {
		return position;
	}
	
	public void setPosition(String position) {
		this.position = position;
	}
	
	@Override
	public int hashCode() {
		if(team == null) {
			return 0;
		}
		
		return team.hashCode() * 100 + number.hashCode(); 
	}
	
	public boolean equals(Player p2) {
		return p2.getTeam().equals(team) && p2.getNumber().equals(number); 
	}
	
	@Override
	public int compareTo(Player p2) {
		int byTeam = team.compareTo(p2.team);
		if(byTeam != 0) {
			return byTeam;
		}
		
		int bench = 999;
		Integer n1 = MathUtil.toInteger(number, bench);
		Integer n2 = MathUtil.toInteger(p2.number, bench);
		
		int byNumber = n1 - n2;
		if(byNumber != 0) {
			return byNumber;
		}
		
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(StrUtil.extend(team, 25));
		sb.append(StrUtil.extend(number, 5));
		sb.append(StrUtil.extend(name, 25));
		sb.append(StrUtil.extend(position, 5));
		sb.append(StrUtil.extend(dob, 18));
		sb.append(StrUtil.extend(country, 15));
		sb.append(StrUtil.extend(height, 6));
		sb.append(StrUtil.extend(weight, 5));
		
		return sb.toString();
	}
}
