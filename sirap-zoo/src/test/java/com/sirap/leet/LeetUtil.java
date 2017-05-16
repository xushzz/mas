package com.sirap.leet;
//package com.pirate.leet;
//
//import java.util.LinkedList;
//
//import com.pirate.basic.tool.C;
//import com.pirate.leet.util.TreeNode;
//
//public class LeetUtil {
//	
//	public static void main(String[] args) {
//		Integer[] arr = {1,2,3,null,null,4,null,null,5};
////		arr = new Integer[]{1,2,3,4,5,6,7,8,9};
////		arr = new Integer[]{10};
//		TreeNode root = generateTree(arr);
////		root = new TreeNode(1);
////		root.left = new TreeNode(2);
////		root.right = new TreeNode(4);
////		root.left.left = new TreeNode(3);
//		printTree(root);
//	}
//	//{1,2,3,#,#,4,#,#,5}
//	public static TreeNode generateTree(Integer[] values) {
//		if(values == null || values.length == 0) {
//			return null;
//		}
//		TreeNode root = new TreeNode(values[0]);
//		LinkedList<TreeNode> currentNodes = new LinkedList<TreeNode>();
//		currentNodes.add(root);
//		
//		LinkedList<TreeNode> nextNodes = new LinkedList<TreeNode>();
//		int idx = 1;
//		while(!currentNodes.isEmpty()) {
//			TreeNode current = currentNodes.remove();
//			if(idx < values.length) {
//				Integer left = values[idx++];
//				if(left != null) {
//					TreeNode node = new TreeNode(left);
//					current.left = node;
//					nextNodes.add(node);
//				}
//			} else {
//				break;
//			}
//			
//			if(idx < values.length) {
//				Integer right = values[idx++];
//				if(right != null) {
//					TreeNode node = new TreeNode(right);
//					current.right = node;
//					nextNodes.add(node);
//				}
//			} else {
//				break;
//			}
//			
//			if(currentNodes.isEmpty()) {
//				currentNodes = nextNodes;
//				nextNodes = new LinkedList<TreeNode>();
//			}
//		}
//		
//		return root;
//	}
//	
//	public static void printTree(TreeNode root) {
//		print(root, "");
//	}
//	
//	private static void print(TreeNode node, String prefix) {
//		if(node == null) {
//			return;
//		}
//		
//		C.pl(prefix + node);
//		print(node.left, prefix + "  ");
//		print(node.right, prefix + "  ");
//	}
//}
