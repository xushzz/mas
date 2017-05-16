package com.sirap.leet;

import com.sirap.basic.tool.C;

public class LongestCommonPrefix {
	public static void main(String[] args) {
		String[] arr = {"abc", "av", "abcd", "ak77"};
		C.pl(solution(arr));
	}
	
	public static String solution(String[] arr) {
		if(arr == null || arr.length == 0) {
			return "";
		}
		
		String prefix = arr[0];
		for(int i = 1; i < arr.length; i++) {
			String current = arr[i];
			
			int len = prefix.length();
			if(prefix.length() > current.length()) {
				len = current.length();
			}
			
			int k = 0;
			for(k = 0; k < len; k++) {
				if(prefix.charAt(k) != current.charAt(k)) {
					break;
				}
			}
			
			prefix = prefix.substring(0, k); 
		}
		
		return prefix;
	}
}
