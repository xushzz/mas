package com.sirap.basic.json.converter;

import java.lang.reflect.Field;

import com.sirap.basic.json.JsonConvertManager;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.util.StrUtil;

public class JsonConverterFields extends JsonConverter<Object> {

	@Override
	public String toJson(Object ant) {
		StringBuffer sb = StrUtil.sb();
		sb.append("{");
		int count = 0;
		Field[] fields= ant.getClass().getDeclaredFields();
		for(Field rock : fields) {
			rock.setAccessible(true);
			Object key = rock.getName();
			Object value = null;
			try {
				value = rock.get(ant);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	public String toPrettyJson(Object ant, int depth) {
		StringBuffer sb = StrUtil.sb();
		sb.append("{");
		sb.append("\n");
		int count = 0;
		String dent = StrUtil.spaces((depth + 1) * DENT);
		Field[] fields = ant.getClass().getDeclaredFields();
		for(Field rock : fields) {
			rock.setAccessible(true);
			Object key = rock.getName();
			Object value = null;
			try {
				value = rock.get(ant);
			} catch (Exception e) {
				e.printStackTrace();
			}
			sb.append(dent);
			sb.append(JsonUtil.quote(key));
			sb.append(":");
			boolean isNotLastElement = count != fields.length - 1;
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
