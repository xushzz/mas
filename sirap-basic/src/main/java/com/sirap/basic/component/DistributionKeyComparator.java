package com.sirap.basic.component;

import java.util.Comparator;

import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;

public class DistributionKeyComparator implements Comparator<String> {

	public static final int TYPE_INT = 0;
	public static final int TYPE_STRING = 1;
	public static final int TYPE_END = 2;
	
	private boolean descend;
	
	public DistributionKeyComparator() {
	}
	
	public DistributionKeyComparator(boolean descend) {
		this.descend = descend;
	}
	
	@Override
	public int compare(String strA, String strB) {
		int value = compareAscend(strA, strB);
		if(descend) {
			return -value;
		} else {
			return value;
		}
	}
	
	public int compareAscend(String strA, String strB) {
		int[] arrA = toTypeAndValue(strA);
		int[] arrB = toTypeAndValue(strB);
		
		int byType = arrA[0] - arrB[0];
		if(byType != 0) {
			return byType;
		}
		
		if(arrA[0] == TYPE_INT) {
			int byIntValue = arrA[1] - arrB[1];
			return byIntValue;
		} else if(arrA[0] == TYPE_STRING) {
			int byAlphabet = strA.toString().toLowerCase().compareTo(strB.toString().toLowerCase());
			return byAlphabet;
		}
		
		return 0;
	}

	private int[] toTypeAndValue(String obj) {
		int[] typeAndValue = new int[2];
		Integer intValue = MathUtil.toInteger(obj);
		if(intValue == null) {
			if(StrUtil.containsIgnoreCase(Konstants.DISTRIBUTION_KEYS_TBD, obj)) {
				typeAndValue[0] = TYPE_END;
			} else {
				typeAndValue[0] = TYPE_STRING;
			}
		} else {
			typeAndValue[0] = TYPE_INT;
			typeAndValue[1] = intValue;
		}
		
		return typeAndValue;
	}
}
