package com.sirap.basic.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ObjectUtil {
	
	public static final String TOP_CLASS = "java.lang.Class";

	public static Object createInstance(String className) {
		return createInstance(className, true);
	}
	
	public static Object createInstance(String className, boolean printExceptionIfFail) {
		if(className == null) {
			return null;
		}
		
		try {
			Object instance = Class.forName(className).newInstance();
			return instance;
		} catch (Exception e) {
			if(printExceptionIfFail) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T createInstanceViaConstructor(Class<?> classType, Object T) {
		T instance = null;
		try {
			Constructor<?> constructor = classType.getDeclaredConstructor();
			instance = (T)(constructor.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return instance;
	}
	
	public static Class<?> getClassType(String className) {
		XXXUtil.nullOrEmptyCheck(className, "String className");
		
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			XXXUtil.alert("ClassNotFoundException: " + className);
		}
		
		return clazz;
	}
	
	public static List<Class> getSuperClasses(String className) {
		Class source = getClassType(className);
		return getSuperClasses(source);
	}
	
	public static List<Class> getSuperClasses(Class source) {
		List<Class> list = new ArrayList<>();
		list.add(source);
		readSuperClassIntoList(list, source);
		
		return list;
	}
	
	private static void readSuperClassIntoList(List<Class> list, Class source) {
		if(source.equals(Object.class)) {
			return;
		}
		
		Class father = source.getSuperclass();
		list.add(father);
		readSuperClassIntoList(list, father);
	}
	
	public static boolean isInherit(String childClassName, Class glass) {
		List<Class> list = ObjectUtil.getSuperClasses(childClassName);
		boolean flag = list.indexOf(glass) >= 0;
		
		return flag;
	}
	
	public static boolean isStaticClass(Object obj) {
		String className = obj.getClass().getName();
		boolean flag = TOP_CLASS.equals(className);
		
		return flag;
	}
	
	public static Object execute(final Object instanceOrClazz, final String methodName, Class[] clazzArr, Object... args) {
		boolean isStaticMethod = ObjectUtil.isStaticClass(instanceOrClazz);
		
		try {
			Class<?> clazz;
			Object obj;
			if(isStaticMethod) {
				clazz = (Class)instanceOrClazz;
				Method m = clazz.getMethod(methodName, clazzArr);
				obj = m.invoke(null, args);
			} else {
				clazz = instanceOrClazz.getClass();
				Method m = clazz.getMethod(methodName, clazzArr);
				obj = m.invoke(instanceOrClazz, args);
			}
			
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Object readFieldValue(String className, String fieldName) {
		try {
			Class<?> clazz = Class.forName(className);
			Field f1 = clazz.getField(fieldName);
			Object obj = f1.get(clazz.newInstance());
			
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
