package com.sirap.leet;

import java.util.HashMap;
import java.util.Stack;

import com.sirap.basic.tool.C;

public class ValidParentheses {
	public static void main(String[] args) {
		C.pl(isValid2("{(s())}"));
	}
	
	public static boolean isValid2(String s) {
		String[] arr = {"\\(\\)", "\\[\\]", "\\{\\}"};
		int len = s.length();
		while(true) {
			for(int i = 0; i < arr.length; i++) {
				String regex = arr[i];
				s = s.replaceAll(regex, "");
			}
			int len2 = s.length();
			if(len2 == 0) {
				return true;
			}
			
			if(len2 == len) {
				return false;
			}
			len = len2;
		}
	}
//	
	public static boolean isValid(String s) {
		HashMap<Character, Character> map = new HashMap<Character, Character>();
		map.put('(', ')');
		map.put('[', ']');
		map.put('{', '}');
	 
		Stack<Character> stack = new Stack<Character>();
		//{(())}
		for (int i = 0; i < s.length(); i++) {
			char curr = s.charAt(i);
	 
			if (map.keySet().contains(curr)) {
				stack.push(curr);
			} else if (map.values().contains(curr)) {
				if (!stack.empty() && map.get(stack.peek()) == curr) {
					stack.pop();
				} else {
					return false;
				}
			}
		}
	 
		return stack.empty();
	}
}
