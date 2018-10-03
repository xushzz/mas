package com.sirap.basic.util;

import com.sirap.basic.component.Mist;
import com.sirap.basic.json.JsonToMapListConverter;
import com.sirap.basic.json.XmlToMapConverter;

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
	
	public static Mist ofJsonFile(String jsonPath) {
		String origin = IOUtil.readString(jsonPath);
		return ofXmlText(origin);
	}
	
	public static Mist ofJsonText(String jsonText) {
		JsonToMapListConverter jack = new JsonToMapListConverter();
		Mist mist = new Mist(jack.fromJsonText(jsonText));
		
		return mist;
	}
}
