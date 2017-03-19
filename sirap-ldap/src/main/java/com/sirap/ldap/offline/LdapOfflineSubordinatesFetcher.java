package com.sirap.ldap.offline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;

public class LdapOfflineSubordinatesFetcher {
	
	private static final int MAX_LEVEL = 5;
	private static final String INDENT = StrUtil.repeat(' ', 4);
	
	private String account;
	private LdapOfflineManager manager;
	private int maxLevel = MAX_LEVEL;
	
	private boolean treelike;

	private List<List<SmartDetail>> staffListOfAllLevels = new ArrayList<>();
	private HashMap<String, List<String>> mapOfSubordinates = new HashMap<>();
	private List<String> staffList = new ArrayList<>();
	
	public LdapOfflineSubordinatesFetcher(LdapOfflineManager manager, String account) {
		this.manager = manager;
		this.account = account;
	}
	
	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	
	public void setTreelike(boolean treelike) {
		this.treelike = treelike;
	}
	
	private void reset() {
		staffListOfAllLevels.clear();
		mapOfSubordinates.clear();
		staffList.clear();
	}

	public List<String> fetch() {
		reset();
		
		List<String> accountList = new ArrayList<>();
		accountList.add(account);
		int level = 0;
		fetch(accountList, level);
		
		if(treelike) {
			String root = "king";
			mapOfSubordinates.put(root, StrUtil.split(account));
			getSubordinates(root, 0, staffList);	
		} else {
			List<SmartDetail> who = new ArrayList<>();;
			List<SmartDetail> subordinates = new ArrayList<>();
			
			for(int i = 0; i < staffListOfAllLevels.size(); i++) {
				List<SmartDetail> tempStaffList = staffListOfAllLevels.get(i);
				if(i == 0) {
					who.addAll(tempStaffList);
				} else {
					subordinates.addAll(tempStaffList);
				}
			}
			
			Collections.sort(subordinates);
			who.addAll(subordinates);
			staffList.addAll(toStaffStringList(who));
		}
		
		return staffList;
	}
	
	private void fetch(List<String> accountList, int level) {
		if(level > maxLevel) {
			return;
		}
		
		if(EmptyUtil.isNullOrEmpty(accountList)) {
			return;
		}
		
		List<String> accountListOfNextLevel = new ArrayList<>();
		List<SmartDetail> staffListOfCurrentLevel = new ArrayList<>();
		for(String acc : accountList) {
			SmartDetail detail = manager.DETAILS.get(acc);
			if(detail == null) {
				detail = new SmartDetail(acc);
			}
			staffListOfCurrentLevel.add(detail);
			
			List<String> unders = manager.UNDERS.get(acc);
			if(!EmptyUtil.isNullOrEmpty(unders)) {
				if(treelike) {
					mapOfSubordinates.put(acc, unders);
				}
				accountListOfNextLevel.addAll(unders);
			}
		}
		
		staffListOfAllLevels.add(staffListOfCurrentLevel);	
		
		int nextLevel = level + 1;
		fetch(accountListOfNextLevel, nextLevel);
	}

	private void getSubordinates(String account, int level, List<String> treelikeStaffList) {
		String padding = StrUtil.repeat(INDENT, level);
		List<String> accounts = mapOfSubordinates.get(account);
		if(EmptyUtil.isNullOrEmpty(accounts)) {
			return;
		}
		
		int index = level;
		if(index >= staffListOfAllLevels.size()) {
			return;
		}
		
		List<SmartDetail> staffListOfCurrentLevel = staffListOfAllLevels.get(index);
		Collections.sort(staffListOfCurrentLevel);
		
		for(SmartDetail staff : staffListOfCurrentLevel) {
			for(String acc: accounts) {
				if(StrUtil.equals(acc, staff.getWho())) {
					treelikeStaffList.add(padding + staff.wrapDetail());
					getSubordinates(acc, level + 1, treelikeStaffList);
					break;
				}
			}
		}
	}
	
	private List<String> toStaffStringList(List<SmartDetail> items) {
		List<String> list = new ArrayList<>();
		
		for(SmartDetail item : items) {
			list.add(item.wrapDetail());
		}
		
		return list;
	}
}
