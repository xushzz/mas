package com.sirap.common.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.IOUtil;

public class KeysReader {
	
	private List<String> methods;
	private String filePath;
	private String shortFileName;
	
	public KeysReader(File sourceFile, List<String> methods) {
		this.methods = methods;
		
		if(sourceFile != null) {
			this.filePath = sourceFile.getAbsolutePath();
			this.shortFileName = sourceFile.getName();
		}
	}
	
	public List<String> readKeysFromFile() {
		List<String> entries = new ArrayList<String>();
		List<String> records = IOUtil.readFileIntoList(filePath);
		for(int i = 0; i < records.size(); i++) {
			String record = records.get(i);
			int lineNumber = i + 1;
			List<String> tempList = readKeysFromRecord(lineNumber, record);
			entries.addAll(tempList);
		}
		
		return entries;
	}

	//SimpleKonfig.java 17 getValuesByKeyword("command.node.")	
	private List<String> readKeysFromRecord(int lineNumber, String record) {
		List<String> records = new ArrayList<String>();
		
		for(String method: methods) {
			String regex = method.trim() + "\\([^\\)]+\\)";
			Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(record);
			List<String> entries = new ArrayList<String>();
			while(m.find()) {
				String temp = m.group().trim();
				if(temp.contains("\"")) {
					entries.add(shortFileName + " " + lineNumber + " " + temp);
				}
			}
			records.addAll(entries);
		}
		
		return records;
	}
}
