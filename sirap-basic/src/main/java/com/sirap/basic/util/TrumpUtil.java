package com.sirap.basic.util;

import com.sirap.security.MrTrump;

public class TrumpUtil {
	
	public static String encodeBySIRAP(String plainText, String passcode) {
		return MrTrump.encodeBySIRAP(plainText, passcode);
	}

	public static String decodeBySIRAP(String encodedText, String passcode) {
		return decodeBySIRAP(encodedText, passcode, false);
	}
	
	public static String decodeBySIRAP(String encodedText, String passcode, boolean throwException) {
		return MrTrump.decodeBySIRAP(encodedText, passcode, throwException);
	}

}
