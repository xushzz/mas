package com.sirap.basic.json.converter;

import java.util.Iterator;
import java.util.Map;

import com.sirap.basic.json.JsonConvertManager;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("rawtypes")
public class JsonConverterMap extends JsonConverter<Map> {
	
	@Override
	public String toJson(Map ant) {
		Map mike = (Map)ant;
		StringBuffer sb = StrUtil.sb();
		sb.append("{");
		Iterator it = mike.keySet().iterator();
		int count = 0;
		while(it.hasNext()) {
			Object key = it.next();
			Object value = mike.get(key);
			if(count != 0) {
				sb.append(",");
			}
			count++;
			sb.append(JsonUtil.quote(key));
			sb.append(":");
			sb.append(JsonConvertManager.g().toJson(value));
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public String toPrettyJson(Map ant, int depth) {
		Map mike = (Map)ant;
		StringBuffer sb = StrUtil.sb();
		sb.append("{");
		sb.append("\n");
		Iterator it = mike.keySet().iterator();
		int count = 0;
		String dent = StrUtil.spaces((depth + 1) * DENT);
		while(it.hasNext()) {
			Object key = it.next();
			Object value = mike.get(key);
			sb.append(dent);
			sb.append(JsonUtil.quote(key));
			sb.append(": ");
			boolean isNotLastElement = count != ant.size() - 1;
			sb.append(JsonConvertManager.g(true).toJson(value, depth + 1));
			if(isNotLastElement) {
				sb.append(", ");
			}
			sb.append("\n");
			count++;
		}
		sb.append(StrUtil.spaces((depth) * DENT));
		sb.append("}");
		return sb.toString();
	}

}
