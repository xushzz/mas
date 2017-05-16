//package com.sirap.poverty;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.TreeMap;
//
//import com.sirap.basic.tool.C;
//import com.sirap.basic.util.MexUtil;
//import com.sirap.basic.util.StrUtil;
//
//public class SwordVote {
//	public static void main(String[] args) {
//		SwordVote jian = new SwordVote();
//		jian.process();
//	}
//	
//	private List<VoteRecord> list;
//	private TreeMap<String, List<VoteRecord>> map = new TreeMap<>();
//	private String path = "E:\\Record\\04Apr\\0406\\VoteSample.txt";
//	
//	public void process() {
//		init();
//		calculate();
//	}
//	
//	public void init() {
//		list = MexUtil.readMexItemsViaClassName(path, VoteRecord.class.getName());
//		
//		for(VoteRecord item : list) {
//			String tun = item.getTun();
//			List<VoteRecord> tempList = map.get(tun);
//			if(tempList == null) {
//				tempList = new ArrayList<VoteRecord>();
//				tempList.add(item);
//				map.put(tun, tempList);
//			} else {
//				tempList.add(item);
//			}
//		}
//	}
//	
//	private void calculate() {
//		Iterator<String> it = map.keySet().iterator();
//		while(it.hasNext()) {
//			String tun = it.next();
//			List<VoteRecord> tunList= map.get(tun);
//			int[] total = accumulate(tunList);
//			String str = tun + "\t" + SwordUtil.toString(total, '\t');
//			C.pl(str);
//		}
//		
//		int[] total = accumulate(list);
//		String str = "Total\t" + "\t" + SwordUtil.toString(total, '\t');
//		C.pl(str);
//	}
//	
//	private int[] accumulate(List<VoteRecord> list) {
//		int[] total = new int[11];
//		total[0] = list.size();
//		for(VoteRecord vr : list) {
//			total[1] += vr.getNumberOfMembers();
//			int[] scores = vr.getScores();
//			for(int i = 0; i < scores.length; i++) {
//				total[2 + i] += scores[i];
//			}
//		}
//		
//		return total;
//	}
//}
//
