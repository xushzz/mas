package com.sirap.basic.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.D;

public class HttpUtil {
	public static final String URL_AKA10_REPO = "http://www.aka10.com/finals";
	public static final String KEY_BALL = "ball";
	public static final String KEY_TITLE = "title";

	public static String postToAka10(String content) {
		return doPost(URL_AKA10_REPO, "ball", content);
	}
	public static String postToAka10(List<Object> items) {
		return null;
	}
	public static String sendTafs(List<?> items) {
		return sendTafs(items, URL_AKA10_REPO, null);
	}
	
	public static String sendTafs(List<?> items, String url, String title) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = null;
        try {
    		ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("utf-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);  
            D.list(items);
            for(Object obj : items) {
            	if(obj instanceof File) {
            		File file = (File)obj;
            		builder.addBinaryBody(file.getName(), file);
            	} else if(obj instanceof MexFile) {
            		File file = ((MexFile)obj).getFile();
            		builder.addBinaryBody(file.getName(), file);
            	} else {
            		builder.addTextBody(KEY_BALL, obj + "", contentType);
            	}
            }
            
            builder.addTextBody("title", title, contentType);
            
            httpPost.setEntity(builder.build());
            response = httpclient.execute(httpPost);
            return EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(httpclient);
            HttpClientUtils.closeQuietly(response);
        }
        return result;
    }
	
	public static String doPost(String url, String key, String value) {  
        String result = "";
        // 创建HttpPost对象  
        HttpPost perry = new HttpPost(url);  
  
        List<NameValuePair> params = new ArrayList<NameValuePair>();  
        params.add(new BasicNameValuePair(key, value));  
        CloseableHttpResponse httpResponse = null;  
          
        try {  
        	perry.setEntity(new UrlEncodedFormEntity(params, Konstants.CODE_UTF8));  
            CloseableHttpClient httpclient = HttpClients.createDefault();  
            httpResponse = httpclient.execute(perry);  
            D.pl(httpResponse);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {  
                HttpEntity httpEntity = httpResponse.getEntity();  
                result = EntityUtils.toString(httpEntity);
            }  
        } catch (Exception ex) {  
            throw new MexException(ex);
        } finally{  
        	try {
				httpResponse.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
        }
        
        return result;
    }
	
	public void doPost(String url, List<Object> items) {
		
	}

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
					String timestr = DateUtil.convertLongToDateStr(Long.parseLong(obj + ""), DateUtil.HOUR_Min_Sec_Milli_AM_WEEK_DATE);
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
				Object obj = ObjectUtil.execute(HttpUtil.class, met, clazzArr, request);
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
