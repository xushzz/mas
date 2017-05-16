package com.sirap.leet;

import com.sirap.basic.tool.D;

public class RemoveDuplicates {
	public static void main(String[] args) {
		int[] arr = {1, 2, 2, 2, 3, 3, 3};
		D.sink(solution(arr));
	}
	
	public static int solution(int[] arr) {
		int j = 0;
		int i = 1;
		while(i < arr.length) {
			if(arr[i] == arr[j]) {
				i++;
			} else {
				j++;
				arr[j] = arr[i];
				i++;
			}
		}
		return j;
	}
}
