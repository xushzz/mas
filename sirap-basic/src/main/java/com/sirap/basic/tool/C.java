package com.sirap.basic.tool;

import java.util.List;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.basic.util.StrUtil;

/***
 * C for Console
 * @author dell
 * @Date June 16, 2014
 */
@SuppressWarnings("rawtypes")
public class C {
	public static void pr(Object obj) {
		System.out.print(obj);
	}

	public static void pl(Object obj) {
		System.out.println(obj);
	}

	public static void msg(String temp, Object... values) {
		System.out.println(StrUtil.occupy(temp, values));
	}
	
	@SafeVarargs
	public static <T> void pla(T... objs) {
		if(objs == null) {
			C.pl(StrUtil.of(objs));
		}
		
		int maxLen = 0;
		for(Object obj : objs) {
			String name = ObjectUtil.simpleNameOfInstance(obj);
			int len = name.length();
			if(maxLen < len) {
				maxLen = len;
			}
		}

		for(Object obj : objs) {
			C.pl(StrUtil.of(obj));
		}
	}

	public static void pl() {
		plk(1);
	}

	public static void pl3() {
		plk(3);
	}

	public static void pl8() {
		plk(8);
	}
	
	public static void plk(int kLines) {
		String lines = StrUtil.repeat('\n', kLines);
		System.out.print(lines);
	}

	public static void pl2(Object obj) {
		System.out.println(obj);
		System.out.println();
	}

	public static void pl2(String temp, Object... values) {
		System.out.println(StrUtil.occupy(temp, values));
		System.out.println();
	}
	
	public static void total(Object number) {
		C.pl(getTotal(number));
	}
	
	public static String getTotal(Object number) {
		return "Total\t" + number;
	}
	
	public static void listMex(List list) {
		if(list == null) return;
	
		for(Object obj:list) {
			if(obj instanceof MexItem) {
				((MexItem)obj).print();
			} else {
				C.pl(obj);
			}
		}
		
		if(list.size() > 5) {
			C.total(list.size());
		}
		
		if(list.size() > 0) {
			C.pl();
		}
	}
	
	public static void list(List list, boolean showTotal) {
		list(list, showTotal, -1);
	}
	
	public static void list(List list, boolean showTotal, long start) {
		if(list == null) {
			return;
		}
		
		for(Object obj: list) {
			if(obj instanceof MexItem) {
				((MexItem)obj).print();
			} else {
				C.pl(obj);
			}
		}
		
		if(showTotal && list.size() > 5) {
			long end = System.currentTimeMillis();
			if(start < 0) {
				String info = "Total " + list.size();
				C.pl(info);
			} else {
				String secondsCost = StrUtil.secondsCost(start, end);
				String info = "Total " + list.size() + ", " + secondsCost;
				C.pl(info);
			}
		}
	}
	
	public static void list(List list) {
		list(list, true);
	}

	public static void listWithoutTotal(List list) {
		list(list, false);
	}
	
	public static void listSome(List list, int maxRecords) {
		listSome(list, maxRecords, false);
	}
	
	public static void listSome(List list, int maxRecords, boolean showOrder) {
		if(list == null) return;
		
		int count = 0;
		for(Object obj:list) {
			if(count < maxRecords) {
				if(obj instanceof MexItem) {
					((MexItem)obj).print();
				} else {
					String order = "";
					if(showOrder) {
						order = StrUtil.occupy("#{0} ", count + 1);
					}
					C.pl(order + obj);
				}
				count++;
			} else {
				C.pl(count + " out of " + list.size() + ", has more...");
				break;
			}
		}
		
		if(list.size() > 0) {
			C.pl();
		}
	}
	
	public static void time(long start, long end) {
		time(start, end, "");
	}
	
	public static void time(long start, long end, String others) {
		String temp = "Timespent: " + (end-start)/1000.0 + " seconds. " + others;
		C.pl(temp);
	}
	
	public static void time2(long start, long end) {
		time(start, end, "");
		C.pl();
	}
	
	public static void time2(long start, long end, String others) {
		String temp = "Timespent: " + (end-start)/1000.0 + " seconds. " + others;
		C.pl(temp);
		C.pl();
	}
	
	public static void fetching(String url) {
		C.pl("Fetching... " + url);
	}
}
