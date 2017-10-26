package com.sirap.basic.thread.business;

import com.sirap.basic.domain.MexFile;
import com.sirap.basic.thread.Worker;
import com.sirap.basic.util.IOUtil;

public class NormalFileCopier extends Worker<MexFile> {
	private String storage;
	
	public NormalFileCopier(String targetFolder) {
		this.storage = targetFolder;
	}
	
	@Override
	public void process(MexFile obj) {
		String path = obj.getPath();
		int count = countOfTasks - tasks.size();
		status(STATUS_FILE_COPY, count, countOfTasks, "Copying...", path, storage);
		IOUtil.copyFileToFolder(path, storage);
		status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Copied", storage + obj.getName());
	}
}