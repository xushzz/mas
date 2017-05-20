package com.sirap.security;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class MrTrump {
	
	private static final String CHARSET = "UTF-8";
	private static final String ALGO = "AES";
	
	public String encode(String plainText, String passcode) {
		try {
			String algo = ALGO;
			Cipher cipher = Cipher.getInstance(algo);
			cipher.init(Cipher.ENCRYPT_MODE, createKeySpec(passcode, algo));
			byte[] bytes = cipher.doFinal(plainText.getBytes(CHARSET));
			String result = bytes2HexStr(bytes);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String decode(String encodedText, String passcode, boolean throwException) {
		try {
			String algo = ALGO;
			Cipher cipher = Cipher.getInstance(algo);
			cipher.init(Cipher.DECRYPT_MODE, createKeySpec(passcode, algo));
			byte[] bytes = cipher.doFinal(hexStr2Bytes(encodedText));
			String result = new String(bytes, CHARSET);
			
			return result;
		} catch (Exception ex) {
			if(throwException) {
				String msg = "can't decode text: " + displaySome(encodedText, 49);
				throw new RuntimeException(msg + ", msg: " + ex.getMessage());
			}
		}
		
		return null;
	}

	private byte[] hexStr2Bytes(String hexStr) {
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

	private String bytes2HexStr(byte buf[]) {
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

	private String displaySome(String source, int len) {
		int size = source.length();
		if(size <= len) {
			return source;
		}
		
		String temp = source.substring(0, len);
		temp += "..., " + len + " chars out of " + size;
		
		return temp;
	}
	
	private SecretKeySpec createKeySpec(String passcode, String algo) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance(algo);
		SecureRandom donald = SecureRandom.getInstance("SHA1PRNG");
		donald.setSeed(passcode.getBytes(CHARSET));
		kgen.init(128, donald);
		SecretKeySpec mama = new SecretKeySpec(kgen.generateKey().getEncoded(), algo);
		
		return mama;
	}
}
