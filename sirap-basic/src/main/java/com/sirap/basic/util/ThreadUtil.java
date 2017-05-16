package com.sirap.basic.util;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.tool.C;

@SuppressWarnings("rawtypes")
public class ThreadUtil {

	public static void executeInNewThread(Runnable runnable) {
		executeInNewThread(runnable, true);
	}
	
	public static void executeInNewThread(Runnable runnable, boolean isDaemon) {
		Thread t = new Thread(runnable);
		t.setDaemon(isDaemon);
		t.start();
	}
	
	public static void sleepInSeconds(double seconds) {
		try {
			Thread.sleep((long)(seconds * Konstants.MILLI_PER_SECOND));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void sleepInMillis(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void executeInNewThread(final Object instanceOrClazz, final String methodName) {
		executeInNewThread(instanceOrClazz, methodName, false);
	}
	
	public static void executeInNewThread(final Object instanceOrClazz, final String methodName, boolean isDaemon) {
		Class[] clazzArr = new Class[0];
		Object[] args = new Object[0];
		
		executeInNewThread(instanceOrClazz, methodName, isDaemon, clazzArr, args);
	}
	
	public static void executeInNewThread(final Object instanceOrClazz, final String methodName, boolean isDaemon, Object... args) {
		int size = args.length;
		Class[] clazzArr = new Class[size];
		
		for(int i = 0; i < size; i++) {
			Object arg = args[i];
			clazzArr[i] = arg.getClass();
		}
		
		executeInNewThread(instanceOrClazz, methodName, isDaemon, clazzArr, args);
	}
	
	public static void executeInNewThread(final Object instanceOrClazz, final String methodName, boolean isDaemon, Class[] clazzArr, Object... args) {

		final Class[] tempClazz;
		if(clazzArr == null) {
			tempClazz = new Class[]{};
		} else {
			tempClazz = clazzArr;
		}

		final Object[] tempArgs;
		
		if(args == null) {
			tempArgs = new Object[]{};
		} else {
			tempArgs = args;
		}
		
		Runnable wang = new Runnable() {
			@Override
			public void run() {
				ObjectUtil.execute(instanceOrClazz, methodName, tempClazz, tempArgs);
			}
		};
		
		Thread laochen = new Thread(wang);
		laochen.setDaemon(isDaemon);
		laochen.start();
	}

	public static Object executeWithTimeout(int timeoutInSeconds, final Object instance, final String methodName) {
		Class[] clazzArr = new Class[0];
		Object[] args = new Object[0];
		
		return executeWithTimeout(timeoutInSeconds, instance, methodName, clazzArr, args);
	}
	
	public static Object executeWithTimeout(int timeoutInSeconds, final Object instance, final String methodName, Object... args) {
		int size = args.length;
		Class[] clazzArr = new Class[size];
		
		for(int i = 0; i < size; i++) {
			Object arg = args[i];
			clazzArr[i] = arg.getClass();
		}
		
		return executeWithTimeout(timeoutInSeconds, instance, methodName, clazzArr, args);
	}
	
	public static Object executeWithTimeout(int timeoutInSeconds, final Object instanceOrClazz, final String methodName, Class[] clazzArr, Object... args) {
		boolean isStaticMethod = ObjectUtil.isStaticClass(instanceOrClazz);
		
		final ExecutorService laojiang = Executors.newSingleThreadExecutor();
		Callable<Object> wang = new Callable<Object>() {
			@Override
			public Object call() {
				return ObjectUtil.execute(instanceOrClazz, methodName, clazzArr, args);
			}
		};
		
		Future future = null;
		String className = null;
		Object result = null;
		try {
			if(isStaticMethod) {
				className = ((Class)instanceOrClazz).getName();
			} else {
				className = instanceOrClazz.getClass().getName();
			}
			future = laojiang.submit(wang);
			result = future.get(timeoutInSeconds, TimeUnit.SECONDS);
		} catch (TimeoutException ex) {
			String params = "timeoutInSeconds:" + timeoutInSeconds + ", where:" + className + ", method:" + methodName + ", what: " + Arrays.asList(args);
			C.pl("TimeoutException => " + params);
			ex.printStackTrace();
			future.cancel(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			future.cancel(true);
		}
		
		laojiang.shutdown();
		return result;
	}
}
