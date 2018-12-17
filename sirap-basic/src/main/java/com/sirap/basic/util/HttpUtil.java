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
	public static String filenameByUrl(String urlstring) {
		return filenameByUrl(urlstring, 15, ".xyz");
	}
	
	public static String filenameByUrl(String urlstring, int useMD5WhenLessThanKSize, String useExtensionIfNeeded) {
		String temp = urlstring.replaceAll("\\?.*", "");
		temp = temp.replaceAll(".+/", "");
		if(!StrUtil.isRegexFound("\\.[a-z]+$", temp)) {
			temp += useExtensionIfNeeded;
		}
		temp = XCodeUtil.urlDecodeUTF8(temp);
		temp = FileUtil.generateUrlFriendlyFilename(temp);
		if(temp.isEmpty()) {
			temp = RandomUtil.name();
		}
		
		if(temp.length() < useMD5WhenLessThanKSize) {
			String md5 = SecurityUtil.md5(urlstring);
			String half = md5.substring(0, 10);
			temp = half + "_" + temp;
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
