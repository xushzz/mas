package com.sirap.basic.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sirap.basic.component.Konstants;

public class MasterItemOriented<PARAM extends Object> extends MasterBase<PARAM> {

	private Map<PARAM, Object> results = new ConcurrentHashMap<PARAM, Object>();
	
	public MasterItemOriented(List<PARAM> tasks, WorkerItemOriented<PARAM> w) {
		setTasks(tasks);
		init(w);
		startWorking();
	}
	
	protected void init(WorkerItemOriented<PARAM> w) {
		super.init(w);
		w.setResults(results);
	}
	
	public Map<PARAM, Object> getResults() {
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
