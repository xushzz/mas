package com.sirap.leet2;

import com.sirap.basic.tool.D;

public class FirstMissingPositive {
	
	public static void main(String[] args) {
		int[] arr = {};
		D.pl(firstMissingPositiveAnd0(arr));
	}
	
	public static int firstMissingPositiveAnd0(int A[]) {
		int i = 0;
        while (i < A.length) {
            if (A[i] != i + 1 && A[i] >= 1 && A[i] <= A.length && A[A[i] - 1] != A[i]) {
                int tmp = A[A[i] - 1];
                A[A[i] - 1] = A[i];
                A[i] = tmp;
            } else
                i++;
        }
        for (i = 0; i < A.length; i++) {
            if (A[i] != i + 1)
                return i + 1;
        }
        return A.length + 1;
	}
}
