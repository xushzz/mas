package com.sirap.basic.util;

import com.sirap.basic.component.xml.XmlBox;

public class XmlUtil {
	/****
	 * <localRepository>C:\M2REPO</localRepository>
	 * @param filePath
	 * @param key localRepository
	 * @return C:\M2REPO
	 */
	public static String readValueFromFile(String filePath, String elementName) {
		String xmlText = IOUtil.readString(filePath);
		return readValue(xmlText, elementName);
	}

	public static String readValue(String xmlText, String elementName) {
		XmlBox box = new XmlBox(xmlText);
		
		return box.readValue(elementName);
	}
}
