package com.sirap.basic.json;

public class User {
	private int count = 1888;
	
	public String base = "project";
	public int[] points = {1,2,3,4,5};
	private Long a;

	public Long getA() {
		return a;
	}

	public void setA(Long a) {
		this.a = a;
	}

	private String b;

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	@Override
	public String toString() {
		return "User [count=" + count + ", base=" + base + ", points=" + points + ", a=" + a + ", b=" + b + "]";
	}
}
