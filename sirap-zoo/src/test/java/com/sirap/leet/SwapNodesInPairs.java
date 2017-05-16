package com.sirap.leet;

public class SwapNodesInPairs {
	
	public static void main(String[] args) {
		ListNode a = new ListNode(1);
		a.next = new ListNode(3);
		a.next.next = new ListNode(9);
		a.next.next.next = new ListNode(14);
		ListNode b = solution(a);
		b.print();
	}
	
	public static ListNode solution(ListNode head) {
		ListNode dm = new ListNode(-1);
		ListNode cur = dm;
		ListNode c = head;
		while(c != null && c.next != null) {
			ListNode n1 = c;
			c = c.next;
			n1.next = null;
			
			ListNode n2 = c;
			c = c.next;
			n2.next = null;
			
			cur.next = n2;
			cur = cur.next;
			cur.next = n1;
			cur = cur.next;
		}
		
		if(c !=null) {
			cur.next = c;
		}
		
		return dm.next;
	}
}
