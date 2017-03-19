package com.sirap.basic.thirdparty.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

class EmailAuth extends Authenticator{
	String userName=null;
	String password=null;
	 
	public EmailAuth(){
	}
	public EmailAuth(String username, String password) { 
		this.userName = username; 
		this.password = password; 
	} 
	protected PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication(userName, password);
	}
}
