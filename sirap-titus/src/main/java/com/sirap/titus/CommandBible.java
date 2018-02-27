package com.sirap.titus;

import java.io.File;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;

public class CommandBible extends CommandBase {

	private static final String KEY_BIBLE = "bible";
	private static final String KEY_BIBLE_SHORT = "bb";
	private static final String KEY_ALL_VERSIONS = "bball";
	private static final String KEY_VERSE = "verse";

	public boolean handle() {
		if(is(KEY_BIBLE)) {
			export(BibleData.BOOKS);
			
			return true;
		}
		
		//display all bible versions and its links
		if(is(KEY_ALL_VERSIONS)) {
			export(BibleData.VERSIONS);
			
			return true;
		}

		//display random verse
		if(is(KEY_VERSE)) {
			String storage = pathBibleStorage();
			String version = g().getUserValueOf("bible.version", "niv");
			String versionCode = BibleManager.g().getVersion(version).getCode();
			StringBuilder bookAndChapter = new StringBuilder();
			List<String> items = BibleManager.g().getRandomChapter(bookAndChapter, storage, versionCode);
			XXXUtil.nullOrEmptyCheck(items);
			String verse = BibleManager.g().readVerseRandom(items);
			export(bookAndChapter.toString() + ":" + verse);
			
			return true;
		}

		//display all book links with given, fetch
		solo = parseParam(KEY_BIBLE_SHORT + "\\.([a-z]+)");
		if(solo != null) {
			String versionCode = BibleManager.g().getVersion(solo).getCode();
			export(BibleManager.g().fetchBooksByVersionCode(versionCode));
			
			return true;
		}
		
		solo = parseParam(KEY_BIBLE_SHORT + "\\s(.*?)");
		if(solo != null) {
			export(CollUtil.filter(BibleData.BOOKS, solo, isCaseSensitive()));
			
			return true;
		}

		solo = parseParam(KEY_ALL_VERSIONS + "\\s(.*?)");
		if(solo != null) {
			export(CollUtil.filter(BibleData.VERSIONS, solo, isCaseSensitive()));
			
			return true;
		}
		
		regex = "#(\\d{1,2})[\\.:\\-](\\d{1,3})(|[\\.:\\-](\\d{1,3}))";
		params = parseParams(regex);
		if(params != null) {
			int bookNumber = Integer.parseInt(params[0]);
			int chapterNumber = Integer.parseInt(params[1]);
			String storage = pathBibleStorage();
			String version = g().getUserValueOf("bible.version", "niv");
			String versionCode = BibleManager.g().getVersion(version).getCode();
			StringBuilder bookAndChapter = new StringBuilder();
			int[] coordinates = {bookNumber, chapterNumber};
			List<String> items = BibleManager.g().getSpecificChapter(coordinates, bookAndChapter, storage, versionCode);
			if(params[3] != null) {
				XXXUtil.nullOrEmptyCheck(items);
				int verseNumber = Integer.parseInt(params[3]);
				String verse = BibleManager.g().readVerse(items, verseNumber);
				export(bookAndChapter.toString() + ":" + verse);
			} else {
				export(items);
			}
			
			return true;
		}
		
		regex = "([123][\\^a-z\\$]{2,}|[\\^a-z\\$]{3,})(\\d{1,3})(|[\\.:\\-](\\d{1,3}))";
		params = parseParams(regex);
		if(params != null) {
			String bookName = params[0];
			int chapter = Integer.parseInt(params[1]);
			BibleBook book = BibleManager.g().searchByName(bookName, chapter, isCaseSensitive());
			if(book != null) {
				if(!StrUtil.equals(book.getName(), bookName)) {
					C.pr("Looking for book " + book.getName() + " chapter " + chapter + ". ");
				}
				
				dealWith(book, chapter, params[3]);
			}
		}
		
		//load means download, to download given version bible
		params = parseParams(KEY_BIBLE_SHORT + "\\.([a-z]+)\\.load" + "(\\s(pdf|txt)|)");
		if(params != null) {
			String versionCode = BibleManager.g().getVersion(params[0]).getCode();
			String fileType = Konstants.DOT_TXT;
			if(!params[1].isEmpty()) {
				fileType = "." + params[1];
			}
			String targetFolder = pathBibleStorage() + versionCode + File.separator;
			FileUtil.makeDirectoriesIfNonExist(targetFolder);
			BibleManager.g().downloadAllBooks(targetFolder, versionCode, fileType);
			C.pl2("Done with downloading as " + fileType);
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
			String version = g().getUserValueOf("bible.versionXXXXXX", "niv");
			String versionCode = BibleManager.g().getVersion(version).getCode();
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
