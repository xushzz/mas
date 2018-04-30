package com.sirap.basic.data;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Maps;
import com.sirap.basic.util.CollUtil;

public class HttpData {

	public static final HashMap<String, String> EGGS = Maps.newHashMap();
	static {
		EGGS.put("100", "Informational, Continue");
		EGGS.put("101", "Informational, Switching Protocols");
		EGGS.put("102", "Informational, Processing (WebDAV)");
		EGGS.put("200", "Success, OK");
		EGGS.put("201", "Success, Created");
		EGGS.put("202", "Success, Accepted");
		EGGS.put("203", "Success, Non-Authoritative Information");
		EGGS.put("204", "Success, No Content");
		EGGS.put("205", "Success, Reset Content");
		EGGS.put("206", "Success, Partial Content");
		EGGS.put("207", "Success, Multi-Status (WebDAV)");
		EGGS.put("208", "Success, Already Reported (WebDAV)");
		EGGS.put("226", "Success, IM Used");
		EGGS.put("300", "Redirection, Multiple Choices");
		EGGS.put("301", "Redirection, Moved Permanently");
		EGGS.put("302", "Redirection, Found");
		EGGS.put("303", "Redirection, See Other");
		EGGS.put("304", "Redirection, Not Modified");
		EGGS.put("305", "Redirection, Use Proxy");
		EGGS.put("306", "Redirection, (Unused)");
		EGGS.put("307", "Redirection, Temporary Redirect");
		EGGS.put("308", "Redirection, Permanent Redirect (experimental)");
		EGGS.put("400", "Client Error, Bad Request");
		EGGS.put("401", "Client Error, Unauthorized");
		EGGS.put("402", "Client Error, Payment Required");
		EGGS.put("403", "Client Error, Forbidden");
		EGGS.put("404", "Client Error, Not Found");
		EGGS.put("405", "Client Error, Method Not Allowed");
		EGGS.put("406", "Client Error, Not Acceptable");
		EGGS.put("407", "Client Error, Proxy Authentication Required");
		EGGS.put("408", "Client Error, Request Timeout");
		EGGS.put("409", "Client Error, Conflict");
		EGGS.put("410", "Client Error, Gone");
		EGGS.put("411", "Client Error, Length Required");
		EGGS.put("412", "Client Error, Precondition Failed");
		EGGS.put("413", "Client Error, Request Entity Too Large");
		EGGS.put("414", "Client Error, Request-URI Too Long");
		EGGS.put("415", "Client Error, Unsupported Media Type");
		EGGS.put("416", "Client Error, Requested Range Not Satisfiable");
		EGGS.put("417", "Client Error, Expectation Failed");
		EGGS.put("418", "Client Error, I'm a teapot (RFC 2324)");
		EGGS.put("420", "Client Error, Enhance Your Calm (Twitter)");
		EGGS.put("422", "Client Error, Unprocessable Entity (WebDAV)");
		EGGS.put("423", "Client Error, Locked (WebDAV)");
		EGGS.put("424", "Client Error, Failed Dependency (WebDAV)");
		EGGS.put("425", "Client Error, Reserved for WebDAV");
		EGGS.put("426", "Client Error, Upgrade Required");
		EGGS.put("428", "Client Error, Precondition Required");
		EGGS.put("429", "Client Error, Too Many Requests");
		EGGS.put("431", "Client Error, Request Header Fields Too Large");
		EGGS.put("444", "Client Error, No Response (Nginx)");
		EGGS.put("449", "Client Error, Retry With (Microsoft)");
		EGGS.put("450", "Client Error, Blocked by Windows Parental Controls (Microsoft)");
		EGGS.put("451", "Client Error, Unavailable For Legal Reasons");
		EGGS.put("499", "Client Error, Client Closed Request (Nginx)");
		EGGS.put("500", "Server Error, Internal Server Error");
		EGGS.put("501", "Server Error, Not Implemented");
		EGGS.put("502", "Server Error, Bad Gateway");
		EGGS.put("503", "Server Error, Service Unavailable");
		EGGS.put("504", "Server Error, Gateway Timeout");
		EGGS.put("505", "Server Error, HTTP Version Not Supported");
		EGGS.put("506", "Server Error, Variant Also Negotiates (Experimental)");
		EGGS.put("507", "Server Error, Insufficient Storage (WebDAV)");
		EGGS.put("508", "Server Error, Loop Detected (WebDAV)");
		EGGS.put("509", "Server Error, Bandwidth Limit Exceeded (Apache)");
		EGGS.put("510", "Server Error, Not Extended");
		EGGS.put("511", "Server Error, Network Authentication Required");
		EGGS.put("598", "Server Error, Network read timeout error");
		EGGS.put("599", "Server Error, Network connect timeout error");
	}
	
	public static List<String> eggs() {
		return CollUtil.mapToList(EGGS, ", ");
	}
}
