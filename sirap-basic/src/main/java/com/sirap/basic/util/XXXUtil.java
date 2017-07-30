package com.sirap.basic.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.exception.NullArgumentException;
import com.sirap.basic.tool.C;

@SuppressWarnings({ "rawtypes"})
public class XXXUtil {
	
	public static void nullOrEmptyCheck(Object obj) {
		nullOrEmptyCheck(obj, null);
	}
	
	
	public static void nullOrEmptyCheck(Object obj, String info) {
		if(obj instanceof String) {
			String str = (String)obj;
			if(EmptyUtil.isNullOrEmpty(str)) {
				throw new NullArgumentException(info);
			}
		} else if(obj instanceof Collection) {
			Collection col = (Collection)obj;
			if(EmptyUtil.isNullOrEmpty(col)) {
				throw new NullArgumentException(info);
			}
		} else if(obj instanceof Map) {
			Map mmm = (Map)obj;
			if(EmptyUtil.isNullOrEmpty(mmm)) {
				throw new NullArgumentException(info);
			}
		} else if(obj instanceof Object[]) {
			Object[] arr = (Object[])obj;
			if(EmptyUtil.isNullOrEmpty(arr)) {
				throw new NullArgumentException(info);
			}
		} else if(obj == null) {
			throw new NullArgumentException(info);
		}
	}
	
	public static void nullCheckOnly(Object obj) {
		nullCheck(obj, null);
	}

	public static void nullCheck(Object obj, String info) {
		if(obj == null) {
			throw new NullArgumentException(info);
		}
	}
	
	public static void shouldBeEqual(Object a, Object b) {
		if(a == null && b == null) {
			return;
		}
		
		if(a == null || !a.equals(b)) {
			throw new MexException("Should be equal for [" + a + "] and [" + b + "]");
		}
	}

	public static void alert(String msg) {
		throw new MexException(msg);
	}

	public static void alert() {
		throw new MexException("These violent delights have violent ends.");
	}

	public static void info(String msg) {
		C.pl(msg);
	}
	
	public static String getStackTrace(Throwable ex) {
		 StringWriter sw = new StringWriter();  
         PrintWriter pw = new PrintWriter(sw);  
         ex.printStackTrace(pw);
         
         String value = sw.toString();
         
         return value;
	}
	
	public static void printStackTrace(String msg) {
		Exception ex = new Exception(msg);
		ex.printStackTrace();
	}
}
