package com.sirap.leet3;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.tool.C;

public class Combinations {
	public static void main(String[] args) {
		Combinations jk = new Combinations();
		C.list(jk.solution(4,  2));
	}
	
	public List<List<Integer>> solution(int n, int k) {
		List<Integer> positions = new ArrayList<Integer>();
		List<List<Integer>> results = new ArrayList<List<Integer>>();
		getResult(n, k, 1, positions, results);
		return results;
	}
	
	public void getResult(int n, int k, int start, List<Integer> positions, List<List<Integer>> results) {
		if(positions.size() == k) {
			List<Integer> result = new ArrayList<Integer>(positions);
			results.add(result);
			return;
		}
		
		for(int i = start; i <= n; i++) {
			Integer idx = i;
			if(positions.indexOf(idx) >= 0) {
				continue;
			}
			
			positions.add(idx);
			getResult(n, k, i + 1, positions, results);
			positions.remove(idx);
		}
	}
}
