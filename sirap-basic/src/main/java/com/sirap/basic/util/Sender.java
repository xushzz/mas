package com.sirap.basic.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.tool.D;

import sun.misc.BASE64Encoder;

public class Sender {

	public static String MD5Base64(String source, String charset) {
		if(source==null)
			return null;
		String encoderStr = "";
		
		try {
			D.pl(IOUtil.charset());
			byte[] utfByte= source.getBytes(charset);
			MessageDigest mdTemp;
			mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(utfByte);
			byte[] md5Byte = mdTemp.digest();
			BASE64Encoder b64Encoder = new BASE64Encoder();
			encoderStr = b64Encoder.encode(md5Byte);
			
		} catch (Exception e) {
			throw new Error("Failed to generate MD5:" +e.getMessage());
		}
		return encoderStr;
	}
	
	public static String HMACSha1(String data, String key, String charset) {
		String result;
		try {
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(charset), "HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(data.getBytes(charset));
			result = (new BASE64Encoder()).encode(rawHmac);
			
		} catch (Exception e) {
			throw new Error("Failed to generate HMAC:" +e.getMessage());
		}
		return result;
			
	}
	
	public static String toGMTString(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z",Locale.UK);
		df.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));
		return df.format(date);
	}

	public static String sendPost(String url, String body, String ak_id, String ak_secret) {
		return sendPost(url, body, ak_id, ak_secret, Konstants.CODE_UTF8);
	}
	
	public static String sendPost(String url, String body, String ak_id, String ak_secret, String charset) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			String method = "POST";
			String accept = "application/json";
			String content_type = "application/json:charset=utf-8";
			String path = realUrl.getFile();
			String date = toGMTString(new Date());
			String host = realUrl.getHost();
			//1.对body做MD5+BASE64加密
			String bodyMd5 = MD5Base64(body, charset);
			String uuid = UUID.randomUUID().toString();
			String stringToSign = method +"\n" +accept +"\n" + bodyMd5 + "\n" + content_type + "\n" + date +"\n"
					+"x-acs-signature-method:HMAC-SHA1\n"
					+"x-acs-signature-nonce:" + uuid + "\n" 
					+path;
			//2.计算HMAC-SHA1
			String signature = HMACSha1(stringToSign,ak_secret, charset);
			//3。得到authorization header
			String authHeader = "acs "+ak_id+":"+signature;
			//打开和URL直接的连接
			URLConnection conn = realUrl.openConnection();
			//设置通用的请求属性
			conn.setRequestProperty("Accept", accept);
			conn.setRequestProperty("Content-Type", content_type);
			conn.setRequestProperty("Content-MD5", bodyMd5);
			conn.setRequestProperty("Date", date);
			conn.setRequestProperty("Host", host);
			conn.setRequestProperty("Authorization", authHeader);
			conn.setRequestProperty("x-acs-signature-nonce", uuid);
			conn.setRequestProperty("x-acs-signature-method", "HMAC-SHA1");
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			out= new PrintWriter(conn.getOutputStream());
			out.print(body);
			out.flush();
			
			InputStream is;
			HttpURLConnection httpconn = (HttpURLConnection)conn;
			int  rescode=httpconn.getResponseCode();
			if(rescode==200) {
				is=httpconn.getInputStream();
			} else {
				is=httpconn.getErrorStream();
			}
			in = new BufferedReader(new InputStreamReader(is, charset));
			String line;
			while((line=in.readLine())!=null) {
				result += line;
			}
		} catch (Exception ex) {
			XXXUtil.alert(ex);
			ex.printStackTrace();
		}
		finally {
			try {
			if(out!=null)
				out.close();
			if(in!=null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
