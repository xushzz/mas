package com.sirap.basic.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class MasterBase<PARAM extends Object> {
	private List<PARAM> tasks;
	private CountDownLatch latch;
	private List<Thread> threads = new ArrayList<Thread>();
	
	protected void setTasks(List<PARAM> tasks) {
		this.tasks = tasks;
	}
	
	protected int countOfThread() {
		return 50;
	}
	
	private void createThreads(int count, WorkerBase<PARAM> w) {
		for(int i = 0; i < count; i++) {
			Thread t = new Thread(w);
			threads.add(t);
		}
	}
	
	public void startWorking() {
		for(Thread t : threads) {
			t.start();
		}
	}
	
	protected void init(WorkerBase<PARAM> w) {
		int count = countOfThread();
		latch = new CountDownLatch(count);
		w.setLatch(latch);
		w.initQueue(new LinkedBlockingQueue<PARAM>(tasks));
		createThreads(count, w);
	}
	
	public void sitAndWait() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
