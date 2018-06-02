package com.sirap.basic.tool;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import com.sirap.basic.json.JsonConvertManager;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.ThreadUtil;

/***
 * D means Debug
 * @author dell
 * @Date September 19, 2014 created
 * @Date June 1, 2018 , the day LeBron James lost G1 to Warriors in 2018 NBA final.
 */
public class D {
	
	public static final String DEBUG = "[DEBUG]";
	
	public static void pl() {
		println();
	}
	
	/***
	 * solid response
	 * [DEBUG] D.pl => from Boston Celtics to Golden State Warriors
	 * @param obj
	 */
	public static void pl(Object obj) {
		StackTraceElement item = whoCalls();
		String simpleName = ObjectUtil.simpleNameOf(item.getClassName());
		String temp = DEBUG + " {0}.{1} > {2} {3}";
		String type = ObjectUtil.simpleNameOfInstance(obj);
		println(StrUtil.occupy(temp, simpleName, item.getMethodName(), type, obj));
		println();
	}
	
	/***
	 * print array with timestamp info, another solid response
	 * @param objs
	 */
	public static <T> void pla(T... objs) {
		String ts = tan();
		printStart(ts);
		printArray(objs);
		printEnd(ts);
	}
	
	/****
	 * print Array, nothing else
	 * @param objs
	 */
	public static <T> void printArray(T... objs) {
		int maxLen = 0;
		for(Object obj : objs) {
			String name = ObjectUtil.simpleNameOfInstance(obj);
			int len = name.length();
			if(maxLen < len) {
				maxLen = len;
			}
		}

		String temp = "#{0} {1} {2}";
		int count = 0;
		for(Object obj : objs) {
			count++;
			String name = ObjectUtil.simpleNameOfInstance(obj);
			String pretty = StrUtil.padRight(name, maxLen);
			println(StrUtil.occupy(temp, count, pretty, StrUtil.of(obj)));
		}
	}
	
	/**
	 * print obj as json format, JS means json
	 * @param obj
	 */
	public static void pjs(Object obj) {
		println(js(obj));
	}
	
	public static void pjs(Object obj, Class<?>... rockClasses) {
		println(js(obj, rockClasses));
	}
	
	public static String js(Object obj) {
		String temp = JsonUtil.toJson(obj);
		return temp;
	}
	
	public static String js(Object obj, Class<?>... rockClasses) {
		String temp = JsonConvertManager.g().toJsonByFields(obj, rockClasses);
		return temp;
	}
	
	/**
	 * print obj as pretty json format, p means pretty, while js means json
	 * @param obj
	 */
	public static String jsp(Object obj) {
		String temp = JsonUtil.toPrettyJson(obj);
		return temp;
	}
	
	public static String jsp(Object obj, Class<?>... rockClasses) {
		String temp = JsonConvertManager.g(true).toJsonByFields(obj, rockClasses);
		return temp;
	}

	public static void params(Object... objects) {
		String methodInfo = whoCalls().toString();
		String temp = DEBUG + " params of " + methodInfo;
		println(temp);
		printArray(objects);
		pl();
	}
	
	public static void sink() {
		sink("");
	}
	
	public static void sink(Object stuff) {
		println(DEBUG + " SINK> " + StrUtil.of(stuff));
	}

	/***
	 * Time and Random stuff
	 * @return
	 */
	public static String tan() {
		String temp = RandomUtil.letters(2, true) + " " + DateUtil.displayDate(new Date(), DateUtil.DATE_TIME_FULL);
		return temp;
	}

	private static String METHOD_INFO = DEBUG + " {0}.{1} {2} {3} {4}";
	
	//[DEBUG] D.pla => SL 2018-06-02_10:49:12.359 com.sirap.basic.tool.D.pla(D.java:53)
	public static void printStart(String tan) {
		StackTraceElement item = whoCalls();
		String simpleName = item.getClassName().replaceAll("^.+\\.", "");
		println(StrUtil.occupy(METHOD_INFO, simpleName, item.getMethodName(), "=>", tan, item));
	}
	
	public static void printEnd(String tan) {
		StackTraceElement item = whoCalls();
		String simpleName = item.getClassName().replaceAll("^.+\\.", "");
		println(StrUtil.occupy(METHOD_INFO, simpleName, item.getMethodName(), "<=", tan, item));
		println();
	}
	
	public static void ts() {
		ts("");
	}
	
	//[DEBUG] 2018
	public static void ts(Object stuff) {
		String str = DateUtil.displayDate(new Date(), DateUtil.DATE_TIME_FULL);
		println(DEBUG + StrUtil.occupy(" {0}> {1}", str, StrUtil.of(stuff)));
	}

	/***
	 * list out stuff
	 * @param list
	 */
	public static <T> void list(List<T> list) {
		String ts = tan();
		printStart(ts);
		printArray(list.toArray());
		printEnd(ts);
	}

	public static void sleepK(double seconds) {
		String temp = "[DEBUG] sleeping for {0} seconds ";
		String str = StrUtil.occupy(temp, (int)seconds);
		System.out.print(str);
		for(int i = 0; i < seconds; i++) {
			ThreadUtil.sleepInSeconds(1);
			System.out.print(".");
		}
		println();
	}
	
	public static void sleep5() {
		sleepK(5);
	}
	
	public static void sleep10() {
		sleepK(10);
	}
	
	public static void sleep20() {
		sleepK(20);
	}
	
	public static void charset() {
		D.ts(Charset.defaultCharset().name());
	}
	
	public static void println() {
		System.out.println();
	}
	
	public static void println(Object obj) {
		System.out.println(obj);
	}
	
	public static StackTraceElement who() {
		StackTraceElement[] items = Thread.currentThread().getStackTrace();
		
		return items[2];
	}
	
	public static void trace() {
		StackTraceElement[] items = Thread.currentThread().getStackTrace();
		printArray(items);
	}
	
	public static StackTraceElement whoCalls() {
		StackTraceElement[] items = Thread.currentThread().getStackTrace();
		
		return items[3];
	}
}
