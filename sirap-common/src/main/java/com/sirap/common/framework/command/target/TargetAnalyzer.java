package com.sirap.common.framework.command.target;

import java.io.File;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.email.EmailCenter;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;

public abstract class TargetAnalyzer {

	public static final String KEY_CREATE_FOLDER = "=";
	public static final String KEY_TO_EXPORT_FOLDER = ".";
	public static final String KEY_EXPORT_INFO_TO_TEXTFILE = "(\\*|)(.*?)";
	public static final String KEY_EXPORT_INFO_TO_PDF = ".*\\.pdf$";
	public static final String KEY_EXPORT_INFO_TO_HTML = ".*\\.htm$";
	public static final String KEY_EXPORT_INFO_TO_EXCEL = ".*\\.xls$";
	public static final String KEY_EXPORT_FILE_TO_FOLDER = "\\$(.*?)";
	
	public Target parse(String command, String target) {
		return parse(command, target, false);
	}
	
	public Target parse(String command, String targetStr, boolean isEmailEnabled) {

		if(EmptyUtil.isNullOrEmpty(targetStr)) {
			Target target = new TargetConsole(true);
			return target;
		}
		
		Target target = null;
		
		boolean isEmailCase = targetStr.indexOf('@') >= 0;
		if(isEmailCase) {
			if(!isEmailEnabled) {
				C.pl2("Email currently disabled.");
				return null;
			}
			
			return createTargetEmail(targetStr, command);
		}
		
		String singleParam = StrUtil.parseParam(KEY_EXPORT_FILE_TO_FOLDER, targetStr);
		if(singleParam != null) {
			target = createTargetFolder(singleParam, command);
			target.setFileRelated(true);
			
			return target;
		}
		
		if(StrUtil.isRegexMatched(KEY_EXPORT_INFO_TO_PDF, targetStr)) {
			String destInfo = targetStr.trim();
			target = createTargetPDF(destInfo, command);

			return target;
		}
		
		if(StrUtil.isRegexMatched(KEY_EXPORT_INFO_TO_HTML, targetStr)) {
			String destInfo = targetStr.trim();
			target = createTargetHtml(destInfo, command);

			return target;
		}
		
		if(StrUtil.isRegexMatched(KEY_EXPORT_INFO_TO_EXCEL, targetStr)) {
			String destInfo = targetStr.trim();
			target = createTargetExcel(destInfo, command);

			return target;
		}
		
		String[] params = StrUtil.parseParams(KEY_EXPORT_INFO_TO_TEXTFILE, targetStr);
		if(params != null) {
			boolean isSirap = !params[0].isEmpty();
			String destInfo = params[1];
			TargetTxtFile txtFile = createTargetTxtFile(destInfo, command);
			if(isSirap) {
				String fileName = txtFile.getFileName().replaceAll(Konstants.DOT_TXT + "$", Konstants.DOT_SIRAP);
				txtFile.setFileName(fileName);
			}
			
			return txtFile;
		}
		
		return target; 
	}
	
	private Target createTargetFolder(String destInfo, String command) {
		String[] params = StrUtil.parseParams("(.*?)(=|)", destInfo);
		String dest = params[0];
		boolean toCreateFolder = !params[1].isEmpty();
		String newFolderName = "";
		if(toCreateFolder) {
			newFolderName = FileUtil.generateLegalFileName(command) + File.separator;
		}
		
		if(dest.isEmpty()) {
			return new TargetFolder(getDefaultExportFolder() + newFolderName);			
		}
		
		String folderPath = parseRealFolderPath(dest);
		if(folderPath != null) {
			return new TargetFolder(folderPath + newFolderName);	
		} else {
			newFolderName = FileUtil.generateLegalFileName(dest) + File.separator;
			return new TargetFolder(getDefaultExportFolder() + newFolderName);
		}
	}
	
	private TargetTxtFile createTargetTxtFile(String destInfo, String command) {
		String commandConvertedFileName = FileUtil.generateLegalFileName(command) + Konstants.DOT_TXT;
		if(destInfo.isEmpty() || destInfo.equalsIgnoreCase(KEY_TO_EXPORT_FOLDER)) {
			return new TargetTxtFile(getDefaultExportFolder(), commandConvertedFileName);			
		}
		
		String folderPath = parseRealFolderPath(destInfo);
		if(folderPath != null) {
			return new TargetTxtFile(folderPath, commandConvertedFileName);
		}
		
		String[] folderAndFile = FileUtil.splitFolderAndFile(destInfo);
		if(!EmptyUtil.isNullOrEmptyOrBlank(folderAndFile[0])) {
			folderPath = parseRealFolderPath(folderAndFile[0]);
			if(folderPath != null) {
				String newFileName = FileUtil.generateLegalFileName(folderAndFile[1]);
				if(needToAddExtensionTxt(newFileName)) {
					newFileName += Konstants.DOT_TXT;
				}
				
				return new TargetTxtFile(folderPath, newFileName);
			}
		}

		String newFileName = FileUtil.generateLegalFileName(destInfo);
		if(needToAddExtensionTxt(newFileName)) {
			newFileName += Konstants.DOT_TXT;
		}
		
		return new TargetTxtFile(getDefaultExportFolder(), newFileName);
	}
	
	private boolean needToAddExtensionTxt(String origin) {
		String solidExtensions = "txt,md";
		for(String item : StrUtil.split(solidExtensions)) {
			if(StrUtil.endsWith(origin, "." + item)) {
				return false;
			}
		}
		
		return true;
	}
	
	private TargetPDF createTargetPDF(String destInfo, String command) {
		String commandConvertedFileName = FileUtil.generateLegalFileName(command) + Konstants.DOT_PDF;
		if(destInfo.equalsIgnoreCase(Konstants.DOT_PDF)) {
			return new TargetPDF(getDefaultExportFolder(), commandConvertedFileName);			
		}
		
		String[] folderAndFile = FileUtil.splitFolderAndFile(destInfo);
		if(!EmptyUtil.isNullOrEmptyOrBlank(folderAndFile[0])) {
			String folderPath = parseRealFolderPath(folderAndFile[0]);
			if(folderPath != null) {
				String newFileName = folderAndFile[1];
				if(newFileName.equalsIgnoreCase(Konstants.DOT_PDF)) {
					newFileName = commandConvertedFileName;
				}
			
				return new TargetPDF(folderPath, newFileName);
			}
		}

		String newFileName = FileUtil.generateLegalFileName(destInfo);
		
		return new TargetPDF(getDefaultExportFolder(), newFileName);
	}
	
	private TargetHtml createTargetHtml(String destInfo, String command) {
		String commandConvertedFileName = FileUtil.generateLegalFileName(command) + Konstants.DOT_HTM;
		if(destInfo.equalsIgnoreCase(Konstants.DOT_HTM)) {
			return new TargetHtml(getDefaultExportFolder(), commandConvertedFileName);			
		}
		
		String[] folderAndFile = FileUtil.splitFolderAndFile(destInfo);
		if(!EmptyUtil.isNullOrEmptyOrBlank(folderAndFile[0])) {
			String folderPath = parseRealFolderPath(folderAndFile[0]);
			if(folderPath != null) {
				String newFileName = folderAndFile[1];
				if(newFileName.equalsIgnoreCase(Konstants.DOT_HTM)) {
					newFileName = commandConvertedFileName;
				}
			
				return new TargetHtml(folderPath, newFileName);
			}
		}

		String newFileName = FileUtil.generateLegalFileName(destInfo);
		
		return new TargetHtml(getDefaultExportFolder(), newFileName);
	}
	
	private TargetExcel createTargetExcel(String destInfo, String command) {
		String commandConvertedFileName = FileUtil.generateLegalFileName(command) + Konstants.DOT_EXCEL;
		if(destInfo.equalsIgnoreCase(Konstants.DOT_EXCEL)) {
			return new TargetExcel(getDefaultExportFolder(), commandConvertedFileName);			
		}
		
		String[] folderAndFile = FileUtil.splitFolderAndFile(destInfo);
		if(!EmptyUtil.isNullOrEmptyOrBlank(folderAndFile[0])) {
			String folderPath = parseRealFolderPath(folderAndFile[0]);
			if(folderPath != null) {
				String newFileName = folderAndFile[1];
				if(newFileName.equalsIgnoreCase(Konstants.DOT_EXCEL)) {
					newFileName = commandConvertedFileName;
				}
			
				return new TargetExcel(folderPath, newFileName);
			}
		}

		String newFileName = FileUtil.generateLegalFileName(destInfo);
		
		return new TargetExcel(getDefaultExportFolder(), newFileName);
	}
	
	public static Target createTargetEmail(String targetStr, String command) {
		String[] params = StrUtil.parseParams("(\\$|)(.*?)", targetStr);
		boolean fileRelated = !params[0].isEmpty();
		String temp = params[1];
		String mixedAddresses = temp;
		String subject = command;
		int idxComma = temp.indexOf(",");
		if(idxComma > 0) {
			mixedAddresses = temp.substring(0, idxComma).trim();
			subject = temp.substring(idxComma + 1).trim();
		}
		
		List<String> toList = EmailCenter.parseLegalAddresses(mixedAddresses);
		if(!EmptyUtil.isNullOrEmpty(toList)) {
			Target email = new TargetEmail(subject, toList);
			email.setFileRelated(fileRelated);
			return email;
		}
		
		return null;
	}
	
	public abstract String getDefaultExportFolder();
	public abstract String parseRealFolderPath(String pseudoFolderpath);
}