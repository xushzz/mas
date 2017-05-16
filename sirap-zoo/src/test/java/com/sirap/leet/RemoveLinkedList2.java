package com.sirap.leet;


/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */
public class RemoveLinkedList2 {
	
    public ListNode reverseBetween(ListNode head, int m, int n) {
        // init m node and n node
        ListNode mNode = head;
        ListNode nNode = head;
        
        // previous node of node m
        ListNode mPreNode = new ListNode(0);
        mPreNode.next = head;
        
        // set up the distance between node m and node n
        for (int i=0; i<n-m; i++) nNode = nNode.next;
        
        // locate node m and node n
        for (int i=0; i<m-1; i++){
            mPreNode = mNode;
            mNode = mNode.next;
            nNode = nNode.next;
        }
        
        // check if case of reversing from head
        boolean fromHead = false;
        if(mNode == head) fromHead = true;
        
        // reverse node range
        while(mNode!=nNode){
            ListNode temp = nNode.next;
            nNode.next = mNode;
            mPreNode.next = mNode.next;
            mNode.next = temp;
            mNode = mPreNode.next;
        }
        
        // case of reverse started from head
        if(fromHead == true) return nNode;
        
        // other cases
        return head;
    }
}