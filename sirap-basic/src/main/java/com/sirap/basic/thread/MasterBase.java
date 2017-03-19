package com.sirap.basic.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import com.sirap.basic.domain.MexItem;

public abstract class MasterBase<T extends MexItem> {
	private List<T> tasks;
	private CountDownLatch latch;
	private List<Thread> threads = new ArrayList<Thread>();
	
	protected void setTasks(List<T> tasks) {
		this.tasks = tasks;
	}
	
	protected int countOfThread() {
		return 50;
	}
	
	private void createThreads(int count, WorkerBase<T> w) {
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
	
	protected void init(WorkerBase<T> w) {
		int count = countOfThread();
		latch = new CountDownLatch(count);
		w.setLatch(latch);
		w.setTasks(new ConcurrentLinkedQueue<T>(tasks));
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
