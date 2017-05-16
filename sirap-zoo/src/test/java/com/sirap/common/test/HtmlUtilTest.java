package com.sirap.common.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sirap.basic.component.html.HtmlExporter;
import com.sirap.basic.output.HtmlParams;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;

public class HtmlUtilTest {
	
	
	public void export() {
		List<String> htmlTemplate = IOUtil.readResourceIntoList("/template_nice.html");
		C.list(htmlTemplate);
		
		List<String> records = StrUtil.split("nothing,is,free");
		HtmlParams params = new HtmlParams();
		params.setTopInfo("crazy world");
		List<String> htmlContent = HtmlExporter.generateHtmlContent(htmlTemplate, records, params);
		C.list(htmlContent);
	}
	
	public void display() {
		List<String> items = new ArrayList<String>();
		items.add("		<artifactId>common	</artifactId>");
		items.add("new 		and \"different\"");
		String content = HtmlUtil.toSimpleHtml(items, true);
		List<String> list = new ArrayList<String>();
		D.pl(content);
		list.add(content);
		String fullFileName = "E:/allen.html";
		C.pl(IOUtil.saveAsTxt(list, fullFileName));

	}
	
	@Test
	public void reduce() {
		C.pl(StrUtil.reduceMultipleSpacesToOne("James          Peake, 2007-"));
	}
}
