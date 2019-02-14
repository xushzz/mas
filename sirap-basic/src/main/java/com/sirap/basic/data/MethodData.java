package com.sirap.basic.data;

import java.util.List;

import com.google.common.collect.Lists;

public class MethodData {
	public static final List<String> URL = Lists.newArrayList();
	static {
		List<String> mets = Lists.newArrayList();
		mets.add("getProtocol");
		mets.add("getDefaultPort");
		mets.add("getHost");
		mets.add("getPort");
		mets.add("getAuthority");
		mets.add("getFile");
		mets.add("getPath");
		mets.add("getQuery");
		mets.add("getRef");
//		mets.add("getUserInfo");
		URL.addAll(mets);
	}
	
	public static final List<String> HttpServletRequest = Lists.newArrayList();
	static {
		List<String> mets = Lists.newArrayList();
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
		HttpServletRequest.addAll(mets);
	}
	
	public static final List<String> ServletRequest = Lists.newArrayList();
	static {
		List<String> mets = Lists.newArrayList();
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
		ServletRequest.addAll(mets);
	}
	
	public static final List<String> HttpSession = Lists.newArrayList();
	static {
		List<String> mets = Lists.newArrayList();
		mets.add("getAttributeNames");
		mets.add("getServletContext");
		mets.add("getId");
		mets.add("getValueNames");
		mets.add("isNew");
		mets.add("getCreationTime");
		mets.add("getLastAccessedTime");
		HttpSession.addAll(mets);
	}
}
