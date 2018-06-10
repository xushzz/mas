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
	
	public static final String DEBUG = "[GUBED]";
	
	public static void pl() {
		StackTraceElement parent = parent();
		String temp = DEBUG + " {0} #by {1}";
		println(StrUtil.occupy(temp, StrUtil.spaces(30), parent));
	}
	
	/***
	 * solid response
	 * [DEBUG] D.pl => from Boston Celtics to Golden State Warriors
	 * @param obj
	 */
	public static void pl(Object obj) {
		String simple = simpleClassNameDotMethodOf(current());
		StackTraceElement parent = parent();
		String temp = DEBUG + " {0} > [{1}] {2}     #by {3}";
		String type = ObjectUtil.simpleNameOfInstance(obj);
		println(StrUtil.occupy(temp, simple, type, StrUtil.of(obj), parent));
	}
	
	/***
	 * print array with timestamp info, another solid response
	 * @param objs
	 */
	@SafeVarargs
	public static <T> void pla(T... objs) {
		String simple = simpleClassNameDotMethodOf(current());
		String random = mark();
		printStart(simple, random, parent());
		printArray(objs);
		printEnd(simple, random, parent());
	}
	
	/****
	 * print Array, nothing else
	 * @param objs
	 */
	@SafeVarargs
	public static <T> void printArray(T... objs) {
		if(objs == null) {
			String simple = simpleClassNameDotMethodOf(current());
			StackTraceElement parent = parent();
			String temp = DEBUG + " {0} > [{1}] {2}     #by {3}";
			String type = ObjectUtil.simpleNameOfInstance(objs);
			println(StrUtil.occupy(temp, simple, type, StrUtil.of(objs), parent));
			return;
		}
		
		int maxLen = 0;
		for(Object obj : objs) {
			String name = ObjectUtil.simpleNameOfInstance(obj);
			int len = name.length();
			if(maxLen < len) {
				maxLen = len;
			}
		}

		String temp = "#{0} [{1}] {2}";
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
	
	public static void pjsp(Object obj) {
		println(jsp(obj));
	}
	
	public static void pjsp(Object obj, Class<?>... rockClasses) {
		println(jsp(obj, rockClasses));
	}
	
	public static String js(Object obj) {
		String temp = JsonUtil.toJson(obj);
		return temp;
	}
	
	public static String js(Object obj, Class<?>... rockClasses) {
		String temp = JsonConvertManager.g().toJsonByFields(obj, rockClasses);
		return temp;
	}
	
	public static String jst(Object obj) {
		String temp = JsonConvertManager.g().toJsonByFields(obj, obj.getClass());
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
		String methodInfo = parent().toString();
		String temp = DEBUG + " params of " + methodInfo;
		println(temp);
		printArray(objects);
		pl();
	}
	
	public static void sink() {
		StackTraceElement parent = parent();
		String temp = DEBUG + " {0} >      #by {1}";
		println(StrUtil.occupy(temp, "SINK", parent));
	}
	
	public static void sink(Object obj) {
		StackTraceElement parent = parent();
		String temp = DEBUG + " {0} > {1} {2}     #by {3}";
		String type = ObjectUtil.simpleNameOfInstance(obj);
		println(StrUtil.occupy(temp, "SINK", type, StrUtil.of(obj), parent));
	}

	/***
	 * Time and Random stuff
	 * @return
	 */
	public static String mark() {
		return RandomUtil.LETTERS(2);
	}

	private static String METHOD_INFO = DEBUG + " {0} {1} {2} {3}     #by {4}";
	
	//[DEBUG] D.pla => SL 2018-06-02_10:49:12.359 com.sirap.basic.tool.D.pla(D.java:53)
	public static void printStart(String simpleClassNameDotMethod, String random, StackTraceElement stackInfo) {
		println(StrUtil.occupy(METHOD_INFO, simpleClassNameDotMethod, "=>", random, now(), stackInfo));
	}
	
	public static String now() {
		String timestamp = DateUtil.displayDate(new Date(), DateUtil.DATE_TIME_FULL);
		return timestamp;
	}
	
	public static void printEnd(String simpleClassNameDotMethod, String random, StackTraceElement stackInfo) {
		println(StrUtil.occupy(METHOD_INFO, simpleClassNameDotMethod, "<=", random, now(), stackInfo));
	}
	
	public static void ts() {
		StackTraceElement parent = parent();
		String temp = DEBUG + " {0} >      #by {1}";
		println(StrUtil.occupy(temp, now(), parent));
	}
	
	//[DEBUG] 2018
	public static void ts(Object obj) {
		StackTraceElement parent = parent();
		String temp = DEBUG + " {0} > {1} {2}     #by {3}";
		String type = ObjectUtil.simpleNameOfInstance(obj);
		println(StrUtil.occupy(temp, now(), type, StrUtil.of(obj), parent));
	}

	/***
	 * list out stuff
	 * @param list
	 */
	public static <T> void list(List<T> list) {
		String simple = simpleClassNameDotMethodOf(current());
		String random = mark();
		printStart(simple, random, parent());
		printArray(list.toArray());
		printEnd(simple, random, parent());
	}
	
	public static String simpleClassNameDotMethodOf(StackTraceElement stackItem) {
		String temp = ObjectUtil.simpleNameOf(stackItem.getClassName()) + "." + stackItem.getMethodName();
		
		return temp;
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
	
	/***
	 * com.sirap.common.command.CommandBase.process(CommandBase.java:110)
	 * c.s.c.c.Command.process
	 * @param item
	 * @return
	 */
	public static String acronymTrace(StackTraceElement item) {
		String temp = "{0}.{1}({2}:{3})";
		String nice = ObjectUtil.acronymNameOf(item.getClassName());
		String msg = StrUtil.occupy(temp, nice, item.getMethodName(), item.getFileName(), item.getLineNumber());
		
		return msg;
	}
	
	public static StackTraceElement current() {
		return parentK(0);
	}
	
	public static StackTraceElement parent() {
		return parentK(1);
	}
	
	public static StackTraceElement parentK(int k) {
		StackTraceElement[] items = Thread.currentThread().getStackTrace();

		int index = k + 3;
		return items[index];
	}
}
