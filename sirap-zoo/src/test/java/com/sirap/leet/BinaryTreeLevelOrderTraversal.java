package com.sirap.leet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.sirap.leet.util.TreeNode;

public class BinaryTreeLevelOrderTraversal {
	
	public List<List<Integer>> levelOrder(TreeNode root) {
	    List<List<Integer>> al = new ArrayList<List<Integer>>();
	    if(root == null)
	        return al;
	 
	    List<Integer> nodeValues = new ArrayList<Integer>();
	    LinkedList<TreeNode> current = new LinkedList<TreeNode>();
	    LinkedList<TreeNode> next = new LinkedList<TreeNode>();
	    current.add(root);
	 
	    while(!current.isEmpty()){
	        TreeNode node = current.remove();
	 
	        if(node.left != null)
	            next.add(node.left);
	        if(node.right != null)
	            next.add(node.right);
	 
	        nodeValues.add(node.val);
	        if(current.isEmpty()){
	            current = next;
	            next = new LinkedList<TreeNode>();
	            al.add(nodeValues);
	            nodeValues = new ArrayList<Integer>();
	        }
	 
	    }
	    return al;
	}
}
