package com.sirap.basic.json.converter;

import com.sirap.basic.json.JsonConvertManager;
import com.sirap.basic.util.StrUtil;

public class JsonConverterObjectArray extends JsonConverter<Object[]> {

	@Override
	public String toJson(Object[] ant) {
		StringBuffer sb = StrUtil.sb();
		sb.append("[");
		int count = 0;
		for(Object obj : ant) {
			if(count != 0) {
				sb.append(",");
			}
			count++;
			sb.append(JsonConvertManager.toJson(obj));
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String toPrettyJson(Object[] ant, int depth) {
		StringBuffer sb = StrUtil.sb();
		sb.append("[");
		sb.append("\n");
		int count = 0;
		String dent = StrUtil.spaces((depth + 1) * DENT);
		for(Object obj : ant) {
			sb.append(dent);
			boolean isNotLastElement = count != ant.length - 1;
			sb.append(JsonConvertManager.toPrettyJson(obj, depth + 1));
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
