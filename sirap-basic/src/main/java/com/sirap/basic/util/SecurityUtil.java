package com.sirap.basic.util;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.algo.SM3Digest;
import com.sirap.basic.component.Konstants;

public class SecurityUtil {

	public static String digest(String source, String algo) {
		if(StrUtil.equals(algo, "sm3")) {
			return sm3(source);
		}
		
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

	public static List<String> digest(List<String> source, String algo) {
		try {
			MessageDigest roy = MessageDigest.getInstance(algo);
			List<String> items = Lists.newArrayList();
			for(String item : source) {
				roy.update(item.getBytes(Konstants.CODE_UTF8));
				items.add(item + ", " + StrUtil.bytesToHexString(roy.digest()));
			}
			return items;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String md5(String source) {
		return digest(source, "MD5");
	}
	
	public static String sm3(String source) {
		byte[] bytes = new byte[32];  
        byte[] msg1 = source.getBytes();  
        SM3Digest sm3 = new SM3Digest();  
        sm3.update(msg1, 0, msg1.length);  
        sm3.doFinal(bytes, 0);
        String result = StrUtil.bytesToHexString(bytes);
        
		return result;
	}
	
	public static String digestFile(String filepath, String algo) {
		MessageDigest roy = null;
		try {
			roy = MessageDigest.getInstance(algo);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try(FileInputStream fis = new FileInputStream(filepath)) {
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
