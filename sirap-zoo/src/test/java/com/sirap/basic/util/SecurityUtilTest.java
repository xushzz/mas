package com.sirap.basic.util;

import java.security.SecureRandom;
import java.security.Security;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;

public class SecurityUtilTest {
	
	@Test
	public void castle() throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        // SHA1PRNG随机数算法
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG");
        rng.setSeed(21);
         
        // 生成随机数
        int numberToGenerate = 9;
        byte randNumbers[] = new byte[numberToGenerate];
        rng.nextBytes(randNumbers);
         
        // 打印随机数
        for (int j = 0; j < numberToGenerate; j++) {
            C.pl(randNumbers[j]);
        }
        D.ts(rng.nextInt(100));
        D.ts(rng.nextInt(100));
        D.ts(rng.nextInt(100));
	}
	
	public void sirap() {
		String sa = "james";
		String passcode = "Obamacare";
		C.pl(TrumpUtil.encodeBySIRAP(sa, passcode));
	}
	
	public void hex2byte() {
//		String va = "f2";
//		String maxIntBin = StrUtil.repeat('1', 32);
//		C.pl(maxIntBin);
//		Integer maxInt = Integer.parseInt("01111111111111111111111111111111", 2);
//		C.pl(maxInt);
//		C.pl(-114 & 0xFF);
//		C.pl(-840 & 0xFF);
//		C.pl(Integer.toHexString(-840 & 0xFF));
//		C.pl(Integer.toHexString(184));
		String hex = "F4";
		int b = Integer.parseInt(hex, 16);
		C.pl(Integer.toBinaryString(b));
		C.pl(b);
		C.pl((byte)b);
		int a = 0xff & -12;
		C.pl(Integer.toHexString(a));
		String bin = Integer.toBinaryString(-840);
		String b255 = Integer.toBinaryString(Integer.MAX_VALUE);
		C.pl(bin);
		C.pl(b255);
		C.pl(StrUtil.extendLeftward(b255, 32, "0"));
//		//C.pl(StrUtil.hexCharsToByte(va));
//		byte[] bs = {64};
//		C.pl(StrUtil.bytesToHexString(bs));
	}
	public void trump() {
		String passcode = "roosevelt";
		String text = "jackdawson";
		String v1 = TrumpUtil.encodeBySIRAP(text, passcode);
		C.pl(v1);
		String v2 = TrumpUtil.encodeBySIRAP("vivianfork", passcode);
		C.pl(v2);
		C.pl(TrumpUtil.decodeBySIRAP(v1 + v2, passcode));
	}
	
	public void some() {
		String s1 = "W3siU19teV9wbW9kZSI6Im15c3FsZCIsIlNfbXlfZ3BhcmEiOiJhIiwiU19teV9ndmFsdWUiOiJzIiwiU19teV9ncGRpc3BsYXkiOiJkIiwiU19teV9jcGFyYSI6ImYiLCJTX215X2N2YWx1ZSI6ImcifSx7IlNfbXlfcG1vZGUiOiJteXNxbGQiLCJTX215X2dwYXJhIjoicSIsIlNfbXlfZ3ZhbHVlIjoidyIsIlNfbXlfZ3BkaXNwbGF5IjoiciIsIlNfbXlfY3BhcmEiOiJ3IiwiU19teV9jdmFsdWUiOiJ0In0seyJTX215X3Btb2RlIjoibXlzcWxkIiwiU19teV9ncGFyYSI6InoiLCJTX215X2d2YWx1ZSI6IngiLCJTX215X2dwZGlzcGxheSI6ImMiLCJTX215X2NwYXJhIjoidiIsIlNfbXlfY3ZhbHVlIjoiYiJ9XQ==";
		D.pl(XCodeUtil.isAbsolutelyNonBase64Encoded(s1));
		String s2 = XCodeUtil.fromBase64(s1);
		D.sink(s2);
		D.sink(XCodeUtil.toBase64(s2));
	}
	
//	@Test
	public void base64() {
//		C.pl(CodeUtil.toBase64(null));
//		C.pl(CodeUtil.toBase64(s1));
		D.sink(XCodeUtil.toBase64("gavin belson"));
		D.sink(XCodeUtil.fromBase64("Z2F2aW4gYmVsc29u"));
//		D.sink(CodeUtil.fromBase64("not a base 64 string"));
		D.sink(XCodeUtil.fromBase64("R2F2aW4gQmVsc29uIOe7r+mXu+e7r+mXu+acjeWKoQ=="));
//		
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("abc==="));
		D.pl(XCodeUtil.isAbsolutelyNonBase64Encoded("++acjeWKoQ=="));
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("=="));
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("=aCACSC="));
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("CAV{}"));
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("CAV"));
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("C/A+V"));
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("[sdsd]"));
	}
}
