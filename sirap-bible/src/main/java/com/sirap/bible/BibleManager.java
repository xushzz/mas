package com.sirap.bible;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.thread.Master;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.common.component.FileOpener;
import com.sirap.common.extractor.Extractor;

public class BibleManager {
	
	private static BibleManager instance;
	
	static final List<BibleBook> BOOKS = new ArrayList<>();
	static {
		BOOKS.add(new BibleBook(1, "Genesis", 50));
		BOOKS.add(new BibleBook(2, "Exodus", 40));
		BOOKS.add(new BibleBook(3, "Leviticus", 27));
		BOOKS.add(new BibleBook(4, "Numbers", 36));
		BOOKS.add(new BibleBook(5, "Deuteronomy", 34));
		BOOKS.add(new BibleBook(6, "Joshua", 24));
		BOOKS.add(new BibleBook(7, "Judges", 21));
		BOOKS.add(new BibleBook(8, "Ruth", 4));
		BOOKS.add(new BibleBook(9, "1 Samuel", 31));
		BOOKS.add(new BibleBook(10, "2 Samuel", 24));
		BOOKS.add(new BibleBook(11, "1 Kings", 22));
		BOOKS.add(new BibleBook(12, "2 Kings", 25));
		BOOKS.add(new BibleBook(13, "1 Chronicles", 29));
		BOOKS.add(new BibleBook(14, "2 Chronicles", 36));
		BOOKS.add(new BibleBook(15, "Ezra", 10));
		BOOKS.add(new BibleBook(16, "Nehemiah", 13));
		BOOKS.add(new BibleBook(17, "Esther", 10));
		BOOKS.add(new BibleBook(18, "Job", 42));
		BOOKS.add(new BibleBook(19, "Psalm", 150));
		BOOKS.add(new BibleBook(20, "Proverbs", 31));
		BOOKS.add(new BibleBook(21, "Ecclesiastes", 12));
		BOOKS.add(new BibleBook(22, "Song of Songs", 8));
		BOOKS.add(new BibleBook(23, "Isaiah", 66));
		BOOKS.add(new BibleBook(24, "Jeremiah", 52));
		BOOKS.add(new BibleBook(25, "Lamentations", 5));
		BOOKS.add(new BibleBook(26, "Ezekiel", 48));
		BOOKS.add(new BibleBook(27, "Daniel", 12));
		BOOKS.add(new BibleBook(28, "Hosea", 14));
		BOOKS.add(new BibleBook(29, "Joel", 3));
		BOOKS.add(new BibleBook(30, "Amos", 9));
		BOOKS.add(new BibleBook(31, "Obadiah", 1));
		BOOKS.add(new BibleBook(32, "Jonah", 4));
		BOOKS.add(new BibleBook(33, "Micah", 7));
		BOOKS.add(new BibleBook(34, "Nahum", 3));
		BOOKS.add(new BibleBook(35, "Habakkuk", 3));
		BOOKS.add(new BibleBook(36, "Zephaniah", 3));
		BOOKS.add(new BibleBook(37, "Haggai", 2));
		BOOKS.add(new BibleBook(38, "Zechariah", 14));
		BOOKS.add(new BibleBook(39, "Malachi", 4));
		BOOKS.add(new BibleBook(40, "Matthew", 28));
		BOOKS.add(new BibleBook(41, "Mark", 16));
		BOOKS.add(new BibleBook(42, "Luke", 24));
		BOOKS.add(new BibleBook(43, "John", 21));
		BOOKS.add(new BibleBook(44, "Acts", 28));
		BOOKS.add(new BibleBook(45, "Romans", 16));
		BOOKS.add(new BibleBook(46, "1 Corinthians", 16));
		BOOKS.add(new BibleBook(47, "2 Corinthians", 13));
		BOOKS.add(new BibleBook(48, "Galatians", 6));
		BOOKS.add(new BibleBook(49, "Ephesians", 6));
		BOOKS.add(new BibleBook(50, "Philippians", 4));
		BOOKS.add(new BibleBook(51, "Colossians", 4));
		BOOKS.add(new BibleBook(52, "1 Thessalonians", 5));
		BOOKS.add(new BibleBook(53, "2 Thessalonians", 3));
		BOOKS.add(new BibleBook(54, "1 Timothy", 6));
		BOOKS.add(new BibleBook(55, "2 Timothy", 4));
		BOOKS.add(new BibleBook(56, "Titus", 3));
		BOOKS.add(new BibleBook(57, "Philemon", 1));
		BOOKS.add(new BibleBook(58, "Hebrews", 13));
		BOOKS.add(new BibleBook(59, "James", 5));
		BOOKS.add(new BibleBook(60, "1 Peter", 5));
		BOOKS.add(new BibleBook(61, "2 Peter", 3));
		BOOKS.add(new BibleBook(62, "1 John", 5));
		BOOKS.add(new BibleBook(63, "2 John", 1));
		BOOKS.add(new BibleBook(64, "3 John", 1));
		BOOKS.add(new BibleBook(65, "Jude", 1));
		BOOKS.add(new BibleBook(66, "Revelation", 22));
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
	
	public List<BibleBook> listBooksByName(String criteria, boolean caseSensitive) {
		List<BibleBook> items = CollectionUtil.filter(BOOKS, criteria, caseSensitive);	

		return items;
	}
	
	public BibleBook searchByName(String name, int chapter, boolean caseSensitive) {
		List<BibleBook> items = CollectionUtil.filter(BOOKS, name, caseSensitive);
		
		if(items.size() == 0) {
			return null;
		}
		
		if(items.size() > 1) {
			String msg = "more than one book found by " + name;
			C.list(items);
			throw new MexException(msg);
		}
		
		BibleBook theBook = items.get(0);

		if(chapter == 0 || chapter > theBook.getMaxChapter()) {
			String msg = "chapter " + chapter + " doesn't exist, max chapter of " + theBook.getName() + " is " + theBook.getMaxChapter();
			throw new MexException(msg);
		}
		
		return theBook;
	}
	
	public List<String> getChapterFromLocal(String bibleFolder, ChapterSense sense) {
		List<String> content = new ArrayList<>();
		String niceBookName = sense.getBook().getNameWithNiceOrder();
		String chapterName = sense.getChapterNameWithNiceOrder();
		String fileType = Konstants.SUFFIX_TXT;
		String filePath = bibleFolder + niceBookName + File.separator + chapterName + fileType;
		if(FileUtil.exists(filePath)) {
			C.pl("Reading... " + filePath);
			content.addAll(IOUtil.readFileIntoList(filePath));
		}
		
		fileType = Konstants.SUFFIX_PDF;
		filePath = bibleFolder + niceBookName + File.separator + chapterName + fileType;
		if(FileUtil.exists(filePath)) {
			FileOpener.open(filePath);
			content.add(filePath);
		}
		
		return content;
	}
	
	public List<MexObject> fetchChapter(String fullBookName, int chapter) {
		Extractor<MexObject> nick = new BibleChapterExtractor(fullBookName, chapter);
		nick.process();
		List<MexObject> items = nick.getMexItems();
		
		return items;
	}
	
	public String downloadAllBooks(String bibleFolder, String fileType) {
		List<ChapterSense> allLinks = new ArrayList<>();
		for(BibleBook bb : BibleManager.BOOKS) {
			List<ChapterSense> links = getChapterSenses(bb);
			allLinks.addAll(links);
		}

		BookChapterFetcher dinesh = new BookChapterFetcher(bibleFolder, fileType);
		Master<ChapterSense> master = new Master<ChapterSense>(allLinks, dinesh);

		master.sitAndWait();

		return bibleFolder;
	}
	
	public List<ChapterSense> getChapterSenses(BibleBook bb) {
		List<ChapterSense> links = new ArrayList<>();
		for(int chapter = 1; chapter <= bb.getMaxChapter(); chapter++) {
			ChapterSense link = new ChapterSense(bb, chapter);
			links.add(link);
		}
		
		return links;
	}
}
