package com.sirap.extractor.impl;

import java.util.Map;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("rawtypes")
public class TulingExtractor extends Extractor<MexObject> {
	
	public static final String HOMEPAGE = "http://www.tuling123.com";
	public static final String CODE_SUCCESS = "100000";
	public static final String URL_TEMPLATE = HOMEPAGE + "/openapi/api?key={0}&info={1}";

	public TulingExtractor(String key, String param) {
		String url = StrUtil.occupy(URL_TEMPLATE, key, encodeURLParam(param));
		setUrl(url);
	}

	/****
	 * {"code":40004,"text":"亲爱的，当天请求次数已用完。"}
	 */
	@Override
	protected void parse() {
		D.pl("marked in 15:41 2018/10/3");
		Map naive = null;//JsonUtil.toMap(source);
		Object code = naive.get("code");
		if(StrUtil.equals(CODE_SUCCESS, code + "")) {
			item = new MexObject(naive.get("text"));
		} else {
			item = new MexObject(source);
		}
	}
}
