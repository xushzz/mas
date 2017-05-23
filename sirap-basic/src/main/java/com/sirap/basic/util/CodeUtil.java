package com.sirap.basic.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;

public class CodeUtil {

	public static final String REGEX_HEX = "[0-9A-F]{2}";
	public static final String REGEX_HEX_TAKE = "(" + REGEX_HEX + ")";
	
	public static Map<Integer, String> ASCII_INFO = new HashMap<>();
	static {
		ASCII_INFO.put(0, "NUL(null)");
		ASCII_INFO.put(1, "SOH(start of headling)");
		ASCII_INFO.put(2, "STX(start of text)");
		ASCII_INFO.put(3, "ETX(end of text)");
		ASCII_INFO.put(4, "EOT(end of transmission)");
		ASCII_INFO.put(5, "ENQ(enquiry)");
		ASCII_INFO.put(6, "ACK(acknowledge)");
		ASCII_INFO.put(7, "BEL(bell)");
		ASCII_INFO.put(8, "BS(backspace)");
		ASCII_INFO.put(9, "HT(horizontal tab)");
		ASCII_INFO.put(10, "LF(NL line feed, new line)");
		ASCII_INFO.put(11, "VT(vertical tab)");
		ASCII_INFO.put(12, "FF(NP form feed, new page)");
		ASCII_INFO.put(13, "CR(carriage return)");
		ASCII_INFO.put(14, "SO(shift out)");
		ASCII_INFO.put(15, "SI(shift in)");
		ASCII_INFO.put(16, "DLE(data link escape)");
		ASCII_INFO.put(17, "DC1(device control 1)");
		ASCII_INFO.put(18, "DC2(device control 2)");
		ASCII_INFO.put(19, "DC3(device control 3)");
		ASCII_INFO.put(20, "DC4(device control 4)");
		ASCII_INFO.put(21, "NAK(negative acknowledge)");
		ASCII_INFO.put(22, "SYN(synchronous idle)");
		ASCII_INFO.put(23, "ETB(end of trans. block)");
		ASCII_INFO.put(24, "CAN(cancel)");
		ASCII_INFO.put(25, "EM(end of medium)");
		ASCII_INFO.put(26, "SUB(substitute)");
		ASCII_INFO.put(27, "ESC(escape)");
		ASCII_INFO.put(28, "FS(file separator)");
		ASCII_INFO.put(29, "GS(group separator)");
		ASCII_INFO.put(30, "RS(record separator)");
		ASCII_INFO.put(31, "US(unit separator)");
		ASCII_INFO.put(32, "(space)");
		ASCII_INFO.put(127, "DEL(delete)");
		ASCII_INFO.put(65, "Alpha");
		ASCII_INFO.put(97, "Alpha");
		ASCII_INFO.put(66, "Bravo");
		ASCII_INFO.put(98, "Bravo");
		ASCII_INFO.put(67, "Charlie");
		ASCII_INFO.put(99, "Charlie");
		ASCII_INFO.put(68, "Delta");
		ASCII_INFO.put(100, "Delta");
		ASCII_INFO.put(69, "Echo");
		ASCII_INFO.put(101, "Echo");
		ASCII_INFO.put(70, "Foxtrot");
		ASCII_INFO.put(102, "Foxtrot");
		ASCII_INFO.put(71, "Golf");
		ASCII_INFO.put(103, "Golf");
		ASCII_INFO.put(72, "Hotel");
		ASCII_INFO.put(104, "Hotel");
		ASCII_INFO.put(73, "India");
		ASCII_INFO.put(105, "India");
		ASCII_INFO.put(74, "Juliet");
		ASCII_INFO.put(106, "Juliet");
		ASCII_INFO.put(75, "Kilo");
		ASCII_INFO.put(107, "Kilo");
		ASCII_INFO.put(76, "Lima");
		ASCII_INFO.put(108, "Lima");
		ASCII_INFO.put(77, "Mike");
		ASCII_INFO.put(109, "Mike");
		ASCII_INFO.put(78, "November");
		ASCII_INFO.put(110, "November");
		ASCII_INFO.put(79, "Oscar");
		ASCII_INFO.put(111, "Oscar");
		ASCII_INFO.put(80, "Papa");
		ASCII_INFO.put(112, "Papa");
		ASCII_INFO.put(81, "Quebec");
		ASCII_INFO.put(113, "Quebec");
		ASCII_INFO.put(82, "Romeo");
		ASCII_INFO.put(114, "Romeo");
		ASCII_INFO.put(83, "Sierra");
		ASCII_INFO.put(115, "Sierra");
		ASCII_INFO.put(84, "Tango");
		ASCII_INFO.put(116, "Tango");
		ASCII_INFO.put(85, "Uniform");
		ASCII_INFO.put(117, "Uniform");
		ASCII_INFO.put(86, "Victor");
		ASCII_INFO.put(118, "Victor");
		ASCII_INFO.put(87, "Whiskey");
		ASCII_INFO.put(119, "Whiskey");
		ASCII_INFO.put(88, "X-ray");
		ASCII_INFO.put(120, "X-ray");
		ASCII_INFO.put(89, "Yankee");
		ASCII_INFO.put(121, "Yankee");
		ASCII_INFO.put(90, "Zulu");
		ASCII_INFO.put(122, "Zulu");
	}
	
	public static String decodeHexChars(String fourOrSixHex, String code) throws MexException {
		byte[] bs = null;
//		if(StrUtil.equals(code, Konstants.CODE_GBK) || StrUtil.equals(code, Konstants.CODE_UNICODE)) {
			
		if(StrUtil.equals(code, Konstants.CODE_UTF8)) {
			String regex = REGEX_HEX_TAKE + REGEX_HEX_TAKE + REGEX_HEX_TAKE;
			String[] params = StrUtil.parseParams(regex, fourOrSixHex);
			if(params == null) {
				throw new MexException("invalid source " + fourOrSixHex + ", " + code);
			}
			int len = params.length;
			bs = new byte[len];
			for(int i = 0; i < len; i++) {
				String hex = params[i];
				bs[i] = StrUtil.hexCharsToByte(hex);
			}
		} else {
			String regex = REGEX_HEX_TAKE + REGEX_HEX_TAKE;
			String[] params = StrUtil.parseParams(regex, fourOrSixHex);
			if(params == null) {
				throw new MexException("invalid source " + fourOrSixHex + ", " + code);
			}
			int len = params.length;
			bs = new byte[len];
			for(int i = 0; i < len; i++) {
				String hex = params[i];
				bs[i] = StrUtil.hexCharsToByte(hex);
			}
		}
		
		try {
			String value = new String(bs, code);
			return value;
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new MexException(ex.getMessage());
		}
	}
	
	public static String encode2HexChars(char charr, String charset) throws MexException {
		return encode2HexChars(charr, charset, false);
	}
	
	public static String encode2HexChars(char charr, String code, boolean ignoreAscii) throws MexException {
		try {
			boolean isUnicode = StrUtil.equals(code, Konstants.CODE_UNICODE);
			String temp = charr + "";
			byte[] bs = temp.getBytes(code);
			StringBuffer sb = new StringBuffer();
			
			if(ignoreAscii) {
				if(bs.length == 1) {
					return temp;
				} else if (isUnicode && bs[2] == 0) {
					return temp;
				}
			}
			
			String hex = StrUtil.bytesToHexString(bs);
			if(isUnicode) {
				hex = hex.substring(4);
			}
			sb.append(hex);
			
			String value = sb.toString().toUpperCase();
			value = "\\u" + value;
			return value;
		} catch (UnsupportedOperationException ex) {
			C.pl("UnsupportedOperationException>> char: "+ charr + ", code: " + code);
			return Konstants.SHITED_FACE;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException("char: "+ charr + ", code: " + code + ", " + ex.getMessage());
		}
	}
	
	/***
	 * 18:40:21 $ cd 万A
		Unicode: \u4E07A
		Unicode: \u4E07\u0041
		UTF-8  : #E4B887#41
		GBK    : \gCDF2\g41
	 * @param source
	 * @return
	 */
	public static String replaceHexChars(String source, String code) {
		if(source == null) {
			return null;
		}
		
		String temp = source;
		String regex = null;
		if (StrUtil.equals(code, Konstants.CODE_UTF8)) {
			regex = "\\\\u([0-9A-F]{6})";
		} else {
			regex = "\\\\u([0-9A-F]{4})";
		}
		
//		if(StrUtil.equals(code, Konstants.CODE_GBK) || StrUtil.equals(code, Konstants.CODE_UNICODE)) {
		
		Matcher mc = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(temp);
		while(mc.find()) {
			String tempChar = mc.group(1);
			String value = decodeHexChars(tempChar, code);
			temp = temp.replace(mc.group(), value);
		}
		
		regex = "\\\\u([0-9A-F]{2})";
		mc = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(temp);
		while(mc.find()) {
			String tempChar = mc.group(1);
			String value = (char)Integer.parseInt(tempChar, 16) + "";
			temp = temp.replace(mc.group(), value);
		}
		
		
		return temp;
	}
	


	/***
	 * RFC2045: The encoded output stream must be represented in lines of no more than 76 characters each
	 * @param source
	 * @return
	 */
	public static String toBase64(String source) {
		XXXUtil.nullCheck(source, "source");
		
		try {
			byte[] data = source.getBytes(Konstants.CODE_UTF8);
			BASE64Encoder jack = new BASE64Encoder();
			String result = jack.encode(data);
			result = result.replaceAll("\r\n","");
			
			return result;
		} catch (Exception ex) {
			throw new MexException(ex.getMessage());
		}
	}

	public static String fromBase64(String source) {
		return fromBase64(source, Konstants.CODE_UTF8);
	}
	
	public static String fromBase64(String source, String charset) {
		XXXUtil.nullCheck(source, "source");
		
		if(isAbsolutelyNonBase64Encoded(source)) {
			throw new MexException("String absolutely non BASE64-based [" + source + "]");
		}
		
		BASE64Decoder  jack = new BASE64Decoder();
		byte[] data = null;
		
		try {
			data = jack.decodeBuffer(source);
			String value = new String(data, charset);
			
			return value;
		} catch (IOException ex) {
			throw new MexException(ex);
		}
	}
	
	/***
	 * 1) contains: a-z A-Z 0-9 + / =
	 * 2) = can only be the end of a string, and max twice occurrence.
	 * @return
	 */
	public static boolean isAbsolutelyNonBase64Encoded(String str) {
		String regex = "[+/0-9a-zA-Z]+[=]{0,2}";
		boolean isMatched = StrUtil.isRegexMatched(regex, str);

		return !isMatched;
	}
}
