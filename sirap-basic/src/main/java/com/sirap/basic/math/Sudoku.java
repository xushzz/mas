package com.sirap.basic.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.StrUtil;

public class Sudoku {
	
	class Sheet {
		private List<String> matrix;
		
		public Sheet(List<String> content) {
			this.matrix = new ArrayList<String>(content);
		}
		
		@Override
		public String toString() {
			return matrix.toString();
		}
		
		public String[] toArray() {
			String[] arr = new String[matrix.size()];
			matrix.toArray(arr);
			return arr;
		}
		
		public boolean isCompleted() {
			return matrix.toString().indexOf(CHAR_ZERO) < 0; 
		}
		
		public int rowInProcess() {
			for(int i = 0; i < SUDOKU_SIZE; i ++) {
				if(matrix.get(i).indexOf(CHAR_ZERO) >= 0) {
					return i;
				}
			}
			
			return -1;
		}
		
		public boolean isCharEligible(char ch, int row) {
			String currentRow = matrix.get(row);
			
			//row-wise currently eligible if no duplication in ROW
			if(currentRow.indexOf(ch) >= 0) {
				return false;
			}
			
			int col = currentRow.indexOf(CHAR_ZERO);

			//column-wise currently eligible if no duplication in COLUMN
			for(int i = 0; i < SUDOKU_SIZE; i++) {
				
				if(row == i) continue;
				
				char cell = matrix.get(i).charAt(col);
				
				if(ch == cell) return false;
			}
			
			//region-wise currently eligible if no duplication in REGION
			for(int i = 0; i < SUDOKU_SIZE; i ++) {
				int[] v = REGION_COORDINATIONS[i];
				String content = selectRegion(v[0], v[1], v[2], v[3]);
				if(!isPotentiallyEligible(content)) {
					return false;
				}
			}
			
			return true;		
		}
		
		private String selectRegion(int rowStart, int rowEnd, int colStart, int colEnd) {
			StringBuffer sb = new StringBuffer();
			for(int i = rowStart; i < rowEnd; i++) {
				sb.append(matrix.get(i).substring(colStart, colEnd));
			}
			
			return sb.toString();
		}
		
		private boolean isPotentiallyEligible(String content) {
			for(int i = 0; i < SUDOKU_SIZE; i++) {
				char ch = content.charAt(i);
				if(ch == CHAR_ZERO) continue;
				
				for(int j = 0; j < SUDOKU_SIZE; j++) {
					if(i == j) continue;
					
					char ch2 = content.charAt(j);
					if(ch == ch2) return false;
				}
			}
			
			return true;
		}
		
		public Sheet create(int row, char ch) {
			Sheet buf = new Sheet(matrix);
			String currentRow = buf.matrix.get(row);
			buf.matrix.set(row, currentRow.replaceFirst(CHAR_ZERO + "", ch + ""));
			return buf;
		}
	}
	
	public static final char CHAR_ZERO = '0';
	public static final String SOURCE = "123456789";
	public static final int SUDOKU_SIZE = 9;
	public static final int[][] REGION_COORDINATIONS = {{0,3,0,3}, {0,3,3,6}, {0,3,6,9}, {3,6,0,3}, {3,6,3,6}, {3,6,6,9}, {6,9,0,3}, {6,9,3,6}, {6,9,6,9}};

	public static final String SAMPLE_INPUT = "409070562,230090874,570420930,090002340,020000057,005000029,900000200,100200480,802004706";
	
	private String[] matrix;
	private String[] optionMatrix = new String[SUDOKU_SIZE];
	
	private List<String[]> answers = new ArrayList<String[]>();
	
	public Sudoku() {}

	public Sudoku(String input) {
		matrix = input.replace(" ", "").split(",");
		solve();
	}
	
	private void solve() {
		boolean isPossibleSudoku = analyze();
		if(!isPossibleSudoku) {
			return;
		}
		
		List<String> pickedList = Arrays.asList(matrix);
		walk(new Sheet(pickedList));
	}
	
	public List<String[]> getAnswers() {
		return answers;
	}
	
	public static List<String[]> evaluate(String expression) {
		Sudoku nick = new Sudoku(expression);
		List<String[]> result = nick.getAnswers();
		
		return result;
	}
	
	private void walk(Sheet buf) {
		if(buf.isCompleted()) {
			answers.add(buf.toArray());
		}

		int row = buf.rowInProcess();
		
		if(row == -1) return;
		
		String option = optionMatrix[row];
		
		for(int i = 0; i < option.length(); i++) {
			char ch = option.charAt(i);
			
			if(!buf.isCharEligible(ch, row)) {
				continue;
			}
			
			walk(buf.create(row, ch));
		}
	}
	
	private boolean analyze() {
		int len = matrix.length;
		
		if(len != SUDOKU_SIZE) {
			return false;
		}
		
		for(int i = 0; i < matrix.length; i++) {
			String record = matrix[i];
			if(record.length() != SUDOKU_SIZE) {
				return false;
			}
			
			if(!StrUtil.isDigitsOnly(record)) {
				return false;
			}
			
			String options = "";
			for(int k = 0; k < record.length(); k++) {
				options = SOURCE.replace(record.charAt(i) + "", ""); 
			}
			
			optionMatrix[i] = options;
		}
		
		return true;
	}
	
	public static void print(List<String[]> solutions) {
		boolean isMultiple = solutions.size() > 1;
		for(int i = 0; i < solutions.size(); i++) {
			if(isMultiple) {
				C.pl("Solution " + (i+1) + ">");
			}
			String[] matrix = solutions.get(i);
			
			for(int m = 0; m < matrix.length; m++) {
				C.pl(matrix[m].replace("", " "));
			}
		}
	}
}
