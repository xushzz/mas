package com.sirap.common.framework.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.component.html.HtmlExporter;
import com.sirap.basic.domain.MexedFile;
import com.sirap.basic.email.EmailCenter;
import com.sirap.basic.output.ConsoleParams;
import com.sirap.basic.output.ExcelParams;
import com.sirap.basic.output.HtmlParams;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.TrumpUtil;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.target.Target;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.common.framework.command.target.TargetDefault;
import com.sirap.common.framework.command.target.TargetEmail;
import com.sirap.common.framework.command.target.TargetExcel;
import com.sirap.common.framework.command.target.TargetFolder;
import com.sirap.common.framework.command.target.TargetHtml;
import com.sirap.common.framework.command.target.TargetPDF;
import com.sirap.common.framework.command.target.TargetTxtFile;

public class Exporter {
	
	public static void exportList(String command, List<Object> records, Target target) {
		if(EmptyUtil.isNullOrEmpty(records)) {
			return;
		}
		
		if(target instanceof TargetDefault) {
			TargetConsole console = new TargetConsole();
			simplePrint(records, console.getParams());
			return;
		}
		
		if(target instanceof TargetConsole) {
			TargetConsole console = (TargetConsole)target;
			simplePrint(records, console.getParams());
			return;
		}
		
		boolean isExportWithTs = SimpleKonfig.g().isExportWithTimestampEnabled();
		if(target instanceof TargetTxtFile) {
			TargetTxtFile txtFile = (TargetTxtFile)target;
			String filePath = isExportWithTs ? txtFile.getTimestampPath() : txtFile.getFilePath();
			if(FileUtil.isSirapFile(filePath)) {
				String content = StrUtil.connectWithLineSeparator(records);
				String passcode = SimpleKonfig.g().getSecurityPasscode();
				String encoded = TrumpUtil.encodeBySIRAP(content, passcode);
				List<String> list = new ArrayList<>();
				list.add(encoded);
				IOUtil.saveAsTxt(list, filePath);
			} else {
				IOUtil.saveAsTxt(records, filePath);
			}
			C.pl2("Exported => " + filePath);
			if(SimpleKonfig.g().isGeneratedFileAutoOpen()) {
				FileOpener.open(filePath);
			}
			return;
		}
		
		if(target instanceof TargetPDF) {
			TargetPDF pdf = (TargetPDF)target;
			String filePath = isExportWithTs ? pdf.getTimestampPath() : pdf.getFilePath();
			PDFParams params = pdf.getParams();
			params.setPrintGreyRow(SimpleKonfig.g().isPrintGreyRowWhenPDF());
			params.setPrintTopInfo(SimpleKonfig.g().isPrintTopInfoWhenPDF());
			params.setUseAsianFont(SimpleKonfig.g().isAsianFontWhenPDF());
			
			String topInfo = command;
			if(records != null && records.size() > 5) {
				topInfo = "(" + records.size() + ") " + topInfo;
			}
			
			params.setTopInfo(topInfo);
			
			IOUtil.saveAsPDF(records, filePath, params);
			C.pl2("Exported => " + filePath);
			if(SimpleKonfig.g().isGeneratedFileAutoOpen()) {
				FileOpener.open(filePath);
			}
			
			return;
		}
		
		if(target instanceof TargetExcel) {
			TargetExcel kay = (TargetExcel)target;
			String filePath = isExportWithTs ? kay.getTimestampPath() : kay.getFilePath();
			ExcelParams params = kay.getParams();
			
			String topInfo = command;
			if(records != null && records.size() > 5) {
				topInfo = "(" + records.size() + ") " + topInfo;
			}
			
			params.setTopInfo(topInfo);
			
			IOUtil.saveAsExcel(records, filePath, params);
			C.pl2("Exported => " + filePath);
			if(SimpleKonfig.g().isGeneratedFileAutoOpen()) {
				FileOpener.open(filePath);
			}
			
			return;
		}
		
		if(target instanceof TargetHtml) {
			TargetHtml html = (TargetHtml)target;
			String filePath = isExportWithTs ? html.getTimestampPath() : html.getFilePath();
			HtmlParams params = html.getParams();
			
			String topInfo = command;
			if(records != null && records.size() > 5) {
				topInfo = "(" + records.size() + ") " + topInfo;
			}
			
			params.setTopInfo(topInfo);
			List<String> htmlTemplate = IOUtil.readResourceIntoList("/template_nice.html");
			List<String> htmlContent = HtmlExporter.generateHtmlContent(htmlTemplate, records, params);
			
			IOUtil.saveAsTxt(htmlContent, filePath);
			C.pl2("Exported => " + filePath);
			if(SimpleKonfig.g().isGeneratedFileAutoOpen()) {
				FileOpener.open(filePath);
			}
			
			return;
		}
		
		if(target instanceof TargetFolder) {
			TargetFolder folder = (TargetFolder)target;
			String filePath = folder.getPath();
			List<MexedFile> mexedFiles = new ArrayList<MexedFile>();
			for(Object item: records) {
				if(item instanceof File) {
					File file = (File)item;
					mexedFiles.add(new MexedFile(file));
				} else if (item instanceof MexedFile) {
					mexedFiles.add((MexedFile)item);
				}
			}
			
			if(mexedFiles.size() > 0) {
				long start = System.currentTimeMillis();
				IOUtil.copyMexedFiles(mexedFiles, filePath);
				long end = System.currentTimeMillis();
				C.time2(start, end);
			}
		}
		
		if(target instanceof TargetEmail) {
			TargetEmail email = (TargetEmail)target;
			String subject = email.getSubject();
			List<String> toList = email.getToList();
			boolean useNewThread = SimpleKonfig.g().isYes("email.send.newthread");
			EmailCenter.g().sendEmail(records, toList, subject, !useNewThread);
			C.pl();
		}
		
		return;
	}
	
	@SuppressWarnings("rawtypes")
	public static void simplePrint(List records, ConsoleParams params) {
		if(params == null) {
			params = new ConsoleParams(true, false);
		}
		
		if(params.isToSplit()) {
			for(Object record:records) {
				List<String> splittedRecords = CollectionUtil.splitIntoRecords(record + "", params.getCharsPerLineWhenSplit());
				C.listWithoutTotal(splittedRecords);
			}
			
			if(records.size() > 5 && params.isPrintTotal()) {
				C.total(records.size());
			}
		} else {
			Object startObj = SimpleKonfig.g().getStash().get("startInMillis");
			if(startObj instanceof Long) {
				long start = (Long)startObj;
				C.list(records, params.isPrintTotal(), start);
			} else {
				C.list(records, params.isPrintTotal());
			}
		}

		C.pl();
		return;
	}
}
