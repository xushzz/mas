package com.sirap.leet;

import com.sirap.basic.tool.C;

public class RemoveElement {

	public static void main(String[] args) {
		int p = 121;
		C.pl(p>>1);
	}

	public int solution(int[] arr, int target) {
		int count = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == target) {
				count++;
			} else if (count > 0) {
				arr[i - count] = arr[i];
			}
		}

		return count;
	}
}
