package com.sirap.extractor;

import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.thread.Worker;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.extractor.manager.Extractors;

public class HistoryEventsFetcher extends Worker<MexObject> {
	private String location;
	
	public HistoryEventsFetcher(String location) {
		this.location = StrUtil.useSeparator(location, "hist");
	}
	
	@Override
	public void process(MexObject mo) {
		FileUtil.makeDirectoriesIfNonExist(location);
		
		String urlParam = mo.getString();
		String shortName = urlParam.replace("-", "") + Konstants.SUFFIX_TXT;
		String fullFileName = StrUtil.useSeparator(location, shortName);
		if(FileUtil.exists(fullFileName)) {
			C.pl("Already exists " + fullFileName);
			return;
		}

		int count = countOfTasks - tasks.size();
		status(STATUS_FILE_COPY, count, countOfTasks, "Downloading...", urlParam, location);
		List<MexObject> items = Extractors.fetchHistoryEventsByDay(urlParam);
		IOUtil.saveAsTxt(items, fullFileName);

		status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "Downloaded", fullFileName);
	}
}
