package com.sirap.basic.component;

public class Finer {
	private String type;
	private int value;
	
	public Finer(String type, int value) {
		this.type = type;
		this.value = value;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Finer [type=" + type + ", value=" + value + "]";
	}
}
