package com.sirap.ldap.offline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.ldap.LdapHelper;
import com.sirap.ldap.LdapManager;

public class LdapOfflineManager implements LdapManager {
	
	private static LdapOfflineManager instance;

	protected HashMap<String, SmartDetail> DETAILS;
	protected HashMap<String, String> SUPERS;
	protected HashMap<String, List<String>> UNDERS;
	
	private LdapOfflineManager() {
		
	}
	
	public static LdapOfflineManager g() {
		if(instance == null) {
			String detail = SimpleKonfig.g().getUserValueOf("ldap.smart.detail");
			String whowho = SimpleKonfig.g().getUserValueOf("ldap.smart.whowho");

			if(!FileUtil.exists(detail)) {
				throw new MexException(detail + " doesn't exist.");
			}

			if(!FileUtil.exists(whowho)) {
				throw new MexException(whowho + " doesn't exist.");
			}
			
			instance = new LdapOfflineManager();
			
			List<SmartRelation> items = MexUtil.readMexItemsViaExplicitClass(whowho, SmartRelation.class);
			instance.initRelations(items);
			
			List<SmartDetail> items2 = MexUtil.readMexItemsViaExplicitClass(detail, SmartDetail.class);
			instance.initDetails(items2);
		}
	    
		return instance;
	}
	
	private void initDetails(List<SmartDetail> items) {
		DETAILS = new HashMap<>();
		
		for(SmartDetail item : items) {
			String who = item.getWho();
			DETAILS.put(who, item);
		}
	}
	
	private void initRelations(List<SmartRelation> items) {
		SUPERS = new HashMap<>();
		UNDERS = new HashMap<>();
		
		for(SmartRelation item : items) {
			String who = item.getWho();
			String supers = item.getSupers();
			String unders = item.getUnders();
			
			if(!EmptyUtil.isNullOrEmpty(supers)) {
				SUPERS.put(who, supers);
			}
			
			if(!EmptyUtil.isNullOrEmpty(unders)) {
				UNDERS.put(who, StrUtil.split(unders));
			}
		}
	}
	
	public List<String> subordinatesOf(String workerNumberXX) {
		return subordinatesOf(workerNumberXX, 1, false);
	}
	
	public List<String> subordinatesOf(String workerNumberXX, int maxLevel, boolean treelike) {
		LdapOfflineSubordinatesFetcher ming = new LdapOfflineSubordinatesFetcher(this, LdapHelper.ignoreTypoAccount(workerNumberXX));
		ming.setMaxLevel(maxLevel);
		ming.setTreelike(treelike);
		
		List<String> items = ming.fetch();
		
		return items;
	}

	public List<String> bossOf(String workerNumber) {
		String account = LdapHelper.ignoreTypoAccount(workerNumber);
		List<String> staffList = new ArrayList<>();
		
		retrieveManagerOf(account, staffList);
		
		return staffList;
	}
	
	private void retrieveManagerOf(String account, List<String> staffList) {
		if(staffList.size() > 20) {
			XXXUtil.alert();
		}
		
		if(EmptyUtil.isNullOrEmpty(account)) {
			return;
		}
		
		SmartDetail detail = DETAILS.get(account);
		if(detail == null) {
			staffList.add(account + ", no detail.");
			return;
		}
		
		staffList.add(detail.getDetail());
		
		String manager = SUPERS.get(account);
		
		if(EmptyUtil.isNullOrEmpty(manager)) {
			return;
		}
		
		retrieveManagerOf(manager, staffList);
	}
}
