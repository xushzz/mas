package com.sirap.basic.email;

public class EmailServerItem {
	private String domain;
	private String smtp;
	private String port;
	
	public EmailServerItem(String domain, String smtp, String port) {
		this.domain = domain;
		this.smtp = smtp;
		this.port = port;
	}
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	@Override
	public int hashCode() {
		int code = domain == null ? 0 : domain.hashCode();
		return code;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		if(obj instanceof EmailServerItem) {
			EmailServerItem item = (EmailServerItem)obj;
			String n1 = item.domain;
			if(n1 != null && n1.equalsIgnoreCase(domain)) {
				return true;
			}
		}
		
		
		return false;
	}
	
}
