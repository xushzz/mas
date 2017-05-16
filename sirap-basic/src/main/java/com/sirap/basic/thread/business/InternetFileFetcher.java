package com.sirap.basic.thread.business;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.thread.WorkerGeneralItemOriented;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;

public class InternetFileFetcher extends WorkerGeneralItemOriented<MexedObject> {
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
	public Object process(MexedObject link) {
		Object tempObj = link.getObj();
		if(tempObj == null) {
			return null;
		}
		
		String url = tempObj.toString();
		int count = countOfTasks - tasks.size();

		status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Fetching...", url);

		String filePath = storage + FileUtil.generateFilenameByUrl(url, suffixWhenObscure);
		if(FileUtil.exists(filePath)) {
			status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Existed =>", filePath);
		} else {
			FileUtil.makeDirectoriesIfNonExist(storage);
			boolean flag = IOUtil.downloadNormalFile(url, filePath);
			if(flag) {
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Saved =>", filePath);
			} else {
				return null;
			}
		}
		
		return filePath;
	}
}