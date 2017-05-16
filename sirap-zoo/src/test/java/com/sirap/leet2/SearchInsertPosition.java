package com.sirap.leet2;

import com.sirap.basic.tool.C;

public class SearchInsertPosition {
	
	public static void main(String[] args) {
		char a = '6';
		C.pl(a - '1');
	}
	
	public int searchInsert(int[] nums, int target) {
        if(nums==null||nums.length==0)
            return 0;
 
        return searchInsert(nums,target,0,nums.length-1);
    }
 
    public int searchInsert(int[] nums, int target, int start, int end){
        int mid=(start+end)/2;
 
        if(target==nums[mid]) 
            return mid;
        else if(target<nums[mid]) 
            return start<mid?searchInsert(nums,target,start,mid-1):start;
        else 
            return end>mid?searchInsert(nums,target,mid+1,end):(end+1);
    }
}
