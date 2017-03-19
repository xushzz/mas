package com.sirap.basic.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.sirap.basic.exception.MexException;

public class NetworkUtil {
	
	public static final String SPACE = " ";
	public static final String BAD_CHARS_FOR_HOSTNAME = "`~!@#$%^&*()=+_[]{}\\|;:.'\",<>/?" + SPACE;
	
	public static boolean isBadHostname(String source) {
		boolean flag = StrUtil.containsAnyChar(source, BAD_CHARS_FOR_HOSTNAME);
		
		return flag;
	}

	public static String getHostByName(String name) throws MexException {
		
		if(isBadHostname(name)) {
			throw new MexException("[" + name + "] is not a legal hostname, shouldn't contain any of [" + BAD_CHARS_FOR_HOSTNAME + "].");
		}
		
		try {
			InetAddress addr = InetAddress.getByName(name);

			return addr.toString();
		} catch (UnknownHostException e) {
			throw new MexException("[" + name + "] is not a known host in current network.");
		}
	}
	
	public static String getLocalhost() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.toString();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getLocalhostNameIpMac() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String value = addr + ", " + getLocalMacAddress();
			
			return value;
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getLocalhostIp() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress();
			return ip;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getLocalhostName() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String name = addr.getHostName();
			return name;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static String formatHardwareAddress(byte[] mac) { 
        StringBuffer sb = new StringBuffer();  
        for(int i = 0; i < mac.length; i++){  
            if(i != 0 ) {  
                sb.append("-");  
            }
            
            String temp = Integer.toHexString(mac[i] & 0xFF);
            if(temp.length() == 1) {
            	temp = "0" + temp;
            }
            
            sb.append(temp);  
        }  
          
        String value = sb.toString().toUpperCase();
        
        return value;
    }
	
	public static String getLocalMacAddress() {
		try {
	        Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();  
	          
	        while(ni.hasMoreElements()) {  
	            NetworkInterface netI = ni.nextElement();  

	            byte[] bytes = netI.getHardwareAddress();
	            if(netI.isUp() && bytes != null && bytes.length == 6){  
	                String value = formatHardwareAddress(bytes);
	                
	                return value;
	            }  
	        }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        
        return null;  
    }
	
	public static List<String> getLocalMacItems() {
        List<String> items = new ArrayList<>();

        try {
	        Enumeration<NetworkInterface> list = NetworkInterface.getNetworkInterfaces();  
	        
	        while(list.hasMoreElements()) {
	            NetworkInterface face = list.nextElement();  
	            byte[] bytes = face.getHardwareAddress();
	            
	            if(bytes != null && bytes.length == 6){
		            StringBuilder sb = new StringBuilder();
		            
		            sb.append(formatHardwareAddress(bytes));
		            if(face.isUp()) {
			            sb.append(" *");
		            }
		            sb.append(" ").append(face.getDisplayName());
	                
	                String value = sb.toString();
	                
	                if(face.isUp()) {
		                items.add(0, value);
	                } else {
		                items.add(value);
	                }
	            }  
	        }
	        
		} catch (Exception ex) {
			ex.printStackTrace();
		}
        
        return items;  
    }
      
}
