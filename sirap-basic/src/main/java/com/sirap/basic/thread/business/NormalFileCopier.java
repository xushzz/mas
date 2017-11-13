package com.sirap.basic.thread.business;

import java.io.File;

import com.sirap.basic.thread.Worker;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;

public class NormalFileCopier extends Worker<File> {
	private String storage;
	
	public NormalFileCopier(String targetFolder) {
		this.storage = targetFolder;
	}
	
	@Override
	public void process(File normalFile) {
		String path = normalFile.getPath();
		int count = countOfTasks - queue.size();
		status(STATUS_FILE_COPY, count, countOfTasks, "Copying...", path, storage);
		IOUtil.copyFileToFolder(path, storage);
		status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Copied", StrUtil.useSeparator(storage, normalFile.getName()));
	}
}