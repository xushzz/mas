package com.sirap.basic.component.comparator;

import java.util.Comparator;

public class StringSenseComparator implements Comparator<String> {

	private boolean sensitive;
	
	public StringSenseComparator() {
	}
	
	public StringSenseComparator(boolean sensitive) {
		this.sensitive = sensitive;
	}
	
	@Override
	public int compare(String strA, String strB) {
		if(sensitive) {
			return strA.compareTo(strB);
		} else {
			return strA.compareToIgnoreCase(strB);
		}
	}
}
