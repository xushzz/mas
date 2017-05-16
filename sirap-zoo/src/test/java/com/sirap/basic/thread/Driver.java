package com.sirap.basic.thread;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.ThreadUtil;

public class Driver {
	public void sink(String name) {
		D.sink(name);
	}

	public void yell() {
		D.sink("myself");
	}
	
	public void salary(Integer amount) {
		D.pl("Increased to " + amount);
	}
	
	public void think() {
		int sleepingSeconds = 4;
//		D.ts("sleeping for " + sleepingSeconds + " seconds");
		ThreadUtil.sleepInMillis(sleepingSeconds * 1000);
		D.sink("Thinking with Orchid");
//		D.ts("timeoutMethod, end of sleeping for " + sleepingSeconds + " seconds");
	}
	
	public static void see(String abc) {
		C.pl("money lost");
	}
	
	public static String take(String abc, Integer amount) {
		C.pl(abc + ", " + amount);
		
		return "money paid";
	}
}
