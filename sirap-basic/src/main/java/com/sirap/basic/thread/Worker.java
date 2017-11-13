package com.sirap.basic.thread;


public abstract class Worker<PARAM extends Object> extends WorkerBase<PARAM> {

	public abstract void process(PARAM obj);
	
	@Override
	public void run() {
		if(queue == null) {
			return;
		}
		
		while(true) {
			PARAM job = queue.poll();
			if(job == null) {
				break;
			}
			
			process(job);
		}
		
		latch.countDown();
	}
}
