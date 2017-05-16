package com.sirap.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

public class LdapPagedVisitor3 {
	List<String> users = new ArrayList<>();
	
	public List<String> getAllUsers() {
		users.clear();
		readAllUsers();
		return users;
	}
	
	public void readAllUsers() {
		Hashtable<String, Object> env = LdapVisitor.getEnv();
		String searchBase = LdapVisitor.SEARCH_BASE;
		String searchFilter = LdapVisitor.SEARCH_FILTER;
		try {
			// Create the initial directory context
			LdapContext ctx = new InitialLdapContext(env, null);
			// Create the search controls
			SearchControls searchCtls = new SearchControls();
			// Specify the attributes to return
			String returnedAtts[] = LdapVisitor.KEYS_ARR;
			searchCtls.setReturningAttributes(returnedAtts);
			// Specify the search scope
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			// Set the page size and initialize the cookie that we pass backin
			// subsequent pages
			int pageSize = 20;
			byte[] cookie = null;
			// Request the paged results control
			// PagedResultsResponseControl
			Control[] ctls = new Control[] { new PagedResultsControl(pageSize, true) };
			ctx.setRequestControls(ctls);
			// initialize counter to total the results
			int totalResults = 0;
			// Search for objects using the filter
			int max = 3;
			int count = 0;
			do {
				if(count >= max) {
					break;
				}
				count++;
				NamingEnumeration<SearchResult> results = ctx.search(searchBase, searchFilter, searchCtls);
				// loop through the results in each page
				while (results != null && results.hasMoreElements()) {
					SearchResult item = results.next();
					String value = LdapVisitor.parseSearchResult(item);
					if(value == null) {
						continue;
					}
					users.add(value);
					
					totalResults++;
				}
				// examine the response controls
				cookie = parseControls(ctx.getResponseControls());
				// pass the cookie back to the server for the next page
				ctx.setRequestControls(new Control[] { new PagedResultsControl(pageSize, cookie, Control.CRITICAL) });
			} while ((cookie != null) && (cookie.length != 0));
			ctx.close();
			System.out.println("Total entries: " + totalResults);
		} catch (NamingException e) {
			System.err.println("Paged Search failed." + e);
		} catch (java.io.IOException e) {
			System.err.println("Paged Search failed." + e);
		}
	}

	static byte[] parseControls(Control[] controls) throws NamingException {
		byte[] cookie = null;
		if (controls != null) {
			for (int i = 0; i < controls.length; i++) {
				if (controls[i] instanceof PagedResultsResponseControl) {
					PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
					cookie = prrc.getCookie();
					System.out.println(">>Next Page \n");
				}
			}
		}
		return (cookie == null) ? new byte[0] : cookie;
	}

}