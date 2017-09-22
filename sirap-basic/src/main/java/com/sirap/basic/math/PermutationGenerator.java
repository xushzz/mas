package com.sirap.basic.math;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.tool.C;

public class PermutationGenerator {
	
	public static void main(String[] args) {
		PermutationGenerator game = new PermutationGenerator("ABCD", 4);
		C.list(game.getResult());
	}
	
	private String source;
	private int targetSize;
	private List<String> result = new ArrayList<String>();
	
	public List<String> getResult() {
		return result;
	}

	public PermutationGenerator(String source, int targetSize) {
		this.source = source;
		this.targetSize = targetSize;
		
		walk(new ArrayList<Integer>());
	}

	private void walk(List<Integer> positions) {
		if(targetSize <= 0) {
			return;
		}
		
		if(positions.size() >= targetSize) {
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < positions.size(); i++) {
				int idx = positions.get(i);
				sb.append(source.charAt(idx));
			}
			result.add(sb.toString());
			return;
		}
		
		for(int i = 0; i < source.length(); i++) {
			int index = positions.indexOf(i);
			if(index >= 0) {
				continue;
			}

			Integer idx = i;
			positions.add(idx);
			walk(positions);
			positions.remove(idx.intValue() - 1);
		}
	}
}
