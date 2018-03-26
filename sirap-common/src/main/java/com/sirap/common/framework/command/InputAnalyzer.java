package com.sirap.common.framework.command;

import java.io.File;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.target.Target;
import com.sirap.common.framework.command.target.TargetAnalyzer;
import com.sirap.common.framework.command.target.TargetConsole;

public class InputAnalyzer {

	public static final String EXPORT_ESACPE = "!";
	public static final String EXPORT_FLAG = ">";
	public static final String OPTIONS_FLAG = "$";
	
	private String command;
	private String options;
	private Target target;

	public String getCommand() {
		return command;
	}
	
	public String getOptions() {
		return options;
	}
	
	public void setOptions(String options) {
		this.options = options;
	}

	public Target getTarget() {
		return target;
	}
	
	public InputAnalyzer(String input) {
		analyze(input);
	}
	
	public int whereToSplit(String source) {
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

	public void analyze(String rawInput) {
		String tempInput = rawInput;
		if(SimpleKonfig.g().isSuckOptionsEnabled()) {
			List<String> sucker = OptionUtil.suckOptions(tempInput);
			tempInput = sucker.get(0);
			options = sucker.get(1);
		}
		int idxExport = whereToSplit(tempInput);
		if(idxExport >= 0) {
			String temp= tempInput.substring(0, idxExport).trim();
			command = removeEscape(temp);
			
			temp = tempInput.substring(idxExport + 1).trim();			
			String targetStr = removeEscape(temp);
			
			target = parseTarget(targetStr);
		} else {
			command = removeEscape(tempInput);
			target = new TargetConsole(true);
		}
		
		command = command.replace("!$", "$");
	}
	
	private String removeEscape(String source) {
		String temp = source.replace(EXPORT_ESACPE + EXPORT_FLAG, EXPORT_FLAG);
		return temp;
	}

	private Target parseTarget(String targetStr) {
		TargetAnalyzer virgil = new TargetAnalyzer() {

			@Override
			public String getDefaultExportFolder() {
				String path = SimpleKonfig.g().pathOf("storage.export", Konstants.FOLDER_EXPORT);
				return path;
			}
			
			@Override
			public String parseRealFolderPath(String param) {
				String path = FileUtil.parseFolderPath(param, SimpleKonfig.g().getStorageWithSeparator());
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
