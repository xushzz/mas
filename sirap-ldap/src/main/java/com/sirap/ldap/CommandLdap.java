package com.sirap.ldap;

import java.util.List;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.ldap.offline.LdapOfflineManager;
import com.sirap.ldap.online.LdapOnlineManager;

public class CommandLdap extends CommandBase {

	private static final String KEY_LDAP = "la";
	private static final String KEY_LDAP_FIND = "fd";
	private static final String KEY_LDAP_BOSS = "bs";
	private static final String KEY_LDAP_SUBORDINATES = "xs";
	private static final String KEY_LDAP_ONLINE = "#";

	public boolean handle() {
		
		singleParam = parseParam(KEY_LDAP + "(|\\s+.+?)");
		if(singleParam != null) {
			LdapOnlineManager ninja = LdapOnlineManager.g();
			String criteria = singleParam.isEmpty() ? null : singleParam.trim();
			List<String> items = ninja.search(criteria);
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_LDAP_FIND + "(\\s+.+?)");
		if(singleParam != null) {
			LdapOnlineManager ninja = LdapOnlineManager.g();
			List<String> items = ninja.findByWorkerNumber(singleParam);
			export(items);
			
			return true;
		}
		
		params = parseParams("(" + KEY_LDAP_ONLINE + "|)" + KEY_LDAP_BOSS + "(\\s+.+?)");
		if(params != null) {
			boolean isOffline = EmptyUtil.isNullOrEmpty(params[0]);
			String account = params[1];
			LdapManager ninja = isOffline ? LdapOfflineManager.g() : LdapOnlineManager.g();
			List<String> items = ninja.bossOf(account);
			export(items);
			
			return true;
		}
		
		params = parseParams("(" + KEY_LDAP_ONLINE + "|)" + KEY_LDAP_SUBORDINATES + "(|\\d)(|\\.)(\\s+.+?)");
		if(params != null) {
			boolean isOffline = EmptyUtil.isNullOrEmpty(params[0]);
			boolean hasSpecificLevel = !EmptyUtil.isNullOrEmpty(params[1]);
			Integer level = 1;
			if(hasSpecificLevel) {
				level = MathUtil.toInteger(params[1]);
			}
			boolean treelike = hasSpecificLevel;
			if(!EmptyUtil.isNullOrEmpty(params[2])) {
				treelike = false;
			}
			String account = params[3];
			
			LdapManager ninja = isOffline ? LdapOfflineManager.g() : LdapOnlineManager.g();
			List<String> items = ninja.subordinatesOf(account, level, treelike);
			export(items);
			
			return true;
		}
		
		return false;
	}
}
