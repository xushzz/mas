package com.sirap.common.command.explicit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.comparator.MexFileComparator;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.FileDeeper;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.MatrixUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.framework.command.FileSizeInputAnalyzer;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.common.manager.VFileManager;

public class CommandFolder extends CommandBase {
	private static final String KEY_LIST_STORAGE = ".";
	private static final String KEY_ALL_DISKS_TWO_COLONS = "::";
	private static final String KEY_VERY_IMPORTANT_FOLDER = "v";
	
	public boolean handle() {
		// :: list immediate sub files of each disk
		// ::\MV list immediate sub files of each disk with \MV, such as C:\MV, D:\MV
		String regexColons = KEY_ALL_DISKS_TWO_COLONS + "(.*)";
		String normal = parseParam(regexColons);
		String withsize = StrUtil.parseParam(regexColons, (new FileSizeInputAnalyzer(input)).getCommand());
		if(normal != null && StrUtil.equals(normal, withsize)) {
//			D.sink("seko in");
			List<String> disks = FileUtil.availableDiskNames();
			List<String> paths = Lists.newArrayList();
			for(String disk : disks) {
				String newPath = parseFolderPath(disk + normal);
				if(FileUtil.exists(newPath)) {
					paths.add(newPath);
				}
			}
			
			dealWithMexFiles(getMexFilesByPaths(paths));
			return true;
//			D.sink("seko out");
		}
		
		// deal with single folder, list sub items or show deepest file items if optioin says max
		String cleverPath = parseFolderPath(command);
		if(FileUtil.exists(cleverPath) && StrUtil.equals(command, (new FileSizeInputAnalyzer(input)).getCommand())) {
//			D.ts("into carrie");
			if(OptionUtil.readBooleanPRI(options, "max", false)) {
				FileDeeper dima = new FileDeeper(cleverPath);
				int maxLevel = dima.howDeep();
				List<MexFile> files = dima.getMaxLevelFiles();
				
				if(target.isFileRelated()) {
					export(files);
				} else {
					List<String> items = new ArrayList<>();
					for(MexFile what: files) {
						items.add(maxLevel + " " + what.getUnixPath());
					}
					
					export(items);
				}
			} else {
				dealWithMexFiles(getMexFilesByPaths(Lists.newArrayList(cleverPath)));
			}
			
//			D.ts("out of carrie");
			
			return true;
		}
		
		if(is(KEY_LIST_STORAGE)) {
			String filepath = storageWithSeparator();
			File file = FileUtil.getIfNormalFolder(filepath);
			XXXUtil.nullCheck(file, "Bad path: " + filepath);
			
			dealWithMexFiles(getMexFilesByPaths(Lists.newArrayList(filepath)));
			
			return true;
		}
		
		//analyze folder and criteria, conduct search
		InputAnalyzer sean = new FileSizeInputAnalyzer(input);
		SearchComponent jack = parseFolderPathAndCriterias(sean.getCommand());
		if(jack != null) {
//			D.ts("folder and path, start");
			this.command = sean.getCommand();
			this.target = sean.getTarget();
			this.options = sean.getOptions();
//			D.pl(command, options, target);
			List<MexFile> allMexedFiles = new ArrayList<>();
			List<String> pathList = jack.getPaths();
			for(String path : pathList) {
				String criterias = jack.getCriteria().trim();
				
				int idxOfSeparator = criterias.indexOf(' ');
				String nameCriteria = null;
				Integer depth = -1;
				
				String regexDepth = "\\d{1,2}";
				if(idxOfSeparator < 0) {
					if(StrUtil.isRegexMatched(regexDepth, criterias)) {
						depth = MathUtil.toInteger(criterias);
					} else {
						nameCriteria = criterias;
					}
				} else {
					String depthString = criterias.substring(0, idxOfSeparator);
					if(StrUtil.isRegexMatched(regexDepth, depthString)) {
						depth = MathUtil.toInteger(depthString);
						nameCriteria = criterias.substring(idxOfSeparator + 1).trim();
					} else {
						nameCriteria = criterias;
					}
				}
				
				if(depth == -1) {
					String negate = StrUtil.parseParam("!(.+)", nameCriteria);
					if(negate != null) {
						nameCriteria = negate;
					}
					depth = 0;
				}
				
				if(depth > 0) {
					depth--;
				}
				
				List<MexFile> allFiles = FileUtil.scanSingleFolder(path, depth, true);
				if(EmptyUtil.isNullOrEmpty(nameCriteria)) {
					allMexedFiles.addAll(allFiles);
				} else {
					List<MexFile> items = Colls.filter(allFiles, useSpace(nameCriteria), isCaseSensitive(), isStayCriteria());
					allMexedFiles.addAll(items);
				}
			}
			
			if(jack.isShowDetail()) {
				useHighOptions("+size");
			}
			
			dealWithMexFiles(allMexedFiles);
//			D.ts("folder and path, end");
			
			return true;
		}
		
		// very important file
		 sean = new FileSizeInputAnalyzer(input);
		String vRegex = "(" + KEY_SHOW_DETAIL + "|)" + KEY_VERY_IMPORTANT_FOLDER + "(\\s(.*?)|)";
		params = StrUtil.parseParams(vRegex, sean.getCommand());
		if(params != null && !EmptyUtil.isNullOrEmpty(params[1])) {
			this.command = sean.getCommand();
			this.target = sean.getTarget();
			this.options = sean.getOptions();
			boolean detail = !params[0].isEmpty();
			String criteria = useSpace(params[1].trim());
			List<MexFile> mexfiles = VFileManager.g().getFileRecordsByName(criteria, isCaseSensitive());
			
			if(detail) {
				useHighOptions("+size");
			}
			
			dealWithMexFiles(mexfiles);
			
			return true;
		}
		
		if(is(KEY_VERY_IMPORTANT_FOLDER + KEY_2DOTS)) {
			List<MexFile> mexfiles = VFileManager.g().getAllFileRecords();
			dealWithMexFiles(mexfiles);
			
			return true;
		}
		
		if(is(KEY_VERY_IMPORTANT_FOLDER + KEY_VERY_IMPORTANT_FOLDER)) {  
			List<String> records = VFileManager.g().getAllFolders();
			export(records);
			
			return true;
		}
		
		if(is(KEY_VERY_IMPORTANT_FOLDER + KEY_REFRESH)) {
			int[] size = VFileManager.g().refresh();
			C.pl2("Refreshed, " + size[1] + " records, before " + size[0] + ".");
			
			return true;
		}
		
		solo = parseParam(KEY_FILE_REMOVE + "\\s+(.+)");
		if(solo != null) {
			String filepath = null;

			File ball = parseFile(solo);
			if(ball != null) {
				filepath = ball.getAbsolutePath();
			} else {
				ball = parseFolder(solo);
				if(ball != null) {
					filepath = ball.getAbsolutePath();
				}
			}
			
			if(filepath != null) {
				removeFile(filepath);
			} else {
				C.pl2("Neither file nor folder: " + solo);
			}
			
			return true;
		}
		
		return false;
	}
	
	private SearchComponent parseFolderPathAndCriterias(String input) {
		String[] expArr = {"(-|)(.*?)\\s(.+)", "(-|)(.*?),\\s?(.+)"};
		List<String> allDisks = null;
		List<String> paths = new ArrayList<>();
		for(int i = 0; i < expArr.length; i++) {
			String[] params = StrUtil.parseParams(expArr[i], input);
			if(params != null) {
				boolean detail = !params[0].isEmpty();
				String sourceName = params[1];
				String criteria = params[2];
				if(sourceName.startsWith(KEY_ALL_DISKS_TWO_COLONS)) {
					if(allDisks == null) {
						allDisks = FileUtil.availableDiskNames();
					}

					for(String disk : allDisks) {
						String newSourceName = sourceName.replaceFirst("::", disk);
						String cleverPath = parseFolderPath(newSourceName);
						if(cleverPath != null) {
							paths.add(cleverPath);
						}
					}
				} else {
					String cleverPath = parseFolderPath(sourceName);
					if(cleverPath != null && FileUtil.getIfNormalFolder(cleverPath) != null) {
						paths.add(cleverPath);
					}
				}
				
				if(!EmptyUtil.isNullOrEmpty(paths)) {
					SearchComponent jack = new SearchComponent(paths, criteria);
					jack.setShowDetail(detail);
					return jack;
				}
			}
		}
		
		return null;
	}
	
	private List<MexFile> getMexFilesByPaths(List<String> paths) {
		List<MexFile> allFiles = Lists.newArrayList();
		for(String path: paths) {
//			List<MexFile> records = FileUtil.listDirectory(path);
			List<MexFile> records = FileUtil.scanSingleFolder(path, 0);
			if(!EmptyUtil.isNullOrEmpty(records)) {
				allFiles.addAll(records);
			}
		}
		
		return allFiles;
	}
	
	private void dealWithMexFiles(List<MexFile> mexfiles) {
		boolean toRemove = OptionUtil.readBooleanPRI(options, "remove", false);
		if(toRemove) {
			for(MexFile mf : mexfiles) {
				removeFile(mf.getPath());
			}
			
			return;
		}
		
		if(target.isFileRelated()) {
			export(Colls.fileListOf(mexfiles));
			return;
		}
		
		boolean orderByNameAsc = OptionUtil.readBooleanPRI(options, "byname", true);
		MexFileComparator cesc = new MexFileComparator(orderByNameAsc);
		cesc.setByTypeAsc(OptionUtil.readBoolean(options, "bytype"));
		cesc.setByDateAsc(OptionUtil.readBoolean(options, "bydate"));
		cesc.setBySizeAsc(OptionUtil.readBoolean(options, "bysize"));
		Collections.sort(mexfiles, cesc);

		useLowOptions("+k");
		boolean showAllDetail = OptionUtil.readBooleanPRI(options, "a", false);
		if(showAllDetail) {
			useLowOptions("+m");
			useLowOptions("+d");
			useLowOptions("+ss");
			useLowOptions("+s");
			useLowOptions("+k");
			useLowOptions("+h");
		}
		
		if(OptionUtil.readBooleanPRI(options, "m", false)) {
			useLowOptions("c=#s2");
			exportMatrix(MatrixUtil.matrixOf(mexfiles, options));
		} else {
			export(mexfiles);
		}
	}
}
