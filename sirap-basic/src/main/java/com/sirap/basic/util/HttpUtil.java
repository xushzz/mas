package com.sirap.basic.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sirap.basic.json.MistUtil;
import com.sirap.basic.tool.D;

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

	public static Map<String, String> queryOf(String querystring) {
		Map<String, String> kid = Amaps.newLinkHashMap();	

		List<String> items = StrUtil.split(querystring, "&");
		for(String item : items) {
			int index = item.indexOf("=");
			if(index != -1) {
				String key = item.substring(0, index);
				String value = item.substring(index + 1);
				kid.put(key, value);
			} else {
				kid.put(item, "");
			}
		}
		
		return kid;
	}

	public static String queryOf(Map<String, String> querymap) {
		StringBuffer query = StrUtil.sb();

		Iterator<String> it = querymap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String value = querymap.get(key);
			query.append(key + "=" + value).append("&");
		}
		
		String temp = query.toString().replaceAll("&$", "");
		
		return temp;
	}
	
	public static String tcnOf(String longUrl) {
		return tcnOf(longUrl, "3271760578");
	}
	
	public static String tcnOf(String longUrl, String appKey) {
		String kid = "http://api.t.sina.com.cn/short_url/shorten.json?source={0}&url_long={1}";
		String temp = StrUtil.occupy(kid, appKey, longUrl);
		String response = IOUtil.readString(temp);
		String surl = MistUtil.ofJsonText(response).findStringBy("url_short");
		
		return surl;
	}
	
	
	public static void main(String[] args) {
		String sa = null;
		sa = "https://blog.csdn.net/qq_20065991/article/details/82902220";
		sa = "https://baijiahao.baidu.com/s?id=1621160874039792094&wfr=spider&for=pc#Fleet_restructuring";
//		sa = "http://en.wikipedia.org/wiki/Air_India?id=1621160874039792094&wfr=spider&for=pc#Fleet_restructuring";
		sa = "http://120.79.195.133:1998/wiki/Air_India?i==d=1621160874039792094&wfr=spider&for=pc#Fleet_restructuring";
//		sa = "xfile:///D:/Gitpro/OSChina/todos/high/picker.html?id=1621#nice";
		Object oa = null;
//		sa = "i=16211&ni=60874039792094&wfr=spider&for=pc";
//		oa = urlDetail(sa);
		sa = "http://www.mp.weixin.qq.com/s/CIPosICgva9haqstMDIHag";
		oa = tcnOf(sa);
//		C.pl(sa);
//		Map map = queryOf(sa);
//		D.pjsp(map);
//		C.pl(queryOf(map));
		D.pjsp(oa);
	}
}
