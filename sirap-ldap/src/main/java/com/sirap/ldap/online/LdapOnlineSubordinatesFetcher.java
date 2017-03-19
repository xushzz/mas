package com.sirap.ldap.online;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.naming.directory.SearchResult;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.ldap.LdapHelper;

public class LdapOnlineSubordinatesFetcher {
	
	private static final int MAX_LEVEL = 5;
	private static final String INDENT = StrUtil.repeat(' ', 4);
	
	private String account;
	private LdapOnlineManager manager;
	private int maxLevel = MAX_LEVEL;
	
	private boolean treelike;
	private List<List<StaffInfo>> staffListOfAllLevels = new ArrayList<>();
	private HashMap<String, List<String>> mapOfSubordinates = new HashMap<>(); 
	private List<String> staffList = new ArrayList<>();
	
	
	public LdapOnlineSubordinatesFetcher(LdapOnlineManager manager, String account) {
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
			List<StaffInfo> who = new ArrayList<>();;
			List<StaffInfo> subordinates = new ArrayList<>();
			
			for(int i = 0; i < staffListOfAllLevels.size(); i++) {
				List<StaffInfo> tempStaffList = staffListOfAllLevels.get(i);
				if(i == 0) {
					who.addAll(tempStaffList);
				} else {
					subordinates.addAll(tempStaffList);
				}
			}
			
			Collections.sort(subordinates);
			who.addAll(subordinates);
			staffList.addAll(LdapHelper.toStaffStringList(who));
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
		
		String filterExpression = LdapHelper.constructAccountExpression(accountList);
		
		List<String> accountListOfNextLevel = new ArrayList<>();
		SearchParams params = manager.createDefaultSearchParams(); 
		params.addAttribute("directReports");
		List<SearchResult> items = manager.fetchSearchResults(filterExpression, params);
		List<StaffInfo> staffListOfCurrentLevel = new ArrayList<>();
		for(SearchResult item : items) {
			StaffInfo staff = manager.parseStaffInfo(item);
			staffListOfCurrentLevel.add(staff);
			
			String source = staff.getDirectReports();
			if(!EmptyUtil.isNullOrEmpty(source)) {
				List<String> temp = LdapHelper.parseWorkerNumbersOfDirectReports(source);
				if(treelike) {
					if(!EmptyUtil.isNullOrEmpty(temp)) {
						mapOfSubordinates.put(staff.getAccount(), temp);
					}
				}
				accountListOfNextLevel.addAll(temp);				
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
		
		List<StaffInfo> staffListOfCurrentLevel = staffListOfAllLevels.get(index);
		Collections.sort(staffListOfCurrentLevel);
		
		for(StaffInfo staff : staffListOfCurrentLevel) {
			for(String acc: accounts) {
				if(StrUtil.equals(acc, staff.getAccount())) {
					treelikeStaffList.add(padding + staff.getDetail());
					getSubordinates(acc, level + 1, treelikeStaffList);
					break;
				}
			}
		}
	}
}
