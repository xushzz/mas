package com.sirap.basic.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.exception.MexException;
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
	
	/***
	 * --**1CC6CC87C4B7CB92E86017F91EE62332**,**A96D3FB916661A18093F09516D8A7DF4**-ninja
	 * @param source
	 * @param passcode
	 * @return
	 */
	public static String decodeMixedTextBySIRAP(String mixedText, String passcode) {
		return decodeMixedTextBySIRAP(mixedText, passcode, false);
	}
	
	public static String decodeMixedTextBySIRAP(String mixedText, String passcode, boolean throwException) {
		String regex = "\\*{2}([\\da-z]{3,})\\*{2}";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(mixedText);
		String temp = mixedText;
		while(m.find()) {
			String origin = m.group();
			String stuff = m.group(1);
			int len = stuff.length();
			if(len % 32 == 0) {
				String value = decodeBySIRAP(stuff, passcode);
				temp = temp.replace(origin, value);
			} else {
				throw new MexException("Failed to decode [" + stuff + "]");
			}
		}
		
		return temp;
	}

}
