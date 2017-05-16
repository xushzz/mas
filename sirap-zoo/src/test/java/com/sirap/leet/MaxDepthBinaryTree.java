package com.sirap.leet;

import java.util.LinkedList;

import com.sirap.leet.util.TreeNode;

public class MaxDepthBinaryTree {
	public int maxDepth(TreeNode root) {
        if(root==null)
            return 0;
        int leftmax = maxDepth(root.left);
        int rightmax = maxDepth(root.right);
        return Math.max(leftmax, rightmax)+1;
    }
	
	public int maxDepth2(TreeNode root) {
	    if(root == null)
	        return 0;
	    
	    int depth = 0;
	    LinkedList<TreeNode> queue = new LinkedList<TreeNode>();
	    queue.add(root);
	    int curNum = 1; //num of nodes left in current level
	    int nextNum = 0; //num of nodes in next level
	    while(!queue.isEmpty()){
	        TreeNode n = queue.poll();
	        curNum--;
	        if(n.left!=null){
	            queue.add(n.left);
	            nextNum++;
	        }
	        if(n.right!=null){
	            queue.add(n.right);
	            nextNum++;
	        }
	        if(curNum == 0){
	            curNum = nextNum;
	            nextNum = 0;
	            depth++;
	        }
	    }
	    return depth;
	}
}
