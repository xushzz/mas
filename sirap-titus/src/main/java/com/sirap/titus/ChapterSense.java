package com.sirap.titus;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class ChapterSense extends MexItem {
	private BibleBook book;
	private int chapterNumber;
	private String version;
	
	public ChapterSense() {
		
	}
	
	public ChapterSense(BibleBook book, int chapterNumber) {
		this.book = book;
		this.chapterNumber = chapterNumber;
	}
	
	public ChapterSense(BibleBook book, int chapterNumber, String version) {
		this.book = book;
		this.chapterNumber = chapterNumber;
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public BibleBook getBook() {
		return book;
	}

	public String getChapterNameWithNiceOrder() {
		int maxLen = (book.getMaxChapter() + "").length();
		String value = StrUtil.padLeft(chapterNumber + "", maxLen, "0");
		return value;
	}

	public int getChapterNumber() {
		return chapterNumber;
	}
}