package com.sirap.leet;

import java.util.ArrayList;
import java.util.List;

import com.sirap.leet.util.TreeNode;

public class ValidateBinaryTree {
	public static void main(String[] args) {
		
	}
	
	public boolean isValidBST2(TreeNode root) {
		return judgeBST(root, Integer.MAX_VALUE, Integer.MIN_VALUE);
    }
	
	public boolean judgeBST(TreeNode root, int max, int min){
		if(root == null) return true;
		if(root.val<max && root.val>min && judgeBST(root.left, root.val, min)
				&& judgeBST(root.right, max, root.val)){
			return true;
		}else {
			return false;
		}
	}
	
	List<Integer> result = new ArrayList<Integer>();
	
	public boolean isValidBST(TreeNode root) {
		if(root == null) {
			return true;
		}
		
		inorder(root);
		for(int i = 0; i <= result.size() - 2; i++) {
			if(result.get(i) >= result.get(i+1)) {
				return false;
			}
		}
		
		return true;
	}
	
	public void inorder(TreeNode node) {
		if(node != null) {
			inorder(node.left);
			result.add(node.val);
			inorder(node.right);
		}
	}
}
