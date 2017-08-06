package com.sirap.bible;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class BibleChapterExtractor extends Extractor<MexObject> {

	public static final String URL_TEMPLATE = "https://www.biblegateway.com/passage/?search={0}&version=NIV";

	private String baseInfo;
	public BibleChapterExtractor(String fullBookName, int chapter) {
		printFetching = true;
		String param = fullBookName + " " + chapter;
		baseInfo = fullBookName + chapter;
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
			boolean firstVerseMarked = false;
			for(String item : items) {
				String temp = item;
				if(!firstVerseMarked) {
					if(StrUtil.isRegexFound("^\\d+", temp)) {
						temp = temp.replaceAll("^\\d+", "1");
						firstVerseMarked = true;
					}
				}
				String error = StrUtil.findFirstMatchedItem("^\\d+(.)", temp);
				if(error != null) {
					temp = temp.replace(error, " ");
				}
				mexItems.add(new MexObject(temp));
			}
		}
		
		if(!EmptyUtil.isNullOrEmpty(mexItems)) {
			mexItems.add(0, new MexObject(baseInfo));
		}
	}
}
