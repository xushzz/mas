package com.sirap.leet2;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.tool.C;

public class Permutation2 {
	
	public static void main(String[] args) {
		Permutation2 james = new Permutation2();
		int[] num = {1,2,3, 4};
		C.list(james.permute(num));
	}
	
	public List<List<Integer>> permute(int[] num) {
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		List<Integer> positions = new ArrayList<Integer>();
		permute(num, positions, result);
		return result;
	}
	 
	void permute(int[] num, List<Integer> positions, List<List<Integer>> result) {
	 
		if (positions.size() == num.length) {
			List<Integer> list = new ArrayList<Integer>();
			for(Integer idx: positions) {
				list.add(num[idx]);
			}
			result.add(list);
		}
		
		for(int i = 0; i < num.length; i++) {
			Integer idx = i;
			if(positions.indexOf(idx) >= 0) {
				continue;
			}
			
			positions.add(idx);
			permute(num, positions, result);
			positions.remove(idx);
		}
	}
}
