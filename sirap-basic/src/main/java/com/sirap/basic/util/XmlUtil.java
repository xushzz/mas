package com.sirap.basic.util;

import com.sirap.basic.component.xml.XmlBox;

public class XmlUtil {
	/****
	 * <localRepository>C:\M2REPO</localRepository>
	 * @param filePath
	 * @param key localRepository
	 * @return C:\M2REPO
	 */
	public static String readValue(String filePath, String key) {
		String xmlText = IOUtil.readFileWithoutLineSeparator(filePath);
		XmlBox box = new XmlBox(xmlText);
		
		return box.readValue(key);
	}
}
