package com.sirap.titus;

import com.sirap.basic.domain.MexItem;

@SuppressWarnings("serial")
public class VerseSake extends MexItem {
	private int verseNumber;
	private String value;
	
	public VerseSake(int verseNumber) {
		this.verseNumber = verseNumber;
	}
	
	public VerseSake(int verseNumber, String value) {
		this.verseNumber = verseNumber;
		this.value = value;
	}

	public int getVerseNumber() {
		return verseNumber;
	}

	public void setVerseNumber(int verseNumber) {
		this.verseNumber = verseNumber;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		return verseNumber + " " + value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + verseNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VerseSake other = (VerseSake) obj;
		if (verseNumber != other.verseNumber)
			return false;
		return true;
	}
}