package com.sirap.common.framework.command;

import java.io.File;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.FileUtil;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.target.Target;
import com.sirap.common.framework.command.target.TargetAnalyzer;
import com.sirap.common.framework.command.target.TargetConsole;

public class InputAnalyzer {
	
	public static final String EXPORT_FLAG = ">";
	
	protected String input;
	private String command;
	private Target target = new TargetConsole();
	
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
		int position = input.indexOf(EXPORT_FLAG);
		return position;
	}

	public void analyze() {
		int idxExport = whereToSplit();
		if(idxExport >= 0) {
			command = input.substring(0, idxExport).trim();
			String targetStr = input.substring(idxExport + 1).trim();
			target = parseTarget(targetStr);
			if(target != null) {
				target.setValue(targetStr);
			}
		} else {
			command = input;
			target = new TargetConsole();
		}
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
		
		return virgil.parse(command, targetStr, SimpleKonfig.g().isEmailEnabled());
	}
}
