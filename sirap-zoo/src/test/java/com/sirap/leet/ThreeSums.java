package com.sirap.leet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sirap.basic.tool.C;

public class ThreeSums {

	public static void main(String[] args) {
		int[] arr = {-1, 0, 1, 2, -1, -4};
		C.pl(solution(arr));
	}

	public static List<List<Integer>> solution(int[] source) {
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		if(source == null || source.length < 3) {
			return result;
		}
		
		Arrays.sort(source);
		
		for(int i = 0; i < source.length - 2; i++) {
			if(i == 0 || source[i - 1] < source[i]) {
				int negate = -source[i];
				int start = i + 1;
				int end = source.length - 1;
				
				while(start < end) {
					int sum = source[start] + source[end];
					if(negate < sum) {
						end--;
					} else if(negate > sum) {
						start++;
					} else {
						List<Integer> list = new ArrayList<Integer>();
						list.add(source[i]);
						list.add(source[start]);
						list.add(source[end]);
						
						result.add(list);
						start++;
						end--;
						
						while(start < end && source[end] == source[end + 1]) {
							end--;
						}
						
						while(start < end && source[start] == source[start - 1]) {
							start++;
						}
					}
				}
			}
		}
		
		return result;
	}
}
