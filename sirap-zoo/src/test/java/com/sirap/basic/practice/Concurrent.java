package com.sirap.basic.practice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.ThreadUtil;

public class Concurrent {
	private List<Integer> list = new ArrayList<Integer>();
	public static void main(String[] args) {
		Concurrent ron = new Concurrent();
		ron.init();
	}
	
	private void init() {
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				fun1();
			}
		});
		
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				fun2();
			}
		});
		
		t1.start();
		ThreadUtil.sleepInSeconds(1);
		t2.start();
	}
	
	private void fun1() {
		List<Integer> temp = new ArrayList<Integer>(list);
		Iterator<Integer> it = list.iterator();
		while(it.hasNext()) {
			ThreadUtil.sleepInSeconds(1);
			C.pl(it.next());
		}
	}

	private void fun2() {
		list.add(6);
	}
}
