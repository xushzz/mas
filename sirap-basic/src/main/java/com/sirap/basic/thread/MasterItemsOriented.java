package com.sirap.basic.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sirap.basic.component.map.AconcMap;

public class MasterItemsOriented<PARAM extends Object, RETURN extends Object> extends MasterBase<PARAM> {

	private Map<PARAM, List<RETURN>> result = new AconcMap<PARAM, List<RETURN>>();
	
	public MasterItemsOriented(List<PARAM> tasks, WorkerItemsOriented<PARAM, RETURN> w) {
		setTasks(tasks);
		init(w);
		startWorking();
	}
	
	protected void init(WorkerItemsOriented<PARAM, RETURN> w) {
		super.init(w);
		w.setResult(result);
	}

	public List<RETURN> getAllMexItems() {
		sitAndWait();
		
		List<RETURN> items = new ArrayList<RETURN>();
		for(Map.Entry<PARAM, List<RETURN>> entry : result.entrySet()) {
			List<RETURN> value = entry.getValue();
			items.addAll(value);
		}
		
		return items;
	}
}
