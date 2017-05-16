package com.sirap.leet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sirap.basic.tool.C;

public class LetterCombinations {
	public static void main(String[] args) {
		C.list(solution(""));
	}
	
	private static HashMap<Integer, String> map = new HashMap<Integer, String>();
	static {
		map.put(0, "");
		map.put(1, "");
		map.put(2, "abc");
		map.put(3, "def");
		map.put(4, "ghi");
		map.put(5, "jkl");
		map.put(6, "mno");
		map.put(7, "pqrs");
		map.put(8, "tuv");
		map.put(9, "wxyz");

	}
	
	public static List<String> solution(String digits) {
		List<String> result = new ArrayList<String>();
		
		if(digits == null || digits.isEmpty()) {
			return result;
		}
		
		List<Character> temp = new ArrayList<Character>();
		getString(digits, temp, result);
		
		return result;
	}
	
	public static void getString(String digits, List<Character> temp, List<String> result) {
		if(digits.length() == 0) {
			char[] chars = new char[temp.size()];
			for(int i = 0; i < temp.size(); i++) {
				chars[i] =  temp.get(i);
			}
			result.add(new String(chars));
			return;
		}
		
		Integer digit = Integer.valueOf(digits.substring(0, 1));
		String letters = map.get(digit);
		if(letters == null || letters.isEmpty()) {
			getString(digits.substring(1), temp, result);
		} else {
			for(int i = 0; i < letters.length(); i++) {
				char c = letters.charAt(i);
				temp.add(c);
				getString(digits.substring(1), temp, result);
				temp.remove(temp.size() - 1);
			}
		}
	}
}
