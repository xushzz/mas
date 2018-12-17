package com.sirap.basic.thread.business;

import com.sirap.basic.thread.WorkerItemOriented;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.HttpUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;

public class InternetFileFetcher extends WorkerItemOriented<String, String> {
	private String storage;
	private String suffixWhenObscure;
	
	public InternetFileFetcher(String storage) {
		this.storage = storage;
	}
	
	public InternetFileFetcher(String storage, String suffixWhenObscure) {
		this.storage = storage;
		this.suffixWhenObscure = suffixWhenObscure;
	}

	@Override
	public String process(String url) {
		if(url == null) {
			return null;
		}
		int count = countOfTasks - queue.size();
		String newFilename = HttpUtil.filenameByUrl(url, 15, suffixWhenObscure);
		String filePath = StrUtil.useSeparator(storage, newFilename);
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