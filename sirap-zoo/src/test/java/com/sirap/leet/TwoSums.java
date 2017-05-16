package com.sirap.leet;

import java.util.HashMap;

import com.sirap.basic.tool.D;

public class TwoSums {

	public static void main(String[] args) {
		int[] numbers = { 0, 4, 3, 0 };
		int target = 0;

		TwoSums james = new TwoSums();
		D.arr(james.twoSum(numbers, target));
	}

	public int[] twoSum(int[] numbers, int target) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		int[] result = new int[2];
		for (int i = 0; i < numbers.length; i++) {
			if (map.get(target - numbers[i]) != null) {
				result[0] = map.get(target - numbers[i]) + 1;
				result[1] = i + 1;
				break;
			} else {
				map.put(numbers[i], i);
			}
		}
		return result;
	}
}
