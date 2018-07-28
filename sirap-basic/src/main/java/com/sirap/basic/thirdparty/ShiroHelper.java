package com.sirap.basic.thirdparty;

import org.apache.shiro.crypto.hash.SimpleHash;

public class ShiroHelper {
	
	public static String md5(Object source) {
		return md5(source, 1);
	}

	public static String md5(Object source, int hashIterations) {
		return md5(source, (Object)null, hashIterations);
	}

	public static String md5(Object source, Object salt) {
		return md5(source, salt, 1);
	}

	public static String md5(Object source, Object salt, int hashIterations) {
		return hash("MD5", source, salt, hashIterations);
	}
	
	public static String hash(String algorithmName, Object source, Object salt, int hashIterations) {
		SimpleHash joe = new SimpleHash(algorithmName, source, salt, hashIterations);
		//D.pla(algorithmName, source, salt, hashIterations);

		return joe.toString();
	}
}
