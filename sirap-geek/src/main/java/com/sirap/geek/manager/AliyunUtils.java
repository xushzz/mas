package com.sirap.geek.manager;

import java.util.List;

import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.Sender;

public class AliyunUtils {

	public static final String URL_WORDSEGMENT = "http://nlp.cn-shanghai.aliyuncs.com/nlp/api/wordsegment";
	public static final String ACCESS_KEY_ID = "LTAIgErsTxDa7rv3";
	public static final String ACCESS_KEY_SECRET = "2TGBHaaaP9TMHwQMaFU7TRqPrlX63m";
	
	/***
	 * http://lbs.qq.com/webservice_v1/guide-gcoder.html
	 * https://www.cnblogs.com/haibin-zhang/p/4955880.html
	 * @param location
	 * @param radius
	 * @return
	 */
	public static List<String> general(String words) {
		D.pl(words);
		String postBody = JsonUtil.toJson("text", words);
		String url = URL_WORDSEGMENT + "/general";
		String result = Sender.sendPost(url, postBody, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
		return JsonUtil.getPrettyTextInLines(result);
	}
}
