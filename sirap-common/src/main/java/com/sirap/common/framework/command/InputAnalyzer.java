package com.sirap.common.framework.command;

import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.target.Target;
import com.sirap.common.framework.command.target.TargetAnalyzer;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.common.framework.command.target.TargetFolder;

public class InputAnalyzer {

	public static final String EXPORT_FILE = "ff";
	public static final String ESACPE = "!";
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
		int previousIndex = indexOfGT - ESACPE.length();
		if(previousIndex < 0) {
			return false;					
		}

		String left = source.substring(0, indexOfGT);
		boolean isMatched = StrUtil.endsWith(left, ESACPE);

		return isMatched;
	}

	public void analyze(String rawInput) {
		String tempInput = rawInput;
		if(SimpleKonfig.g().isSuckOptionsEnabled()) {
			List<String> sucker = OptionUtil.suckOptions(tempInput);
			tempInput = sucker.get(0);
			options = sucker.get(1);
		}

		boolean fileRelated = OptionUtil.readBooleanPRI(options, "ff", false);
		
		int idxExport = whereToSplit(tempInput);
		TargetAnalyzer virgil = analyzer();
		if(idxExport >= 0) {
			String temp= tempInput.substring(0, idxExport).trim();
			command = removeEscape(temp);
			
			temp = tempInput.substring(idxExport + 1).trim();			
			String targetstr = removeEscape(temp);
			try {
				target = virgil.parse(command, targetstr);
				if(fileRelated) {
					target.setFileRelated(true);
				}
				if(TargetFolder.class.isInstance(target)) {
					target.setFileRelated(true);
				}
				XXXUtil.nullCheck(target, "target");
				target.setValue(targetstr);
			} catch (MexException ex) {
				D.pl(ex.getMessage());
				C.pl("[Will use console as target]");
				target = new TargetConsole(true);
			}
		} else {
			command = removeEscape(tempInput);
			target = new TargetConsole(true);
		}
		
		target.setCommand(rawInput);
	}
	
	private String removeEscape(String source) {
		String temp = source.replace("!>", ">");
		temp = source.replace("!$", "$");
		return temp;
	}
	
	private TargetAnalyzer analyzer() {
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
				
				return StrUtil.useSeparator(path, "");
			}
		};
		
		return virgil;
	}
}
