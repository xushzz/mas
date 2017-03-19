package com.sirap.basic.thread;

import java.util.List;

import com.sirap.basic.domain.MexItem;

public class Master<T extends MexItem> extends MasterBase<T> {

	public Master(List<T> tasks, Worker<T> w) {
		setTasks(tasks);
		init(w);
		startWorking();
	}
}
