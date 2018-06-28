package com.sirap.basic.tool;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.basic.util.StrUtil;

public class D2 {
	public static void pm(URLConnection conn) {
		D.pjsp(map(conn));
	}
	
	public static Map<String, Object> map(URLConnection conn) {
		Map<String, Object> methodAndResult = new LinkedHashMap<>();

		List<String> mets = Lists.newArrayList();
		mets.add("== URLConnection ==");
		mets.add("getHeaderFields");
		mets.add("getContent");
		mets.add("getPermission");
		mets.add("getContentEncoding");
		mets.add("getContentType");
		mets.add("getURL");
		mets.add("getAllowUserInteraction");
		mets.add("getDefaultUseCaches");
		mets.add("getDoInput");
		mets.add("getDoOutput");
		mets.add("getUseCaches");
		mets.add("getConnectTimeout");
		mets.add("getContentLength");
		mets.add("getReadTimeout");
		mets.add("getDate");
		mets.add("getExpiration");
		mets.add("getIfModifiedSince");
		mets.add("getLastModified");
		mets.add("getDefaultAllowUserInteraction");
		mets.add("getFileNameMap");
		
		for(String met : mets) {
			if(StrUtil.isRegexFound("^[a-z]", met)) {
				Object obj = ObjectUtil.execute(conn, met);
				methodAndResult.put(met, obj);
			} else {
				methodAndResult.put(met, StrUtil.repeat('=', 30));
			}
		}
		
		if(HttpURLConnection.class.isInstance(conn)) {
			mets.clear();
			mets.add("== HttpURLConnection ==");
			mets.add("getPermission");
			mets.add("getRequestMethod");
			mets.add("getResponseMessage");
			mets.add("getInstanceFollowRedirects");
			mets.add("getResponseCode");
			mets.add("getFollowRedirects");
			
			for(String met : mets) {
				if(StrUtil.isRegexFound("^[a-z]", met)) {
					Object obj = ObjectUtil.execute(conn, met);
					methodAndResult.put(met, obj);
				} else {
					methodAndResult.put(met, StrUtil.repeat('=', 30));
				}
			}
		}

		return methodAndResult;
	}
	
	/***
	 * file:///D:/Gitpro/SIRAP/maspri/sirap-zoo/.gitignore
	 * https://blog.csdn.net/z0157/article/details/47863253
	 * @param url
	 */
	public static void pm(URL url) {
		D.pjsp(map(url));
	}
	
	public static Map<String, Object> map(URL url) {
		Map<String, Object> methodAndResult = new LinkedHashMap<>();

		List<String> mets = Lists.newArrayList();
		mets.add("== URL ==");
		mets.add("getAuthority");
		mets.add("getFile");
		mets.add("getHost");
		mets.add("getPath");
		mets.add("getProtocol");
		mets.add("getQuery");
		mets.add("getRef");
		mets.add("getUserInfo");
		mets.add("toExternalForm");
		mets.add("toURI");
		mets.add("getContent");
		mets.add("getDefaultPort");
		mets.add("getPort");
		mets.add("hashCode");
		
		for(String met : mets) {
			if(StrUtil.isRegexFound("^[a-z]", met)) {
				Object obj = ObjectUtil.execute(url, met);
				methodAndResult.put(met, obj);
			} else {
				methodAndResult.put(met, StrUtil.repeat('=', 30));
			}
		}

		return methodAndResult;
	}
}
