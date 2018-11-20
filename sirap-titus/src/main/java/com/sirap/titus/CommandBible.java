package com.sirap.titus;

import java.io.File;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.MexItemsFetcher;
import com.sirap.titus.extractor.BibleFetchers;

public class CommandBible extends CommandBase {

	private static final String KEY_BIBLE = "bible";
	private static final String KEY_BIBLE_SHORT = "bb";
	private static final String KEY_BIBLE_VERSION = "bv";
	private static final String KEY_ALL_VERSIONS = "bball";
	private static final String KEY_VERSE = "verse";

	public boolean handle() {
		if(is(KEY_BIBLE)) {
			exportMatrix(BibleData.BOOKS);
			return true;
		}
		
		flag = searchAndProcess(KEY_BIBLE_SHORT, new MexItemsFetcher<BibleBook>() {
			@Override
			public void handle(List<BibleBook> items) {
				exportMatrix(items);
			}
			
			@Override
			public List<BibleBook> body() {
				String vcode = OptionUtil.readString(options, "v");
				if(OptionUtil.readBooleanPRI(options, "cn", false)) {
					vcode = "CNVS";
				} else if(OptionUtil.readBooleanPRI(options, "en", false)) {
					vcode = "MEV";
				}
				if(vcode == null) {
					return BibleData.BOOKS;
				} else {
					String version = BibleData.versionCodeOf(vcode);
					D.pla(vcode, version);
					return BibleFetchers.getBooks(version);
				}
			}
		});
		if(flag) return true;

		flag = searchAndProcess(KEY_BIBLE_VERSION, new MexItemsFetcher<ValuesItem>() {
			@Override
			public void handle(List<ValuesItem> items) {
				exportMatrix(items);
			}
			
			@Override
			public List<ValuesItem> body() {
				boolean fromWeb = OptionUtil.readBooleanPRI(options, "w", false);
				if(fromWeb) {
					return BibleFetchers.getVersions();
				} else {
					return BibleData.VERSIONS;
				}
			}
		});
		if(flag) return true;

		//display random verse
		if(is(KEY_VERSE)) {
			String storage = pathBibleStorage();
			String versionCode = BibleFetchers.DEFAULT_BIBLE_CHINESE_VERSION;
			StringBuilder bookAndChapter = new StringBuilder();
			List<String> items = BibleManager.g().getRandomChapter(bookAndChapter, storage, versionCode);
			XXXUtil.nullOrEmptyCheck(items);
			String verse = BibleManager.g().readVerseRandom(items);
			export(bookAndChapter.toString() + ":" + verse);
			
			return true;
		}

		//display all book links with given, fetch
//		solo = parseParam(KEY_BIBLE_SHORT + "\\.(.+?)");
//		if(solo != null) {
//			String versionCode = solo;
//			export(BibleManager.g().fetchBooksByVersionCode(versionCode));
//			
//			return true;
//		}
		
		solo = parseParam(KEY_ALL_VERSIONS + "\\s(.*?)");
		if(solo != null) {
			export(Colls.filter(BibleData.VERSIONS, solo, isCaseSensitive()));
			
			return true;
		}
		
//		regex = "#(\\d{1,2})[\\.:\\-](\\d{1,3})(|[\\.:\\-](\\d{1,3}))";
//		params = parseParams(regex);
//		if(params != null) {
//			int bookNumber = Integer.parseInt(params[0]);
//			int chapterNumber = Integer.parseInt(params[1]);
//			String storage = pathBibleStorage();
//			String versionCode = g().getUserValueOf("bible.version", "niv");
//			StringBuilder bookAndChapter = new StringBuilder();
//			int[] coordinates = {bookNumber, chapterNumber};
//			List<String> items = BibleManager.g().getSpecificChapter(coordinates, bookAndChapter, storage, versionCode);
//			if(params[3] != null) {
//				XXXUtil.nullOrEmptyCheck(items);
//				int verseNumber = Integer.parseInt(params[3]);
//				String verse = BibleManager.g().readVerse(items, verseNumber);
//				export(bookAndChapter.toString() + ":" + verse);
//			} else {
//				export(items);
//			}
//			
//			return true;
//		}
//		
//		regex = "([123][\\^a-z\\$]{2,}|[\\^a-z\\$]{3,})(\\d{1,3})(|[\\.:\\-](\\d{1,3}))";
//		params = parseParams(regex);
//		if(params != null) {
//			String bookName = params[0];
//			int chapter = Integer.parseInt(params[1]);
//			BibleBook book = BibleManager.g().searchByName(bookName, chapter, isCaseSensitive());
//			if(book != null) {
//				if(!StrUtil.equals(book.getName(), bookName)) {
//					C.pr("Looking for book " + book.getName() + " chapter " + chapter + ". ");
//				}
//				
//				dealWith(book, chapter, params[3]);
//				return true;
//			}
//		}
		
		params = parseParams("(bb|bv)\\.([a-z]+)");
		if(params != null) {
			String vcode = BibleData.versionCodeOf(params[1]);
			boolean toDownload = OptionUtil.readBooleanPRI(options, "l", false);
			if(toDownload) {
				String versionFolder = pathBibleStorage() + vcode + File.separator;
				String fileType = Konstants.DOT_TXT;
				if(OptionUtil.readBooleanPRI(options, "pdf", false)) {
					fileType = Konstants.DOT_PDF;
				}
				FileUtil.makeDirectories(versionFolder);
				int[] info = BibleManager.g().downloadXBooks(versionFolder, vcode, fileType);
				C.pl2("Done with {0} books {1} chapters, please check {2}", info[0], info[1], versionFolder);
			} else {
				List<BibleBook> books = BibleFetchers.getBooks(vcode);
				exportMatrix(books);
			}
			
			return true;
		}
		
		return false;
	}
	
	public void dealWith(BibleBook book, int chapter, String verseInfo) {
		ChapterSense sense = new ChapterSense(book, chapter);
		List items = readChapterDetail(sense);
		if(verseInfo != null) {
			int verseNumber = Integer.parseInt(verseInfo);
			XXXUtil.shouldBePositive(verseNumber);
			XXXUtil.nullOrEmptyCheck(items);
			String verse = BibleManager.g().readVerse(items, verseNumber);
			export(baseInfo(book.getName(), sense.getChapterNumber()) + verse);
		} else {
			export(items);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List readChapterDetail(ChapterSense sense) {
		boolean fetchForcibly = OptionUtil.readBooleanPRI(options, "force", false);
		List items = null;
		if(fetchForcibly) {
			items = BibleManager.g().fetchChapterFromSirap(sense);
		} else {
			String version = g().getUserValueOf("bible.versionXXXXXX", "MEV");
			String versionCode = BibleData.versionCodeOf(version);
			String versionStorage = StrUtil.useSeparator(pathBibleStorage(), versionCode);
			
			String niceBookName = sense.getBook().getNameWithNiceOrder();
			String chapterName = sense.getChapterNameWithNiceOrder();
			String fileType = Konstants.DOT_TXT;
			String chapterLocation = StrUtil.useSeparator(versionStorage, niceBookName, chapterName + fileType);
			
			if(FileUtil.getIfNormalFile(chapterLocation) != null) {
				C.pl("Reading... " + chapterLocation);
				items = IOUtil.readFileIntoList(chapterLocation);
			}
			
			if(EmptyUtil.isNullOrEmpty(items)) {
				C.pl("Not found in storage " + chapterLocation);
				items = BibleManager.g().fetchChapterFromSirap(sense);
			}
		}
		
		return items;
	}
	
	private String baseInfo(String bookName, int chapter) {
		String baseInfo = bookName.replace(" ", "") + chapter + ":";
		return baseInfo;
	}
	
	public String pathBibleStorage() {
		String where = pathOf("bible.storage", "misc/bible");
		
		return where;
	}
}
