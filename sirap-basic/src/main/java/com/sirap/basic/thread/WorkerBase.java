package com.sirap.basic.thread;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.StrUtil;

public abstract class WorkerBase<PARAM extends Object> implements Runnable {

	protected CountDownLatch latch;
	protected Queue<PARAM> queue;
	protected int countOfTasks;
	protected static final String STATUS_TEMPLATE_SIMPLE = "{0}/{1} {2} {3}";
	protected static final String STATUS_FILE_COPY = "{0}/{1} {2} {3} to {4}";
	
	public void initQueue(Queue<PARAM> queue) {
		this.queue = queue;
		countOfTasks = queue.size();
	}
	
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}
	
	public void status(String template, Object... params) {
		C.pl(StrUtil.occupy(template, params));
	}
}
