package com.sirap.titus;

import java.io.File;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.thread.Worker;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.third.msoffice.PdfHelper;

public abstract class BookXFetcher extends Worker<ChapterSense> {
	private String versionFolder;
	private String fileType = Konstants.DOT_TXT;
	
	public BookXFetcher(String bibleFolder) {
		this.versionFolder = bibleFolder;
	}
	
	public BookXFetcher(String versionFolder, String fileType) {
		this.versionFolder = versionFolder;
		this.fileType = fileType;
	}
	
	@Override
	public void process(ChapterSense sense) {
		String bookFolderDir = versionFolder + sense.getBook().getNameWithNiceOrder();
		FileUtil.makeDirectories(bookFolderDir);
		
		String fileName = sense.getChapterNameWithNiceOrder() +  fileType;
		String fullFileName = bookFolderDir + File.separator + fileName;
		File kid = new File(fullFileName);
		if(kid.exists()) {
			if(kid.length() == 0) {
				kid.delete();
			} else {
				C.pl("Already exists " + fullFileName);
				return;
			}
		}

		int count = countOfTasks - queue.size();
		status(STATUS_FILE_COPY, count, countOfTasks, "Downloading...", sense.getBook().getName() + " " + sense.getChapterNumber(), versionFolder);
		List<String> lines = getVerses(sense.getBook(), sense.getChapterNumber(), sense.getVersion());
		if(lines.isEmpty()) {
			C.pl2("Fetch no verse for book {} chapter {1} from {2}", sense.getBook().getName(), sense.getChapterNumber());
		} else {
			if(StrUtil.equals(Konstants.DOT_TXT, fileType)) {
				IOUtil.saveAsTxtWithUTF8(lines, fullFileName);
			} else if(StrUtil.equals(Konstants.DOT_PDF, fileType)) {
				PdfHelper.export(lines, fullFileName, new PDFParams());
			}
		}

		status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Downloaded", fullFileName);
	}
	
	public abstract List<String> getVerses(BibleBook book, int chapterId, String version);
}