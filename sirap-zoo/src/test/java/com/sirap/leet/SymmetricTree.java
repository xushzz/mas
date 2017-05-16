package com.sirap.leet;

import com.sirap.leet.util.TreeNode;

public class SymmetricTree {
	
	public boolean isSymmetric(TreeNode node) {
		if(node == null) {
			return true;
		}
		
		return isSymmetric(node.left, node.right);
	}
	
	public boolean isSymmetric(TreeNode n1, TreeNode n2) {
		if(n1 == null && n2 == null) {
			return true;
		}
		
		if(n1 == null || n2 == null) {
			return false;
		}
		
		if(n1.val != n2.val) {
			return false;
		}

		boolean flagA = isSymmetric(n1.left, n2.right);
		boolean flagB = isSymmetric(n2.left, n1.right);
		
		return flagA && flagB;
	}
}
