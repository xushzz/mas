package com.sirap.basic.thread.business;

import com.sirap.basic.component.TimestampIDGenerator;
import com.sirap.basic.thread.WorkerItemOriented;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;

public class InternetFileFetcher extends WorkerItemOriented<String, String> {
	private String storage;
	private String suffixWhenObscure;
	private boolean useUniqueFilename;
	
	public InternetFileFetcher(String storage) {
		this.storage = storage;
	}
	
	public InternetFileFetcher(String storage, String suffixWhenObscure) {
		this.storage = storage;
		this.suffixWhenObscure = suffixWhenObscure;
	}
	
	public boolean isUseUniqueFilename() {
		return useUniqueFilename;
	}

	public void setUseUniqueFilename(boolean useUniqueFilename) {
		this.useUniqueFilename = useUniqueFilename;
	}

	@Override
	public String process(String url) {
		if(url == null) {
			return null;
		}
		int count = countOfTasks - queue.size();
		String unique = "";
		if(useUniqueFilename) {
			unique = TimestampIDGenerator.nextId() + "_";
		}
		String filePath = storage + unique + FileUtil.generateFilenameByUrl(url, suffixWhenObscure);
		if(FileUtil.exists(filePath)) {
			status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Existed =>", filePath);
		} else {
			status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", url);
			FileUtil.makeDirectories(storage);
			boolean flag = IOUtil.downloadNormalFile(url, filePath, true);
			if(flag) {
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Saved =>", filePath);
			} else {
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Error =>", url);
				return null;
			}
		}
		
		return filePath;
	}
}