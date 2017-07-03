package com.sirap.farm;

import java.nio.charset.Charset;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;


public class IOUtilTest {
	
	@Test
	public void type() throws Exception {
		String sa = "com.sirap.common.framework.Konfig";
		Object oa = Thread.currentThread().getContextClassLoader().loadClass(sa);
		D.ts(oa);
		
		C.pl(Long.TYPE);
		C.pl(Integer.TYPE);
		C.pl(Short.TYPE);
		C.pl(Byte.TYPE);
	}
	public void read() throws Exception {
		D.pl(Charset.defaultCharset());
		String sa = "K";
		D.pl(sa.getBytes());
		D.pl();
		sa = "曜";
		D.pl(sa.getBytes("GBK"));
		D.pl();
		sa = "开";
		char[] arr = sa.toCharArray();
		D.pl(sa.getBytes());
	}
}
