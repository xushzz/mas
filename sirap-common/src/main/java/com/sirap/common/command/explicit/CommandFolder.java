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
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
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
			if(!EmptyUtil.isNullOrEmpty(paths)) {
				dealWithFolderPaths(paths);
				return true;
			}
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
				dealWithFolderPaths(Lists.newArrayList(cleverPath));
			}
			
//			D.ts("out of carrie");
			
			return true;
		}
		
		if(is(KEY_LIST_STORAGE)) {
			File file = FileUtil.getIfNormalFolder(storageWithSeparator());
			if(file != null) {
				String path = file.getAbsolutePath();
				if(path != null) {
					List<MexFile> allFiles = FileUtil.listDirectory(path);
					boolean orderByNameAsc = OptionUtil.readBooleanPRI(options, "byname", true);
					MexFileComparator cesc = new MexFileComparator(orderByNameAsc);
					boolean orderByTypeDirAtTop = OptionUtil.readBooleanPRI(options, "bytype", true);
					cesc.setByTypeAsc(orderByTypeDirAtTop);
					cesc.setByDateAsc(OptionUtil.readBoolean(options, "bydate"));
					cesc.setBySizeAsc(OptionUtil.readBoolean(options, "bysize"));
					Collections.sort(allFiles, cesc);
					String tempOptions = options;
					if(options == null || !options.contains("kids")) {
						tempOptions += ",+kids";
					}

					export(allFiles, tempOptions);
				}
			}
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
					List<MexFile> items = Colls.filter(allFiles, nameCriteria, isCaseSensitive(), isStayCriteria());
					allMexedFiles.addAll(items);
				}
			}
			
			if(EmptyUtil.isNullOrEmpty(allMexedFiles)) {
				exportEmptyMsg();
			} else {
				boolean toRemove = OptionUtil.readBooleanPRI(options, "remove", false);
				if(toRemove) {
					for(MexFile mf : allMexedFiles) {
						removeFile(mf.getPath());
					}
					
					return true;
				}
				if(target.isFileRelated()) {
					List<File> files = new ArrayList<File>();
					for(MexFile mf:allMexedFiles) {
						File fileItem = mf.getFile();
						if(fileItem.isFile()) {
							files.add(fileItem);
						}
					}
					
					export(files);
				} else {
					boolean orderByNameAsc = OptionUtil.readBooleanPRI(options, "byname", true);
					MexFileComparator cesc = new MexFileComparator(orderByNameAsc); 
					cesc.setByTypeAsc(OptionUtil.readBoolean(options, "bytype"));
					cesc.setByDateAsc(OptionUtil.readBoolean(options, "bydate"));
					cesc.setBySizeAsc(OptionUtil.readBoolean(options, "bysize"));
					Collections.sort(allMexedFiles, cesc);
					String tempOptions = jack.isShowDetail() ? "+size" : "";
					String finalOptions = OptionUtil.mergeOptions(options, tempOptions);
					export(allMexedFiles, finalOptions);
				}
			}
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
			String criteria = params[1].trim();
			List<MexFile> records = VFileManager.g().getFileRecordsByName(criteria, isCaseSensitive());
			boolean toRemove = OptionUtil.readBooleanPRI(options, "remove", false);
			if(toRemove) {
				for(MexFile mf : records) {
					removeFile(mf.getPath());
				}
				
				return true;
			}
			if(target.isFileRelated()) {
				Collections.sort(records);
				export(Colls.toFileList(records));
			} else {
				boolean orderByNameAsc = OptionUtil.readBooleanPRI(options, "byname", true);
				MexFileComparator cesc = new MexFileComparator(orderByNameAsc); 
				cesc.setByDateAsc(OptionUtil.readBoolean(options, "bydate"));
				cesc.setBySizeAsc(OptionUtil.readBoolean(options, "bysize"));
				Collections.sort(records, cesc);
				String tempOptions = detail ? "+size" : "";
				String finalOptions = OptionUtil.mergeOptions(options, tempOptions);
				export(records, finalOptions);
			}
			
			return true;
		}
		
		if(is(KEY_VERY_IMPORTANT_FOLDER + KEY_2DOTS)) {
			List<MexFile> records = VFileManager.g().getAllFileRecords();
			boolean orderByNameAsc = OptionUtil.readBooleanPRI(options, "byname", true);
			MexFileComparator cesc = new MexFileComparator(orderByNameAsc); 
			cesc.setByDateAsc(OptionUtil.readBoolean(options, "bydate"));
			cesc.setBySizeAsc(OptionUtil.readBoolean(options, "bysize"));
			Collections.sort(records, cesc);

			if(target.isFileRelated()) {
				export(Colls.toFileList(records));
			} else {
				export(records);
			}
			
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
	
	private void dealWithFolderPaths(List<String> paths) {
		List<MexFile> allFiles = new ArrayList<>();
		for(String path: paths) {
			List<MexFile> records = FileUtil.listDirectory(path);
			if(!EmptyUtil.isNullOrEmpty(records)) {
				allFiles.addAll(records);
			}
		}
		
		if(target.isFileRelated()) {
			export(Colls.toFileList(allFiles));
			return;
		}
		
		boolean orderByNameAsc = OptionUtil.readBooleanPRI(options, "byname", true);
		MexFileComparator cesc = new MexFileComparator(orderByNameAsc);
		boolean orderByTypeDirAtTop = OptionUtil.readBooleanPRI(options, "bytype", true);
		cesc.setByTypeAsc(orderByTypeDirAtTop);
		cesc.setByDateAsc(OptionUtil.readBoolean(options, "bydate"));
		cesc.setBySizeAsc(OptionUtil.readBoolean(options, "bysize"));
		Collections.sort(allFiles, cesc);
		String tempOptions = "+kids";
		String finalOptions = OptionUtil.mergeOptions(options, tempOptions);
		export(allFiles, finalOptions);
	}
}
