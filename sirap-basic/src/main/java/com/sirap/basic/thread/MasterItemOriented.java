package com.sirap.basic.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sirap.basic.component.Konstants;

public class MasterItemOriented<PARAM extends Object, RETURN extends Object> extends MasterBase<PARAM> {

	private Map<PARAM, RETURN> results = new ConcurrentHashMap<PARAM, RETURN>();
	
	public MasterItemOriented(List<PARAM> tasks, WorkerItemOriented<PARAM, RETURN> w) {
		setTasks(tasks);
		init(w);
		startWorking();
	}
	
	protected void init(WorkerItemOriented<PARAM, RETURN> w) {
		super.init(w);
		w.setResults(results);
	}
	
	public Map<PARAM, RETURN> getResults() {
		sitAndWait();
		
		return results;
	}
	
	public List<String> getValidStringResults() {
		sitAndWait();
		
		List<Object> allItems = new ArrayList<Object>(results.values());
		List<String> list = new ArrayList<String>(); 
		for(Object item : allItems) {
			if(item == null || item.equals(Konstants.SHITED_FACE)) {
				continue;
			}
			
			list.add(item.toString());
		}
		
		return list;
	}
}
