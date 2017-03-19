package com.sirap.basic.thirdparty.email;

import java.io.File;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.email.Email;
import com.sirap.basic.email.EmailCenter;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.ThreadUtil;

public class EmailService {
	
	public EmailService(Email mail) {
		this.mail = mail;
	}
	
	private boolean printException = true;
	
	protected Email mail;
	
	public void deliver(boolean wait2complete) {
		
		if(wait2complete) {
			boolean flag = send();
			if(flag) {
				C.pl("Sent.");
			}
		} else {
			C.pl("Send and don't care.");
			ThreadUtil.executeInNewThread(new Runnable() {
				public void run() {
					send();
				}
			});
		}
	}
	
	public boolean send() {
		
		EmailAuth authenticator = null;
		if (mail.isAuthRequired()) {
			authenticator = new EmailAuth(mail.getUsername(), mail.getPassword());
		}
		
		Session sendMailSession = Session.getInstance(mail.getProperties(), authenticator);
		try {
			MimeMessage mailMessage = new MimeMessage(sendMailSession);
			Address from = new InternetAddress(mail.getMailFrom());
			mailMessage.setFrom(from);
			Address to = new InternetAddress(mail.getMailTo());
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			mailMessage.setSubject(mail.getSubject());
			mailMessage.setSentDate(mail.getSendDate());
			setContent(mailMessage, mail.getItems());
			mail.printBasicInfo();
			C.pl("Sending...");
			Transport.send(mailMessage);
			
			return true;
		} catch (AuthenticationFailedException ex) {
			ex.printStackTrace();
			if(printException) {
				C.pl("Email sending, authentication failure with [" + mail.getUsername() + ", " + EmailCenter.getTwistedPassword(mail.getPassword()) + "].");
			}
		} catch (MessagingException ex) {
			if(printException) {
				C.pl(ex + "\nLocation=>" + getClass().getName());
			}
		} catch (Exception ex) {
			if(printException) {
				ex.printStackTrace();
			}
		}
		
		return false;
	}
	
	protected void setContent(MimeMessage mailMessage, List<Object> items) throws Exception {
		Multipart mp = new MimeMultipart();
		BodyPart mbp = new MimeBodyPart();
		String content = HtmlUtil.toSimpleHtml(items, true);
		mbp.setContent(content, "text/html; charset=utf-8");
		mp.addBodyPart(mbp);
		addAttatchments(mp, mail.getAttachments());
		mailMessage.setContent(mp);
	}
	
	protected void addAttatchments(Multipart mp, List<File> files) {
		for(File file: files) {
			addSingleFile(file, mp);
		}
	}
	
	public void addSingleFile(File file, Multipart mp) {
		FileDataSource fds = new FileDataSource(file);
        try {
        	MimeBodyPart mbp=new MimeBodyPart(); 
        	mbp.setDataHandler(new DataHandler(fds));
        	String fileName = fds.getName();
        	if(fileName.endsWith(Konstants.SUFFIX_BAT)) {
        		fileName = fileName + Konstants.SUFFIX_TXT;
        	}
            mbp.setFileName(fileName);  
            mp.addBodyPart(mbp);
        } catch (Exception ex) {
        	 ex.printStackTrace();        	 
        }
	}

	public void setPrintException(boolean printException) {
		this.printException = printException;
	}
}
