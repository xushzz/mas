package com.sirap.ldap.online;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.ldap.LdapHelper;
import com.sirap.ldap.LdapManager;

public class LdapOnlineManager implements LdapManager {
	
	public static final String USER_FILTER_EXPRESSION = "(&{0}(|(title=*)(department=*)(mail=*)(mobile=*)(telephoneNumber=*))(objectClass=User)(objectCategory=person))";
	
	public static HashMap<String, String> MAP_ALIAS = new HashMap<>();
	static {
		MAP_ALIAS.put("ghao", "sAMAccountName");
		MAP_ALIAS.put("zhao", "sAMAccountName");
		MAP_ALIAS.put("acc", "sAMAccountName");
	}
	
	public static HashMap<String, String> MAP_SPACE = new HashMap<>();
	static {
		MAP_SPACE.put("=", "=");
		MAP_SPACE.put("&", "&");
		MAP_SPACE.put("\\|", "|");
		MAP_SPACE.put("\\(", "(");
		MAP_SPACE.put("\\)", ")");
	}
	
	private static LdapOnlineManager instance;
	//private SearchControls controls;
	private String searchBase;
	private List<String> orderbyKeys;
	private Hashtable<String, Object> env;

	private int maxPage = 2;
	private int pageSize = 100;
	private List<String> returningAttributes;
	
	private static final List<String> NO_SHOW = StrUtil.split("userCertificate,objectSid,msExchMailboxSecurityDescriptor,objectGUID,msExchMailboxGuid,protocolSettings", ',');
	
	private LdapOnlineManager() {
		
	}
	
	public static LdapOnlineManager g() {
		String principal = SimpleKonfig.g().getUserValueOf("ldap.principal");
		String credentials = SimpleKonfig.g().getUserValueOf("ldap.credentials");
		String providerUrl = SimpleKonfig.g().getUserValueOf("ldap.providerUrl");
		String searchBase = SimpleKonfig.g().getUserValueOf("ldap.searchBase");
		String returningAttributes = SimpleKonfig.g().getUserValueOf("ldap.returningAttributes");
		int maxPage = SimpleKonfig.g().getUserNumberValueOf("ldap.maxPage", 2);
		int pageSize = SimpleKonfig.g().getUserNumberValueOf("ldap.pageSize", 100);

		instance = g(principal, credentials, providerUrl, searchBase, returningAttributes, maxPage, pageSize);
	    
		return instance;
	}
	
	public static LdapOnlineManager g(String principal, String credentials, String providerUrl, String searchBase, String returningAttributes, int maxPage, int pageSize) {
		instance = new LdapOnlineManager();
		instance.searchBase = searchBase;
		instance.orderbyKeys = StrUtil.split(returningAttributes, ',');
		
		instance.returningAttributes = StrUtil.split(returningAttributes);
		instance.maxPage = maxPage;
		instance.pageSize = pageSize;		 

		instance.equipContext(principal, credentials, providerUrl);
		
		return instance;
	}

	private void equipContext(String principal, String credentials, String providerUrl) {
		XXXUtil.nullCheck(principal, "principal");
		XXXUtil.nullCheck(credentials, "credentials");
		XXXUtil.nullCheck(providerUrl, "providerUrl");
		
		env = new Hashtable<>();
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, credentials);
		env.put(Context.PROVIDER_URL, providerUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put("com.sun.jndi.ldap.connect.pool", "true");
	}
	
	private SearchControls createSearchControls(List<String> returningAttributes) {
		SearchControls controls = new SearchControls();
		
		if(!EmptyUtil.isNullOrEmpty(returningAttributes)) {
			String[] arr = new String[returningAttributes.size()];
			returningAttributes.toArray(arr);
			controls.setReturningAttributes(arr);
		}
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		
		return controls;
	}
	
	private InitialLdapContext createContext(int pageSize) {
		InitialLdapContext context = null;
		try {
			context = new InitialLdapContext(env, null);
			Control[] ctls = new Control[] { new PagedResultsControl(pageSize, true)};
			context.setRequestControls(ctls);
		} catch(Exception ex) {
			throw new MexException(ex);
		}
		
		return context;
	}
	public List<String> search() {
		return search(null);
	}
	
	public List<String> searchByAccounts(String accounts) {
		List<String> tempList = StrUtil.split(accounts);
		List<String> result = searchByAccounts(tempList);
		
		return result;
	}
	
	public List<String> searchByAccounts(List<String> accountList) {
		String filterExpression = LdapHelper.constructAccountExpression(accountList);
		List<String> result = searchByFilterExpressionOrderByCreationDate(filterExpression);
		
		return result;
	}
	
	public List<String> search(String criteria) {
		String filterExpression;
		if(criteria == null) {
			filterExpression = StrUtil.occupy(USER_FILTER_EXPRESSION, "");
		} else if(LdapHelper.containsWordCommaSpaceOnly(criteria)) {
			List<String> result = searchByAccounts(criteria);
			
			return result;
		} else {
			String temp = criteria.replace(':', '=');
			filterExpression = processByDictionary(temp);
			if(!StrUtil.equals(temp, filterExpression)) {
				C.pl("before: " + criteria + ", after: " + filterExpression);
			}
			
			filterExpression = removeAdditionalSpaces(filterExpression);
		}
		List<String> result = searchByFilterExpressionOrderByCreationDate(filterExpression);
		
		return result;
	}
	
	private static String processByDictionary(String source) {
		Iterator<String> it = MAP_ALIAS.keySet().iterator();
		String temp = source;
		while(it.hasNext()) {
			String key = it.next();
			String value = MAP_ALIAS.get(key);
			temp = temp.replace(key + "=", value + "=");
		}
		
		return temp;
	}
	
	/****
	 * @param source = "onTime  stamp =      13122 & a ( b )c = 272 & 272   =ju | 91=    3223";
	 * @return "onTime  stamp=13122&a(b)c=272&272=ju|91=3223"
	 */
	private static String removeAdditionalSpaces(String source) {
		Iterator<String> it = MAP_SPACE.keySet().iterator();
		String temp = source;
		while(it.hasNext()) {
			String key = it.next();
			String value = MAP_SPACE.get(key);
			temp = temp.replaceAll("\\s*" + key + "\\s*", value);
		}

		return temp;
	}
	
	public List<String> subordinatesOf(String workerNumberXX) {
		return subordinatesOf(workerNumberXX, 1, false);
	}
	
	public List<String> subordinatesOf(String workerNumberXX, int maxLevel, boolean treelike) {
		LdapOnlineSubordinatesFetcher ming = new LdapOnlineSubordinatesFetcher(this, LdapHelper.ignoreTypoAccount(workerNumberXX));
		ming.setMaxLevel(maxLevel);
		ming.setTreelike(treelike);
		
		List<String> items = ming.fetch();
		
		return items;
	}
	
	public SearchParams createDefaultSearchParams() {
		SearchParams params = new SearchParams(returningAttributes, maxPage, pageSize);
		
		return params;
	}

	public List<String> bossOf(String workerNumber) {
		SearchParams params = createDefaultSearchParams();
		params.addAttribute("manager");
		createSearchControls(returningAttributes);
		List<StaffInfo> staffList = new ArrayList<>();		
		retrieveManagerOf(LdapHelper.ignoreTypoAccount(workerNumber), staffList, params);
		
		List<String> result = new ArrayList<>();
		for(StaffInfo who : staffList) {
			result.add(who.getDetail());
		}
		
		return result;
	}
	
	private List<StaffInfo> toStaffInfoList(List<SearchResult> items) {
		List<StaffInfo> list = new ArrayList<>();
		
		for(SearchResult item : items) {
			StaffInfo who = parseStaffInfo(item);
			list.add(who);
		}
		
		return list;
	}

	private void retrieveManagerOf(String workerNumber, List<StaffInfo> staffList, SearchParams params) {
		if(staffList.size() > 20) {
			XXXUtil.alert();
		}
		
		if(EmptyUtil.isNullOrEmpty(workerNumber)) {
			return;
		}
		
		String filterExpression = "(sAMAccountName=" + workerNumber + ")";
		List<SearchResult> tempList = fetchSearchResults(filterExpression, params);
		
		if(EmptyUtil.isNullOrEmpty(tempList)) {
			return;
		}
		
		if(tempList.size() > 1) {
			throw new MexException("Uncanny, got more than one boss for " + workerNumber);					
		}
		
		StaffInfo who = parseStaffInfo(tempList.get(0));
		staffList.add(who);
		
		String manager = who.getManager();
		if(EmptyUtil.isNullOrEmpty(manager)) {
			return;
		}
		
		String managerWorkerNumber = LdapHelper.parseWorkerNumber(who.getManager());
		retrieveManagerOf(managerWorkerNumber, staffList, params);
	}
	
	public List<String> searchByFilterExpressionOrderByCreationDate(String filterExpression) {
		SearchParams params = createDefaultSearchParams(); 
		List<SearchResult> tempList = fetchSearchResults(filterExpression, params);
		List<StaffInfo> staffList = toStaffInfoList(tempList);
		Collections.sort(staffList);
		
		List<String> result = new ArrayList<>();
		for(StaffInfo who : staffList) {
			result.add(who.getDetail());
		}
		
		return result;
	}

	public List<String> findByWorkerNumber(String workerNumber) {
		SearchParams params = createDefaultSearchParams();
		params.setReturningAttributes(null);
		
		return findByWorkerNumber(workerNumber, params);
	}
	
	public List<String> findByWorkerNumber(String workerNumber, SearchParams params) {
		String filterExpression = "(sAMAccountName=" + LdapHelper.ignoreTypoAccount(workerNumber) + ")";
		List<SearchResult> items = fetchSearchResults(filterExpression, params);
		
		if(items.size() > 1) {
			throw new MexException("Multiple records with filter: " + filterExpression);					
		}
		
		List<String> result = new ArrayList<>();
		if(items.size() == 1) {
			result = itemToList(items.get(0));
		}
		
		return result;
	}

	public List<SearchResult> fetchSearchResults(String filterExpression, SearchParams params) {
		List<SearchResult> list = new ArrayList<>();
		try {
			InitialLdapContext context = createContext(params.getPageSize());
			byte[] cookie = null;
			int count = 0;
			do {
				if(count >= params.getMaxPage()) {
					break;
				}
				count++;
				NamingEnumeration<SearchResult> entries = context.search(searchBase, filterExpression, createSearchControls(params.getReturningAttributes()));
				while (entries != null && entries.hasMoreElements()) {
					SearchResult entry = entries.next();
					list.add(entry);
				}
				cookie = parseResponseControls(context.getResponseControls());
				context.setRequestControls(new Control[] { new PagedResultsControl(pageSize, cookie, Control.CRITICAL) });
				
			} while ((cookie != null) && (cookie.length != 0));
			
			context.close();
		} catch (Exception ex) {
			throw new MexException(ex);
		}
		
		return list;
	}

	private byte[] parseResponseControls(Control[] controls) {
		byte[] cookie = null;
		
		if (controls != null) {
			for (int i = 0; i < controls.length; i++) {
				if (controls[i] instanceof PagedResultsResponseControl) {
					PagedResultsResponseControl jack = (PagedResultsResponseControl) controls[i];
					cookie = jack.getCookie();
				}
			}
		}
		
		return (cookie == null) ? new byte[0] : cookie;
	}

	public StaffInfo parseStaffInfo(SearchResult entry) {
		StringBuffer buf = new StringBuffer();
		StaffInfo who = new StaffInfo();
        NamingEnumeration<? extends Attribute> attrs = entry.getAttributes().getAll();
        try {
        	List<Attribute> attrList = new ArrayList<>();
        	
            while(attrs.hasMore()) {
                Attribute attr = attrs.next();
                attrList.add(attr);
            }
            Collections.sort(attrList, new LdapAttributeComparator(orderbyKeys));
            for(Attribute attr : attrList ) {
                String id = attr.getID();
                Object value = attr.get();
                if(StrUtil.equals(id, "whenCreated")) {
                	String whenCreated = parseCreatedTime(value + "");
                	who.setWhenCreated(whenCreated);
                	buf.append(whenCreated);
                } else if(StrUtil.equals(id, "sAMAccountName")) {
                	who.setAccount(value + "");
                } else if(StrUtil.equals(id, "manager")) {
                	who.setManager(value + "");
                } else if(StrUtil.equals(id, "directReports")) {
                	value = attr.toString().replace(id + ":", "");
                	who.setDirectReports(value + "");
                } else {
                	buf.append(value);
                }
                buf.append(" ");
            }
            
        } catch (Exception ex) {
        	throw new MexException(ex);
        }
        
        who.setDetail(buf.toString().trim());
        return who;
	}

	public static List<String> itemToList(SearchResult item) {
		
		List<String> items = new ArrayList<>();
        NamingEnumeration<? extends Attribute> attrs = item.getAttributes().getAll();
        try {
            while(attrs.hasMore()) {
                Attribute attr = attrs.next();
                String id = attr.getID();
                if(NO_SHOW.contains(id)) {
                	continue;
                }
                
                items.add(attr.toString());
            }
        } catch (Exception ex) {
        	throw new MexException(ex);
        }
        
        return items;
	}

	/***
	 * 20061228060545.0Z
	 * @param source
	 * @return
	 */
	public static String parseCreatedTime(String source) {
		String regex = "(\\d{4})(\\d{2})(\\d{2})\\d{6}.*?Z$";
		String[] params = StrUtil.parseParams(regex, source);
		if(params == null) {
			return source;
		}
		String value = params[0] + "-" + params[1] + "-" + params[2];
		
		return value;
	}
}

class WhenCreatedComparator implements Comparator<String> {

	@Override
	public int compare(String a1, String a2) {
		if(a1 == null || a2 == null) {
			return 0;
		}
		
		return a1.compareTo(a2);
	}
	
}

class LdapAttributeComparator implements Comparator<Attribute> {
	
	private List<String> orderbyKeys;
	
	public LdapAttributeComparator(List<String> orderbyKeys) {
		this.orderbyKeys = orderbyKeys;
	}

	@Override
	public int compare(Attribute a1, Attribute a2) {
		if(orderbyKeys == null) {
			return 0;
		}

		int v1 = orderValue(a1);
		int v2 = orderValue(a2);

		return v1 - v2;
	}
	
	private int orderValue(Attribute attr) {
		String id = attr.getID();
		int idx = orderbyKeys.indexOf(id);
		
		int value = idx;
		if(idx == -1) {
			value = orderbyKeys.size() + 10;
		}
		
		return value;
	}

}