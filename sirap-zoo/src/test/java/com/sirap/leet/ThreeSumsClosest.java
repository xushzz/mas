package com.sirap.leet;

import java.util.Arrays;

import com.sirap.basic.tool.C;

public class ThreeSumsClosest {
	public static void main(String[] args) {
		int[] nums = {0, 2, 1, -3};
		int target = 1;
		C.pl(sum(nums, target));
	}
	
	public static int sum(int[] nums, int target) {
		int min = Integer.MAX_VALUE;
		int result = 0;
		
		Arrays.sort(nums);
		
		for(int i = 0; i < nums.length; i++) {
			int start = i + 1;
			int end = nums.length - 1;
			while(start < end) {
				int sum = nums[i] + nums[start] + nums[end];
				int diff = Math.abs(sum - target);
				if(diff == 0) {
					return sum;
				}
				
				if(diff < min) {
					min = diff;
					result = sum;
				}
				
				if(sum > target) {
					end--;
				} else {
					start++;
				}
			}
		}
		
		return result;
	}
}
