package com.sirap.basic.thread;

import java.util.Map;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexItem;

public abstract class WorkerGeneralItemOriented<T extends MexItem> extends WorkerBase<T> {

	protected Map<T, Object> results;
	
	public abstract Object process(T obj);
	
	public void setResults(Map<T, Object> results) {
		this.results = results;
	}

	@Override
	public void run() {
		if(tasks == null) {
			return;
		}
		
		while(true) {
			T job = tasks.poll();
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
