package com.sirap.leet;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.tool.C;

public class GenerateParenthesis {
	public static void main(String[] args) {
		GenerateParenthesis james = new GenerateParenthesis();
		C.list(james.generate(4));
	}
	
	public List<String> generate(int n) {
		List<String> result = new ArrayList<String>();
		deep("", n, n, result);
		
		return result;
	}
	
	public void deep(String str, int left, int right, List<String> result) {
		if(left > right) {
			return;
		}
		
		if(left == 0 && right == 0) {
			result.add(str);
		}

		if(left > 0) {
			deep(str + "(", left - 1, right, result);
		}
		if(right > 0) {
			deep(str + ")", left, right - 1, result);
		}
	}
}
