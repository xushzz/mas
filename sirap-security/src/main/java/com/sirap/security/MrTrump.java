package com.sirap.security;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class MrTrump {
	private static final String CODE_UTF8 = "UTF-8";
	public static String encodeBySIRAP(String plainText, String passcode) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(passcode.getBytes(CODE_UTF8)));
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));

			byte[] result = cipher.doFinal(plainText.getBytes(CODE_UTF8));
			return parseByte2HexStr(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String decodeBySIRAP(String encodedText, String passcode) {
		return decodeBySIRAP(encodedText, passcode, false);
	}
	
	public static String decodeBySIRAP(String encodedText, String passcode, boolean throwException) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(passcode.getBytes(CODE_UTF8)));
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));

			byte[] temp = parseHexStr2Byte(encodedText);
			byte[] result = cipher.doFinal(temp);
			return new String(result, CODE_UTF8);
		} catch (Exception ex) {
			if(throwException) {
				String msg = "can't decode text: " + displaySome(encodedText, 49);
				throw new RuntimeException(msg + ", msg: " + ex.getMessage());
			}
		}
		
		return null;
	}

	private static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	private static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	private static String displaySome(String source, int len) {
		int size = source.length();
		if(size <= len) {
			return source;
		}
		
		String temp = source.substring(0, len);
		temp += "..., " + len + " chars out of " + size;
		
		return temp;
	}

}
