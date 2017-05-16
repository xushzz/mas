package com.sirap.leet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class MergeKSortedLists {
	public static void main(String[] args) {
		ListNode a = new ListNode(1);
		a.next = new ListNode(3);
		a.next.next = new ListNode(9);
		a.next.next.next = new ListNode(14);
		
		ListNode b = new ListNode(2);
		b.next = new ListNode(8);
		b.next.next = new ListNode(9);
		b.next.next.next = new ListNode(41);
		
		ListNode c = new ListNode(7);
		c.next = new ListNode(18);
		c.next.next = new ListNode(19);
		c.next.next.next = new ListNode(30);
		
		merge(a, b, c).print();
	}
	
	public static ListNode merge(ListNode... lists) {
		PriorityQueue<ListNode> q = new PriorityQueue<ListNode>(lists.length, new Comparator<ListNode>(){

			@Override
			public int compare(ListNode o1, ListNode o2) {
				if(o1.val < o2.val)
					return -1;
				else if(o1.val == o2.val)
					return 0;
				else 
					return 1;
				}
			});
		
		for(ListNode node: lists) {
			q.add(node);
		}
		
		ListNode head = new ListNode(0);
		ListNode p = head;
		while(!q.isEmpty()) {
			ListNode node = q.poll();
			p.next = node;
			
			if(node.next != null) {
				q.add(node.next);
			}
			
			p = p.next;
		}
		
		return head.next;
	}
}
