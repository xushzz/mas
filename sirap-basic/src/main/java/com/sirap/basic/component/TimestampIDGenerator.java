package com.sirap.basic.component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

public class TimestampIDGenerator {
	private static AtomicReference<String> base = new AtomicReference<>();
	private static AtomicInteger index = new AtomicInteger(1);
	
	private TimestampIDGenerator() {
		
	}
	
	public static String nextId() {
		String current = DateUtil.timestamp();
		String value = null;
		if(StrUtil.equals(base.get(), current)) {
			value = current + "_K" + index.incrementAndGet();
		} else {
			value = current;
			base.set(current);;
			index.set(1);
		}
		
		return value;
	}
}
