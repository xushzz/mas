package com.sirap.basic.thread;

import com.sirap.basic.domain.MexItem;

public abstract class Worker<T extends MexItem> extends WorkerBase<T> {

	public abstract void process(T obj);
	
	public void run() {
		if(tasks == null) {
			return;
		}
		
		while(true) {
			T job = tasks.poll();
			if(job == null) {
				break;
			}
			
			process(job);
		}
		
		latch.countDown();
	}
}
