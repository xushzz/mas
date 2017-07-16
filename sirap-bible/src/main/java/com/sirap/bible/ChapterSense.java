package com.sirap.bible;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class ChapterSense extends MexItem {
	private BibleBook book;
	private int chapterNumber;
	
	public ChapterSense() {
		
	}
	
	public ChapterSense(BibleBook book, int chapterNumber) {
		this.book = book;
		this.chapterNumber = chapterNumber;
	}
	
	public BibleBook getBook() {
		return book;
	}

	public String getChapterNameWithNiceOrder() {
		String value = StrUtil.extendLeftward(chapterNumber + "", 2, "0");
		return value;
	}

	public int getChapterNumber() {
		return chapterNumber;
	}
}