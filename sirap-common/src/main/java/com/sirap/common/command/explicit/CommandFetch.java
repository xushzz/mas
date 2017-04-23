package com.sirap.common.command.explicit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.domain.xml.XmlItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.common.manager.WeatherManager;
	
public class CommandFetch extends CommandBase {

	private static final String KEY_FETCH = "f";
	private static final String KEY_CURL = "cur";

	public boolean handle() {
		
		String singleParam = parseParam(KEY_FETCH + "\\s(.+?)");
		if(singleParam != null) {
			if(handleHttpRequest(singleParam)) {
				return true;
			}

			List<MexedObject> links = new ArrayList<MexedObject>();
			List<String> records = new ArrayList<String>();
			File file = parseFile(singleParam);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					records = IOUtil.readFileIntoList(filePath, g().getCharsetInUse());
				}
			}
			
			for(String record: records) {
				if(EmptyUtil.isNullOrEmpty(record)) {
					continue;
				}
				String temp = record.trim();
				String url = StrUtil.parseParam(KEY_HTTP, temp);
				if(url != null && FileOpener.isPossibleNormalFile(url)) {
					links.add(new MexedObject(url));
				}
			}
			
			if(EmptyUtil.isNullOrEmpty(links)) {
				return false;
			}

			long start = System.currentTimeMillis();
			
			String temp = file.getName();
			int idxDot = temp.lastIndexOf(".");
			if(idxDot >= 1) {
				temp = temp.substring(0, idxDot);
			}
			String folderName = temp;
			String destination = miscPath() + folderName + File.separator;
			
			List<String> pathList = downloadFiles(destination, links);
			
			if(g().isGeneratedFileAutoOpen() && !pathList.isEmpty()) {
				String lastFile = pathList.get(pathList.size() - 1);
				FileOpener.open(lastFile);
			}

			if(target instanceof TargetConsole) {
				return true;
			}
			
			if(target.isFileRelated()) {
				export(CollectionUtil.toFileList(pathList));
			} else {
				export(pathList);
			}
			
			long end = System.currentTimeMillis();
			C.time2(start, end);
			
			return true;
		}
		
		singleParam = parseParam("w\\.([^\\.]+?)");
		if(singleParam != null) {
			String cityName = singleParam;
			List<XmlItem> items = WeatherManager.g().search(cityName);
			setIsPrintTotal(false);
			export(items);
		}
		
		singleParam = parseParam(KEY_CURL + "\\s+" + KEY_HTTP);
		if(singleParam != null) {
			String pageUrl = singleParam;
			String source = IOUtil.readURL(pageUrl, g().getCharsetInUse());
			export(source);
		}
		
		return false;
	}
}
