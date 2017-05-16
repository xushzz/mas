package com.sirap.leet;

import com.sirap.basic.tool.C;

public class MaxSubArray {
	
	public static void main(String[] args) {
		MaxSubArray jk = new MaxSubArray();
		int[] arr = {-2,1,-3,4,-1,2,1,-5,4};
		int max = jk.func(arr);
		C.pl(max);
	}
	
	public int func(int[] arr) {
		int sum = arr[0];
		int max = sum;
		
		for(int i = 1; i < arr.length; i++) {
			int current = arr[i];
			if(sum < 0) {
				sum = current;
			} else {
				sum += current;
			}
			
			if(max < sum) {
				max = sum;
			}
		}
		
		return max;
	}
}
