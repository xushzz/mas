package com.sirap.basic.thread;

import java.util.List;

public class Master<PARAM extends Object> extends MasterBase<PARAM> {

	public Master(List<PARAM> tasks, Worker<PARAM> w) {
		setTasks(tasks);
		init(w);
		startWorking();
	}
}
