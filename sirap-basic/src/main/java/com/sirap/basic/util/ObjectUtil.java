package com.sirap.basic.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;

@SuppressWarnings("rawtypes")
public class ObjectUtil {
	
	public static final String TOP_CLASS = "java.lang.Class";

	public static Class forName(String className) {
		try {
			return Class.forName(className);
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}
	
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
		if(father == null) {
			return;
		}
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
	
	public static Object execute(final Object instanceOrClazz, final String methodName) {
		Class[] clazzArr = {};
		Object[] args = {};
		
		return execute(instanceOrClazz, methodName, clazzArr, args);
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
		} catch (Exception ex) {
			ex.printStackTrace();
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
	
	public static Object readFieldValue(Object instance, String fieldName) {
		try {
			Class<?> clazz = Class.forName(instance.getClass().getName());
			Field fd = clazz.getDeclaredField(fieldName);
			fd.setAccessible(true);
			Object obj = fd.get(instance);
			
			return obj;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static String simpleNameOfInstance(Object instance) {
		return instance != null ? instance.getClass().getSimpleName() : Konstants.FAKED_NULL;
	}
	
	/***
	 * 
	 * @param fullClassName could be full class name or regular class name
	 * @return
	 */
	public static String simpleNameOf(String fullClassName) {
		return fullClassName != null ? fullClassName.replaceAll("^.+\\.", "") : Konstants.FAKED_NULL;
	}
	
	/***
	 * com.sirap.basic.tool.Debug =>
	 * c.s.b.t.Debug
	 * @param instance
	 * @return
	 */
	public static String acronymNameOf(String fullClassName) {
		String dot = ".";
		List<String> items = StrUtil.split(fullClassName, dot);
		StringBuffer sb = StrUtil.sb();
		int count = 0;
		for(String item : items) {
			if(count == items.size() - 1) {
				sb.append(item);
				break;
			}
			if(!item.isEmpty()) {
				sb.append(item.charAt(0));
			}
			sb.append(dot);
			count++;
		}
		
		return sb.toString();
	}
	
	public static String simpleNameOf(Class<?> clazz) {
		return clazz != null ? clazz.getSimpleName() : Konstants.FAKED_NULL;
	}
	
	public static Map<String, Object> detailOf(Object instance, List<String> methodNames) {
		Map<String, Object> info = Amaps.newLinkHashMap();	
		
		for(String met : methodNames) {
			Object obj = ObjectUtil.execute(instance, met);
			info.put(met, obj);
		}
		
		return info;
	}
}
