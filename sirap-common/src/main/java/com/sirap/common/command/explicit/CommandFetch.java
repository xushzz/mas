package com.sirap.common.command.explicit;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.WebReader;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.domain.TZRecord;
import com.sirap.common.extractor.CommonExtractors;
import com.sirap.common.extractor.WorldTimeBJTimeOrgExtractor;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.common.framework.command.target.TargetPDF;
import com.sirap.common.manager.TimeZoneManager;
	
public class CommandFetch extends CommandBase {

	private static final String KEY_FETCH = "f";
	private static final String KEY_WEB_CONTENT_PRINT = "(={1,2})";
	private static final String KEY_DATETIME_GMT = "d.";
	private static final String KEY_DATETIME_TIMEZONE = "d\\.(.{1,20})";

	{
		helpMeanings.put("timeserver.bjtimes", WorldTimeBJTimeOrgExtractor.URL_TIME);
	}
	
	@Override
	public boolean handle() {
		
		solo = parseSoloParam(KEY_FETCH + "\\s(.+?)"); 
		if(solo != null) {
			if(handleHttpRequest(solo)) {
				return true;
			}

			List<MexObject> links = new ArrayList<MexObject>();
			List<String> records = new ArrayList<String>();
			File file = parseFile(solo);
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
				String url = StrUtil.parseParam(KEY_HTTP_WWW, temp);
				url = equiHttpProtoclIfNeeded(url);
				if(url != null && FileOpener.isPossibleNormalFile(url)) {
					links.add(new MexObject(url));
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
		
		params = parseParams(KEY_WEB_CONTENT_PRINT + KEY_HTTP_WWW);
		if(params != null) {
			boolean isPretty = params[0].length() == 1;
			String pageUrl = equiHttpProtoclIfNeeded(params[1]);
			WebReader xiu = new WebReader(pageUrl, g().getCharsetInUse(), true);
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
		
		if(is(KEY_DATETIME_GMT)) {
			Date date = CommonExtractors.getWorldTime();
			if(date != null) {
				export(DateUtil.displayDateWithGMT(date, DateUtil.HOUR_Min_Sec_AM_WEEK_DATE, g().getLocale(), 0));
			} else {
				export("UTC unavailable.");
			}
			
    		return true;
		}
		
		solo = parseSoloParam(KEY_DATETIME_TIMEZONE);
		if(solo != null) {
			List<TZRecord> records = TimeZoneManager.g().getTimeZones(solo, g().getLocale());
			if(!EmptyUtil.isNullOrEmpty(records)) {
				if(target instanceof TargetPDF) {
					int[] cellsWidth = {5, 1, 5};
					int[] cellsAlign = {0, 1, 2};
					PDFParams pdfParams = new PDFParams(cellsWidth, cellsAlign);
					target.setParams(pdfParams);
					List<List<String>> items = CollectionUtil.items2PDFRecords(records);
					export(items);
				} else {
					export(CollectionUtil.items2PrintRecords(records));					
				}

	    		return true;
			}
		}
		
		return false;
	}
}
