package com.sirap.leet2;



public class RotateImage {
	
	public static void main(String[] args) {
		RotateImage james = new RotateImage();
		int len = 4;
		int[][] num = new int[len][len];
		
		for(int i = 0; i <len; i++) {
			for(int k = 0; k <len; k++) {
				num[i][k] = i + 100 * k;
			}
		}
		
		james.rotate(num);
	}
	
	public void rotate(int[][] matrix) {
		int n = matrix.length;
		for (int i = 0; i < n / 2; i++) {
			for (int j = 0; j < Math.ceil(((double) n) / 2.); j++) {
				int temp = matrix[i][j];
				matrix[i][j] = matrix[n-1-j][i];
				matrix[n-1-j][i] = matrix[n-1-i][n-1-j];
				matrix[n-1-i][n-1-j] = matrix[j][n-1-i];
				matrix[j][n-1-i] = temp;
			}
		}
	}
}
