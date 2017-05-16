package com.sirap.leet2;

public class JumpGame {
	public static void main(String args[]) {
		int[] A = {3,2,1,0,4};
		JumpGame jp = new JumpGame();
		if (jp.canJump(A))
			System.out.println("We win");
		else
			System.out.println("we lose");
	}

	//[3,2,1,0,4]
	public boolean canJump(int[] A) {
		int max = 0;
		int step = 1;
		if (A.length <= 1)
			return true;
		if (A[0] == 0 && A.length > 1)
			return false;
		for (int i = 0; i < A.length; i++) {

			step--;
			if (i + A[i] > max) {
				max = i + A[i];
				step = A[i];
			}
			if (step == 0 && i < A.length - 1)
				return false;

		}
		return true;
	}
}