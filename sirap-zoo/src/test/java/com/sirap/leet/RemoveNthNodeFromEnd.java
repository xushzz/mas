package com.sirap.leet;

import com.sirap.basic.tool.C;


public class RemoveNthNodeFromEnd {
	
	public static void main(String[] args) {
		ListNode a = new ListNode(1);
//		a.next = new ListNode(3);
//		a.next.next = new ListNode(9);
//		a.next.next.next = new ListNode(4);
//		a.next.next.next.next = new ListNode(6);
//		a.next.next.next.next.next = new ListNode(7);
		
		C.pl(remove(a, 1));
	}
	
	public static ListNode remove(ListNode head, int n) {
		ListNode fast = head;
		ListNode slow = head;
		
		for(int i = 0; i < n; i++) {
			fast = fast.next;
		}
		
		if(fast == null) {
			head = head.next;
			return head;
		}
		
		while(fast.next != null) {
			fast = fast.next;
			slow = slow.next;
		}
		
		slow.next = slow.next.next;
		
		return head;
	}
}
