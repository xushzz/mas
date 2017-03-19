package com.sirap.ldap;

import java.util.List;

public interface LdapManager {
	public List<String> bossOf(String workerNumber);
	public List<String> subordinatesOf(String workerNumberXX);
	public List<String> subordinatesOf(String workerNumberXX, int maxLevel, boolean treelike);
}
