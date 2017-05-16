package com.sirap.leet;

import com.sirap.basic.tool.C;

public class ContainerWithMostWater {
	public static void main(String[] args) {
		int[] h = {5, 3, 4, 6, 1};
		C.pl(area(h));
	}
	
	public static int area(int[] h) {
		int max = 0;
		int left = 0;
		int right = h.length - 1;
		while(left < right) {
			int height = Math.min(h[left], h[right]);
			int length = right - left;
			int area = height * length;
			
			max = Math.max(area, max);
			if(h[left] < h[right]) {
				left++;
			} else {
				right--;
			}
		}
		
		return max;
	}
}
