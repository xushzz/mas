package com.sirap.common.framework.command;

import java.io.File;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.target.Target;
import com.sirap.common.framework.command.target.TargetAnalyzer;
import com.sirap.common.framework.command.target.TargetConsole;

public class InputAnalyzer {

	public static final String EXPORT_ESACPE = "!";
	public static final String EXPORT_FLAG = ">";
	
	protected String input;
	private String command;
	private String targetStr;
	private Target target;
	
	public String getInput() {
		return input;
	}

	public String getCommand() {
		return command;
	}

	public Target getTarget() {
		return target;
	}
	
	public InputAnalyzer(String input) {
		this.input = input;
		analyze();
	}
	
	public int whereToSplit() {
		String source = input;
		int indexOfGT = source.indexOf(EXPORT_FLAG);
		while(indexOfGT >= 0) {
			
			boolean flagA = isMatchedEscape(source, indexOfGT);
			if(flagA) {
				indexOfGT = source.indexOf(EXPORT_FLAG, indexOfGT + 1);
			} else {
				break;
			}
		}
		
		return indexOfGT;
	}
	
	protected boolean isMatchedEscape(String source, int indexOfGT) {
		int previousIndex = indexOfGT - EXPORT_ESACPE.length();
		if(previousIndex < 0) {
			return false;					
		}

		String left = source.substring(0, indexOfGT);
		boolean isMatched = StrUtil.endsWith(left, EXPORT_ESACPE);

		return isMatched;
	}

	public void analyze() {
		int idxExport = whereToSplit();
		if(idxExport >= 0) {
			String temp= input.substring(0, idxExport).trim();
			command = removeEscape(temp);
			
			temp = input.substring(idxExport + 1).trim();			
			targetStr = removeEscape(temp);
			
			target = parseTarget(targetStr);
		} else {
			command = removeEscape(input);
			target = new TargetConsole(true);
		}
	}
	
	private String removeEscape(String source) {
		String temp = source.replace(EXPORT_ESACPE + EXPORT_FLAG, EXPORT_FLAG);
		return temp;
	}

	private Target parseTarget(String targetStr) {
		TargetAnalyzer virgil = new TargetAnalyzer() {

			@Override
			public String getDefaultExportFolder() {
				String path = SimpleKonfig.g().pathWithSeparator("storage.export", Konstants.FOLDER_EXPORT);
				return path;
			}
			
			@Override
			public String parseRealFolderPath(String param) {
				String path = FileUtil.parseFolderPath(param, SimpleKonfig.g().getStorageWithSeprator());
				if(path == null) {
					return null;
				}
				
				return path + File.separator;
			}
		};
		
		Target target = virgil.parse(command, targetStr, SimpleKonfig.g().isEmailEnabled());
		if(target != null) {
			target.setValue(targetStr);
		}
		
		return target;
	}
}
