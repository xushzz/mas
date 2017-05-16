package com.sirap.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.StrUtil;
import com.sirap.security.MrTrump;

public class LdapVisitor {

//	String principal = MrTrump.decodeBySIRAP("8A8882488530CC42ABA4152D5DC6A4EC", "ninja");
//	String credentials = MrTrump.decodeBySIRAP("1E4197B6A97CC0E92B03BBA0C837D6D5", "ninja");
//	String providerUrl = MrTrump.decodeBySIRAP("99CD5241C5889AA614313D03A1683861920D31BC1CF0F7CF480A651602026C01", "ninja");

	public static final String SEARCH_BASE = MrTrump.decodeBySIRAP("FFADDFAD0AAC753736CFE94B4239E486", "ldap");
	//"(&(title=*)(objectClass=User)(objectCategory=person)(!(userAccountControl:1.2.840.113556.1.4.803:=2))(|(accountExpires=0)(accountExpires=9223372036854775807)))";
	public static final String SEARCH_FILTER = "(&(sn=*骥*)(title=*)(objectClass=User)(objectCategory=person))";
	private static final String PROVIDER_URL = MrTrump.decodeBySIRAP("5080C9E4967E1ADD0AB2C4FAB4EBA35FDA90AB572919E3BA5623E333DEBBAB89", "ldap");;
	private static final String PRINCIPAL = MrTrump.decodeBySIRAP("C04D3AB2C032499BF8CF8DFCD7BAC07B", "ldap");
	private static final String CREDENTIALS = MrTrump.decodeBySIRAP("A0A036A81F0AACBF8EF67A625473A6D7", "ldap");
	
	public static String KEYS_STR = "whenCreated,name,mail,title,displayName,department,mobile,givenName";
	public static List<String> KEYS_LIST = StrUtil.split(KEYS_STR);
	public static String[] KEYS_ARR = KEYS_STR.split(",");
	
	List<String> users = new ArrayList<>();
	
	public InitialDirContext house = null;
	
	public LdapVisitor() {
		init(getEnv());
	}
	
	public static Hashtable<String, Object> getEnv() {
		Hashtable<String, Object> env = new Hashtable<>();
		env.put(Context.SECURITY_PRINCIPAL, PRINCIPAL);
		env.put(Context.SECURITY_CREDENTIALS, CREDENTIALS);
		env.put(Context.PROVIDER_URL, PROVIDER_URL);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put("com.sun.jndi.ldap.connect.pool", "true");
		
		return env;
	}
	
	public boolean init(Hashtable<String, Object> env) {
		try {
			house = new InitialDirContext(env);
			return true;
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void readAllUsers() {
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		try {
			NamingEnumeration<SearchResult> ldapItems = house.search(SEARCH_BASE, SEARCH_FILTER, null, controls);
			parseResult(ldapItems);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public List<String> getAllUsers() {
		readAllUsers();
		C.pl();

		readAllUsers();
		return users;
	}
	
	public void parseResult(NamingEnumeration<SearchResult> ne) {
		int count2 = 0;
		try {
			int count = 0;
			int max = 200;
			while (ne.hasMoreElements()) {
				count2++;
	        	if(count2 >= max) {
	        		break;
	        	}
	        	
	        	SearchResult item = ne.next();
	        	String str = parseSearchResult(item);
		        
//		        C.pl();

		        if(str == null) {
		        	continue;
		        }
		        
		        users.add(str);
		        count++;
		    }
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		C.pl("count2=" + count2);
	}
	
	/***
	 * 
	 * @param source 20160401100501.0Z
	 * @return
	 */
	public static String parseCreatedTime(String source) {
		
		//20061228060545.0Z
		//20061103104041.0Z
		String regex = "(\\d{4})(\\d{2})(\\d{2})\\d{6}.*?Z$";
		String[] params = StrUtil.parseParams(regex, source);
		//D.pl(params);
		if(params == null) {
			D.sink("uncanny");
			return source;
		}
		if(!source.isEmpty()) {
			//return source;
		}
		String value = params[0] + "-" + params[1] + "-" + params[2];
		
		return value;
	}
	
	public static String parseSearchResult(SearchResult item) {
    	Attributes atts = item.getAttributes();
    	Attribute attTitle = atts.get("title");
    	if(attTitle == null) {
    		C.pl("no title");
    		return null;
    	}
    	
		StringBuffer buf = new StringBuffer();
        NamingEnumeration<? extends Attribute> attrs = atts.getAll();
        try {
            while (attrs.hasMore()) {
                Attribute attr = attrs.next();
                String id = attr.getID();
                if(!KEYS_LIST.contains(id)) {
                	continue;	
                }
                
                String value = attr.get() + "";
                if(StrUtil.equals(id, "whenCreated")) {
                	String createdTime = parseCreatedTime(value);
                	buf.append(createdTime);
                } else {
                	buf.append(value);
                }
//                C.pl(id + ":" + value);
                buf.append(" ");
            }
        } catch (Exception ex) {
        	throw new MexException(ex);
        }
        
        return buf.toString();
	}
}
