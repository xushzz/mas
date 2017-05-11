package com.sirap.basic.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sirap.basic.thirdparty.email.EmailService;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;

public class EmailCenter {
	
	public static final String DEF_RECEIVER = "@";
	private static final String TEMPLATE_EMAILINFO = "account:{0}, password:{1}, default receiver:{2}"; 

	private static EmailCenter instance;
	
	private String username; 
	private String password;
	private String defReceivers;
	private Map<String, EmailServerItem> extraServers;
	
	private Map<String, EmailServerItem> servers = new HashMap<String, EmailServerItem>();
	{
		servers.put("163.com", new EmailServerItem("163.com", "smtp.163.com", "25"));
	}
	
	public void config(String username, String password, String defReceivers) {
		config(username, password, defReceivers, null);
	}
	
	public void config(String username, String password, String defReceivers, Map<String, EmailServerItem> extraServers) {
		this.username = username;
		this.password = password;
		this.defReceivers = defReceivers;
		this.extraServers = extraServers;
	}

	public static EmailCenter g() {
		if(instance == null) {
			instance = new EmailCenter();
		}
		
		return instance;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDefReceivers() {
		return defReceivers;
	}

	public void setDefReceivers(String defReceivers) {
		this.defReceivers = defReceivers;
	}

	public void sendEmail(List<Object> items, List<String> toList, String subject, boolean wait2complete) {
		if(toList.size() == 1 && DEF_RECEIVER.equals(toList.get(0))) {
			if(EmptyUtil.isNullOrEmpty(defReceivers)) {
				C.pl("Wrong, default receivers not yet configured");
				return;
			}
			
			toList = StrUtil.splitByRegex(defReceivers);
		}
		
		for(int i = 0; i < toList.size(); i++) {
			if(i != 0) {
				C.pl();
			}
			
			String receiver = toList.get(i);
			Email mail = createEmail(items, receiver, subject);
			if(mail == null || mail.isInvalid()) {
				continue;
			}
			
			EmailService jack = new EmailService(mail);
			jack.deliver(wait2complete);
		}
	}
	
	private EmailServerItem getEmailServerItem(String username) {
		int idx = username.lastIndexOf('@');
		String domain = username.substring(idx + 1);
		
		EmailServerItem item = servers.get(domain);
		if(item != null) {
			return item;
		}
		
		if(!EmptyUtil.isNullOrEmpty(extraServers)) {
			item = extraServers.get(domain);			
		}
		
		return item;		
	}
	
	private Email createEmail(List<Object> items, String receiver, String subject) {
		if(username == null) {
			return null;
		}
		
		EmailServerItem server = getEmailServerItem(username);
		if(server == null) {
			String extraInfo = "";
			if(!EmptyUtil.isNullOrEmpty(extraServers)) {
				extraInfo = " and " + extraServers.keySet();
			}
			
			C.pl("No servers found for [" + username + "], available servers are " + servers.keySet() + extraInfo + ".");
			return null;
		}
		
		String host = server.getSmtp();
		String port = server.getPort();
		Email mail = new Email(username, password, host, port);
		mail.setItems(items);
		mail.setMailTo(receiver);
		if(!EmptyUtil.isNullOrEmptyOrBlank(subject)) {
			mail.setSubject(subject);
		}
		
		return mail;
	}
	
	public static List<String> parseLegalAddresses(String mixedAddresses) {
		String temp = mixedAddresses.trim();
		List<String> list = new ArrayList<String>();
		
		if(DEF_RECEIVER.equals(mixedAddresses)) {
			temp = g().defReceivers;
		}
		
		List<String>  items = StrUtil.split(temp, ";");
		
		for(String item:items) {
			if(StrUtil.isEmail(item)) {
				list.add(item.trim());
			}
		}
		
		if(EmptyUtil.isNullOrEmpty(list)) {
			//C.pl2("Email, receivers["+ temp + "] kind of totally illeagl.");
		}
		
		return list;
	}
	
	private String display(String item) {
		return display(item, false);
	}
	
	public String display(String item, boolean starInsteadOfChar) {
		if(item == null) {
			item = "";
		}
		
		String temp = item;
		if(starInsteadOfChar) {
			temp = StrUtil.pseudoPartlyEncrypt(temp);
		}
		
		return temp;
	}
	
	public String getEmailInfo() {
		List<String> items = new ArrayList<String>();
		items.add(display(username));
		items.add(display(password, true));
		items.add(display(defReceivers));
		
		String info = StrUtil.occupy(TEMPLATE_EMAILINFO, items.toArray());
		
		return info;
	}

	public static String getTwistedPassword(String password) {
		if(password == null) {
			return null;
		}
	
		String result = StrUtil.pseudoPartlyEncrypt(password);
		return result;
	}
}
