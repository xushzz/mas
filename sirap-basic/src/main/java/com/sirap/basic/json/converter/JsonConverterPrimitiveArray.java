package com.sirap.basic.json.converter;

import java.util.Arrays;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class JsonConverterPrimitiveArray extends JsonConverter<Object> {

	@Override
	public String toJson(Object ant) {
		String temp = rawString(ant);
		temp = temp.replace(" ", "");
		
		return temp;
	}
	
	private String rawString(Object ant) {
		Class<?> carrie = ant.getClass();
		if(!Konstants.PRIMITIVE_ARRAY_CLASSES.contains(carrie)) {
			XXXUtil.alert("Should be one of {1}, current type: {0}", carrie, Konstants.PRIMITIVE_ARRAY_SIMPLENAMES);
		}
		
		String temp;
		if(carrie.equals(char[].class)) {
			char[] chars = (char[])ant;
			StringBuffer sb = StrUtil.sb();
			sb.append("[");
			int count = 0;
			for(char ch : chars) {
				if(count != 0) {
					sb.append(", ");
				}
				count++;
				sb.append(JsonUtil.quote(ch));
			}
			sb.append("]");
			temp = sb.toString();
		} else {
			Object obj = ObjectUtil.execute(Arrays.class, "toString", new Class[]{carrie}, ant);
			temp = obj + "";
		}
		
		return temp;
	}

	@Override
	public String toPrettyJson(Object ant, int depth) {
		String temp = rawString(ant);
		//[true, false, true, false, false]
		String dent = StrUtil.spaces((depth + 1) * DENT);
		String random = RandomUtil.letters(8);
		temp = temp.replace(" ", random);
		temp = temp.replace("[", "[\n" + dent);
		temp = temp.replace("]", "\n" + StrUtil.spaces(depth * DENT) + "]");
		temp = temp.replace(random, "\n" + dent);
		
		return temp;
	}
}
