package com.sirap.basic.domain;

import java.util.Date;

import com.sirap.basic.util.DateUtil;

public class Person {
	private String dateOfBirth;
	private char gender;
	private String areaCode;
	private String areaName;

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public String getTightDateOfBirth() {
		String temp = dateOfBirth;
		if(temp != null) {
			temp = temp.replaceAll("\\D", "");
		}
		
		return temp;
	}

	public Date getBirthDate() {
		Date date = DateUtil.parse(DateUtil.DATE_TIGHT, getTightDateOfBirth());
		
		return date;
	}
	
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public char getGender() {
		return gender;
	}
	public void setGender(char gender) {
		this.gender = gender;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
	public String getAreaName() {
		return areaName != null ? areaName : areaCode;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String displayGender() {
		String value = "";
		if(gender == 'M') {
			value = "Male";
		} else if(gender == 'F') {
			value = "Female";
		} else {
			value = "Transgender";
		}
		
		return value;
	}
}
