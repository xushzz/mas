package com.sirap.leet2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/*
 Given a collection of candidate numbers (C) and a target number (T), find all unique combinations in C where the candidate numbers sums to T. Each number in C may only be used ONCE in the combination.

 Note:
 1) All numbers (including target) will be positive integers.
 2) Elements in a combination (a1, a2, … , ak) must be in non-descending order. (ie, a1 ≤ a2 ≤ … ≤ ak).
 3) The solution set must not contain duplicate combinations.
 */
public class CombinationSumII {
	public List<ArrayList<Integer>> combinationSum2(int[] num, int target) {
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		if (num == null || num.length == 0)
			return result;

		Arrays.sort(num);

		ArrayList<Integer> temp = new ArrayList<Integer>();
		getCombination(num, 0, target, temp, result);

		HashSet<ArrayList<Integer>> set = new HashSet<ArrayList<Integer>>(
				result);

		// remove duplicate lists
		result.clear();
		result.addAll(set);

		return result;
	}

	public void getCombination(int[] num, int start, int target,
			ArrayList<Integer> temp, ArrayList<ArrayList<Integer>> result) {
		if (target == 0) {
			ArrayList<Integer> t = new ArrayList<Integer>(temp);
			result.add(t);
			return;
		}

		for (int i = start; i < num.length; i++) {
			if (target < num[i])
				continue;

			temp.add(num[i]);
			getCombination(num, i + 1, target - num[i], temp, result);
			temp.remove(temp.size() - 1);
		}
	}
}
