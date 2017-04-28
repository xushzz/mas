package com.sirap.common.command.explicit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.WebReader;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.domain.WeatherRecord;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.common.manager.WeatherManager;
	
public class CommandFetch extends CommandBase {

	private static final String KEY_FETCH = "f";
	private static final String KEY_WEB_PRETTY_PRINT = "=";
	private static final String KEY_WEB_UGLY_PRINT = "==";
	private static final String KEY_WEATHER = "w";

	public boolean handle() {
		
		singleParam = parseParam(KEY_FETCH + "\\s(.+?)");
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
				url = equiHttpProtoclIfNeeded(url);
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
		
		if(is(KEY_WEATHER + KEY_2DOTS)) {
			List<WeatherRecord> items = WeatherManager.g().allRecords();
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_WEATHER + "\\.([^\\.]+)");
		if(singleParam != null) {
			List<WeatherRecord> items = WeatherManager.g().search(singleParam);
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_WEB_PRETTY_PRINT + KEY_HTTP);
		if(singleParam != null) {
			String pageUrl = equiHttpProtoclIfNeeded(singleParam);
			WebReader xiu = new WebReader(pageUrl, g().getCharsetInUse(), true);
			List<String> items = xiu.readIntoList();
			String charsetInUse = xiu.getCharset();
			String serverCharset = xiu.getServerCharset();
			if(serverCharset == null) {
				serverCharset = "unclear";
			}
			export(items);
			C.pl2("charset in use: " + charsetInUse + ", charset on server: " + serverCharset);
		}
		
		singleParam = parseParam(KEY_WEB_UGLY_PRINT + KEY_HTTP);
		if(singleParam != null) {
			String pageUrl = equiHttpProtoclIfNeeded(singleParam);;
			WebReader xiu = new WebReader(pageUrl, g().getCharsetInUse(), true);
			String content = xiu.readIntoString();
			String charsetInUse = xiu.getCharset();
			String serverCharset = xiu.getServerCharset();
			if(serverCharset == null) {
				serverCharset = "unclear";
			}
			export(content);
			C.pl2("length: " + content.length() + ", charset in use: " + charsetInUse + ", charset on server: " + serverCharset);
		}
		
		return false;
	}
}
