package com.sirap.basic.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.json.converter.JsonConverter;
import com.sirap.basic.json.converter.JsonConverterCollection;
import com.sirap.basic.json.converter.JsonConverterMap;
import com.sirap.basic.json.converter.JsonConverterObjectArray;
import com.sirap.basic.json.converter.JsonConverterFields;
import com.sirap.basic.json.converter.JsonConverterPrimitiveArray;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.XXXUtil;

public class JsonConvertManager {

	private static JsonConvertManager normal;
	private static JsonConvertManager pretty;

	public static JsonConvertManager g() {
		if(normal == null) {
			normal = new JsonConvertManager();
		}
		
		return normal;
	}
	
	public static JsonConvertManager g(boolean toPretty) {
		if(!toPretty) {
			return g();
		}
		
		if(pretty == null) {
			pretty = new JsonConvertManager();
		}
		pretty.setToPretty(true);
		
		return pretty;
	}

	public static final JsonConverter<Object> FIELDS_CONVERTER = new JsonConverterFields();
	public static final Map<Class<?>, JsonConverter<?>> REPO = new LinkedHashMap<>();
	static {
		try {
			REPO.put(Map.class, JsonConverterMap.class.newInstance());
			REPO.put(Collection.class, JsonConverterCollection.class.newInstance());
			REPO.put(Object[].class, JsonConverterObjectArray.class.newInstance());
			JsonConverterPrimitiveArray perry = new JsonConverterPrimitiveArray();
			for(Class<?> primitiveClass : Konstants.PRIMITIVE_ARRAY_CLASSES) {
				REPO.put(primitiveClass, perry);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private Map<Class<?>, JsonConverter<?>> converters = new LinkedHashMap<>();
	private boolean toPretty;

	public void setToPretty(boolean toPretty) {
		this.toPretty = toPretty;
	}

	public String toJsonByFields(Object ant) {
		return toJsonByFields(ant, ant.getClass());
	}
	
	public String toJsonByFields(Object ant, Class<?>... rockClasses) {
		if(ant == null) {
			return null;
		}
		
		return toJson(ant, FIELDS_CONVERTER, rockClasses);
	}
	
	public String toJson(Object ant, JsonConverter<?> rockConverter, Class<?>... rockClasses) {
		converters.clear();
		converters.putAll(REPO);
		Map<Class<?>, JsonConverter<?>> mars = new LinkedHashMap<>();
		for(Class<?> rockClass : rockClasses) {
			mars.put(rockClass, rockConverter);
		}
		
		return toJson(ant, mars);
	}
	
	public String toJson(Object ant, Map<Class<?>, JsonConverter<?>> mars) {
		converters.clear();
		
		converters.putAll(REPO);
		Iterator<Class<?>> it = mars.keySet().iterator();
		while(it.hasNext()) {
			Class<?> rockClass = it.next();
			if(REPO.containsKey(rockClass)) {
				XXXUtil.alert("Already exists {0}", rockClass.getSimpleName());
			}
		}
		
		converters.putAll(mars);
		
		String temp = toJson(ant);
		
		return temp;
	}
	
	public String toJson(Object ant) {
		return toJson(ant, 0);
	}
	
	public String toJson(Object ant, int depth) {
		if(ant == null || ant instanceof Number || ant instanceof Boolean) {
			return ant + "";
		}
		
		if(EmptyUtil.isNullOrEmpty(converters)) {
			converters.putAll(REPO);
		}
		
		Iterator<Class<?>> it = converters.keySet().iterator();
//		D.pl(ant.getClass());
//		D.pl(ant);
		while(it.hasNext()) {
			Class<?> key = it.next();
//			D.pl(key.getSimpleName());
			if(!key.isInstance(ant)) {
				continue;
			}
			JsonConverter jc = converters.get(key);
//			D.pl("The king: ", key, jc);
			if(toPretty) {
				return jc.toPrettyJson(ant, depth);
			} else {
				return jc.toJson(ant);
			}
		}
		
		return JsonUtil.quote(ant);
	}
}
