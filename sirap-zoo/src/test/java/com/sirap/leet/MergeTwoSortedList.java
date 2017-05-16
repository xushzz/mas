package com.sirap.leet;


public class MergeTwoSortedList {
	public static void main(String[] args) {
		ListNode a = new ListNode(1);
		a.next = new ListNode(3);
		a.next.next = new ListNode(9);
		a.next.next.next = new ListNode(14);
		
		ListNode b = new ListNode(2);
		b.next = new ListNode(8);
		b.next.next = new ListNode(9);
		b.next.next.next = new ListNode(41);
		
		merge(a, b).print();
	}
	
	public static ListNode merge(ListNode l1, ListNode l2) {
		ListNode fakeHead = new ListNode(0);
		ListNode p = fakeHead;
		
		ListNode p1 = l1;
		ListNode p2 = l2;
		
		while(p1 != null && p2 != null) {
			if(p1.val < p2.val) {
				p.next = p1;
				p1 = p1.next;
			} else {
				p.next = p2;
				p2 = p2.next;
			}
			
			p = p.next;
		}
		
		if(p1 != null) {
			p.next = p1;
		} 
		
		if(p2 != null) {
			p.next = p2;
		} 

		return fakeHead.next;
	}
}
