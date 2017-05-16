package com.sirap.leet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.sirap.basic.tool.C;

public class FourSum {

	public static void main(String[] args) {
		int[] arr = {1, 0, -1, 0, -2, 2};
		C.list(fourSum(arr, 0));
	}
	
	public static List<List<Integer>> fourSum(int[] arr, int target) {
		
		HashSet<List<Integer>> set = new HashSet<List<Integer>>();
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		Arrays.sort(arr);
		
		for(int i = 0; i < arr.length; i++) {
			for(int j = i + 1; j < arr.length; j++) {
				int start = j + 1;
				int end = arr.length - 1;
				
				while(start < end) {
					int sum = arr[i] + arr[j] + arr[start] + arr[end];
					if(sum == target) {
						List<Integer> list = new ArrayList<Integer>();
						list.add(arr[i]);
						list.add(arr[j]);
						list.add(arr[start]);
						list.add(arr[end]);
						
						if(!set.contains(list)) {
							set.add(list);
							result.add(list);
						}
						
						start++;
						end--;
					} else if(sum < target) {
						start++;
					} else {
						end--;
					}
				}
			}
		}
		
		return result;
	}
}
