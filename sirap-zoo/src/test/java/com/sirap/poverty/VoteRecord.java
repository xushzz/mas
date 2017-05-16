//package com.sirap.poverty;
//
//import java.util.List;
//
//import com.sirap.basic.domain.MexItem;
//import com.sirap.basic.tool.D;
//import com.sirap.basic.util.MathUtil;
//import com.sirap.basic.util.StrUtil;
//
//public class VoteRecord extends MexItem {
//	private static final long serialVersionUID = 1L;
//	private String village;
//	private String tun;
//	private int numberOfMembers;
//	private int[] scores = new int[9];
//	
//	public String getVillage() {
//		return village;
//	}
//
//	public void setVillage(String village) {
//		this.village = village;
//	}
//
//	public String getTun() {
//		return tun;
//	}
//
//	public void setTun(String tun) {
//		this.tun = tun;
//	}
//
//	public int getNumberOfMembers() {
//		return numberOfMembers;
//	}
//
//	public void setNumberOfMembers(int numberOfMembers) {
//		this.numberOfMembers = numberOfMembers;
//	}
//
//	public int[] getScores() {
//		return scores;
//	}
//
//	public void setScores(int[] scores) {
//		this.scores = scores;
//	}
//
//	public boolean parse(String record) {
//		String[] arr = record.split(",");
//		if(arr.length < 15) {
//			return false;
//		}
//		D.sink("valid: " + record);
//		village = arr[1];
//		tun = arr[2];
//		List<Integer> intList = StrUtil.extractIntegers(arr[4].trim());
//		if(intList.isEmpty()) {
//			return false;
//		}
//		String numberTemp = ""; 
//		for(Integer item : intList) {
//			numberTemp += item;
//		}
//		Integer num = MathUtil.toInteger(numberTemp);
//		D.sink("SHIT" + num);
//		if(num == null) {
//			return false;
//		}
//		
//		numberOfMembers = num;
//		for(int i = 0; i < scores.length; i++) {
//			String scoreTemp = arr[i + 6].trim();
//			if(!scoreTemp.isEmpty()) {
//				scores[i] = 1;
//			}
//		}
//		
//		return true;
//	}
//	
//	@Override
//	public String toString() {
//		return village + ", " + tun + ", " + numberOfMembers + " > " + SwordUtil.toString(scores, ',');
//	}
//}
