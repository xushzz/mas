package com.sirap.leet3;

import com.sirap.basic.tool.D;

public class SortColors {
	public static void main(String[] args) {
		SortColors james = new SortColors();
		int[] arr = { 2, 1, 0, 1, 2, 0, 2, 1, 1, 0 };
		james.solution2(arr);
		D.arr(arr);
	}
	
	public void solution2(int[] arr) {
		int left = -1;
		int right = arr.length;
		for(int i = 0; i < right; i++) {
			int cur = arr[i];
			if(cur == 0) {
				swap(arr, ++left, i);
			} else if(cur == 2) {
				swap(arr, --right, i);
				i--;
			}
		}
	}
	
	public void swap(int[] arr, int x, int y) {
		int temp = arr[x];
		arr[x] = arr[y];
		arr[y] = temp;
	}

	public void solution(int[] arr) {
		int count0 = 0;
		int count1 = 0;
		int count2 = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == 0) {
				count0++;
			}
			if (arr[i] == 1) {
				count1++;
			}
			if (arr[i] == 2) {
				count2++;
			}
		}
		for (int i = 0; i < count0; i++) {
			arr[i] = 0;
		}
		for (int i = count0; i < count0 + count1; i++) {
			arr[i] = 1;
		}
		for (int i = count0 + count1; i < count0 + count1 + count2; i++) {
			arr[i] = 2;
		}
	}
}
