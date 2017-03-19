package com.sirap.basic.util;

import java.io.FileInputStream;
import java.security.MessageDigest;

import com.sirap.basic.component.Konstants;

public class SecurityUtil {

	public static String digest(String source, String algo) {
		try {
			byte[] btInput = source.getBytes(Konstants.CODE_UTF8);
			MessageDigest roy = MessageDigest.getInstance(algo);
			roy.update(btInput);
			byte[] bytes = roy.digest();
			
			String result = StrUtil.bytesToHexString(bytes);
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String md5(String source) {
		return digest(source, "MD5");
	}
	
	@SuppressWarnings("resource")
	public static String digestFile(String filepath, String algo) {
		FileInputStream fis = null;

	      try {
	        MessageDigest roy = MessageDigest.getInstance(algo);
	        fis = new FileInputStream(filepath);
	        byte[] buffer = new byte[2048];
	        int length = -1;
	        while ((length = fis.read(buffer)) != -1) {
	        	roy.update(buffer, 0, length);
	        }
	        byte[] bytes = roy.digest();
	        
	        String result = StrUtil.bytesToHexString(bytes);
	        
			return result;
	      } catch (Exception ex) {
	        ex.printStackTrace();
	        return null;
	      }
	}
}
