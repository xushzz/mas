package com.sirap.leet2;

import com.sirap.leet.ListNode;

public class RotateList {
    public ListNode rotateRight(ListNode head, int n) {
        if (head == null || n == 0)
            return head;
        ListNode slow = head;
        ListNode fast = head;
        while (n > 0) {
            n--;
            fast = fast.next;
            if (fast == null)
                fast = head;
        }
        if (fast == null || slow == fast)
            return head;
        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }
        ListNode newHead = slow.next;
        slow.next = null;
        fast.next = head;
        return newHead;
    }
}