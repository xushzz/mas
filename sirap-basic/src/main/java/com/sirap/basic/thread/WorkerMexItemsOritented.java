package com.sirap.basic.thread;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.sirap.basic.domain.MexItem;

public abstract class WorkerMexItemsOritented<T extends MexItem, RETURN extends MexItem> extends WorkerBase<T> {

	protected Map<T, List<RETURN>> results;
	
	public abstract List<RETURN> process(T obj);
	
	public void setResult(Map<T, List<RETURN>> results) {
		this.results = results;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		if(tasks == null) {
			return;
		}
		
		while(true) {
			T job = tasks.poll();
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
