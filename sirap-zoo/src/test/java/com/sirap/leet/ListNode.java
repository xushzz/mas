package com.sirap.leet;

import com.sirap.basic.tool.D;

public class ListNode {
	public int val;
	public ListNode next;

	public ListNode(int x) {
		val = x;
	}
	
	public void print() {
		D.pl(val);
		if(next != null) {
			next.print();
		} else {
			D.pl("END");
		}
	}	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(val);
		if(next != null) {
			sb.append("->"  + next.toString());
		} else {
			sb.append("END");
		}
		
		return sb.toString();
	}
}