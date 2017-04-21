package com.sirap.bible;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class BibleBooksExtractor extends Extractor<BibleBook> {

	public static final String URL = "https://www.biblegateway.com/versions/New-International-Version-NIV-Bible/#booklist";

	public BibleBooksExtractor() {
		printFetching = true;
		setUrl(URL);
	}
	
	@Override
	protected void parseContent() {
		String regex = "<td data-target=.*?</a></td>";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
		while(m.find()) {
			String section = m.group(0);
			parseBookAndChapters(section);
		}
	}
	
	public void parseBookAndChapters(String section) {
		String regexBook = "span>(.*?)<span";
		String name = StrUtil.findFirstMatchedItem(regexBook, section);
		String regexChapter = ">(\\d{1,3})</a";
		Matcher m = Pattern.compile(regexChapter, Pattern.CASE_INSENSITIVE).matcher(section);
		int countOfChapters = 0;
		while(m.find()) {
			countOfChapters++;
		}
		BibleBook bb = new BibleBook(name, countOfChapters);
		mexItems.add(bb);
	}

}
