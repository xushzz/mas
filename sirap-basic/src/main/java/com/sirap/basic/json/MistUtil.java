package com.sirap.basic.json;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.util.IOUtil;

/***
 * Map + List
 * Maplist
 * @author carospop
 *
 */
public class MistUtil {
	
	public static Mist ofMapOrList(Object mapOrList) {
		Mist mist = new Mist(mapOrList);
		
		return mist;
	}
	
	public static Mist ofXmlFile(String xmlPath) {
		String origin = IOUtil.readString(xmlPath);
		return ofXmlText(origin);
	}
	
	public static Mist ofXmlText(String xmlText) {
		XmlToMapConverter jack = new XmlToMapConverter();
		Mist mist = new Mist(jack.fromXmlText(xmlText));
		
		return mist;
	}
	
	public static Mist ofXmlText(String xmlText, boolean useAttributes) {
		XmlToMapConverter jack = new XmlToMapConverter();
		jack.setUseAttributes(useAttributes);
		Mist mist = new Mist(jack.fromXmlText(xmlText));
		
		return mist;
	}
	
	public static Mist ofJsonFile(String jsonPath) {
		String origin = IOUtil.readString(jsonPath);
//		D.pl(origin);
		return ofJsonText(origin);
	}
	
	public static Mist ofJsonText(String jsonText) {
		JsonToMapListConverter jack = new JsonToMapListConverter();
		Mist mist = new Mist(jack.fromJsonText(jsonText));
		
		return mist;
	}

	public static List wrapMapIfNeeded(Object moon) {
		List items = Lists.newArrayList();
		if(List.class.isInstance(moon)) {
			items.addAll((List)moon);
		} else if(Map.class.isInstance(moon)) {
			items.add(moon);
		} else {
//			XXXUtil.alert("Unsupported type: {0}", moon.getClass().getName());
		}
		
		return items;
	}
}
