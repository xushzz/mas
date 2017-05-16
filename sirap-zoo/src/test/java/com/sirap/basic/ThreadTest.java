package com.sirap.basic;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.thread.Master;
import com.sirap.basic.thread.Worker;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.RandomUtil;

public class ThreadTest {
	public static ThreadLocal<String> GLOBAL_THREAD_LOCAL = new ThreadLocal<String>();
	
	@Test
	public void move() {
		List<MexedObject> list = new ArrayList<>();
		
		for(int i = 0; i < 50; i++) {
			String value = "A_" + i + "_B";
			list.add(new MexedObject(value));
		}
		
		Master<MexedObject> george = new Master<MexedObject>(list, new BrickMover()) {
			protected int countOfThread() {
				return 5;
			}
		};
		george.sitAndWait();
	}
}


class BrickMover extends Worker<MexedObject> {
	
	public BrickMover() {
		
	}
	
	public void process(MexedObject obj) {
		String msg = RandomUtil.letters(4) + DateUtil.timestamp() + "_" + Thread.currentThread();
		ThreadTest.GLOBAL_THREAD_LOCAL.set(msg);
		C.pl(msg);
	}
}