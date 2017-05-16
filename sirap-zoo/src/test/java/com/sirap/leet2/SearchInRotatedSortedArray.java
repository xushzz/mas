package com.sirap.leet2;

import com.sirap.basic.tool.C;

public class SearchInRotatedSortedArray {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] arr = {3, 4, 5, 6, 7, 1, 2};
		int key = 1;
		C.pl(solution(arr, key));
	}

	public static int solution(int nums[], int target) {
		int L = 0;
		int R = nums.length - 1;

		while (L <= R) {
			// Avoid overflow, same as M=(L+R)/2
			int M = L + ((R - L) / 2);
			if (nums[M] == target)
				return M;

			// the bottom half is sorted
			if (nums[L] <= nums[M]) {
				if (nums[L] <= target && target < nums[M])
					R = M - 1;
				else
					L = M + 1;
			}
			// the upper half is sorted
			else {
				if (nums[M] < target && target <= nums[R])
					L = M + 1;
				else
					R = M - 1;
			}
		}
		return -1;
	}
}
