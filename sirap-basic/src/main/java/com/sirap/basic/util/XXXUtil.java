package com.sirap.basic.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Map;

import com.sirap.basic.data.HttpData;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.exception.NullArgumentException;
import com.sirap.basic.tool.C;

@SuppressWarnings({ "rawtypes"})
public class XXXUtil {
	
	public static void checkMonthRange(int month) {
		if(month < 1 || month > 12) {
			throw new MexException("Bad month '{0}', should be between 1 and 12.", month);
		}
	}
	
	public static void checkDayRange(int day) {
		if(day < 1 || day > 31) {
			throw new MexException("Bad day '{0}', should be between 1 and 31.", day);
		}
	}
	
	public static void checkRange(long value, long min, long max) {
		if(value < min || value > max) {
			throw new MexException("Value '{0}' out of range, should be between {1} and {2}, both inclusive.", value, min, max);
		}
	}
	
	public static void checkRange(long value, long min, long max, String info) {
		if(value < min || value > max) {
			throw new MexException("Invalid {3} [{0}], should be between {1} and {2}, both inclusive.", value, min, max, info);
		}
	}
	
	public static void checkMonthDayRange(int month, int day) {
		checkMonthRange(month);
		int maxDay = DateUtil.MAX_DAY_IN_MONTH_LEAP_YEAR[month - 1];
		if(day < 1 || day > maxDay) {
			throw new MexException("Bad day '{0}', should be between 1 and {1}.", day, maxDay);
		}
	}
	
	public static void checkMonthDayRange(int month, int day, int year) {
		int[] maxDays;
		if(new GregorianCalendar().isLeapYear(year)) {
			maxDays = DateUtil.MAX_DAY_IN_MONTH_LEAP_YEAR;
		} else {
			maxDays = DateUtil.MAX_DAY_IN_MONTH;
		}
		checkMonthRange(month);
		int maxDay = maxDays[month - 1];
		if(day < 1 || day > maxDay) {
			throw new MexException("Bad day '{0}', should be between 1 and {1}.", day, maxDay);
		}
	}
	
	public static void checkYearRange(int year, int maxYear) {
		if(year < 1 || year > maxYear) {
			throw new MexException("Bad year '{0}', should be between 1 and {1}.", year, maxYear);
		}
	}

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
	
	public static void shouldBeTrue(boolean flag) {
		if(!flag) {
			throw new MexException("variable should be true.");
		}
	}
	
	public static void shouldBePositive(long value) {
		if(value <= 0) {
			throw new MexException("value {0} should be positive.", value);
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
	
	public static void alert(String msgTemplate, Object... params) {
		String msg = StrUtil.occupy(msgTemplate, params);
		alert(msg);
	}

	public static void alert(String msg) {
		throw new MexException(msg);
	}

	public static void alert() {
		throw new MexException("These violent delights have violent ends.");
	}

	public static void alert(Exception ex) {
		throw new MexException(ex);
	}

	public static void info(String msg) {
		C.pl(msg);
	}

	public static void info(String msg, Object... values) {
		C.pl(StrUtil.occupy(msg, values));
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
	
	public static String explainResponseException(String message) {
		String regex = "Server returned HTTP response code: (\\d+) for URL";
		String code = StrUtil.findFirstMatchedItem(regex, message);
		if(code == null) {
			return null;
		}
		String explanation = HttpData.EGGS.get(code);
		if(explanation == null) {
			return null;
		}
		
		return code + " " + explanation;
	}
}
