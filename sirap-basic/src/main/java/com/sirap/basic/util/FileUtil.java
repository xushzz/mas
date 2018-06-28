package com.sirap.basic.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileSystemView;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.D;
import com.sirap.basic.tool.FileSizeCalculator;
import com.sirap.basic.tool.FileWalker;

@SuppressWarnings("unchecked")
public class FileUtil {
	
	public static final String SUFFIXES_IMAGE = "png;bmp;jpg;jpeg;jpe;jfif;gif;tif;tiff;ico";
	public static final String SUFFIXES_AUDIO = "mp3;wma;wav;mid;midi;mpa;aac";
	public static final String SUFFIXES_VIDEO = "3gp;avi;wmv;wmp;asf;rm;ram;rmvb;ra;mpg;mpeg;mp4;mpa;mkv";
	public static final String SUFFIXES_TEXT = "txt;properties;java;js;css;xml;log;pom;bat;cpp;h;sh";
	public static final String SUFFIXES_ZIP = "zip;jar;war;ear;rar";
	public static final String SUFFIXES_PDF = "pdf";
	public static final String SUFFIXES_WORD = "doc;docx";
	public static final String SUFFIXES_EXCEL = "xls;xlsx";
	public static final String SUFFIXES_HTML = "html;htm";
	public static final String SUFFIXES_EXECUTABLE = "exe";
	public static final String SUFFIX_MEX = "mex";
	public static final String SUFFIX_SIRAP = "sirap";
	public static final String SUFFIXES_OTHERS = "jar;apk;zip";
	
	public static final List<String> EXTENSIONS_TEXT = StrUtil.split("txt,properties,java,js,json,css,xml,pom,bat,cpp,sh,py,sql,cmd,md,ini");
	public static final List<String> EXTENSIONS_PDF = StrUtil.split("pdf");
	public static final List<String> EXTENSIONS_EXCEL = StrUtil.split("xls,xlsx");
	public static final List<String> EXTENSIONS_HTML = StrUtil.split("htm,html");
	public static final List<String> EXTENSIONS_SIRAP = StrUtil.split("sir,aka");

	public static final char[] BAD_CHARS_FOR_FILENAME_WINDOWS = {'/','\\',':','\"','*','?','|','>','<'};
	public static final char[] BAD_CHARS_FOR_FILENAME_MAC = {'/','?','~','^','&','*'};
	
	public static final String SLASH_DOUBLE = "\\\\";

	public static boolean isExcel(String filepath) {
		return StrUtil.endsWith(filepath, Konstants.DOT_EXCEL);
	}

	public static boolean isExcelX(String filepath) {
		return StrUtil.endsWith(filepath, Konstants.DOT_EXCEL_X);
	}

	public static boolean isCSV(String filepath) {
		return StrUtil.endsWith(filepath, Konstants.DOT_CSV);
	}
	
	public static boolean isNormalFile(String fileName) {
		File file = new File(fileName);
		return file.isFile();
	}

	public static File getIfNormalFile(String fileName) {
		File file = new File(fileName);
		
		if (file.isFile()) {
			return file;
		} else {
			return null;
		}
	}
	
	public static File getIfNormalFolder(String folderName) {
		return getIfNormalFolder(folderName, false, true);
	}
	
	public static File getIfNormalFolder(String folderName, boolean debug, boolean niceOnly) {
		if(EmptyUtil.isNullOrEmpty(folderName)) {
			return null;
		}
		
		//network file style:
		//\\\\LITVINOV\\work
		
		if(PanaceaBox.isWindows() && niceOnly) {
			String[] params = StrUtil.parseParams("([A-Z]:)(.*)", folderName);
			if(params != null) {
				String disk = params[0];
				if(StrUtil.isRegexMatched("[\\/\\.]*", params[1])) {
					File fileA = new File(disk + File.separator);
					if(fileA.isDirectory()) {
						return fileA;
					}
				}
			}
		}
		
		File file = new File(folderName);

		if(debug) {
			D.ts("filepath: " + folderName);
			D.ts("file.getPath(): " + file.getPath());
			D.ts("file.getAbsolutePath(): " + file.getAbsolutePath());
			
			try {
				if(file.exists()) {
					String canon = file.getCanonicalPath();
					if(debug) {
						D.ts("file.getCanonicalPath(): " + canon);
					}
				} else {
					if(debug) {
						D.ts("file.getCanonicalPath(): Non exists");
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		if (file.isDirectory()) {
			return file;
		}
		
		return null;
	}

	public static boolean exists(String filePath) {
		if(filePath == null) {
			return false;
		}
		
		File file = new File(filePath);
		return file.exists();
	}

	public static boolean makeDirectoriesIfNonExist(String storage) {
		if (EmptyUtil.isNullOrEmptyOrBlank(storage)) {
			return false;
		}

		File file = new File(storage);
		if (!file.exists()) {
			file.mkdirs();
			return false;
		}

		return true;
	}

	public static String generateFileName(String prefix, String name) {
		return generateFileName(prefix, name, Konstants.DOT_TXT);
	}

	public static String generateFileName(String prefix, String name, String fileType) {
		return generateFileName(prefix, name, "", fileType);
	}

	public static String generateFileName(String prefix, String name, String suffix, String fileType) {
		return prefix + name + suffix + fileType;
	}
	
	/***
	 * 
	 * @param fileName
	 * @param suffixes txt;png;java
	 * @return
	 */
	public static boolean isAnyTypeOf(String fileName, String suffixes) {
		if(fileName == null || suffixes == null) {
			return false;
		}
		
		String[] suffixArr = suffixes.split(";");
		
		for(int i = 0; i < suffixArr.length; i++) {
			String suffix = suffixArr[i];
			if(EmptyUtil.isNullOrEmptyOrBlank(suffix)) {
				continue;
			}
			
			String tempName = fileName.toLowerCase();
			String tempSfx = suffix.toLowerCase();
			if(tempSfx.startsWith(".")) {
				if(tempName.endsWith(tempSfx)) {
					return true;
				}
			} else {
				if(tempName.endsWith("." + tempSfx)) {
					return true;
				}
			}
			
		}
		
		return false;
	}
	
	public static boolean isMexFile(String fileName) {
		return isAnyTypeOf(fileName, Konstants.DOT_MEX);
	}
	
	public static boolean isSirapFile(String fileName) {
		return isAnyTypeOf(fileName, Konstants.DOT_SIRAP);
	}

	public static List<MexFile> scanSingleFolder(String directory, int depth) {
		return scanSingleFolder(directory, depth, true);
	}
	
	public static List<MexFile> scanSingleFolder(String directory, int depth, boolean includeFolder) {
		XXXUtil.nullOrEmptyCheck(directory, "directory");
		
		String temp = directory.trim();
		
		File file = FileUtil.getIfNormalFolder(temp);
		if(file == null) {
			String msg = StrUtil.occupy("'{0}' is not a valid directory.", directory);
			XXXUtil.printStackTrace(msg);
			return Collections.EMPTY_LIST;
		}
		
		String path = file.getAbsolutePath();
		FileWalker mary = new FileWalker(path, includeFolder);
		
		List<MexFile> items = mary.listFilesRecursively(depth);
		
		return items;
	}

	public static List<MexFile> scanFolders(List<String> paths, boolean includeFolder) {
		return scanFolders(paths, includeFolder, null);
	}
	
	public static List<MexFile> scanFolders(List<String> paths, boolean includeFolder, String fileCriteria) {
		XXXUtil.nullCheck(paths, "paths");

		List<MexFile> allFiles = new ArrayList<>();
		for (String path : paths) {
			String target = path;
			int depth = 0;

			String regexPathDepth = "(.+?)\\$(\\d{1,2})$";
			String[] params = StrUtil.parseParams(regexPathDepth, target);
			if(params != null) {
				target = params[0];
				depth = MathUtil.toInteger(params[1], depth);
			}
			allFiles.addAll(scanSingleFolder(target, depth, includeFolder));
		}
		
		if(EmptyUtil.isNullOrEmpty(fileCriteria)) {
			return allFiles;
		} else {
			List<MexFile> items = CollUtil.filter(allFiles, fileCriteria);
			return items;
		}
	}

	public static String generateLegalFileName(String source) {
		if(source == null) {
			return null;
		}
		
		String badChars = new String(BAD_CHARS_FOR_FILENAME_WINDOWS);
		if(PanaceaBox.isMacOrLinuxOrUnix()) {
			badChars = new String(BAD_CHARS_FOR_FILENAME_MAC);
		}
		
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < source.length(); i++) {
			char key = source.charAt(i);
			if(badChars.indexOf(key) >= 0) {
				sb.append("%" + Integer.toHexString(key).toUpperCase());
			} else {
				sb.append(key);
			}
		}
		
		String fileName = sb.toString(); 
		fileName = fileName.replace("\t", " ");

		return fileName;
	}
	public static String generateLegalFileNameBySpace(String source) {
		if(source == null) {
			return null;
		}
		
		String badChars = new String(BAD_CHARS_FOR_FILENAME_WINDOWS);
		if(PanaceaBox.isMacOrLinuxOrUnix()) {
			badChars = new String(BAD_CHARS_FOR_FILENAME_MAC);
		}
		
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < source.length(); i++) {
			char key = source.charAt(i);
			if(badChars.indexOf(key) >= 0) {
				sb.append(" ");
			} else {
				sb.append(key);
			}
		}
		
		String fileName = sb.toString(); 
		fileName = fileName.replace("\t", " ");

		return fileName;
	}
	
	public static String escapeChars(String source, char[] charsToEscape) {
		String temp = new String(charsToEscape);
		String regex = "[" + temp + "]";
		
		String result = source.replaceAll(regex, "-");
		
		return result;
	}
	
	public static boolean startWithDiskName(String path) {
		if(EmptyUtil.isNullOrEmpty(path)) {
			return false;
		}

		Matcher m = Pattern.compile("^[A-Z]:", Pattern.CASE_INSENSITIVE).matcher(path);
		boolean flag = m.lookingAt();
		
		return flag;
	}
	
	public static boolean isDiskName(String path) {
		Matcher m = Pattern.compile("[A-Z]:", Pattern.CASE_INSENSITIVE).matcher(path);
		boolean flag = m.matches();
		
		return flag;
	}
	
	public static File parseNormalFile(List<String> possibleFileNames) {
		if(EmptyUtil.isNullOrEmpty(possibleFileNames)) {
			return null;
		}
		
		for(String fileName:possibleFileNames) {
			File file = FileUtil.getIfNormalFile(fileName);
			
			if(file != null) {
				return file;
			}
		}
		
		return null;
	}
	
	public static List<MexFile> listDirectory(String dir) {
		File file = new File(dir);
		List<MexFile> records = new ArrayList<>();
		final List<MexFile> normalFiles = new ArrayList<>();
		final List<MexFile> subFolders = new ArrayList<>();
		file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File currentFile) {
				MexFile june = new MexFile(currentFile);
				if(currentFile.isDirectory()) {
					subFolders.add(june);
				} else {
					normalFiles.add(june);
				}
				
				return true;
			}
		});
		
		records.add(new MexFile(file));
		records.addAll(subFolders);
		records.addAll(normalFiles);
		
		return records;
	}
	
	public static List<Map> listShortnames(String dir) {
		File file = new File(dir);
		final List<Map> fileitems = Lists.newArrayList();
		file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File current) {
				if(current.isFile()) {
					Map fileitem = new LinkedHashMap<>();
					fileitem.put("name", current.getName());
					fileitem.put("size", FileUtil.formatSize(current.length()));
					fileitems.add(fileitem);
				}

				return true;
			}
		});
		
		return fileitems;
	}
	
	public static String generateFilenameByUrl(String httpUrl) {
		return generateFilenameByUrl(httpUrl, null);
	}
	
	public static String generateFilenameByUrl(String httpUrl, String suffixWhenObscure) {
		String temp = httpUrl.substring(httpUrl.lastIndexOf("/") + 1);
		int idxOfAsk = temp.indexOf('?');
		if(idxOfAsk != -1) {
			temp = temp.substring(0, idxOfAsk);
		}
		temp = FileUtil.generateLegalFileName(temp);
		if(temp.indexOf(".") == -1 && suffixWhenObscure != null) {
			temp += suffixWhenObscure;
		}
		
		String filePath = XCodeUtil.urlDecodeUTF8(temp);
		
		return filePath;
	}
	
	public static File parseFile(String param, String defaultFolder) {
		if(EmptyUtil.isNullOrEmpty(param)) {
			return null;
		}
		
		List<String> possibleFileNames = new ArrayList<String>();
		
		if(PanaceaBox.isWindows() && startWithDiskName(param)) {
			possibleFileNames.add(param);
			possibleFileNames.add(param + Konstants.DOT_TXT);
		}
		
		if (PanaceaBox.isMacOrLinuxOrUnix()){
			possibleFileNames.add(param);
			possibleFileNames.add(param + Konstants.DOT_TXT);
		}
		
		if(param.startsWith("\\\\")) {
			possibleFileNames.add(param);
			possibleFileNames.add(param + Konstants.DOT_TXT);
		} else {
			possibleFileNames.add(StrUtil.useSeparator(defaultFolder, param));
			possibleFileNames.add(StrUtil.useSeparator(defaultFolder, param + Konstants.DOT_TXT));
		}
		
		return parseNormalFile(possibleFileNames);
	}
	
	public static File parseFolder(String param, String defaultFolder) {
		boolean isBad = StrUtil.isRegexMatched("[\\\\/\\.]{1,}", param);
		if(isBad) {
//			XXXUtil.alert("Not nice, I personally hate this style, pls do away with " + param);
			return null;
		}
		
		File folder = FileUtil.getIfNormalFolder(param);
		if(folder != null) {
			return folder;
		}
		
		folder = FileUtil.getIfNormalFolder(StrUtil.useSeparator(defaultFolder, param));
		if(folder != null) {
			return folder;
		}
		
		return null;
	}
	
	public static String parseFolderPath(String param, String defaultFolder) {
		File folder = parseFolder(param, defaultFolder);
		
		try {
			if(folder != null) {
				return folder.getCanonicalPath();
			}
		} catch (Exception ex) {
			throw new MexException(ex);
		}
		
		return null;
	}
	
	public static String[] splitByLastFileSeparator(String info) {
		String unix = unixSeparator(info);
		int idxOfLastSeparator = unix.lastIndexOf("/");
		
		if(idxOfLastSeparator < 0) {
			return new String[] {null, info.trim()};
		}
		
		String left = info.substring(0, idxOfLastSeparator).trim();
		String right = info.substring(idxOfLastSeparator + 1).trim();
		
		return new String[] {left, right};
	}
	
	public static String extensionOf(String filename) {
		String regex = "\\.([^\\.]*)$";
		return StrUtil.findFirstMatchedItem(regex, filename);
	}
	
	public static String[] filenameAndExtensionOf(String filename) {
		String regex = "(.*)\\.([^\\.]*)$";
		return StrUtil.parseParams(regex, filename);
	}

	public static String extractFilenameWithoutExtension(String filepath) {
		if(EmptyUtil.isNullOrEmpty(filepath)) {
			return null;
		}
		
		int idxOfLastSeparator = filepath.lastIndexOf(File.separator);
		String filename = filepath.substring(idxOfLastSeparator + 1);
		int idxOfLastDot = filename.lastIndexOf(".");
		if(idxOfLastDot >= 0) {
			filename = filename.substring(0, idxOfLastDot);
		}
		
		return filename;
	}
	
	public static String filenameWithExtensionOf(String filepath) {
		XXXUtil.nullOrEmptyCheck(filepath, "filepath");
		int ia = filepath.lastIndexOf('\\');
		int ib = filepath.lastIndexOf('/');
		int index = Math.max(ia, ib);
		if(index >= 0) {
			return filepath.substring(index + 1);
		} else {
			return filepath;
		}
	}

	/***
	 * [abc/n/in\\ja.doc] => [ja.doc]
	 * @param filepath
	 * @return
	 */
	public static String[] folderpathAndFilenameOf(String filepath) {
		XXXUtil.nullOrEmptyCheck(filepath, "filepath");

		int ia = filepath.lastIndexOf('\\');
		int ib = filepath.lastIndexOf('/');
		int index = Math.max(ia, ib);
		
		if(ib >= 0) {
			String[] arr = {filepath.substring(0, index), filepath.substring(index + 1)};
			return arr;
		} else {
			return new String[]{".", filepath};
		}
	}
	
	/***
	 * 
	 * @return C:
	 *         D:
	 */
	public static List<String> availableDiskDetails() {
		List<String> items = new ArrayList<>();
		String template = "{0} {3}% available {1} out of {2}";
		FileSystemView dan = FileSystemView.getFileSystemView();
		
		for(char flag = 'A'; flag <= 'Z'; flag++) {
			String folderName = flag + ":\\";
			File file = FileUtil.getIfNormalFolder(folderName);
			if(file == null) {
				continue;
			}
			
			String displayName = dan.getSystemDisplayName(file);
			long free = file.getFreeSpace();
			long total = file.getTotalSpace();
			int percentage = (int)(free * 100.0 / total);
			String record = StrUtil.occupy(template, displayName, formatSize(free), formatSize(total), percentage);
			items.add(record);
		}
		
		return items;
	}
	
	/***
	 * 
	 * @return C: (CA) available 20291M out of 76800M
	 *         D: (DA) available 5940M out of 81925M
	 */
	public static List<String> availableDiskNames() {
		List<String> items = new ArrayList<>();
		
		for(char flag = 'A'; flag <= 'Z'; flag++) {
			String folderName = flag + ":\\";
			File file = FileUtil.getIfNormalFolder(folderName);
			if(file == null) {
				continue;
			}
			
			items.add(flag + ":");
		}
		
		return items;
	}
	
	public static long sizeOf(String fileOrFolderPath) {
		FileSizeCalculator james = new FileSizeCalculator(fileOrFolderPath);
		return james.getTotalSize();
	}
	
	public static String formatSize(String filePath) {
		File file = new File(filePath);
		if(!file.exists()) {
			throw new MexException("Non-exist file: {0}", filePath);
		}
		
		long size = file.length();
		String value = formatSize(size);
		
		return value;
	}
	
	public static String formatSize(long sizeInByte) {
		NumberFormat pretty = NumberFormat.getNumberInstance();
		pretty.setMaximumFractionDigits(2);
		pretty.setRoundingMode(RoundingMode.HALF_UP);
		
		int base = Konstants.FILE_SIZE_STEP;
		String value = null;
		String units = Konstants.FILE_SIZE_UNIT;
		for(int i = 0; i < units.length(); i++) {
			double max = Math.pow(base, (i + 1));
			if(sizeInByte < max) {
				double number = sizeInByte / Math.pow(base, i);
				value = pretty.format(number) + units.charAt(i);
				
				break;
			}
		}
		
		if(value == null) {
			throw new MexException("The size [" + sizeInByte + "] is extraordinary large, are you sure?");
		}
		
		return value;
	}
	
	/**
	 * 
	 * @param source 2K
	 * @return 2048
	 */
	public static long parseSize(String source) {
		String units = Konstants.FILE_SIZE_UNIT;
		
		String regex = Konstants.REGEX_FLOAT + "([" + units + "])";
		String[] params = StrUtil.parseParams(regex, source.toUpperCase());
		if(params == null) {
			throw new MexException("can't parse file size [{0}], try legal examples like 2B, 12M, 38.8G and so on.", source);
		}
		
		return parseSize(params[0], params[1].charAt(0));
	}
	
	public static long parseSize(String numberStr, char unit) {
		String units = Konstants.FILE_SIZE_UNIT;
		
		Double number = Double.valueOf(numberStr);
		int power = units.indexOf(unit);
		Double result = number * Math.pow(Konstants.FILE_SIZE_STEP, power);
		
		if(result > Long.MAX_VALUE) {
			throw new MexException("The size [" + result + "] is larger than Long.MAX_VALUE [ " + Long.MAX_VALUE + "], are you sure?");
		} else {
			return result.longValue();
		}
	}
	
	public static List<String> detail(String filepath) {
		List<String> items = new ArrayList<>();
		
		Path path = Paths.get(filepath);

		items.add(createItem(path, "size"));
		items.add(createItem(path, "creationTime"));
		items.add(createItem(path, "lastModifiedTime"));
		
		return items;
	}
	
	private static String createItem(Path path, String key) {
		try {
			Object value = Files.getAttribute(path, "basic:" + key);
			if(StrUtil.equals("size", key) && value instanceof Long) {
				value = formatSize((Long)value);
			} else if(value instanceof FileTime) {
				FileTime ft = (FileTime)value;
				value = DateUtil.displayDate(new Date(ft.toMillis()), DateUtil.DATE_TIME);
			}
			
			String temp = key;

			if(StrUtil.equals("lastModifiedTime", key)) {
				temp = key.replace("Time", "");
			}
			
			Pattern p = Pattern.compile("[A-Z]");
			Matcher m = p.matcher(temp);
			String display = temp;
			
			while(m.find()) {
				String tempChar = m.group(0);
				display = display.replace(tempChar, " " + tempChar.toLowerCase());
			}
			
			String result = display + ": " + value;
			
			return result;
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}
	
	public static List<String> explodeAsterisk(String filepathHavingAsterisk) {
		String temp = filepathHavingAsterisk;
		boolean isWindowsStyle = temp.indexOf('\\') >= 0;
		if(isWindowsStyle) {
			temp = FileUtil.unixSeparator(filepathHavingAsterisk);
		}
		String regex = "(.+?)/([^/]*\\*[^/]*)(/?$|/.+)";
		String[] params = StrUtil.parseParams(regex, temp);
		List<String> matchedFiles = new ArrayList<>();
		if(params == null) {
			matchedFiles.add(temp);
			return matchedFiles;
		}
		String head = params[0];
		String body = params[1];
		String tail = params[2];
		final String goodTail = tail;
		File folder = getIfNormalFolder(head);
		if(folder != null) {
			final String subRegex = body.replace(".", "\\.").replace("*", ".*");
			folder.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					boolean isMatched = StrUtil.isRegexMatched(subRegex, name);
					if(isMatched) {
						String jack = dir.getAbsolutePath() + File.separator + name + goodTail;
						matchedFiles.add(jack);
					}
					return isMatched;
				}
			});
		}
		
		return matchedFiles;
	}

	public static boolean remove(String filepath) {  
		return remove(filepath, false);
	}
	
	public static boolean removeKids(String filepath, boolean printAlong) {
		File file = new File(filepath);
		File[] files = file.listFiles(); 

		if(files == null) {
	    	return false;
	    }
		
	    for (int i = 0; i < files.length; i++) {  
	        remove(files[i].getAbsolutePath(), printAlong);  
	    }
	    
	    return true;
	}
	
	public static boolean remove(String filepath, boolean printAlong) {  
		File file = new File(filepath);
	    if (!file.exists()) {
	    	XXXUtil.info("File not found: {0}", filepath);
	    	return false;
	    }
	    if (file.isFile()) {
	    	boolean flag = false;
	    	String size = formatSize(file.length());
	    	if(printAlong) {
		    	XXXUtil.info("Removing " + filepath + ", " + size);
		    	flag = file.delete();
		    	String side = flag ? "Removed " : "Unable to remove ";
		    	XXXUtil.info(side + filepath + ", " + size);
	    	} else {
	    		flag = file.delete();
	    	}
	        return flag;
	    }
	    File[] files = file.listFiles();  
	    if(files == null) {
	    	return false;
	    }
	    for (int i = 0; i < files.length; i++) {  
	        remove(files[i].getAbsolutePath(), printAlong);  
	    }
	    
	    boolean flag = false;
    	if(printAlong) {
    		XXXUtil.info("Removing " + filepath);
	    	flag = file.delete();
	    	String side = flag ? "Removed " : "Unable to remove ";
	    	XXXUtil.info(side + filepath);
    	} else {
    		flag = file.delete();
    	}
    	
        return flag;
	}
	
	public static List<String> getAllXXXFiles(String rootPath, String suffix) {
		File file = new File(rootPath);
		List<String> items = new ArrayList<>();
		if(file.isDirectory()) {
			getAllJarFiles(items, file, suffix);
		}
		
		return items;
	}
	
	private static void getAllJarFiles(List<String> items, File file, String suffix) {
		if(!file.exists()) {
			return;
		}
		
		if(file.isDirectory()) {
			File[] subFiles = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File subFile) {
					if(subFile.isDirectory() || StrUtil.endsWith(subFile.getAbsolutePath(), suffix)) {
						return true;
					}
					
					return false;
				}
			});
			
			for(File subFile : subFiles) {
				getAllJarFiles(items, subFile, suffix);
			}
		} else {
			items.add(file.getAbsolutePath());
		}
	}
	
	public static String canonicalPathOf(String origin) {
		return canonicalPathOf(origin, false);
	}
	
	public static String canonicalPathOf(String origin, boolean printException) {
		XXXUtil.nullCheck(origin, "origin");

		File filo = new File(origin);
		
		try {
			return filo.getCanonicalPath();
		} catch (Exception ex) {
			if(printException) {
				ex.printStackTrace();
			}
			return null;
		}
	}
	
	public static List<String> muse(String folderPath, String mexCriteria) {
		File folder = new File(folderPath);
		List<String> names = Lists.newArrayList();
		folder.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				File item = new File(dir, name);
				if(item.isFile()) {
					MexObject mo = new MexObject(name);
					if(EmptyUtil.isNullOrEmpty(mexCriteria) || mo.isMexMatched(mexCriteria)) {
						names.add("\"" + name + "\"");
					}
				}
				return false;
			}
		});
		
		String json = "[" + StrUtil.connect(names, ", ") + "]";
		List<String> jsonItems = JsonUtil.getPrettyTextInLines(json);
		
		return jsonItems;
	}
	
	public static String shellStyle(String filepath) {
		StringBuffer sb = StrUtil.sb();
		Matcher ma = StrUtil.createMatcher("([a-z]):", unixSeparator(filepath));
		while(ma.find()) {
			String replacement = Konstants.FILE_SEPARATOR_UNIX + ma.group(1); 
			ma.appendReplacement(sb, replacement);
		}
		
		ma.appendTail(sb);
		return sb.toString();
	}
	
	public static String unixSeparator(String filepath) {
		return filepath.replace(Konstants.FILE_SEPARATOR_WINDOWS, Konstants.FILE_SEPARATOR_UNIX);
	}
	
	public static String windowsSeparator(String filepath) {
		return filepath.replace(Konstants.FILE_SEPARATOR_UNIX, Konstants.FILE_SEPARATOR_WINDOWS);
	}

	/***
	 * 1) A B C gets A/B/C in Unix
	 * 2) A/ B\ C D gets A/B\C\D in Windows
	 * @param items
	 * @return
	 */
	public static String bySeparator(Object... items) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < items.length; i++) {
			String item = items[i] + "";
			sb.append(item);
			
			if(i == items.length - 1) {
				break;
			}

			if(!item.endsWith(Konstants.FILE_SEPARATOR_UNIX) && !item.endsWith(Konstants.FILE_SEPARATOR_WINDOWS)) {
				sb.append(File.separator);
			}
		}
		
		return sb.toString();
	}
	
	public static File of(Object obj) {
		if(obj instanceof File) {
			return (File)obj;
		} else if(obj instanceof MexFile) {
			return ((MexFile)obj).getFile();
		} else if(obj instanceof String) {
			String filestr = (String)obj;
			return getIfNormalFile(filestr);
		}
		
		return null;
	}
}
