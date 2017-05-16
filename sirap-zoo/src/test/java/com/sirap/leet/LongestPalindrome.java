package com.sirap.leet;

import com.sirap.basic.tool.C;

public class LongestPalindrome {
	public static void main(String[] args) {
		String str = "abb";
		C.pl(longestPalindrome(str));
	}
	
	public static String longestPalindrome(String str) {
		if(str == null) {
			return null;
		}
		
		if(str.length() <= 2) {
			return str;
		}
		
		String longest = str.substring(0, 1);
		for(int i = 0; i < str.length(); i++) {
			String str1 = helper(str, i, i);
			if(str1.length() > longest.length()) {
				longest = str1;
			}
			
			String str2 = helper(str, i, i + 1);
			if(str2.length() > longest.length()) {
				longest = str2;
			}
		}
		
		return longest;
	}
	
	public static String helper(String str, int begin, int end) {
		while(begin >=0 && end <= str.length() - 1 &&  str.charAt(begin) == str.charAt(end)) {
			begin--;
			end++;
		}
		
		return str.substring(begin + 1, end);
	}
}
