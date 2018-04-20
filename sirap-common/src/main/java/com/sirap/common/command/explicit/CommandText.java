package com.sirap.common.command.explicit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.domain.MexTextLine;
import com.sirap.basic.search.TextSearcher;
import com.sirap.basic.thirdparty.msoffice.MsExcelHelper;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.domain.TextSearchEngine;

public class CommandText extends CommandBase {
	//group text search
	//text file search
	
	public boolean handle() {
		// 1. conduct text search in given text file
		// 2. print given sheet when it comes to given excel file
		String[] regexArr = {"(.*?)\\s+(.+)", "(.*?)\\s*,\\s*(.+)"};
		String[] filepathAndCriteria = parseFilePathAndCriterias(input, regexArr);
		if(filepathAndCriteria != null) {
			String filePath = filepathAndCriteria[0];
			String criteria = filepathAndCriteria[1];
			
			if(FileOpener.isTextFile(filePath)) {
				List<MexObject> all = readFileIntoList(filePath);
				List<MexObject> items = CollUtil.filter(all, criteria, isCaseSensitive(), isStayCriteria());
				export(CollUtil.items2PrintRecords(items, options));
			} else if(FileUtil.isAnyTypeOf(filePath, FileUtil.SUFFIXES_EXCEL)) {
				Integer index = MathUtil.toInteger(criteria);
				if(index == null) {
					C.pl("try index like 0, 1, 2, 3... with " + filePath);
				} else {
					List<List<String>> data = MsExcelHelper.readSheetByIndex(filePath, index); 
					export(data);
				}
			}
			
			return true;
		}
		
		List<TextSearchEngine> locals = getTextSearchEngines();
		for(TextSearchEngine engine:locals) {
			boolean isMatched = conductTextSearch(engine);
			if(isMatched) {
				return true;
			}
		}
		
		return false;
	}
	
	private List<TextSearchEngine> getTextSearchEngines() {
		List<TextSearchEngine> engines = new ArrayList<TextSearchEngine>();
		List<String> items = g().getUserValuesByKeyword("text.search.");
		for(String engineInfo : items) {
			TextSearchEngine se = new TextSearchEngine();
			boolean flag = se.parse(engineInfo);
			if(flag) {
				engines.add(se);
			}
		}
		
		return engines;
	}
	
	private boolean conductTextSearch(TextSearchEngine engine) {
		String regex = engine.getPrefix() + "\\s(.+?)";
		String contentCriteria = parseParam(regex);
		if(contentCriteria != null) {
			String folders = engine.getFolders();
			String fileCriteria = engine.getFileCriteria();
			String engineOptions = engine.getOptions();
			List<MexTextLine> list = TextSearcher.search(folders, fileCriteria, contentCriteria);
			String finalOptions = OptionUtil.mergeOptions(options, engineOptions);
			export(list, finalOptions);
			
			return true;
		}
		
		return false;
	}
	
	private String[] parseFilePathAndCriterias(String input, String[] regexArr) {
		for(int i = 0; i < regexArr.length; i++) {
			String[] params = parseParams(regexArr[i]);
			if(params != null) {
				String sourceName = params[0];
				String criteria = params[1];
				
				File file = parseFile(sourceName);
				if(file == null) {
					continue;
				}
				
				String filePath = file.getAbsolutePath();
				return new String[]{filePath, criteria};
			}
		}
		
		return null;
	}
	
	public List<MexObject> readFileIntoList(String fileName) {
		String cat = IOUtil.charsetOfTextFile(fileName);
		List<MexObject> list = new ArrayList<>();
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), cat);
			BufferedReader br = new BufferedReader(isr);
			String record = br.readLine();
			int line = 0;
			while (record != null) {
				line++;
				MexObject mo = new MexObject(record);
				mo.setPseudoOrder(line);
				list.add(mo);
				record = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
}
