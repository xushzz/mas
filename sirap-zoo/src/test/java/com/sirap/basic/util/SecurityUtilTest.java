package com.sirap.basic.util;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.security.MrTrump;

public class SecurityUtilTest {
	
	@Test
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
		String v1 = MrTrump.encodeBySIRAP(text, passcode);
		C.pl(v1);
		String v2 = MrTrump.encodeBySIRAP("vivianfork", passcode);
		C.pl(v2);
		C.pl(MrTrump.decodeBySIRAP(v1 + v2, passcode));
	}
	
	public void some() {
		String s1 = "W3siU19teV9wbW9kZSI6Im15c3FsZCIsIlNfbXlfZ3BhcmEiOiJhIiwiU19teV9ndmFsdWUiOiJzIiwiU19teV9ncGRpc3BsYXkiOiJkIiwiU19teV9jcGFyYSI6ImYiLCJTX215X2N2YWx1ZSI6ImcifSx7IlNfbXlfcG1vZGUiOiJteXNxbGQiLCJTX215X2dwYXJhIjoicSIsIlNfbXlfZ3ZhbHVlIjoidyIsIlNfbXlfZ3BkaXNwbGF5IjoiciIsIlNfbXlfY3BhcmEiOiJ3IiwiU19teV9jdmFsdWUiOiJ0In0seyJTX215X3Btb2RlIjoibXlzcWxkIiwiU19teV9ncGFyYSI6InoiLCJTX215X2d2YWx1ZSI6IngiLCJTX215X2dwZGlzcGxheSI6ImMiLCJTX215X2NwYXJhIjoidiIsIlNfbXlfY3ZhbHVlIjoiYiJ9XQ==";
		D.pl(CodeUtil.isAbsolutelyNonBase64Encoded(s1));
		String s2 = CodeUtil.fromBase64(s1);
		D.sink(s2);
		D.sink(CodeUtil.toBase64(s2));
	}
	
//	@Test
	public void base64() {
//		C.pl(CodeUtil.toBase64(null));
//		C.pl(CodeUtil.toBase64(s1));
		D.sink(CodeUtil.toBase64("gavin belson"));
		D.sink(CodeUtil.fromBase64("Z2F2aW4gYmVsc29u"));
//		D.sink(CodeUtil.fromBase64("not a base 64 string"));
		D.sink(CodeUtil.fromBase64("R2F2aW4gQmVsc29uIOe7r+mXu+e7r+mXu+acjeWKoQ=="));
//		
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("abc==="));
		D.pl(CodeUtil.isAbsolutelyNonBase64Encoded("++acjeWKoQ=="));
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("=="));
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("=aCACSC="));
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("CAV{}"));
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("CAV"));
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("C/A+V"));
//		C.pl(CodeUtil.isAbsolutelyNonBase64Encoded("[sdsd]"));
	}
}
