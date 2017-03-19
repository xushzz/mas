package com.sirap.basic.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sirap.basic.domain.MexItem;

public class MasterMexItemsOriented<T extends MexItem, RETURN extends MexItem> extends MasterBase<T> {

	private Map<T, List<RETURN>> result = new ConcurrentHashMap<T, List<RETURN>>();
	
	public MasterMexItemsOriented(List<T> tasks, WorkerMexItemsOritented<T, RETURN> w) {
		setTasks(tasks);
		init(w);
		startWorking();
	}
	
	protected void init(WorkerMexItemsOritented<T, RETURN> w) {
		super.init(w);
		w.setResult(result);
	}

	public List<RETURN> getAllMexItems() {
		sitAndWait();
		
		List<RETURN> items = new ArrayList<RETURN>();
		for(Map.Entry<T, List<RETURN>> entry : result.entrySet()) {
			List<RETURN> value = entry.getValue();
			items.addAll(value);
		}
		
		return items;
	}
}
