package com.sirap.basic.thread;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.ThreadUtil;

@SuppressWarnings("rawtypes")
public class ThreadTest {

	public static void main(String[] args) {
		// jack.timeout();
		//jack.method();
		//Driver brave = new Driver();
		//executeInNewThread(brave, "yell");
		//executeInNewThread(brave, "sink", "yi sheng bu keng");
		//executeInNewThread(brave, "salary", 17777);
		
		int timeoutInSeconds = 2;
		Object instance = null; //brave;
		String methodName = "sink";
		String param = "the winter is coming.";
		//jack.timeoutMethod(timeoutInSeconds, instance, methodName, param);
		
		methodName = "pseudoEncrypt";
		methodName = "pseudoPartlyEncrypt";
		methodName = "see";
		methodName = "take";
		param = "abcdef";
//		C.pl(instance);
//		C.pl(instance.getClass());
//		C.pl(instance.getClass().getName());
//		C.pl(instance.getClass().getSimpleName());
//		C.pl(instance.getClass().getCanonicalName());
//		instance = StrUtil.class;
//		C.pl(instance);
//		C.pl(instance.getClass().getCanonicalName());

		D.ts(120);
		ThreadUtil.executeInNewThread(instance, "think");
		D.ts(123);
//		Object obj = ThreadUtil.timeoutMethod(timeoutInSeconds, instance, "think");
//		C.pl("REST => " + obj);
//		obj = ThreadUtil.timeoutMethod(timeoutInSeconds, Driver.class, "take", new Class[]{String.class, Integer.class}, "jac788888888888k", 89);
//		C.pl("REST => " + obj);
		//jack.timeoutStaticMethod(timeoutInSeconds, Driver.class, "see", new Class[]{String.class}, param);
		//Object obj = jack.timeoutStaticMethod(timeoutInSeconds, Driver.class, "take", new Class[]{String.class, Integer.class}, "jack", 89);
		//C.pl("RETURN " + obj);
		//jack.timeout();
	}
	
	public static void executeInNewThread(final Object instance, final String methodName) {
		Class[] clazzArr = new Class[0];
		Object[] args = new Object[0];
		
		executeInNewThread(instance, methodName, clazzArr, args);
	}
	
	public static void executeInNewThread(final Object instance, final String methodName, Object... args) {
		int size = args.length;
		Class[] clazzArr = new Class[size];
		
		for(int i = 0; i < size; i++) {
			Object arg = args[i];
			clazzArr[i] = arg.getClass();
		}
		
		executeInNewThread(instance, methodName, clazzArr, args);
	}
	
	public static void executeInNewThread(final Object instance, final String methodName, Class[] clazzArr, Object[] args) {
		Runnable yes = new Runnable(){
			@Override
			public void run() {
				
				try {
					Class<?> clazz = instance.getClass();
					Method m = clazz.getMethod(methodName, clazzArr);
					m.invoke(instance, args);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				D.ts(1990);				
		}};
		
		Thread ok = new Thread(yes);
		ok.start();
	}
	
	public Object timeoutStaticMethod(int timeoutInSeconds, final Class<?> clazz, final String methodName, Class[] clazzArr, Object... args) {
		
		final ExecutorService laojiang = Executors.newSingleThreadExecutor();
		Callable<Object> call = new Callable<Object>() {
			public Object call() {
				try {
					Method m = clazz.getMethod(methodName, clazzArr);
					Object obj = m.invoke(null, args);
					
					return obj;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
		};
		
		Future future = null;
		Object obj = null;
		try {
			future = laojiang.submit(call);
			obj = future.get(timeoutInSeconds, TimeUnit.SECONDS);
		} catch (TimeoutException ex) {
			String params = "from:" + clazz.getCanonicalName() + ", timeoutInSeconds:" + timeoutInSeconds + ", methodName:" + methodName + ", args: " + Arrays.asList(args);
			C.pl("TimeoutException => " + params);
			ex.printStackTrace();
			future.cancel(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			future.cancel(true);
		}
		
		laojiang.shutdown();
		
		return obj;		
	}
	
	public void timeout() {
		final ExecutorService exec = Executors.newSingleThreadExecutor();

		Callable<String> call = new Callable<String>() {
			public String call() throws Exception {
				// 开始执行耗时操作
				D.ts("sleeping for five seconds");
				Thread.sleep(1000 * 5);
				if ("".isEmpty()) {
					//throw new RuntimeException("Nothing is free");
				}
				D.ts("awake");
				return "线程执行完成.";
			}
		};

		Runnable runner = new Runnable() {
			public void run() {
				// 开始执行耗时操作
				D.ts("sleeping for five seconds");
				ThreadUtil.sleepInSeconds(5);
				if ("".isEmpty()) {
					//throw new RuntimeException("Nothing is free");
				}
				D.ts("awake");
				//return "线程执行完成.";
			}
		};

		Future future = null;
		try {
			int seconds = 3;
			//Future<String> future = exec.submit(call);
			future = exec.submit(runner);
			future.get(1000 * seconds, TimeUnit.MILLISECONDS); // 任务处理超时时间设为
			//System.out.println("任务成功返回:" + obj);
		} catch (TimeoutException ex) {
			System.out.println("处理超时啦....");
			ex.printStackTrace();
//			future.cancel(true);
		} catch (Exception e) {
			System.out.println("处理失败.");
			e.printStackTrace();
		}
		// 关闭线程池
		exec.shutdown();
	}
}
