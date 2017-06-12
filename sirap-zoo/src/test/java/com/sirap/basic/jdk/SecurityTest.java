package com.sirap.basic.jdk;

import java.security.Provider;
import java.security.Security;
import java.util.List;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.SecurityUtil;
import com.sirap.basic.util.StrUtil;

public class SecurityTest {

	@Test
	public void sha1() {
		C.pl(SecurityUtil.digest("leyi", "sha1"));
	}
	
	public void digest() {
		String algos = "SHA-1,SHA-224,SHA-256,MD2,SHA,SHA-512,MD5";
		List<String> items = StrUtil.split(algos);
		String va = "abc";
		va = "D:\\M2REPO\\dom4j\\dom4j\\1.6.1\\dom4j-1.6.1.jar";
//		D.pl(SecurityUtil.SHA1(va));
		for(String algo : items) {
//			String digest = SecurityUtil.digest(va, algo);
			String digest = SecurityUtil.digestFile(va, algo);
			D.pl(va + "+" + algo, digest);
		}
	}
	
	public void verify() {
		System.out.println("-------列出加密服务提供者-----");
		Provider[] pro = Security.getProviders();
		for (Provider p : pro) {
			System.out.println("Provider:" + p.getName() + " - version:" + p.getVersion());
			System.out.println(p.getInfo());
		}
		System.out.println("");
		System.out.println("-------列出系统支持的消息摘要算法：");
		for (String s : Security.getAlgorithms("MessageDigest")) {
			System.out.print(s);
			System.out.print(",");
		}
		System.out.println("-------列出系统支持的生成公钥和私钥对的算法：");
		for (String s : Security.getAlgorithms("KeyPairGenerator")) {
			System.out.println(s);
		}
	}
}
