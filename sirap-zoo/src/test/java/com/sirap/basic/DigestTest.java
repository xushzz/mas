package com.sirap.basic;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import com.sirap.basic.tool.C;

public class DigestTest {
	
	@Test
	public void list() {
		C.pl("-------列出加密服务提供者-----");  
        Provider[] pro = Security.getProviders();  
        for (Provider p : pro) {  
            C.pl("Provider:" + p.getName() + " - version:" + p.getVersion());  
            C.pl("\t\t" + p.getInfo());  
        }  
        C.pl("");  
        C.pl("-------列出系统支持的消息摘要算法：");  
        for (String s : Security.getAlgorithms("MessageDigest")) {  
            C.pl(s);  
        }  
        C.pl("-------列出系统支持的生成公钥和私钥对的算法：");  
        for (String s : Security.getAlgorithms("KeyPairGenerator")) {  
            C.pl(s);  
        }  
	}
}
