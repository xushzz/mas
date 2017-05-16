package com.sirap.leet3;

import com.sirap.basic.tool.C;


public class UniquePaths {
	public static void main(String[] args) {
		UniquePaths james = new UniquePaths();
		C.pl(james.uniquePaths(3, 3));
	}
	
	public int uniquePaths(int m, int n) {
	    return dfs(0,0,m,n);
	}
	 
	public int dfs(int i, int j, int m, int n){
	    if(i==m-1 && j==n-1){
	        return 1;
	    }
	 
	    if(i<m-1 && j<n-1){
	        return dfs(i+1,j,m,n) + dfs(i,j+1,m,n);
	    }
	 
	    if(i<m-1){
	        return dfs(i+1,j,m,n);
	    }
	 
	    if(j<n-1){
	        return dfs(i,j+1,m,n);
	    }
	 
	    return 0;
	}
	
	public int uniquePaths2(int m, int n) {
	    if(m==0 || n==0) return 0;
	    if(m==1 || n==1) return 1;
	 
	    int[][] grid = new int[m][n];
	 
	    //left column
	    for(int i=0; i<m; i++){
	        grid[i][0] = 1;
	    }
	 
	    //top row
	    for(int j=0; j<n; j++){
	        grid[0][j] = 1;
	    }
	 
	    //fill up the dp table
	    for(int i=1; i<m; i++){
	        for(int j=1; j<n; j++){
	            grid[i][j] = grid[i-1][j] + grid[i][j-1];
	        }
	    }
	 
	    return grid[m-1][n-1];
	}
}
