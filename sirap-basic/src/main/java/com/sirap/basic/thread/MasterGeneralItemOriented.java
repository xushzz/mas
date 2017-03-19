package com.sirap.basic.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexItem;

public class MasterGeneralItemOriented<T extends MexItem> extends MasterBase<T> {

	private Map<T, Object> results = new ConcurrentHashMap<T, Object>();
	
	public MasterGeneralItemOriented(List<T> tasks, WorkerGeneralItemOriented<T> w) {
		setTasks(tasks);
		init(w);
		startWorking();
	}
	
	protected void init(WorkerGeneralItemOriented<T> w) {
		super.init(w);
		w.setResults(results);
	}
	
	public Map<T, Object> getResults() {
		sitAndWait();
		
		return results;
	}
	
	public List<String> getValidStringResults() {
		sitAndWait();
		
		List<Object> allItems = new ArrayList<Object>(results.values());
		List<String> list = new ArrayList<String>(); 
		for(Object item:allItems) {
			if(item == null || item.equals(Konstants.SHITED_FACE)) {
				continue;
			}
			
			list.add(item.toString());
		}
		
		return list;
	}
}
