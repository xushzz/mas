package com.sirap.basic.component;

public class Ease<T extends Object> extends Extractor<T> {

	private String location;
	
	public Ease(String location) {
		this.location = location;
	}
	
	@Override
	public String getUrl() {
		return location;
	}
		
	@Override
	protected void parse() {
		throw new UnsupportedOperationException("please override this method.");
	}
}
