package com.sirap.basic.tool;

import java.util.List;

import com.sirap.basic.domain.MexItem;

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

	public static void pl() {
		System.out.println();
	}

	public static void pl2(Object obj) {
		System.out.println(obj);
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
		if(list == null) {
			return;
		}
		
		for(Object obj: list) {
			C.pl(obj);
		}
		
		if(showTotal && list.size() > 5) {
			C.total(list.size());
		}
	}
	
	public static void list(List list) {
		list(list, true);
	}

	public static void listWithoutTotal(List list) {
		list(list, false);
	}
	
	public static void listSome(List list, int maxRecords) {
		if(list == null) return;
		
		int count = 0;
		for(Object obj:list) {
			if(count < maxRecords) {
				if(obj instanceof MexItem) {
					((MexItem)obj).print();
				} else {
					C.pl(obj);
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
