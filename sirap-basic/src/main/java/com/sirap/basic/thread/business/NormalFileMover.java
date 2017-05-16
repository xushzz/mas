package com.sirap.basic.thread.business;

import com.sirap.basic.domain.MexedFile;
import com.sirap.basic.thread.Worker;
import com.sirap.basic.util.IOUtil;

public class NormalFileMover extends Worker<MexedFile> {
	private String storage;
	
	public NormalFileMover(String targetFolder) {
		this.storage = targetFolder;
	}
	
	@Override
	public void process(MexedFile obj) {
		String path = obj.getPath();
		int count = countOfTasks - tasks.size();
		status(STATUS_FILE_COPY, count, countOfTasks, "Copying...", path, storage);
		IOUtil.copyFile(path, storage);
		status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Copied", storage + obj.getName());
	}
}