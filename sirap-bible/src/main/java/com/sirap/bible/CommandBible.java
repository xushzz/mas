package com.sirap.bible;

import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;

public class CommandBible extends CommandBase {

	private static final String KEY_BIBLE = "bible";

	public boolean handle() {
		if(is(KEY_BIBLE)) {
			List<BibleBook> items = BibleManager.g().listAllBooks();
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_BIBLE + "\\s(.*?)");
		if(singleParam != null) {
			List<BibleBook> items = BibleManager.g().listBooksByName(singleParam, isCaseSensitive());
			export(items);
			
			return true;
		}
		
		String regex = "([123][\\^a-z\\$]{2,}|[\\^a-z\\$]{3,})(\\d{1,3})";
		params = parseParams(regex);
		if(params != null) {
			String bookName = params[0];
			int chapter = Integer.parseInt(params[1]);
			BibleBook book = BibleManager.g().searchByName(bookName, chapter, isCaseSensitive());
			if(book != null) {
				if(!StrUtil.equals(book.getName(), bookName)) {
					C.pl("Looking for book " + book.getName() + " chapter " + chapter);
				}
				boolean fetchForcibly = OptionUtil.readBoolean(options, "force", false);
				if(fetchForcibly) {
					List<MexObject> items = BibleManager.g().fetchChapter(book.getName(), chapter);
					export(items);
				} else {
					ChapterSense sense = new ChapterSense(book, chapter);
					List<String> content = BibleManager.g().getChapterFromLocal(pathBibleFolder(), sense);
					if(EmptyUtil.isNullOrEmpty(content)) {
						List<MexObject> items = BibleManager.g().fetchChapter(book.getName(), chapter);
						export(items);
					} else {
						export(content);
					}
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
}
