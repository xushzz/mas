package com.sirap.leet;


public class ReverseLinkedList {
	public ListNode reverseList2(ListNode head) {
	    if(head==null || head.next == null) 
	        return head;
	 
	    ListNode p1 = head;
	    ListNode p2 = head.next;
	 
	    head.next = null;
	    while(p1!= null && p2!= null){
	        ListNode t = p2.next;
	        p2.next = p1;
	        p1 = p2;
	        if (t!=null){
	            p2 = t;
	        }else{
	            break;
	        }
	    }
	 
	    return p2;
	}
	
	public ListNode reverseList(ListNode head) {
	    if(head==null || head.next == null)
	        return head;
	 
	    //get second node    
	    ListNode second = head.next;
	    //set first's next to be null
	    head.next = null;
	 
	    ListNode rest = reverseList(second);
	    second.next = head;
	 
	    return rest;
	}
}
