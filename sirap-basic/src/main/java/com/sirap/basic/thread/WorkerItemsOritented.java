package com.sirap.basic.thread;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class WorkerItemsOritented<PARAM extends Object, RETURN extends Object> extends WorkerBase<PARAM> {

	protected Map<PARAM, List<RETURN>> results;
	
	public abstract List<RETURN> process(PARAM obj);
	
	public void setResult(Map<PARAM, List<RETURN>> results) {
		this.results = results;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run() {
		if(queue == null) {
			return;
		}
		
		while(true) {
			PARAM job = queue.poll();
			if(job == null) {
				break;
			}
			
			List<RETURN> items = process(job);
			if(items != null) {
				results.put(job, items);
			} else {
				results.put(job, Collections.EMPTY_LIST);
			}
		}
		
		latch.countDown();
	}
}
