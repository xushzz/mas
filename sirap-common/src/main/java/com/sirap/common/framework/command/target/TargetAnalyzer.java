package com.sirap.common.framework.command.target;

import java.io.File;
import java.util.List;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MiscUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.third.email.base.EmailCenter;
import com.sirap.third.http.HttpHelper;

public abstract class TargetAnalyzer {
	
	public Target parse(String command, String targetstr) {
		XXXUtil.nullOrEmptyCheck(targetstr);
		if(EmptyUtil.isNullOrEmpty(targetstr)) {
			return new TargetConsole(true);
		}

		Target tweb = createTargetWeb(targetstr, command);
		if(tweb != null) {
			return tweb;			
		}

		Target tunix = createTargetUnix(targetstr, command);
		if(tunix != null) {
			tunix.setFileRelated(true);
			return tunix;			
		}

		Target temail = createTargetEmail(targetstr, command);
		if(temail != null) {
			if(SimpleKonfig.g().isEmailEnabled()) {
				return temail;
			} else {
				XXXUtil.alert("Email currently unavailable.");
			}
		}

		Target tfolder = createTargetFolder(targetstr, command);
		if(tfolder != null) {
			return tfolder;
		}

		Target tfile = createTargetFile(targetstr, command);
		if(tfile != null) {
			return tfile;
		}
		
		String def = "." + FileUtil.EXTENSIONS_TEXT.get(0);
		tfile = createTargetFile(targetstr + def, command);
		if(tfile != null) {
			return tfile;
		}
		
		return null; 
	}

	/****
	 * [.html]
	 * [.txt]
	 * [E:/cake/abc.txt]
	 * [E:/cake/.txt]
	 * [E:/cake/abc]
	 * @param targetstr
	 * @param command
	 * @return
	 */
	private TargetFile targetOf(String current) {
		if(StrUtil.isIn(current, FileUtil.EXTENSIONS_EXCEL)) {
			return new TargetExcel();
		}
		
		if(StrUtil.isIn(current, FileUtil.EXTENSIONS_PDF)) {
			return new TargetPdf();
		}
		
		if(StrUtil.isIn(current, FileUtil.EXTENSIONS_TEXT)) {
			return new TargetText();
		}
		
		if(StrUtil.isIn(current, FileUtil.EXTENSIONS_HTML)) {
			return new TargetHtml();
		}
		
		if(StrUtil.isIn(current, FileUtil.EXTENSIONS_SIRAP)) {
			return new TargetSirap();
		}
		
		return null;		
	}
	
	private TargetFile createTargetFile(String targetstr, String command) {
		XXXUtil.nullOrEmptyCheck(targetstr);
		
		String defaultFolder = getDefaultExportFolder();
		String commandConverted = FileUtil.generateLegalFileName(command);

		String typeSecret = "." + FileUtil.EXTENSIONS_SIRAP.get(0);
		String sirapfile = StrUtil.parseParam("\\*(.*)", targetstr);
		if(sirapfile != null) {
			if(sirapfile.isEmpty()) {
				String filename = commandConverted + typeSecret;
				return new TargetSirap(defaultFolder, filename);
			}
			String tempAK = parseRealFolderPath(sirapfile);
			if(tempAK != null) {
//				D.pl(101);
				String filename = commandConverted + typeSecret;
				return new TargetText(tempAK, filename);
			}
		}
		
		String typeText = "." + FileUtil.EXTENSIONS_TEXT.get(0);
		if(StrUtil.equals(targetstr, ".")) {
//			D.pl(200);
			String filename = commandConverted + typeText;
			return new TargetText(defaultFolder, filename);
		}
		
		String folderpath = parseRealFolderPath(targetstr);
		if(folderpath != null) {
//			D.pl(201);
			String filename = commandConverted + typeText;
			return new TargetText(folderpath, filename);
		}
		
		String[] arr = FileUtil.filenameAndExtensionOf(targetstr);
//		D.pla(targetstr, command, arr);
		if(arr == null) {
			//[abc.pdfs]
//			D.pl(203);
			return null;
		}
		
		String leftoflastdot = arr[0];
		String extension = arr[1];
		TargetFile target = targetOf(extension);
		if(target == null) {
			return null;
		}
		
		if(leftoflastdot.isEmpty()) {
			//[.txt]
			String truefilename = FileUtil.generateLegalFileName(command) + targetstr;
			target.setFilename(truefilename);
			target.setFolderpath(getDefaultExportFolder());
			
			return target;
		}
		
		//[E:]
		File folder = FileUtil.getIfNormalFolder(targetstr);
		if(folder != null) {
			String truefilename = FileUtil.generateLegalFileName(command);
			target.setFilename(truefilename);
			target.setFolderpath(getDefaultExportFolder());
			
			return target;
		}
		
		arr = FileUtil.splitByLastFileSeparator(targetstr);
//		D.pla(arr);
		String parentfolder = arr[0];
		String filenameinfo = arr[1];
		String[] arr2 = FileUtil.filenameAndExtensionOf(filenameinfo);
//		D.pla(parentfolder, arr2[0], arr2[1]);
		String shortname;
		if(arr2[0].isEmpty()) {
			shortname = FileUtil.generateLegalFileName(command) + filenameinfo;
		} else {
			shortname = FileUtil.generateLegalFileName(filenameinfo);
		}
		
		if(EmptyUtil.isNullOrEmpty(parentfolder)) {
//			D.pl(100);
			//[abc.txt]
			//use def exp
			target.setFolderpath(getDefaultExportFolder());
			target.setFilename(shortname);
		} else {
//			D.pl(200);
			//[E:/exist/abc.txt]
			//[E:/nonexist/abc.txt]
			String tempA = parseRealFolderPath(parentfolder);
			if(tempA != null) {
//				D.pl(201);
				//[E:/exist/abc.txt]
				target.setFolderpath(tempA);
				target.setFilename(shortname);
			} else {
//				D.pl(202);
				//[E:/nonexist/abc.txt]
				//use def exp
				shortname = FileUtil.generateLegalFileName(targetstr);
				target.setFolderpath(getDefaultExportFolder());
				target.setFilename(shortname);
			}
		}
		
		return target;
	}
	
	public Target createTargetFolder(String origin, String command) {
		String defaultFolder = getDefaultExportFolder();
		
		String targetstr = StrUtil.parseParam("#(.*)", origin);
		if(targetstr == null) {
			return null;
		}
		
		if(targetstr.isEmpty()) {
			return new TargetFolder(defaultFolder);
		}
		
		String folderpath = parseRealFolderPath(targetstr);
//		D.pla("createTargetFolder", targetstr, command, folderpath);
		if(folderpath != null) {
			return new TargetFolder(folderpath);
		}
		
		String[] arr = FileUtil.splitByLastFileSeparator(targetstr);
//		D.pla(arr);
		if(arr[0] != null) {
			//[E:/exists/jack]
			//[E:/nonexists/jack]
//			D.pl(100);
			String parentfolderpath = parseRealFolderPath(arr[0]);
			if(parentfolderpath != null) {
				//[E:/exists/jack]
//				D.pl(101);
				//create new folder
				String newfolderpath = StrUtil.useSeparator(parentfolderpath, arr[1]);
				(new File(newfolderpath)).mkdirs();
				return new TargetFolder(newfolderpath);
			} else {
				//[E:/nonexists/jack]
//				D.pl(102);
				String legalfoldername = FileUtil.generateLegalFileName(targetstr);
				String newfolderpath = StrUtil.useSeparator(getDefaultExportFolder(), legalfoldername);
				(new File(newfolderpath)).mkdirs();
				return new TargetFolder(newfolderpath);	
			}
		} else {
			//nicejacket
//			D.pl(200);
			String legalfoldername = FileUtil.generateLegalFileName(targetstr);
			String newfolderpath = StrUtil.useSeparator(getDefaultExportFolder(), legalfoldername);
			(new File(newfolderpath)).mkdirs();
			return new TargetFolder(newfolderpath);	
		}
	}
	
	/*****
	 * 
	 * @param targetStr
	 * [@]
	 * [aol007@163.com]
	 * [aol007@163.com, lovely basketball]
	 * [john@163.com;jack@173.com, lovely basketball]
	 * @param command
	 * @return
	 */
	public static Target createTargetEmail(String targetstr, String command) {
		String mixedAddresses;
		String subject;
		int idxComma = targetstr.indexOf(",");
		if(idxComma > 0) {
			mixedAddresses = targetstr.substring(0, idxComma).trim();
			subject = targetstr.substring(idxComma + 1).trim();
		} else {
			mixedAddresses = targetstr;
			subject = command;
		}
		
		List<String> toList = EmailCenter.parseLegalAddresses(mixedAddresses);
		if(!toList.isEmpty()) {
			Target email = new TargetEmail(subject, toList);
			return email;
		}
		
		return null;
	}
	
	public static Target createTargetUnix(String targetstr, String command) {
		XXXUtil.nullOrEmptyCheck(targetstr);
		
		if(targetstr.startsWith("/")) {
			return new TargetUnix(targetstr);
		}
		
		return null;
	}
	
	/***
	 * 
	 * @param targetstr
	 * [#]
	 * [http://remote.com/upload]
	 * @param command
	 * @return
	 */
	public static Target createTargetWeb(String targetstr, String command) {
		String remoteurl;
		String subject;
		int idxComma = targetstr.indexOf(",");
		if(idxComma > 0) {
			remoteurl = targetstr.substring(0, idxComma).trim();
			subject = targetstr.substring(idxComma + 1).trim();
		} else {
			remoteurl = targetstr;
			String commandConverted = FileUtil.generateLegalFileNameBySpace(command);
			subject = commandConverted;
		}
		
		String site = StrUtil.parseParam(":(.*)", remoteurl);
		if(site != null) {
			if(site.isEmpty()) {
				remoteurl = HttpHelper.URL_AKA10_REPO;
			} else {
				remoteurl = SimpleKonfig.g().getUserValueOf(site);
				String msg = ":No user config for website key: " + site;
				XXXUtil.nullOrEmptyCheck(remoteurl, msg);
			}
		}
		
		if(MiscUtil.isHttp(remoteurl)) {
			Target tweb = new TargetWeb(subject, remoteurl);
			return tweb;
		}
		
		return null;
	}
	
	public abstract String getDefaultExportFolder();
	
	public abstract String parseRealFolderPath(String pseudoFolderpath);
}