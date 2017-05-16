package com.sirap.leet;
//package com.pirate.leet;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import com.pirate.basic.tool.C;
//import com.pirate.leet.util.TreeNode;
//
///**
// * Definition for binary tree
// * public class TreeNode {
// *     int val;
// *     TreeNode left;
// *     TreeNode right;
// *     TreeNode(int x) { val = x; left = null; right = null; }
// * }
// */
//public class UniqueBinarySearch2 {
//	
//	public static void main(String[] args) {
//		UniqueBinarySearch2 jk = new UniqueBinarySearch2();
//		C.list(jk.generateTrees(4));
//	}
//	
//	public List<TreeNode> generateTrees(int n) {
//	    return generateTrees(1, n);
//	}
//	 
//	public List<TreeNode> generateTrees(int start, int end) {
//	    List<TreeNode> list = new LinkedList<>();
//	 
//	    if (start > end) {
//	        list.add(null);
//	        return list;
//	    }
//	 
//	    for (int i = start; i <= end; i++) {
//	        List<TreeNode> lefts = generateTrees(start, i - 1);
//	        List<TreeNode> rights = generateTrees(i + 1, end);
//	        for (TreeNode left : lefts) {
//	            for (TreeNode right : rights) {
//	                TreeNode node = new TreeNode(i);
//	                node.left = left;
//	                node.right = right;
//	                list.add(node);
//	            }
//	        }
//	    }
//	 
//	    return list;
//	}
//}