package com.sirap.leet;

import com.sirap.basic.tool.D;

public class ReverseInteger {
	public static void main(String[] args) {
//		D.pl(reverse(1534236469));
		D.pl(reverse(-2147483648));
		D.pl(Integer.MAX_VALUE);
	}
	
	public static int reverse(int x) {
		long rev = 0;
		long p = x;
		while( p != 0) {
			rev = rev*10 + p%10;
			p = p/10;
		}
		
		if(rev > Integer.MAX_VALUE || rev < Integer.MIN_VALUE) {
			rev = 0;
		}
		return (int)rev;
	}
}
