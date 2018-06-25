package com.sirap.basic.util;

import java.util.Arrays;

import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.thirdparty.TrumpHelper;
import com.sirap.basic.thirdparty.http.HttpHelper;
import com.sirap.basic.tool.D;

public class WeixinUtil {

	public static final String APPID = "wxfb0d4e7b7d891f94";
	public static final String APPSECRET = "99baea22d01d0b8a094f2a39882f" + TrumpHelper.decodeBySIRAP("EA8270591FBFDF013F591412A930EA46", "sick");
	public static final String API_ACCESSTOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";
	
	public static boolean checkSignature(String token, String timestamp, String nonce, String signature) {
		String[] arr = {token, timestamp, nonce};
		Arrays.sort(arr);
		StringBuffer sb = StrUtil.sb();
		for(String item : arr) {
			sb.append(item);
		}
		
		String sha1 = SecurityUtil.sha1(sb.toString());
		D.pl(sha1);
		D.pl(signature);
		
		return StrUtil.equals(signature, sha1);
	}
	
	public static String createSignature() {
		return null;
	}
	
	public static String accessToken() {
		String url = StrUtil.occupy(API_ACCESSTOKEN, APPID, APPSECRET);
		String result = HttpHelper.doGet(url);
        String value = JsonUtil.getFirstStringValueByKey(result, "access_token");
        
        return value;
	}
}
