package com.sirap.common.component;

public class FlexibleTuner implements Comparable<FlexibleTuner> {
	
	private String name;
	private Number number;
	private int amplifier = 1000;
	
	public <E extends Number> FlexibleTuner(String name, E number) {
		this.name = name;
		this.number = number;
	}
	
	public <E extends Number> FlexibleTuner(String name, E number, int amplifier) {
		this.name = name;
		this.number = number;
		this.amplifier = amplifier;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getNumber() {
		return number;
	}

	public void setNumber(Number number) {
		this.number = number;
	}

	@Override
	public int compareTo(FlexibleTuner o) {
		int v1 = parseValue();
		int v2 = o.parseValue();
		
		int byNumber = v1 - v2;
		if(byNumber != 0) {
			return byNumber;
		}
		
		int result = name.compareTo(o.name);
		
		return result;
	}
	
	private int parseValue() {
		int value = (int)(number.doubleValue() * amplifier);
		
		return value;
	}
	
	@Override
	public String toString() {
		return name + ", " + number;
	}

}
