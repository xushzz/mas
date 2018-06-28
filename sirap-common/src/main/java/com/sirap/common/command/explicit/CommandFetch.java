package com.sirap.common.command.explicit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.WebReader;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.command.target.TargetConsole;
	
public class CommandFetch extends CommandBase {

	private static final String KEY_FETCH = "f";
	private static final String KEY_WEB_CONTENT_PRINT = "(={1,2})";

	@Override
	public boolean handle() {
		
		solo = parseParam(KEY_FETCH + "\\s(.+?)"); 
		if(solo != null) {
			if(handleHttpRequest(solo)) {
				return true;
			}

			List<String> links = new ArrayList<>();
			List<String> records = new ArrayList<>();
			File file = parseFile(solo);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					records = IOUtil.readFileIntoList(filePath);
				}
			}
			
			for(String record: records) {
				if(EmptyUtil.isNullOrEmpty(record)) {
					continue;
				}
				String temp = record.trim();
				String url = StrUtil.parseParam(KEY_HTTP_WWW, temp);
				url = equiHttpProtoclIfNeeded(url);
				if(url != null && FileOpener.isPossibleNormalFile(url)) {
					links.add(url);
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
				export(CollUtil.toFileList(pathList));
			} else {
				export(pathList);
			}
			
			long end = System.currentTimeMillis();
			C.time2(start, end);
			
			return true;
		}
		
		params = parseParams(KEY_WEB_CONTENT_PRINT + KEY_HTTP_WWW);
		if(params != null) {
			boolean isPretty = params[0].length() == 1;
			String pageUrl = equiHttpProtoclIfNeeded(params[1]);
			WebReader xiu = new WebReader(pageUrl, g().getCharsetInUse());
			if(OptionUtil.readBooleanPRI(options, "h", false)) {
				List<String> headers = xiu.readHeaders();
				export(headers);
			} else {
				xiu.setMethodPost(OptionUtil.readBooleanPRI(options, "post", false));
				String tag;
				if(isPretty) {
					List<String> items = xiu.readIntoList();
					tag = "lines: " + items.size();
					export(items);
				} else {
					String content = xiu.readIntoString();
					tag = "chars: " + content.length();
					export(content);
				}
				String charsetInUse = xiu.getCharset();
				String serverCharset = xiu.getServerCharset();
				if(serverCharset == null) {
					serverCharset = "unclear";
				}
				String template = "{0}, charset: {1}, server: {2}.";
				C.pl2(StrUtil.occupy(template, tag, charsetInUse, serverCharset));
			}
			
			return true;
		}
		
		return false;
	}
}
