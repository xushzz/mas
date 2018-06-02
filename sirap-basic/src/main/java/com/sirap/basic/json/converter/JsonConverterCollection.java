package com.sirap.basic.json.converter;

import java.util.Collection;

import com.sirap.basic.json.JsonConvertManager;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("rawtypes")
public class JsonConverterCollection extends JsonConverter<Collection> {

	@Override
	public String toJson(Collection ant) {
		StringBuffer sb = StrUtil.sb();
		sb.append("[");
		int count = 0;
		for(Object obj : ant) {
			if(count != 0) {
				sb.append(",");
			}
			count++;
			sb.append(JsonConvertManager.g().toJson(obj));
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String toPrettyJson(Collection ant, int depth) {
		StringBuffer sb = StrUtil.sb();
		sb.append("[");
		sb.append("\n");
		int count = 0;
		String dent = StrUtil.spaces((depth + 1) * DENT);
		for(Object obj : ant) {
			sb.append(dent);
			boolean isNotLastElement = count != ant.size() - 1;
			sb.append(JsonConvertManager.g(true).toJson(obj, depth + 1));
			if(isNotLastElement) {
				sb.append(", ");
			}
			sb.append("\n");
			count++;
		}
		sb.append(StrUtil.spaces((depth) * DENT));
		sb.append("]");
		return sb.toString();
	}

}
