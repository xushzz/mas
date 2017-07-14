package com.sirap.bible;

import java.io.File;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.thread.Worker;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class BookChapterFetcher extends Worker<ChapterSense> {
	private String storage;
	
	public BookChapterFetcher(String bibleFolder) {
		this.storage = bibleFolder;
	}
	
	@Override
	public void process(ChapterSense sense) {
		String bookName = sense.getBookName();
		String bookFolderName = StrUtil.extendLeftward(sense.getBookNumber() + "", 2, "0") + " " + bookName;
		String bookFolderDir = storage + File.separator + bookFolderName;
		FileUtil.makeDirectoriesIfNonExist(bookFolderDir);
		
		int chapter = sense.getChapterNumber();
		String fileName = StrUtil.extendLeftward(chapter + "", 2, "0") +  Konstants.SUFFIX_TXT;
		String fullFileName = bookFolderDir + File.separator + fileName;
		if(FileUtil.exists(fullFileName)) {
			C.pl("Already exists " + fullFileName);
			return;
		}

		int count = countOfTasks - tasks.size();
		status(STATUS_FILE_COPY, count, countOfTasks, "Downloading...", bookName + " " + chapter, storage);
		Extractor<MexedObject> nick = new BibleChapterExtractor(bookName, chapter);
		nick.process();
		List<MexedObject> items = nick.getMexItems();
		IOUtil.saveAsTxt(items, fullFileName);
		status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Downloaded", fullFileName);
	}
}