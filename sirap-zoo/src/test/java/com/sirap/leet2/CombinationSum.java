package com.sirap.leet2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sirap.basic.tool.C;

/*
Given a set of candidate numbers (C) and a target number (T), find all unique combinations in C where the candidate numbers sums to T.

The same repeated number may be chosen from C unlimited number of times.

Note:
All numbers (including target) will be positive integers.
Elements in a combination (a1, a2, … , ak) must be in non-descending order. (ie, a1 ≤ a2 ≤ … ≤ ak).
The solution set must not contain duplicate combinations.
For example, given candidate set 2,3,6,7 and target 7, 
A solution set is: 
[7] 
[2, 2, 3] 
*/		
public class CombinationSum {
	public static void main(String[] args) {
		CombinationSum james = new CombinationSum();
		int[] candidates = {2, 3, 6, 7};
		int target = 7;
		
		C.list(james.combinationSum(candidates, target));
	}
	
	public List<List<Integer>> combinationSum(int[] candidates,
			int target) {
		List<List<Integer>> result = new ArrayList<List<Integer>>();

		if (candidates == null || candidates.length == 0)
			return result;

		ArrayList<Integer> current = new ArrayList<Integer>();
		Arrays.sort(candidates);

		combinationSum(candidates, target, 0, current, result);

		return result;
	}

	public void combinationSum(int[] candidates, int target, int j,
			List<Integer> curr, List<List<Integer>> result) {
		if (target == 0) {
			ArrayList<Integer> temp = new ArrayList<Integer>(curr);
			result.add(temp);
			return;
		}

		for (int i = j; i < candidates.length; i++) {
			if (target < candidates[i])
				return;		
			curr.add(candidates[i]);
			C.pl(curr);
			combinationSum(candidates, target - candidates[i], i, curr, result);
			curr.remove(curr.size() - 1);
		}
	}
}
