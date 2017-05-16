package com.sirap.leet;



public class AddTwoNumbers {
	
	public static void main(String[] args) {
		AddTwoNumbers james = new AddTwoNumbers();
		ListNode a = new ListNode(0);
//		a.next = new ListNode(4);
//		a.next.next = new ListNode(9);

		ListNode b = new ListNode(0);
//		b.next = new ListNode(6);
//		b.next.next = new ListNode(4);
		
		ListNode node = james.addTwoNumbers(a, b);
		node.print();		
	}

	public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
		int carry = 0;

		ListNode newHead = new ListNode(0);
		ListNode p1 = l1, p2 = l2, p3 = newHead;

		while (p1 != null || p2 != null) {
			if (p1 != null) {
				carry += p1.val;
				p1 = p1.next;
			}

			if (p2 != null) {
				carry += p2.val;
				p2 = p2.next;
			}

			p3.next = new ListNode(carry % 10);
			p3 = p3.next;
			carry /= 10;
		}

		if (carry == 1)
			p3.next = new ListNode(1);
		return newHead.next;
	}
}
