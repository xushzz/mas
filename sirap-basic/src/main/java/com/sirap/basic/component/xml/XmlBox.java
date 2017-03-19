package com.sirap.basic.component.xml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.StrUtil;

public class XmlBox {
	
	private String xmlText;
	private String fixedText;
	
	public XmlBox(String xmlText) {
		this.xmlText = xmlText;
		init();
	}
	
	private void init() {
		removeHeaderAndComments();
	}
	
	private void removeHeaderAndComments() {
		String regex = "<!--.*?-->";
		fixedText = xmlText.replaceAll(regex, "");
		regex = "<\\?xml.*\\?>";
		fixedText = fixedText.replaceAll(regex, "");
	}
	
	public String readValue(String key) {
		String template = "<{0}(|\\s.+?)>(.*?)</{0}>";
		String regex = StrUtil.occupy(template, key);

		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(fixedText);
		if(m.find()) {
			String value = m.group(2);
			return value;
		}
		
		return null;
	}
}


