package com.sirap.basic.thread;

import java.util.Map;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.tool.D;

public abstract class WorkerItemOriented<PARAM extends Object, RETURN extends Object> extends WorkerBase<PARAM> {

	protected Map<PARAM, RETURN> results;

	public abstract RETURN process(PARAM obj);
	
	public RETURN whenNull() {
		return (RETURN)Konstants.SHITED_FACE;
	}
	
	public void setResults(Map<PARAM, RETURN> results) {
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
			
			RETURN result = process(job);
			if(result != null) {
				results.put(job, result);
			} else {
				D.pl("The result is null by job " + job);
			}
		}
		
		latch.countDown();
	}
}
