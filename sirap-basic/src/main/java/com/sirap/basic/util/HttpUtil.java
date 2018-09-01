package com.sirap.basic.util;

public class HttpUtil {
	
	public static boolean isHttp(String source) {
		String regex = "https?://[\\S]{3,}";
		return StrUtil.isRegexMatched(regex, source);
	}
	
	/***
	 * https://imgix.ranker.com/balconet-all-people-photo-u2?w=650&q=50&fm=jpg&fit
	 * https://imgix.ranker.com/balconet-all-people-photo-u2.jpg?w=650&q=50&fm=jpg&fit
	 * @param urlstring
	 * @return
	 */
	public static String prettyFilenameOfUrl(String urlstring) {
		String temp = urlstring.replaceAll("\\?.+", "");
		temp = temp.replaceAll(".+/", "");
		temp = XCodeUtil.urlDecodeUTF8(temp);
		temp = FileUtil.generateLegalFileNameBySpace(temp);
		if(temp.isEmpty()) {
			temp = RandomUtil.name();
		}
		return temp;
	}

	public static String extensionOfUrl(String urlstring) {
		String temp = urlstring.replaceAll("\\?.+", "");
		temp = temp.replaceAll(".+/", "");
		if(!temp.contains(".")) {
			return null;
		}
		temp = temp.replaceAll(".*\\.", "");
		return temp;
	}
}
