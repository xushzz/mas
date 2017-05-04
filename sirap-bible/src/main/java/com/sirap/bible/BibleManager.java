package com.sirap.bible;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.tool.C;
import com.sirap.common.extractor.Extractor;

public class BibleManager {
	
	private static BibleManager instance;
	
	static final List<BibleBook> BOOKS = new ArrayList<>();
	static {
		BOOKS.add(new BibleBook("Genesis", 50));
		BOOKS.add(new BibleBook("Exodus", 40));
		BOOKS.add(new BibleBook("Leviticus", 27));
		BOOKS.add(new BibleBook("Numbers", 36));
		BOOKS.add(new BibleBook("Deuteronomy", 34));
		BOOKS.add(new BibleBook("Joshua", 24));
		BOOKS.add(new BibleBook("Judges", 21));
		BOOKS.add(new BibleBook("Ruth", 4));
		BOOKS.add(new BibleBook("1 Samuel", 31));
		BOOKS.add(new BibleBook("2 Samuel", 24));
		BOOKS.add(new BibleBook("1 Kings", 22));
		BOOKS.add(new BibleBook("2 Kings", 25));
		BOOKS.add(new BibleBook("1 Chronicles", 29));
		BOOKS.add(new BibleBook("2 Chronicles", 36));
		BOOKS.add(new BibleBook("Ezra", 10));
		BOOKS.add(new BibleBook("Nehemiah", 13));
		BOOKS.add(new BibleBook("Esther", 10));
		BOOKS.add(new BibleBook("Job", 42));
		BOOKS.add(new BibleBook("Psalm", 150));
		BOOKS.add(new BibleBook("Proverbs", 31));
		BOOKS.add(new BibleBook("Ecclesiastes", 12));
		BOOKS.add(new BibleBook("Song of Songs", 8));
		BOOKS.add(new BibleBook("Isaiah", 66));
		BOOKS.add(new BibleBook("Jeremiah", 52));
		BOOKS.add(new BibleBook("Lamentations", 5));
		BOOKS.add(new BibleBook("Ezekiel", 48));
		BOOKS.add(new BibleBook("Daniel", 12));
		BOOKS.add(new BibleBook("Hosea", 14));
		BOOKS.add(new BibleBook("Joel", 3));
		BOOKS.add(new BibleBook("Amos", 9));
		BOOKS.add(new BibleBook("Obadiah", 1));
		BOOKS.add(new BibleBook("Jonah", 4));
		BOOKS.add(new BibleBook("Micah", 7));
		BOOKS.add(new BibleBook("Nahum", 3));
		BOOKS.add(new BibleBook("Habakkuk", 3));
		BOOKS.add(new BibleBook("Zephaniah", 3));
		BOOKS.add(new BibleBook("Haggai", 2));
		BOOKS.add(new BibleBook("Zechariah", 14));
		BOOKS.add(new BibleBook("Malachi", 4));
		BOOKS.add(new BibleBook("Matthew", 28));
		BOOKS.add(new BibleBook("Mark", 16));
		BOOKS.add(new BibleBook("Luke", 24));
		BOOKS.add(new BibleBook("John", 21));
		BOOKS.add(new BibleBook("Acts", 28));
		BOOKS.add(new BibleBook("Romans", 16));
		BOOKS.add(new BibleBook("1 Corinthians", 16));
		BOOKS.add(new BibleBook("2 Corinthians", 13));
		BOOKS.add(new BibleBook("Galatians", 6));
		BOOKS.add(new BibleBook("Ephesians", 6));
		BOOKS.add(new BibleBook("Philippians", 4));
		BOOKS.add(new BibleBook("Colossians", 4));
		BOOKS.add(new BibleBook("1 Thessalonians", 5));
		BOOKS.add(new BibleBook("2 Thessalonians", 3));
		BOOKS.add(new BibleBook("1 Timothy", 6));
		BOOKS.add(new BibleBook("2 Timothy", 4));
		BOOKS.add(new BibleBook("Titus", 3));
		BOOKS.add(new BibleBook("Philemon", 1));
		BOOKS.add(new BibleBook("Hebrews", 13));
		BOOKS.add(new BibleBook("James", 5));
		BOOKS.add(new BibleBook("1 Peter", 5));
		BOOKS.add(new BibleBook("2 Peter", 3));
		BOOKS.add(new BibleBook("1 John", 5));
		BOOKS.add(new BibleBook("2 John", 1));
		BOOKS.add(new BibleBook("3 John", 1));
		BOOKS.add(new BibleBook("Jude", 1));
		BOOKS.add(new BibleBook("Revelation", 22));
	}
	
	private BibleManager() {}
	
	public static BibleManager g() {
		if(instance == null) {
			instance = new BibleManager();
		}
		
		return instance;
	}
	
	public List<BibleBook> listAllBooks() {
		return BOOKS;
	}
	
	public List<BibleBook> listBooksByName(String criteria) {
		MexFilter<BibleBook> filter = new MexFilter<BibleBook>(criteria, BOOKS);
		List<BibleBook> items = filter.process();	

		return items;
	}
	
	public BibleBook searchByName(String name, int chapter) {
		MexFilter<BibleBook> filter = new MexFilter<BibleBook>(name, BOOKS);
		List<BibleBook> items = filter.process();
		
		if(items.size() == 0) {
			return null;
		}
		
		if(items.size() > 1) {
			String msg = "more than one book found by " + name;
			C.list(items);
			throw new MexException(msg);
		}
		
		BibleBook book = items.get(0);
		if(chapter == 0 || chapter > book.getMaxChapter()) {
			String msg = "chapter " + chapter + " doesn't exist, max chapter of " + book.getName() + " is " + book.getMaxChapter();
			throw new MexException(msg);
		}
		
		return book;
	}
	
	public List<MexedObject> getChapter(String fullBookName, int chapter) {
		Extractor<MexedObject> nick = new BibleChapterExtractor(fullBookName, chapter);
		nick.process();
		List<MexedObject> items = nick.getMexItems();
		
		return items;
	}
}
