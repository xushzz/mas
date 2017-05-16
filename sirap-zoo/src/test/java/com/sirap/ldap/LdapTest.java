package com.sirap.ldap;

import java.util.List;

import javax.naming.directory.SearchResult;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.StrUtil;
import com.sirap.ldap.offline.SmartRelation;
import com.sirap.ldap.online.LdapOnlineManager;
import com.sirap.ldap.online.LdapOnlineSubordinatesFetcher;
import com.sirap.ldap.online.SearchParams;
import com.sirap.security.MrTrump;

public class LdapTest {
	 
	private String filterExpression = "(&{0}(title=*)(objectClass=User)(objectCategory=person))";
	
	private LdapOnlineManager g() {
		String principal = MrTrump.decodeBySIRAP("8A8882488530CC42ABA4152D5DC6A4EC", "ninja");
		String credentials = MrTrump.decodeBySIRAP("1E4197B6A97CC0E92B03BBA0C837D6D5", "ninja");
		String providerUrl = MrTrump.decodeBySIRAP("99CD5241C5889AA614313D03A1683861920D31BC1CF0F7CF480A651602026C01", "ninja");
		String searchBase = MrTrump.decodeBySIRAP("5DC909556223CAAFEB088CB3A9CF36CC", "ninja");
		String returningAttributes = "name,whenCreated,title,displayName,department,mail,mobile,telephoneNumber,sAMAccountName";
		int maxPage = 10;
		int pageSize = 400;
		D.pl(principal,credentials,providerUrl);
		return LdapOnlineManager.g(principal, credentials, providerUrl, searchBase, returningAttributes, maxPage, pageSize);
	}
	
	//@Test
	public void fetch() {
		List<String> accountList = StrUtil.split("01226504,000001");
		String filterExpression = LdapHelper.constructAccountExpression(accountList);
		//String filterExpression = "sAMAccountName=01226504";
		LdapOnlineManager xian = g();
		SearchParams params = xian.createDefaultSearchParams(); 
		params.addAttribute("directReports");
		List<SearchResult> list = g().fetchSearchResults(filterExpression, params);
		D.ts();
		for(SearchResult item : list) {
			C.list(LdapOnlineManager.itemToList(item));
		}
		D.ts();
	}
	/***
	 * sAMAccountName:01100795
	 */	
	public void typo() {
		C.pl(LdapHelper.ignoreTypoAccount("=399020)"));
		C.pl(LdapHelper.ignoreTypoAccount("19181981816)"));
		C.pl(LdapHelper.ignoreTypoAccount("399a023*@#2!@#$0)"));
	}

	//@Test
	public void containsWordCommaSpaceOnly() {
		C.pl(LdapHelper.containsWordCommaSpaceOnly("72640110,01139953"));
		C.pl(LdapHelper.containsWordCommaSpaceOnly("72640110,011399=53"));
		C.pl(LdapHelper.containsWordCommaSpaceOnly("72640110,011399 53"));
		C.pl(LdapHelper.containsWordCommaSpaceOnly("72640110,01:1399 53"));
	}
	
	public void findByAccounts() {
		String accounts = "72640110,01139953";
		List<String> items = g().searchByAccounts(accounts);
		
		C.list(items);
	}
	
	//@Test
	public void sxKLevel() {
		String who = "024273";
		who = "326332";
		List<String> list = g().subordinatesOf(who, 2, true);
		C.list(list);
	}
	
	public void sxLevel() {
		C.pl();
		String who = "024273";
//		accountList.add("326332");
//		accountList.add("72640110");
//		accountList.add("01149202");
//		accountList.add("000001");
		LdapOnlineSubordinatesFetcher ming = new LdapOnlineSubordinatesFetcher(g(), who);
		ming.fetch();
		C.pl();
	}
	
	//@Test
	public void shouxia() {
		String exp = "(sAMAccountName=01100795)";
		exp = "(sAMAccountType=805306368)";
		String who = "01223802";
		who = "000001";
		List<String> items = g().subordinatesOf(who);
		C.list(items);
	}
	
	//@Test
	public void boss() {
		String v1 = "01100795";
		v1 = "358280";
		List<String> items = g().bossOf(v1);
		C.list(items);
	}
	
	//@Test
	public void sxia() {
		String v1 = "01100795";
		v1 = "358280";
		List<String> items = g().subordinatesOf(v1);
		C.list(items);
	}

	//@Test
	public void parseManager() {
		List a = StrUtil.split("a,b,c");
		List b = a;
		C.pl(a);
		C.pl(b);
		b.remove("a");
		C.pl(a);
		C.pl(b);
		String v1 = "cn=BRUCE(26dsd504),OU=IT_Service_D(EP,OU)=IT_DEP,OU=HQ,OU=MSS-Express,DC=MSS,DC=com";
		C.pl(LdapHelper.parseWorkerNumber(v1));
	}
	
	//@Test
	public void accountType() {
		String exp = "(sAMAccountName=01100795)";
		exp = "(|(sAMAccountName=01100795)(sAMAccountName=01100795))";
		List<String> items = StrUtil.split("01100795,01100795,80002154 ");
		StringBuffer bf = new StringBuffer("(|");
		for(String item : items) {
			bf.append("(").append("sAMAccountName=").append(item).append(")");
		}
		bf.append(")");
		exp = bf.toString();
		C.pl(exp);
		//exp = "(sAMAccountType=805306368)";
		//items = g().searchByFilterExpression(exp);
		List<String> items2 = g().search(exp);
		//C.listSome(items, 10);
		C.list(items2);
	}
	
//	@Test
	public void byemail() {
		String filterExp = "(mail={0}@MSS-express.com)";
		String who = "mizhihui";
		String value = StrUtil.occupy(filterExp, who);
		List<String> items = null;///LdapPagedVisitor.g().search(value);
		C.list(items);
		C.pl();
		who = "01100795";
		//who = "80002154";
		value = StrUtil.occupy(filterExp, who);
		items = g().findByWorkerNumber(who);
		C.list(items);
	}
	
	//@Test
	public void v4() {
//		C.pl(StrUtil.occupy(filterExpression, "AAAA"));
//		C.pl(StrUtil.occupy(filterExpression, ""));
		LdapOnlineManager ninja = g();
		String workerNumber = "01100795";
		D.ts("v4");
		workerNumber = "01226504";
		SearchParams params = ninja.createDefaultSearchParams();
		params.addAttribute("directReports");
		params.removeAttribute("mail");
		params.removeAllAttributes();
		List<String> items = ninja.findByWorkerNumber(workerNumber, params);
		C.list(items);
	}
	
	public void v3() {
//		LdapPagedVisitor3 ninja = new LdapPagedVisitor3();
//		List<String> users = ninja.getAllUsers();
//		D.ts(users.size());
//		C.list(users);
		//C.list();
		//ninja.search();
	}
	
	public void v2() {
//		LdapManager ninja = g();
//		String criteria = "mail:*ding*";
//		criteria = "sn=*郁*";
//		criteria = null;
//		List<String> users = ninja.search(criteria);
//		D.ts(users.size());
//		C.list(users);
		//C.list();
		//ninja.search();
	}
	
//	@Test
	public void list() {
//		LdapVisitor ninja = new LdapVisitor();
//		List<String> users = ninja.getAllUsers();
//		D.ts(users.size());
//		C.list(users);
	}

	@Test
	public void smart() {
		String va = "082147 $[  397084] #[  01223804, 835887, 835871]";
//		va = "082147$[397084]#[01223804, 835887, 835871]";
//		va = "082147$[397084]";
//		va = "082147#[01223804, 835887, 835871]";
//		va = "082147";
//		va = "N";
		va = "082147  #[01223804, 835887, 835871]$[397084]";
		SmartRelation item = new SmartRelation();
		item.parse(va);
	}
}
