package com.sirap.basic.json;

import org.junit.Test;

import com.sirap.basic.component.xml.XmlBox;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.XmlUtil;

public class XmlBoxTest {
	private String dir = "D:/Github/SIRAP/mas/sirap-basic/src/test/data/";

	@Test
	public void readValue() {
		String fileName = dir + "A2.xml";
		C.pl(XmlUtil.readValue(fileName, "localRepository"));
	}
	
	@Test
	public void readM1() {
		String fileName = dir + "A2.xml";
		String source = IOUtil.readFileWithoutLineSeparator(fileName);
		XmlBox chen = new XmlBox(source);
		C.pl(chen.readValue("localRepository"));
//		C.pl(chen.readValue("username"));
//		C.pl(chen.readValue("server"));
	}
}
