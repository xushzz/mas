package com.sirap.leet;

import com.sirap.basic.tool.C;

public class UniqueBinarySearch {
	
	public static void main(String[] args) {
		UniqueBinarySearch jk = new UniqueBinarySearch();
		C.pl(jk.numTrees(3));
	}
	
	public int numTrees(int n) {
		if(n == 1) {
			return 1;
		}
		if(n == 2) {
			return 2;			
		}
		int[] record = new int[n + 1];
		record[0] = 1;
		record[1] = 1;
		record[2] = 2;
		for(int i = 3; i <= n; i++) {
			int temp = 0;
			for(int k = 0; k < i; k++) {
				temp += record[k] * record[i - k - 1];
			}
			record[i] = temp;
		}
		return record[n];
	}
}

