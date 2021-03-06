package com.sirap.titus;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.thread.Master;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.domain.Link;
import com.sirap.titus.extractor.BibleBooksExtractor;
import com.sirap.titus.extractor.BibleChapterFromSirapExtractor;
import com.sirap.titus.extractor.BibleFetchers;

public class BibleManager {
	
	private static BibleManager instance;
	
	private BibleManager() {}
	
	public static BibleManager g() {
		if(instance == null) {
			instance = new BibleManager();
		}
		
		return instance;
	}
	
	public List<String> getSpecificChapter(int[] coordinates, StringBuilder bookAndChapter, String bibleStorage, String version) {
		XXXUtil.shouldBeTrue(coordinates.length >= 2);
		String versionCode = BibleData.versionCodeOf(version);
		String versionStorage = StrUtil.useSeparator(bibleStorage, versionCode);
		
		String specificBook = specificSubFileName(versionStorage, coordinates[0], true, false);
		String bookLocation = StrUtil.useSeparator(versionStorage, specificBook);
		String specificChapter = specificSubFileName(bookLocation, coordinates[1], false, true);
		String chapterLocation = StrUtil.useSeparator(bookLocation, specificChapter);
		
		if(bookAndChapter != null) {
			bookAndChapter.append(specificBook.replaceAll("^\\d+", "").trim());
			bookAndChapter.append(specificChapter.replaceAll("^0+", "").replaceAll("\\.\\w{3}$", "").trim());
		}
		
		String filePath = chapterLocation;
		C.pl("Reading... " + filePath);
		
		return IOUtil.readLines(filePath, Konstants.CODE_UTF8);
	}
	
	public List<String> getRandomChapter(StringBuilder bookAndChapter, String bibleStorage, String version) {
		String versionCode = BibleData.versionCodeOf(version);
		String versionStorage = StrUtil.useSeparator(bibleStorage, versionCode);
		
		String randomBook = randomSubFolderName(versionStorage, true, false);
		String bookLocation = StrUtil.useSeparator(versionStorage, randomBook);
		String randomChapter = randomSubFolderName(bookLocation, false, true);
		String chapterLocation = StrUtil.useSeparator(bookLocation, randomChapter);
		
		if(bookAndChapter != null) {
			bookAndChapter.append(randomBook.replaceAll("^\\d+", "").trim());
			bookAndChapter.append(randomChapter.replaceAll("^0+", "").replaceAll("\\.\\w{3}$", "").trim());
		}
		
		String filePath = chapterLocation;
		C.pl("Reading... " + filePath);
		
		return IOUtil.readLines(filePath, Konstants.CODE_UTF8);
	}
	
	public String randomSubFolderName(String folderName, final boolean checkFolder, final boolean checkFile) {
		File where = FileUtil.getIfNormalFolder(folderName);
		XXXUtil.nullCheck(where, ":" + folderName + " doesn't exist.");
		String[] arr = where.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File parent, String name) {
				File subFolder = new File(parent, name);
				if(checkFolder && !subFolder.isDirectory()) {
					return false;
				}
				if(checkFile&& !subFolder.isFile()) {
					return false;
				}
				
				boolean flag = StrUtil.isRegexFound("^\\d{1,3}", name);
				
				return flag;
			}
		});
		
		if(arr == null || arr.length == 0) {
			XXXUtil.alert("No solid content in " + folderName);
		}
		
		int bookIndex = RandomUtil.number(1000)%(arr.length);
		
		return arr[bookIndex];
	}
	
	public String specificSubFileName(String folderName, final int numberYouWant, final boolean checkFolder, final boolean checkFile) {
		File where = FileUtil.getIfNormalFolder(folderName);
		XXXUtil.nullCheck(where, ":" + folderName + " is not a valid folder.");
		String[] arr = where.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File parent, String name) {
				File subFolder = new File(parent, name);
				if(checkFolder && !subFolder.isDirectory()) {
					return false;
				}
				if(checkFile&& !subFolder.isFile()) {
					return false;
				}
				
				String regex = "^0?(\\d{1,3})[\\s\\.]";
				String number = StrUtil.findFirstMatchedItem(regex, name);
				if(number != null && Integer.parseInt(number) == numberYouWant) {
					return true;
				}
				
				return false;
			}
		});
		
		if(arr != null && arr.length > 0) {
			return arr[0];
		}
		
		throw new MexException("Can't find {0} in '{1}'.", numberYouWant, folderName);
	}
	
	public BibleBook searchByName(String name, int chapter, boolean caseSensitive) {
		String wholeWord = "^" + name + "$";
		List<BibleBook> items = Colls.filter(BibleData.BOOKS, wholeWord, caseSensitive);
		BibleBook theBook = null;
		if(items.size() == 1) {
			theBook = items.get(0);
		} else {
			items = Colls.filter(BibleData.BOOKS, name, caseSensitive);
			
			if(items.size() == 0) {
				return null;
			}
			
			if(items.size() > 1) {
				String msg = "more than one book found by name " + name;
				C.list(items);
				throw new MexException(msg);
			}
			
			theBook = items.get(0);
		}

		if(chapter == 0 || chapter > theBook.getMaxChapter()) {
			throw new MexException("chapter {0} doesn't exist, max chapter of {1} is {2}.", chapter, theBook.getName(), theBook.getMaxChapter());
		}
		
		return theBook;
	}
	
	public List<MexObject> fetchChapterFromSirap(ChapterSense sense) {
		String bookName = sense.getBook().getNameWithNiceOrder();
		String chapter = sense.getChapterNameWithNiceOrder();
		
		String urlInfo = StrUtil.occupy("{0}/{1}.txt", bookName, chapter);
		Extractor<MexObject> nick = new BibleChapterFromSirapExtractor(urlInfo);
		
		return nick.process().getItems();
	}
	
	public List<Link> fetchBooksByVersionCode(String versionCode) {
		Extractor<Link> nick = new BibleBooksExtractor(versionCode);
		nick.process();
		
		return nick.getItems();
	}
	
	public int[] downloadXBooks(String versionFolder, String version, String fileType) {
		List<BibleBook> books = BibleFetchers.getBooks(version);
		List<ChapterSense> alist = Lists.newArrayList();
		for(BibleBook bo : books) {
			for(int chapterId = 1; chapterId <= bo.getMaxChapter(); chapterId++) {
				ChapterSense kid = new ChapterSense(bo, chapterId, version);
				alist.add(kid);
			}
		}
//		alist = Colls.top(alist, 3);
//		alist = Colls.last(alist, 3);
//		alist = Colls.range(alist, 40, 46);
//		List<String> blist = StrUtil.split("a,b,c,e,f,g,h,yr");
//		C.list(Colls.range(blist, 3, 5));
//		if(!alist.isEmpty()) {
//			return null;
//		}
		Master<ChapterSense> master = new Master<ChapterSense>(alist, new BookXFetcher(versionFolder, fileType){
			@Override
			public List<String> getVerses(BibleBook book, int chapterId, String version) {
				return BibleFetchers.getVersesFromBibleGateway(book.getName(), chapterId, version);
			}
		});

		master.sitAndWait();
		return new int[]{books.size(), alist.size()};
	}
	
	public String downloadAllBooks(String bibleFolder, String versionCode, String fileType) {
		Extractor<Link> nick = new BibleBooksExtractor(versionCode);
		List<Link> bookLinks = nick.process().getItems();
		
		int size = bookLinks.size();
		int[] range = BibleData.CHAPTERS_OLD_NEW_ALL.get(size);
		XXXUtil.nullCheck(range, "The count of book links you get is uncanny " + size + " with version " + versionCode);
		XXXUtil.shouldBeTrue(range.length == 2);
		XXXUtil.shouldBePositive(range[0]);
		XXXUtil.shouldBePositive(range[1]);
		XXXUtil.shouldBeTrue(bookLinks.size() == (range[1] - range[0] + 1));
		
		List<ChapterSense> allLinks = new ArrayList<>();
		
		for(int i = 0; i < bookLinks.size(); i++) {
			Link link = bookLinks.get(i);
			int bookIndex = range[0] - 1 + i;
			BibleBook bb = BibleData.BOOKS.get(bookIndex).clone();
			bb.setVersion(versionCode);
			bb.setName(link.getName());
			//bb.setHref(link.getHref());
			
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
	
	public String readVerse(List items, int verseYouWant) {
		List<VerseSake> verses = getPureVerses(items);
		int maxVerse = verses.size();
		if(verseYouWant > maxVerse) {
			throw new MexException("Not found verse {0}, the max verse is {1}", verseYouWant, maxVerse);
		}
		
		return findByVerseNumber(verses, verseYouWant);
	}
	
	@SuppressWarnings("rawtypes")
	public String readVerseRandom(List items) {
		List<VerseSake> verses = getPureVerses(items);
		int random = RandomUtil.number(1, verses.size());

		return findByVerseNumber(verses, random);
	}
	
	public String findByVerseNumber(List<VerseSake> items, int verseYouWant) {
		for(VerseSake item : items) {
			if(item.getVerseNumber() == verseYouWant) {
				return item.toPrint();
			}
		}
		
		throw new MexException("Verse {0} not found.", verseYouWant);
	}
	
	public List<VerseSake> getPureVerses(List items) {
		List<VerseSake> verses = new ArrayList<>();
		for(int i = 1; i < items.size(); i++) {
			String item = items.get(i) + "";
			String regex = "^#(\\d+).(.+)";
			String[] params = StrUtil.parseParams(regex, item);
			if(params != null) {
				int number = Integer.parseInt(params[0]);
				verses.add(new VerseSake(number, params[1]));
			}
		}
		
		return verses;
	}
}
