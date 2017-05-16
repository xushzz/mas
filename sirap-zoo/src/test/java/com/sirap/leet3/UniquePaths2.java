package com.sirap.leet3;

import com.sirap.basic.tool.C;


public class UniquePaths2 {
	public static void main(String[] args) {
		
		UniquePaths2 james = new UniquePaths2();
		int[][] grid = null;
		C.pl(james.uniquePaths(grid));
	}
	
	public int uniquePaths(int[][] grid) {
        if(grid.length==0||grid[0].length==0) return 0;
        if(grid[grid.length-1][grid[0].length-1]==1) return 0; 
        
        for(int i=0; i<grid.length; i++){
            for(int j=0; j<grid[0].length;j++){
                if(grid[i][j]==1) grid[i][j] = -1;
            }
        }
        
        for(int i=0; i<grid[0].length; i++){
            if(grid[0][i]!=-1) grid[0][i] = 1;
            else break;
        }
        
        for(int i=0; i<grid.length; i++) {
            if(grid[i][0] !=-1) grid[i][0] = 1;
            else break;
        }
        
        for(int i=1; i<grid.length; i++){
            for(int j=1; j<grid[0].length;j++){
                if(grid[i][j]!=-1){
                    if(grid[i-1][j]!=-1) grid[i][j] += grid[i-1][j];
                    if(grid[i][j-1]!=-1) grid[i][j] += grid[i][j-1];
                }
                // dp[i][j] = dp[i-1][j] + dp[i][j-1];
            }
        }
        
        return grid[grid.length-1][grid[0].length-1];
    }
}
