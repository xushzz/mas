package com.sirap.leet2;

import java.util.Stack;

import com.sirap.basic.tool.C;

public class LongestValidParentheses {

	public static void main(String[] args) {
		C.pl(solution("))()())"));
	}
	
	public static int solution(String s) {
		if(s == null || s.isEmpty()) {
			return 0;
		}
		
		Stack<Integer> stk = new Stack<Integer>();
		int max = 0;
		int last = -1;
		
		char[] chars = s.toCharArray();
		int len = chars.length;
		
		for(int i = 0; i < len; i++) {
			if(chars[i] == '(') {
				stk.push(i);
			} else {
				if(stk.isEmpty()) {
					last = i;
					continue;
				}
				
				stk.pop();
				if(stk.isEmpty()) {
					int temp = i - last;
					max = Math.max(max, temp);
				} else {
					int temp = i - stk.peek();
					max = Math.max(max, temp);
				}
			}
		}
		
		return max;
	}

}
