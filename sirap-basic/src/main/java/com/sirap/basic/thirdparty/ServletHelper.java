package com.sirap.basic.thirdparty;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.common.collect.Lists;
import com.sirap.basic.data.MethodData;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.basic.util.StrUtil;

public class ServletHelper {

	public static String getHomeUrl(HttpServletRequest request) {
		String servlet = request.getServletPath();
		String origin = request.getRequestURL().toString();
		String home = origin.replaceAll(servlet + "$", "");
		
		return home;
	}
	
	public static Map<String, String> getRequestHeaders(HttpServletRequest request) {
		Enumeration items = request.getHeaderNames();
		Map<String, String> headers = new TreeMap<>();
		while(items.hasMoreElements()) {
			String key = items.nextElement() + "";
			String value = request.getHeader(key);
			headers.put(key, value);
		}
		
		return headers;
	}
	
	public static Map<String, Object> getSessionAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Enumeration items = session.getAttributeNames();
		Map<String, Object> atts = new TreeMap<>();
		while(items.hasMoreElements()) {
			String key = items.nextElement() + "";
			Object value = session.getAttribute(key);
			atts.put(key, value);
		}
		
		return atts;
	}
	
	public static String detail(HttpServletRequest request) {
		return D.jsp(detailX(request), Cookie.class);
	}
		
	public static Map<String, Object> detailX(HttpServletRequest request) {
		Map<String, Object> methodAndResult = new LinkedHashMap<>();
		
		List<String> mets = Lists.newArrayList();
		mets.add("== HttpServletRequest ==");
		mets.addAll(MethodData.HttpServletRequest);
		mets.add("== ServletRequest ==");
		mets.addAll(MethodData.ServletRequest);
		
		for(String met : mets) {
			if(StrUtil.isRegexFound("^[a-z]", met)) {
				Object obj = ObjectUtil.execute(request, met);
				methodAndResult.put(met, obj);
			} else {
				methodAndResult.put(met, "");
			}
		}
		
		mets.clear();
		mets.add("== HttpSession ==");
		mets.addAll(MethodData.HttpSession);
		
		for(String met : mets) {
			if(StrUtil.isRegexFound("^[a-z]", met)) {
				Class[] clazzArr = {HttpSession.class};
				Object obj = ObjectUtil.execute(request.getSession(), met);
				if(StrUtil.contains(met, "time") && StrUtil.isDigitsOnly(obj + "")) {
					String timestr = DateUtil.strOf(Long.parseLong(obj + ""), DateUtil.GMT2);
					methodAndResult.put(met, obj + " " + timestr);
				} else {
					methodAndResult.put(met, obj);
				}
			} else {
				methodAndResult.put(met, "");
			}
		}
		
		mets.clear();
		mets.add("== HttpUtil ==");
		mets.add("getHomeUrl");
		mets.add("getOriginIp");
		mets.add("getRequestHeaders");
		mets.add("getSessionAttributes");
		
		for(String met : mets) {
			if(StrUtil.isRegexFound("^[a-z]", met)) {
				Class[] clazzArr = {HttpServletRequest.class};
				Object obj = ObjectUtil.execute(ServletHelper.class, met, clazzArr, request);
				methodAndResult.put(met, obj);
			} else {
				methodAndResult.put(met, "");
			}
		}
		
		methodAndResult.put("this request", request);

		return methodAndResult;
	}
	
	/** 
	 * Want to know detail? check this:
	 * https://www.cnblogs.com/zhengyun_ustc/archive/2012/09/19/getremoteaddr.html
	 * @param request
	 * @return
	 */
    public static String getOriginIp(HttpServletRequest request) {  
//    	request.getRequestDispatcher("").
    	request.getSession();
    	List<String> headers = Lists.newArrayList();
    	headers.add("x-forwarded-for");
    	headers.add("Proxy-Client-IP");
    	headers.add("WL-Proxy-Client-IP");
    	headers.add("HTTP_CLIENT_IP");
    	headers.add("HTTP_X_FORWARDED_FOR");
    	
    	String ip = request.getRemoteAddr();
    	for(String header : headers) {
    		String value = request.getHeader(header);
//    		D.pla(header, value);
    		if(value == null || value.isEmpty() || StrUtil.equals("unknown", value)) {
    			continue;
    		}
    		
    		ip = value;
    		break;
    	}
    	
        return ip;  
    }  
}
