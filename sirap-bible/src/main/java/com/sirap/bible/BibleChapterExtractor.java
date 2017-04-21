package com.sirap.bible;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class BibleChapterExtractor extends Extractor<MexedObject> {

	public static final String URL_TEMPLATE = "https://www.biblegateway.com/passage/?search={0}&version=NIV";

	public BibleChapterExtractor(String fullBookName, int chapter) {
		String param = fullBookName + " " + chapter;
		printFetching = true;
		String url = StrUtil.occupy(URL_TEMPLATE, encodeURLParam(param));
		setUrl(url);
	}
	
	@Override
	protected void parseContent() {
		String regex = "</span>\\s*</h1>(.*)</span>\\s*</p>";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
		while(m.find()) {
			String text = m.group(1);
			String h3WithNumber = text.replaceAll("<h3>", "#<h3>");
			String section = HtmlUtil.removeHttpTag(h3WithNumber);
			section = section.replaceAll("&nbsp;", " ");
			section = StrUtil.reduceMultipleSpacesToOne(section);
			String regexVerse = "(\\d+|#).*?([^\\d#]+)";
			List<String> items = StrUtil.findAllMatchedItems(regexVerse, section);
			mexItems.addAll(CollectionUtil.convert(items));
		}
	}
}
