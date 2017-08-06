package com.sirap.bible;

import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;

public class CommandBible extends CommandBase {

	private static final String KEY_BIBLE = "bible";

	public boolean handle() {
		if(is(KEY_BIBLE)) {
			List<BibleBook> items = BibleManager.g().listAllBooks();
			export(items);
			
			return true;
		}

		if(is(KEY_BIBLE + KEY_ASTERISK)) {
			ChapterSense sense = BibleManager.g().randomChapterSense();
			List items = readChapterDetail(sense);
			XXXUtil.nullOrEmptyCheck(items);
			String verse = BibleManager.g().readVerseRandom(items);
			export(verse);
			
			return true;
		}

		singleParam = parseParam(KEY_BIBLE + "\\s(.*?)");
		if(singleParam != null) {
			List<BibleBook> items = BibleManager.g().listBooksByName(singleParam, isCaseSensitive());
			export(items);
			
			return true;
		}
		
		String regex = "([123][\\^a-z\\$]{2,}|[\\^a-z\\$]{3,})(\\d{1,3})(|[\\.:\\-](\\d{1,3}))";
		params = parseParams(regex);
		if(params != null) {
			String bookName = params[0];
			int chapter = Integer.parseInt(params[1]);
			XXXUtil.shouldBePositive(chapter);
			BibleBook book = BibleManager.g().searchByName(bookName, chapter, isCaseSensitive());
			if(book != null) {
				if(!StrUtil.equals(book.getName(), bookName)) {
					C.pl("Looking for book " + book.getName() + " chapter " + chapter);
				}
				
				ChapterSense sense = new ChapterSense(book, chapter);
				List items = readChapterDetail(sense);
				if(params[3] != null) {
					int verseNumber = Integer.parseInt(params[3]);
					XXXUtil.shouldBePositive(verseNumber);
					XXXUtil.nullOrEmptyCheck(items);
					String verse = BibleManager.g().readVerse(items, verseNumber);
					export(verse);
				} else {
					export(items);
				}
				
				return true;
			}
		}
		
		singleParam = parseParam(KEY_BIBLE + KEY_BIBLE + "(\\s(pdf|txt)|)");
		if(singleParam != null) {
			String fileType = Konstants.SUFFIX_TXT;
			if(!singleParam.isEmpty()) {
				fileType = "." + singleParam;
			}
			BibleManager.g().downloadAllBooks(pathBibleFolder(), fileType);
			C.pl2("Done with downloading as " + fileType);

			return true;
		}

		return false;
	}
	
	public String pathBibleFolder() {
		String where = pathWithSeparator("storage.bible", "misc/bible");
		
		return where;
	}
	
	@SuppressWarnings("rawtypes")
	public List readChapterDetail(ChapterSense sense) {
		boolean fetchForcibly = OptionUtil.readBoolean(options, "force", false);
		String bookName = sense.getBook().getName();
		int chapter = sense.getChapterNumber();
		List items = null;
		if(fetchForcibly) {
			items = BibleManager.g().fetchChapter(bookName, chapter);
		} else {
			items = BibleManager.g().getChapterFromLocal(pathBibleFolder(), sense);
			if(EmptyUtil.isNullOrEmpty(items)) {
				items = BibleManager.g().fetchChapter(bookName, chapter);
			}
		}
		
		return items;
	}
}
