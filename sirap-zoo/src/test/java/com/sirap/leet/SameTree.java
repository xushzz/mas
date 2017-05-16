package com.sirap.leet;

import com.sirap.leet.util.TreeNode;

public class SameTree {
	public boolean isSame(TreeNode n1, TreeNode n2) {
		if(n1 == null && n2 == null) {
			return true;
		}
		
		if(n1 != null && n2 != null) {
			if(n1.val == n2.val) {
				return isSame(n1.left, n2.left) && isSame(n2.right, n2.right);
			}
		} else {
			return false;
		}
		
		return false;
	}
}
