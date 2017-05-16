package com.sirap.poverty;

import com.sirap.basic.domain.MexItem;

public class Poverty extends MexItem {
	private int number;
	private int score;
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean parse(String record) {
		String[] arr = record.split("\t");
		
		if(arr.length != 2) {
			return false;
		}
		
		number = Integer.parseInt(arr[0]);
		score = Integer.parseInt(arr[1]);
		
		return true;
	}
	
	@Override
	public String toString() {
		return number + ", " + score;
	}
}
