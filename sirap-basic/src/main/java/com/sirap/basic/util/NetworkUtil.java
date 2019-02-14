package com.sirap.basic.util;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.data.MethodData;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;

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
	
	public static String getPublicIp() {
		return getPublicIp(false);
	}
	
	public static String getPublicIp(boolean showUrl) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				if(showUrl) {
					showFetching();
				}
				useGBK().setPrintExceptionIfNeeded(true);
				String url = "http://www.net.cn/static/customercare/yourip.asp";
				return url;
			}
			
			@Override
			protected void parse() {
				String regex = "<h2>(.+?)</h2>";
				item = StrUtil.findFirstMatchedItem(regex, source);
			}
		};
		
		return neymar.process().getItem();
	}
	
	public static List<String> ipDetail(String ipOrDomainname, boolean showUrl) {
		Extractor<String> neymar = new Extractor<String>() {

			public String getUrl() {
				if(showUrl) {
					showFetching();
				}
				useGBK().setPrintExceptionIfNeeded(true);
				String url = "http://www.ip138.com/ips1388.asp?ip={0}&action=2";
				return StrUtil.occupy(url, ipOrDomainname);
			}
			
			@Override
			protected void parse() {
				String regexDomain = "<h1><font color=\"blue\">(.+?)</font></h1></td>";
				String domain = StrUtil.findFirstMatchedItem(regexDomain, source);
				if(domain != null) {
					mexItems.add(domain);
				}
				String regexLi = "<ul class=\"ul1\">(.+?)</ul></td>";
				String section = StrUtil.findFirstMatchedItem(regexLi, source);
				if(section == null) {
					return;
				}
				String benzhan = XCodeUtil.urlDecodeUTF8("%E6%9C%AC%E7%AB%99");
				section = section.replace(benzhan, "IP138");
				List<String> items = StrUtil.findAllMatchedItems("<li>(.+?)</li>", section, 1);
				mexItems.addAll(items);
			}
		};
		
		return neymar.process().getItems();
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

	public static long ipToNumber(String source) {
		Matcher ma = StrUtil.createMatcher(Konstants.REGEX_IP, source);
		if(!ma.matches()) {
			XXXUtil.alert("Not a legal ip [{0}], must be like [0-255]x4 and dots.", source);
		}
		
		long total = 0;
		List<String> items = Lists.newArrayList(ma.group(1), ma.group(2), ma.group(3), ma.group(4));
		for(int i = 0; i < items.size(); i++) {
			String item = items.get(i);
			long value = Integer.parseInt(item);
			total += value << ((3 - i) * 8);
		}
		
		return total;
	}
	
	/***
	 * 103.228.210.1
	 * 1743049217
	 * @param source
	 * @return
	 */
	public static String ipFromNumber(long source) {
		long maxip = 0L + Integer.MAX_VALUE - Integer.MIN_VALUE;
		XXXUtil.checkRange(source, 0, maxip, "ip");
		long remain = source;
		String ip = "";
		for(int i = 0; i < 4; i++) {
			long offset = (3 - i) * 8;
			long mod = remain >> offset;
			remain = remain - (mod << offset);
			if(i != 0) {
				ip += ".";
			}
			ip += mod;
		}
		
		return ip;
	}
	
	public static boolean isLegalIP(String source) {
		if(EmptyUtil.isNull(source)) {
			return false;
		}
		
		return StrUtil.isRegexMatched(Konstants.REGEX_IP, source);
	}
	
	public static Map<String, Object> urlDetail(String urlstring) {
		URL uk = null;
		try {
			uk = new URL(urlstring);
			return ObjectUtil.detailOf(uk, MethodData.URL);
		} catch (MalformedURLException ex) {
			XXXUtil.alert(ex);
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		String sa = null;
		sa = "https://blog.csdn.net/qq_20065991/article/details/82902220";
		sa = "https://baijiahao.baidu.com/s?id=1621160874039792094&wfr=spider&for=pc#Fleet_restructuring";
//		sa = "http://en.wikipedia.org/wiki/Air_India?id=1621160874039792094&wfr=spider&for=pc#Fleet_restructuring";
		sa = "http://120.79.195.133:1998/wiki/Air_India?i= =d=162 1160874039792094&wfr=spider&for=pc#Fleet_restructuring";
//		sa = "xfile:///D:/Gitpro/OSChina/todos/high/picker.html?id=1621#nice";
		Object oa = null;
		sa = "i=16211&ni=60874039792094&wfr=spider&for=pc";
//		oa = urlDetail(sa);
		C.pl(sa);
//		Map map = queryOf(sa);
//		D.pjsp(map);
//		C.pl(queryOf(map));
//		D.pjsp(oa);
	}
}
