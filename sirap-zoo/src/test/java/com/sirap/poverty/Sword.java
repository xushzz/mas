package com.sirap.poverty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.MexUtil;

public class Sword {
	public static void main(String[] args) {
		Sword jian = new Sword();
		jian.process();
	}
	
	private List<Poverty> list;
	private TreeMap<Integer, List<Poverty>> map = new TreeMap<>();
	private String path = "E:\\Record\\03Mar\\0327\\TOTAL.txt";
	
	public void process() {
		init();
		calculate();
	}
	
	public void init() {
		String className = Poverty.class.getName();
		list = MexUtil.readMexItemsViaClassName(path, className);
		
		for(Poverty item : list) {
			Integer score = item.getScore();
			List<Poverty> tempList = map.get(score);
			if(tempList == null) {
				tempList = new ArrayList<Poverty>();
				tempList.add(item);
				map.put(score, tempList);
			} else {
				tempList.add(item);
			}
		}
	}
	
	private void calculate() {
		Iterator<Integer> it = map.keySet().iterator();
		while(it.hasNext()) {
			Integer score = it.next();
			List<Poverty> list = map.get(score);
			String str = score + "\t" + list.size() + "\t" + numberOfPersons(list);
			C.pl(str);
		}
	}
	
	private int numberOfPersons(List<Poverty> list) {
		
		int total = 0;
		
		for(Poverty item : list) {
			total += item.getNumber();
		}
		
		return total;
	}
}

