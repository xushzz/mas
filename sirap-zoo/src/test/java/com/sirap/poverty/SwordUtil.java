package com.sirap.poverty;

public class SwordUtil {

	
	public static String toString(int[] arr, char splitter) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < arr.length; i++) {
			sb.append(arr[i]);
			if(i != arr.length - 1) {
				sb.append(splitter);
			}
		}
		
		return sb.toString();
	}
}
