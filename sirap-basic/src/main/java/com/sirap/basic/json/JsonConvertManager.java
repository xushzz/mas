package com.sirap.basic.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.json.converter.JsonConverter;
import com.sirap.basic.json.converter.JsonConverterList;
import com.sirap.basic.json.converter.JsonConverterMap;
import com.sirap.basic.json.converter.JsonConverterMexItem;
import com.sirap.basic.json.converter.JsonConverterObjectArray;
import com.sirap.basic.json.converter.JsonConverterPrimitiveArray;
import com.sirap.basic.util.EmptyUtil;

public class JsonConvertManager {
	
	public static final Map<Class<?>, JsonConverter<?>> REPO = new LinkedHashMap<>();
	static {
		try {
			REPO.put(Map.class, JsonConverterMap.class.newInstance());
			REPO.put(Collection.class, JsonConverterList.class.newInstance());
			REPO.put(Object[].class, JsonConverterObjectArray.class.newInstance());
			JsonConverterPrimitiveArray perry = new JsonConverterPrimitiveArray();
			for(Class<?> primitiveClass : Konstants.PRIMITIVE_ARRAY_CLASSES) {
				REPO.put(primitiveClass, perry);
			}
			REPO.put(MexItem.class, JsonConverterMexItem.class.newInstance());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static Map<Class<?>, JsonConverter<?>> extraRepo = new LinkedHashMap<>();
	
	public static String toJson(Object ant) {
		if(ant == null || ant instanceof Number || ant instanceof Boolean) {
			return ant + "";
		}
		
		Map<Class<?>, JsonConverter<?>> allRepos = new LinkedHashMap<>(extraRepo);
		allRepos.putAll(REPO);
		
		if(!EmptyUtil.isNullOrEmpty(allRepos)) {
			Iterator<Class<?>> it = allRepos.keySet().iterator();
//			D.pl(ant.getClass());
//			D.pl(ant);
			while(it.hasNext()) {
				Class<?> key = it.next();
//				D.pl(key.getSimpleName());
				if(!key.isInstance(ant)) {
					continue;
				}
				JsonConverter jc = allRepos.get(key);
//				D.pl("The king: ", key, jc);
				return jc.toJson(ant);
			}
		}
		
		return JsonUtil.quote(ant);
	}
	
	public static String toJson(Object ant, Map<Class<?>, JsonConverter<?>> converters) {
		extraRepo.putAll(converters);
		String temp = toJson(ant);
		extraRepo.clear();
		
		return temp;
	}
	
	public static String toJson(Object ant, Class<?> rockClass, JsonConverter<?> rockConverter) {
		extraRepo.put(rockClass, rockConverter);
		String temp = toJson(ant);
		extraRepo.clear();
		
		return temp;
	}
	
	public static String toPrettyJson(Object ant, int depth) {
		if(ant == null || ant instanceof Number || ant instanceof Boolean) {
			return ant + "";
		}
		
		Map<Class<?>, JsonConverter<?>> allRepos = new LinkedHashMap<>(extraRepo);
		allRepos.putAll(REPO);
		
		if(!EmptyUtil.isNullOrEmpty(allRepos)) {
			Iterator<Class<?>> it = allRepos.keySet().iterator();
//			D.pl(ant.getClass());
//			D.pl(ant);
			while(it.hasNext()) {
				Class<?> key = it.next();
				if(!key.isInstance(ant)) {
					continue;
				}
				JsonConverter jc = allRepos.get(key);
//				D.pl("The king: ", key, jc);
				return jc.toPrettyJson(ant, depth);
			}
		}
		
		return JsonUtil.quote(ant);
	}
	
	public static String toPrettyJson(Object ant, int depth, Map<Class<?>, JsonConverter<?>> converters) {
		extraRepo.putAll(converters);
		String temp = toPrettyJson(ant, depth);
		extraRepo.clear();
		
		return temp;
	}
	
	public static String toPrettyJson(Object ant, int depth, Class<?> rockClass, JsonConverter<?> rockConverter) {
		extraRepo.put(rockClass, rockConverter);
		String temp = toPrettyJson(ant, depth);
		extraRepo.clear();
		
		return temp;
	}
}
