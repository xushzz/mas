package com.sirap.algo.july;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;

public class PermutationString {
	public static void main(String[] args) {
		String origin = "ABCD";
		origin = "ABC";
		int start = 0;
		
		PermutationString sirap = new PermutationString();
		sirap.arrange(origin.toCharArray(), start);
	}
	
	private void arrange(char[] arr, int start) {
		int len = arr.length;
		
		if(start == len - 1) {
			C.pl(new String(arr));
			return;
		}
		
		for(int i = start; i < len; i++) {
			C.pl(new String(arr) + ", start = " + start + ", i = " + i);
			swap(arr, start, i);
			D.sink(new String(arr));
			arrange(arr, start + 1);
			C.pl(new String(arr) + ", start = " + start + ", i = " + i);
			swap(arr, start, i);
			D.sink(new String(arr));
		}
	}
	
	private void swap(char[] arr, int m, int n) {
		char temp = arr[m];
		arr[m] = arr[n];
		arr[n] = temp;
	}
}