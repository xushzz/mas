package com.sirap.leet3;

public class WordSearch {
	public static void main(String[] args) {
		
	}
	
	public boolean solution(char[][] board, String word) {
		int m = board.length;
		int n = board[0].length;
		
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {
				boolean isExist = dfs(board, word, i, j, 0);
				if(isExist) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean dfs(char[][] board, String word, int i, int j, int k) {
		int m = board.length;
		int n = board[0].length;
		
		if(i < 0 || i > m || j < 0 || j > n) {
			return false;
		}
		
		if(board[i][j] == word.charAt(k)) {
			char temp = board[i][j];
			board[i][j] = '?';
			
			if(k == word.length() - 1) {
				return true;
			} else if(dfs(board, word, i-1, j, k + 1) ||dfs(board, word, i, j - 1, k + 1) ||dfs(board, word, i+1, j, k + 1) ||dfs(board, word, i, j + 1, k + 1)) {
				return true;
			}
			
			board[i][j] = temp;
		}
		
		return false;
	}
}
