package com.sirap.third.email;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import com.sirap.basic.domain.EmailCommandRecord;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.third.email.base.EmailCenter;

public class EmailFetcher {

	private String host = "pop3.163.com";
    private String provider = "pop3";
	private String username;
	private String password;
	private boolean printException = true;
	
	public EmailFetcher(String username, String password) {
		this.username = username;
		this.password = password;
	}
    
    public Properties getProperties() { 
  	  Properties p = System.getProperties();
  	  p.put("mail.smtp.timeout", "10000");
  	  
  	  return p; 
  	}
    
    private Store getStore() throws Exception {
    	try {
        	Session ss = Session.getDefaultInstance(getProperties(), null);
            Store store = ss.getStore(provider);
            store.connect(host, username, password);
            
            return store;
    	} catch (AuthenticationFailedException ex) {
			if(printException) {
				C.pl("Email fetching, authentication failure with [" + username + ", " + EmailCenter.getTwistedPassword(password) + "].");
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
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
	public List<EmailCommandRecord> fetch() {
    	long start = System.currentTimeMillis();
    	List<EmailCommandRecord> records = new ArrayList<EmailCommandRecord>();
        try {
            Store store = getStore();
            if(store == null) {
            	return Collections.EMPTY_LIST;
            }
            Folder inbox = store.getFolder("INBOX");
            if (inbox == null) {
            	XXXUtil.alert("Uncanny, no INBOX");
            }
            inbox.open(Folder.READ_ONLY);
            Message[] msgs = inbox.getMessages();
            for (int i = 0; i < msgs.length; i++) {
            	Message msg = msgs[i];
            	EmailCommandRecord record = parseMessage(msg);
            	records.add(record);
            }
            inbox.close(false);
            store.close();
        } catch (Exception ex) {
        	long end = System.currentTimeMillis();
            ex.printStackTrace();
            C.time2(start, end);
        }
        
        return records;
    }
    
    private EmailCommandRecord parseMessage(Message msg) {
    	EmailCommandRecord record = new EmailCommandRecord();
    	try {
        	record.setSentDate(msg.getSentDate());
        	record.setMsgFrom(listAddress(msg.getFrom()));
        	record.setMsgTo(listAddress(msg.getAllRecipients()));
        	
        	String temp = msg.getSubject();
        	if(temp != null) {
        		temp = temp.trim();
        	}
        	record.setSubject(temp);
    	} catch (Exception ex) {
    		ex.printStackTrace();    		
    	}
    	
    	return record;
    }
    
    private List<String> listAddress(Address[] adds) {
    	List<String> list = new ArrayList<String>();
    	if(EmptyUtil.isNullOrEmpty(adds)) {
    		return list;
    	}
    	
    	for(int i = 0; i < adds.length; i++) {
    		String location = parseEmailAddress(adds[i].toString());
    		list.add(location);
    	}
    	
    	return list;
    }

	private String parseEmailAddress(String input) {
		String param = StrUtil.parseParam(".*?<(.+?)>.*?", input);
		if(param != null) {
			return param;
		} else {
			return input.trim();
		}
	}
}
