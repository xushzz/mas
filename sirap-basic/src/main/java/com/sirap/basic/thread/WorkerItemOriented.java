package com.sirap.basic.thread;

import java.util.Map;

import com.sirap.basic.component.Konstants;

public abstract class WorkerItemOriented<PARAM extends Object> extends WorkerBase<PARAM> {

	protected Map<PARAM, Object> results;
	
	public abstract Object process(PARAM obj);
	
	public void setResults(Map<PARAM, Object> results) {
		this.results = results;
	}

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
			
			Object result = process(job);
			if(result == null) {
				result = Konstants.SHITED_FACE;
			}
			results.put(job, result);
		}
		
		latch.countDown();
	}
}
