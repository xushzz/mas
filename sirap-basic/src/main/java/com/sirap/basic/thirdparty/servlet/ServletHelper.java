package com.sirap.basic.thirdparty.servlet;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.common.collect.Lists;
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
		mets.add("getCookies");
		mets.add("getHeaderNames");
		mets.add("getSession");
		mets.add("getUserPrincipal");
		mets.add("getAuthType");
		mets.add("getContextPath");
		mets.add("getMethod");
		mets.add("getPathInfo");
		mets.add("getPathTranslated");
		mets.add("getQueryString");//返回请求行中的参数部分
		mets.add("getRemoteUser");
		mets.add("getRequestURI");//返回请求行中的资源名称
		mets.add("getRequestURL");//获得客户端发送请求的完整url
		mets.add("getRequestedSessionId");
		mets.add("getServletPath");
		mets.add("isRequestedSessionIdFromCookie");
		mets.add("isRequestedSessionIdFromURL");
		mets.add("isRequestedSessionIdFromUrl");
		mets.add("isRequestedSessionIdValid");
		mets.add("== ServletRequest ==");
//		mets.add("getReader");//getReader() has already been called for this request
		mets.add("getAttributeNames");
		mets.add("getLocales");
		mets.add("getParameterNames");
		mets.add("getLocale");
		mets.add("getParameterMap");
		mets.add("getInputStream");
		mets.add("getRemoteHost");
		mets.add("getCharacterEncoding");
		mets.add("getContentType");
		mets.add("getLocalAddr");
		mets.add("getLocalName");
		mets.add("getProtocol");
		mets.add("getRemoteAddr");//返回发出请求的IP地址
		mets.add("getRemoteHost");//返回发出请求的客户机的主机名
		mets.add("getScheme");
		mets.add("getServerName");
		mets.add("isSecure");
		mets.add("getContentLength");
		mets.add("getLocalPort");
		mets.add("getRemotePort");//返回发出请求的客户机的主机名
		mets.add("getServerPort");//返回发出请求的客户机的端口号。
		
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
		mets.add("getAttributeNames");
		mets.add("getServletContext");
		mets.add("getId");
		mets.add("getValueNames");
		mets.add("isNew");
		mets.add("getCreationTime");
		mets.add("getLastAccessedTime");
		
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
