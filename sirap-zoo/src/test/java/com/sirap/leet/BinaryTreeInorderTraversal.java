package com.sirap.leet;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.sirap.leet.util.TreeNode;

/**
 * The key to solve inorder traversal of binary tree includes the following:
 * 
 * The order of "inorder" is: left child -> parent -> right child Use a stack to
 * track nodes Understand when to push node into the stack and when to pop node
 * out of the stack
 */

class Solution {
    List<Integer> result = new ArrayList<Integer>();
 
    public List<Integer> inorderTraversal(TreeNode root) {
        if(root !=null){
            helper(root);
        }
 
        return result;
    }
 
    public void helper(TreeNode p){
        if(p.left!=null)
            helper(p.left);
 
        result.add(p.val);
 
        if(p.right!=null)
            helper(p.right);
    }
}

public class BinaryTreeInorderTraversal {
	public ArrayList<Integer> inorderTraversal(TreeNode root) {
		// IMPORTANT: Please reset any member data you declared, as
		// the same Solution instance will be reused for each test case.
		ArrayList<Integer> lst = new ArrayList<Integer>();

		if (root == null)
			return lst;

		Stack<TreeNode> stack = new Stack<TreeNode>();
		// define a pointer to track nodes
		TreeNode p = root;

		while (!stack.empty() || p != null) {

			// if it is not null, push to stack
			// and go down the tree to left
			if (p != null) {
				stack.push(p);
				p = p.left;

				// if no left child
				// pop stack, process the node
				// then let p point to the right
			} else {
				TreeNode t = stack.pop();
				lst.add(t.val);
				p = t.right;
			}
		}

		return lst;
	}
}
