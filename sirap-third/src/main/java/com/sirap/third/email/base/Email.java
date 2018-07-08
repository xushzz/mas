package com.sirap.third.email.base;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MiscUtil;
import com.sirap.basic.util.StrUtil;

public class Email {
	
	private String serverHost; 
	private String serverPort; 
	private String mailFrom; 
	private String mailTo; 
	private String username; 
	private String password;  
	private String subject = "ALEXANDER THE GREAT";
	private List<Object> items = new ArrayList<Object>();
	private List<File> attachments = new ArrayList<File>();
	private Date sendDate = new Date();
	private boolean authRequired = true;
	
	public Email(String username, String password, String host, String port) {
		this.username = username;
		this.mailFrom = username;
		this.password = password;
		serverHost = host;
		serverPort = port;
	}

	public Properties getProperties() { 
	  Properties p = new Properties(); 
	  p.put("mail.smtp.host", serverHost); 
	  p.put("mail.smtp.port", serverPort); 
	  p.put("mail.smtp.auth", authRequired ? "true" : "false"); 
	  p.put("mail.smtp.timeout", "10000");
	  
	  return p; 
	}
	
	public Date getSendDate() {
		return sendDate;
	}

	public String getServerHost() {
		return serverHost;
	}

	public String getServerPort() {
		return serverPort;
	}
	
	public String getMailFrom() {
		return mailFrom;
	}

	protected void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public List<Object> getItems() {
		return items;
	}

	public void setItems(List<Object> items) {
		this.items = items;
		attachments = calculateAttachments();
	}
	
	public List<File> getAttachments() {
		return attachments;
	}

	public boolean isAuthRequired() {
		return authRequired;
	}
	
	public boolean isInvalid() {
		String template = "Invalid {0}[{1}], please conduct mail setting.";
		
		if(!MiscUtil.isEmail(mailFrom)) {
			C.pl(StrUtil.occupy(template, "sender", mailFrom));
			return true;
		}
		
		if(EmptyUtil.isNullOrEmptyOrBlank(password)) {
			C.pl(StrUtil.occupy(template, "password", password));
			return true;
		}
		
		if(!MiscUtil.isEmail(mailTo)) {
			C.pl(StrUtil.occupy(template, "receiver", mailTo));
			return true;
		}
		
		return false;
	}
	
	public void printBasicInfo() {
		String template = "From[{0}] to[{1}] date[{2}]\nSubject[{3}]";
		String dateStr = DateUtil.strOf(sendDate, DateUtil.GMT);
		StringBuffer sb = new StringBuffer();
		sb.append(StrUtil.occupy(template, mailFrom, mailTo, dateStr, subject));
		List<File> attatchments = getAttachments();
		if(!attatchments.isEmpty()) {
			sb.append("\nAttachments:\n");				
		}
		for(int i = 0; i < attatchments.size(); i++) {
			File filePath = attatchments.get(i);
			
			sb.append((i+1) + " ").append(filePath);
			if(filePath != null) {
				String fileSize = FileUtil.formatSize(filePath.length());
				sb.append(" ").append(fileSize);
			}
			
			if(i != attatchments.size() - 1) {
				sb.append("\n");
			}
		}
		
		C.pl(sb.toString());
	}
	
	private List<File> calculateAttachments() {
		List<File> attachments = new ArrayList<File>();
		
		for(Object item:items) {
			if(item instanceof File) {
				File file = (File)item;
				if(file.isFile()) {
					attachments.add(file);
				}
			}
		}
		
		return attachments;
	}
} 