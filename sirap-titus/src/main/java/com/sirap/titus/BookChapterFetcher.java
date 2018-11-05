package com.sirap.titus;

import java.io.File;
import java.util.List;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.thread.Worker;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.third.msoffice.PdfHelper;
import com.sirap.titus.extractor.BibleChapterExtractor;

public class BookChapterFetcher extends Worker<ChapterSense> {
	private String storage;
	private String fileType = Konstants.DOT_TXT;
	
	public BookChapterFetcher(String bibleFolder) {
		this.storage = bibleFolder;
	}
	
	public BookChapterFetcher(String bibleFolder, String fileType) {
		this.storage = bibleFolder;
		this.fileType = fileType;
	}
	
	@Override
	public void process(ChapterSense sense) {
		String bookFolderDir = storage + sense.getBook().getNameWithNiceOrder();
		FileUtil.makeDirectories(bookFolderDir);
		
		String fileName = sense.getChapterNameWithNiceOrder() +  fileType;
		String fullFileName = bookFolderDir + File.separator + fileName;
		if(FileUtil.exists(fullFileName)) {
			C.pl("Already exists " + fullFileName);
			return;
		}

		int count = countOfTasks - queue.size();
		status(STATUS_FILE_COPY, count, countOfTasks, "Downloading...", sense.getBook().getName() + " " + sense.getChapterNumber(), storage);
		Extractor<MexObject> nick = new BibleChapterExtractor(sense.getChapterHref());
		nick.process();
		List<MexObject> items = nick.getItems();
		if(StrUtil.equals(Konstants.DOT_TXT, fileType)) {
			IOUtil.saveAsTxt(items, fullFileName);
		} else if(StrUtil.equals(Konstants.DOT_PDF, fileType)) {
//			FileHelper.saveAsPDF(items, fullFileName, new PDFParams());
			PdfHelper.export(items, fullFileName, new PDFParams());
		}

		status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Downloaded", fullFileName);
	}
}