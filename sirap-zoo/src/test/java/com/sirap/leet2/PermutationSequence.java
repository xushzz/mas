package com.sirap.leet2;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.tool.C;

public class PermutationSequence {
	
	public static void main(String[] args) {
		PermutationSequence james = new PermutationSequence();
		C.pl(james.getPermutation(4, 11));
	}
	
	public String getPermutation(int n, int k) {
		 
		// initialize all numbers
		List<Integer> numberList = new ArrayList<Integer>();
		for (int i = 1; i <= n; i++) {
			numberList.add(i);
		}
 
		// change k to be index
		k--;
 
		// set factorial of n
		int mod = 1;
		for (int i = 1; i <= n; i++) {
			mod = mod * i;
		}
 
		String result = "";
 
		// find sequence
		for (int i = 0; i < n; i++) {
			mod = mod / (n - i);
			// find the right number(curIndex) of
			int curIndex = k / mod;
			// update k
			k = k % mod;
 
			// get number according to curIndex
			result += numberList.get(curIndex);
			// remove from list
			numberList.remove(curIndex);
		}
 
		return result.toString();
	}
}
