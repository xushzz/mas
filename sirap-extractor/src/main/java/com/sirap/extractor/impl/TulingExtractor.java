package com.sirap.extractor.impl;

import java.util.Map;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

@SuppressWarnings("rawtypes")
public class TulingExtractor extends Extractor<MexedObject> {
	
	public static final String HOMEPAGE = "http://www.tuling123.com";
	public static final String CODE_SUCCESS = "100000";
	public static final String URL_TEMPLATE = HOMEPAGE + "/openapi/api?key={0}&info={1}";

	public TulingExtractor(String key, String param) {
		String url = StrUtil.occupy(URL_TEMPLATE, key, encodeURLParam(param));
		setUrl(url);
	}

	@Override
	protected void parseContent() {
		Map naive = JsonUtil.toMap(source);
		Object code = naive.get("code");
		if(StrUtil.equals(CODE_SUCCESS, code + "")) {
			mexItem = new MexedObject(naive.get("text"));
		} else {
			mexItem = new MexedObject(source);
		}
	}
}
