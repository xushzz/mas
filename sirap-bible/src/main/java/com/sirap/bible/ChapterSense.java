package com.sirap.bible;

import com.sirap.basic.domain.MexItem;

@SuppressWarnings("serial")
public class ChapterSense extends MexItem {
	private int bookNumber;
	private String bookName;
	private int chapterNumber;
	
	public ChapterSense() {
		
	}
	
	public ChapterSense(String bookName, int chapterNumber, int bookNumber) {
		this.bookName = bookName;
		this.chapterNumber = chapterNumber;
		this.bookNumber = bookNumber;
	}
	
	public int getBookNumber() {
		return bookNumber;
	}

	public void setBookNumber(int bookNumber) {
		this.bookNumber = bookNumber;
	}

	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public int getChapterNumber() {
		return chapterNumber;
	}
	public void setChapterNumber(int chapterNumber) {
		this.chapterNumber = chapterNumber;
	}
}