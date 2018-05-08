package com.sirap.basic.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class XCodeUtil {

	public static final String REGEX_HEX = "[0-9A-F]{2}";
	public static final String REGEX_HEX_TAKE = "(" + REGEX_HEX + ")";
	
	public static String decodeHexChars(String fourOrSixHex, String code) throws MexException {
		List<String> items = StrUtil.findAllMatchedItems(REGEX_HEX, fourOrSixHex);
		
		int size = items.size();
		byte[] bytes = new byte[size];
		for(int k = 0; k < size; k++) {
			int value = Integer.parseInt(items.get(k), 16);
			bytes[k] = (byte)value; 
		}
		
		try {
			String value = new String(bytes, code);
			return value;
		} catch(Exception ex) {
			throw new MexException(ex);
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
	 * @param source \u4E07\u0041
	 * 		  code unicode
	 * @return 万A
	 */
	public static String replaceHexChars(String source, String code) {
		XXXUtil.nullCheck(source, "source");
		XXXUtil.nullCheck(code, "code");
		
		String temp = source;
		int[] digits = {6, 4, 2};
		String late = "\\\\u([0-9A-F]{{0}})";
		for(int k = 0; k < digits.length; k++) {
			String regex = StrUtil.occupy(late, digits[k]);
			Matcher mc = StrUtil.createMatcher(regex, source);
			while(mc.find()) {
				String value = decodeHexChars(mc.group(1), code);
				temp = temp.replace(mc.group(), value);
			}
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
	
	public static String urlParamsEncode(String wholeUrl, String charset) {
		String regex = "([^\\?&]+=)([^\\?&]*)";
		Matcher ma = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(wholeUrl);
		StringBuffer sb = new StringBuffer();
		while(ma.find()) {
			String rest = ma.group(1);
			String value = ma.group(2);
			String replacement = rest + urlEncode(value, charset);
			ma.appendReplacement(sb, replacement);
		}
		
		ma.appendTail(sb);
		
		return sb.toString();
	}
	
	public static String urlEncode(String param, String charset) {
		try {
			String result = URLEncoder.encode(param, charset);
			return result;
		} catch (UnsupportedEncodingException ex) {
			throw new MexException(ex);
		}
	}
	
	public static String urlEncodeUTF8(String param) {
		return urlEncode(param, Konstants.CODE_UTF8);
	}
	
	public static String urlDecode(String source, String charset) {
		try {
			String result = URLDecoder.decode(source, charset);
			return result;
		} catch (UnsupportedEncodingException ex) {
			throw new MexException(ex);
		}
	}
	
	public static String urlDecodeUTF8(String source) {
		return urlDecode(source, Konstants.CODE_UTF8);
	}
	
	public static List<ValuesItem> group(List<String> list, int kPerLine) {
		List<ValuesItem> groups = Lists.newArrayList();
		ValuesItem vi = new ValuesItem();
		for(String item : list) {
			vi.add(item);
			if(vi.size() == kPerLine) {
				groups.add(vi);
				vi = new ValuesItem();
			}
		}
		
		if(vi.isNotEmpty()) {
			groups.add(vi);
		}
		
		return groups;
	}
	

	
	public static List<String> bytesOfString(String txt, String charset) {
		return bytesOfString(txt, charset, true);
	}
	
	public static List<String> bytesOfUTF8String(String txt, boolean toHex) {
		return bytesOfString(txt, Konstants.CODE_UTF8, toHex);
	}
	
	public static List<String> bytesOfString(String txt, String charset, boolean toHex) {
		try(InputStream fis = new ByteArrayInputStream(txt.getBytes(charset))) {
			C.pl("Charset: " + charset);
			List<String> items = bytesOfInputStream(fis, toHex);
			return items;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException(ex);
		}
	}
	
	public static List<String> bytesOfFile(String filepath) {
		return bytesOfFile(filepath, true);
	}
	
	public static List<String> bytesOfFile(String filepath, boolean toHex) {
		try(InputStream fis = new FileInputStream(filepath)) {
			List<String> items = bytesOfInputStream(fis, toHex);
			return items;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException(ex);
		}
	}
	
	public static List<String> bytesOfInputStream(InputStream is, boolean toHex) {
		List<String> items = Lists.newArrayList();
		int next;
		try {
	        while ((next = is.read()) != -1) {
	        	if(toHex) {
	        		String hex = Integer.toHexString(next & 0xFF);
	        		items.add(StrUtil.padLeft(hex, 2));
	        	} else {
	        		String decimal = next + "";
	        		items.add(StrUtil.padLeft(decimal, 3));
	        	}
	        }
	        return items;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException(ex);
		}
	}
	
	public static String bytesToUTF8(String separatedBytes) {
		return bytesToString(separatedBytes, Konstants.CODE_UTF8);
	}
	
	/***
	 * 
	 * @param bytes 12,23,34,45,00
	 * @param filePath
	 */
	public static String bytesToString(String separatedBytes, String charset) {
		List<String> items = StrUtil.splitByRegex(separatedBytes, "[,\\-\\s]+");
		byte[] data = new byte[items.size()];
		for(int i = 0; i < items.size(); i++) {
			String item = items.get(i);
//			D.pl(StrUtil.occupy("[{0}]", item));
			int in = Integer.parseInt(item, 16);
			byte by = 0;
			XXXUtil.checkRange(in, -255, 256);
			if(in > 127) {
				by = (byte)(in - 256);
			} else {
				by = (byte)in;
				if(by != in) {
					D.pl("Uncanny, not equal by and in ", by, in);
				}
			}
			data[i] = by;
		}
		
		try {
			C.pl("Charset: " + charset);
			return new String(data, charset);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException(ex);
		}
	}
}
