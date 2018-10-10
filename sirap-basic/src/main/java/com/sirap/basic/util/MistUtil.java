package com.sirap.basic.util;

import java.awt.List;
import java.util.ArrayList;
import java.util.Map;

import com.sirap.basic.component.Mist;
import com.sirap.basic.json.JsonToMapListConverter;
import com.sirap.basic.json.XmlToMapConverter;
import com.sirap.basic.tool.D;

/***
 * Map + List
 * Maplist
 * @author carospop
 *
 */
public class MistUtil {

	public static boolean isMapOrList(Object obj) {
		D.pla(obj.getClass(), Map.class.isInstance(obj), List.class.isInstance(obj), List.class.isInstance(obj));
		return Map.class.isInstance(obj) || List.class.isInstance(obj);
	}
	
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
	
	public static Mist ofJsonFile(String jsonPath) {
		String origin = IOUtil.readString(jsonPath);
		return ofJsonText(origin);
	}
	
	public static Mist ofJsonText(String jsonText) {
		JsonToMapListConverter jack = new JsonToMapListConverter();
		Mist mist = new Mist(jack.fromJsonText(jsonText));
		
		return mist;
	}
}
