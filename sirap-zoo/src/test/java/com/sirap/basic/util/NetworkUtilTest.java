package com.sirap.basic.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.NetworkUtil;

public class NetworkUtilTest {
	
	@Test
	public void legalHostname() {
		C.pl(NetworkUtil.isBadHostname("abc?"));
		C.pl(NetworkUtil.isBadHostname("abc "));
		C.pl(NetworkUtil.isBadHostname("abc\\"));
		C.pl(NetworkUtil.isBadHostname("abc*"));
		C.pl(NetworkUtil.isBadHostname("abc?"));
		
	}
	//@Test
	public void testMacAddress() throws Exception {
		//C.pl(NetworkUtil.getLocalMacAddress());
		C.pl(NetworkUtil.getLocalMacAddress());
	}
	
	@Test
	public void getHostByName() {
//		C.pl(NetworkUtil.getHostByName("10.198.1.12"));
		C.pl(NetworkUtil.getHostByName("LIUHU?AN-PC"));
//		C.pl(NetworkUtil.getHostByName("LIUHUAN-PC"));
//		C.pl(NetworkUtil.getLocalhost());
//		C.pl(NetworkUtil.getLocalhostIp());
//		C.pl(NetworkUtil.getLocalhostName());
	}
	
	//@Test
	public void brent() {
		String str = "172.20.223.120";
        String[] ipStr = str.split("\\.");
        byte[] ipBuf = new byte[4];
        for(int i = 0; i < 4; i++){
            ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
        }

		try {
			InetAddress ia = InetAddress.getByAddress("hao.wen",ipBuf);
//			C.pl(ia.getagetgetAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
