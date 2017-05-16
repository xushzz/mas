package com.sirap.leet;

import com.sirap.basic.tool.C;

//https://leetcode.com/problems/palindrome-number/
public class PalindromeNumber {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		C.pl(solution(34543));
		C.pl(solution(3443));
		C.pl(solution(35));
	}
	
	public static boolean solution(int x) {
		if(x < 0) {
			return false;
		}
		
		int div = 1;
		while(x / div > 10) {
			div *= 10;
		}
		
		while(x != 0) {
			int right = x % 10;
			int left = x / div;
			
			if(right != left) {
				return false;
			}
			
			x = x % div / 10;
			div /= 100;
		}
		
		return true;
	}

}
