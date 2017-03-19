package com.sirap.basic.math;

import java.util.ArrayList;
import java.util.List;

public class ArrangementCalculatorWithList {
	private List<String[]> source;
	private List<String> result;
	
	public List<String> getResult() {
		return result;
	}

	public ArrangementCalculatorWithList(List<String[]> source) {
		this.source = source;
		result = new ArrayList<String>();
		walk(0, "");
	}
	
	public void walk(int depth, String formedStr) {
		if(depth >= source.size()) {
			result.add(formedStr);
			return;
		}
		
		String[] arr = source.get(depth);
		for(int i = 0; i < arr.length; i++) {
			walk(depth + 1, formedStr + arr[i]);
		}
	}
}
