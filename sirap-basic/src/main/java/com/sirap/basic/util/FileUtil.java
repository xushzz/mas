package com.sirap.basic.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileSystemView;

import com.sirap.basic.component.CleverFolder;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.FileWalker;

@SuppressWarnings("unchecked")
public class FileUtil {
	
	public static final String SUFFIXES_IMAGE = "png;bmp;jpg;jpeg;jpe;jfif;gif;tif;tiff;ico";
	public static final String SUFFIXES_AUDIO = "mp3;wma;wav;mid;midi;mpa;aac";
	public static final String SUFFIXES_VIDEO = "3gp;avi;wmv;wmp;asf;rm;ram;rmvb;ra;mpg;mpeg;mp4;mpa;mkv";
	public static final String SUFFIXES_TEXT = "txt;properties;java;js;css;xml;log;pom;bat;cpp;h;sh";
	public static final String SUFFIXES_ZIP = "jar;zip";
	public static final String SUFFIXES_PDF = "pdf";
	public static final String SUFFIXES_WORD = "doc;docx";
	public static final String SUFFIXES_EXCEL = "xls;xlsx";
	public static final String SUFFIXES_HTML = "html;htm";
	public static final String SUFFIXES_EXECUTABLE = "exe";
	public static final String SUFFIX_MEX = "mex";
	public static final String SUFFIX_SIRAP = "sirap";
	public static final String SUFFIXES_OTHERS = "jar;apk;zip";

	public static final char[] BAD_CHARS_FOR_FILENAME_WINDOWS = {'/','\\',':','\"','*','?','|','>','<'};
	public static final char[] BAD_CHARS_FOR_FILENAME_MAC = {'/','?','~','^','&','*'};
	
	public static final String SLASH_DOUBLE = "\\\\";
	
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
		if(EmptyUtil.isNullOrEmpty(folderName)) {
			return null;
		}
		
		File file = new File(folderName);

		if (file.isDirectory()) {
			return file;
		} else {
			return null;
		}
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
		return generateFileName(prefix, name, Konstants.SUFFIX_TXT);
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
		return isAnyTypeOf(fileName, Konstants.SUFFIX_MEX);
	}
	
	public static boolean isSirapFile(String fileName) {
		return isAnyTypeOf(fileName, Konstants.SUFFIX_SIRAP);
	}

	public static List<MexFile> scanSingleFolder(String directory, int depth) {
		return scanSingleFolder(directory, depth, true);
	}
	
	public static List<MexFile> scanSingleFolder(String directory, int depth, boolean includeFolder) {
		XXXUtil.nullOrEmptyCheck(directory, "directory");
		
		String temp = directory.trim();
		if(FileUtil.isMaliciousPath(temp)) {
			String msg = StrUtil.occupy("'{0}' is not a specific directory.", directory);
			XXXUtil.printStackTrace(msg);
			return Collections.EMPTY_LIST;
		}
		
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
			List<MexFile> items = CollectionUtil.filter(allFiles, fileCriteria);
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

	public static boolean isMaliciousPath(String path) {
		if(EmptyUtil.isNullOrEmpty(path)) {
			return true;
		}
		
		Matcher m = Pattern.compile("(\\.|/|\\\\)+", Pattern.CASE_INSENSITIVE).matcher(path);
		boolean flag = m.matches();
		
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
	
	public static List<String> listDirectory(String dir) {
		File file = new File(dir);
		List<String> records = new ArrayList<String>();
		final List<String> normalFiles = new ArrayList<String>();
		final List<String> subFolders = new ArrayList<String>();
		file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File currentFile) {
				if(currentFile.isDirectory()) {
					String[] files = currentFile.list();
					if(files != null) {
						subFolders.add(currentFile.getAbsolutePath() + "(" + files.length + ")");
					}
				} else {
					normalFiles.add(currentFile.getAbsolutePath());
				}
				
				return true;
			}
		});
		
		records.add(dir);
		records.addAll(subFolders);
		records.addAll(normalFiles);
		
		return records;
	}
	
	public static List<String> readResourceFilesIntoList(String filePath) {
		return readResourceFilesIntoList(filePath, "");
	}

	public static List<String> readResourceFilesIntoList(String filePath, String prefix) {
		List<String> records = new ArrayList<String>();
		InputStream inputStream = InputStream.class.getResourceAsStream(filePath);
		if(inputStream == null) {
			return Collections.EMPTY_LIST;
		}
		
		List<String> items = IOUtil.readStreamIntoList(inputStream, false, prefix);
		if(items != null) {
			records.addAll(items);
		}
		
		return records;
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
	
	public static String getCleverPath(String folderName) {
		CleverFolder mf = new CleverFolder(folderName);
		
		return mf.getCleverFolderPath();
	}
	
	public static File parseFile(String param, String defaultFolder) {
		if(EmptyUtil.isNullOrEmpty(param)) {
			return null;
		}
		
		List<String> possibleFileNames = new ArrayList<String>();
		
		if(PanaceaBox.isWindows() && startWithDiskName(param)) {
			possibleFileNames.add(param);
			possibleFileNames.add(param + Konstants.SUFFIX_TXT);
		}
		
		if (PanaceaBox.isMacOrLinuxOrUnix()){
			possibleFileNames.add(param);
			possibleFileNames.add(param + Konstants.SUFFIX_TXT);
		}
		
		if(param.startsWith("\\\\")) {
			possibleFileNames.add(param);
			possibleFileNames.add(param + Konstants.SUFFIX_TXT);
		} else {
			possibleFileNames.add(defaultFolder + param);
			possibleFileNames.add(defaultFolder + param + Konstants.SUFFIX_TXT);
		}
		
		return parseNormalFile(possibleFileNames);
	}
	
	public static File parseFolder(String param, String defaultFolder) {
		
		String path = getCleverPath(param);
		if(path != null) {
			return new File(path);
		}
		
		if(param.startsWith("\\\\")) {
			File file = FileUtil.getIfNormalFolder(param);
			if(file != null) {
				return file;
			}
		}
		
		if (PanaceaBox.isMacOrLinuxOrUnix()){
			File file = FileUtil.getIfNormalFolder(param);
			if(file != null) {
				return file;
			}
		}
		
		return FileUtil.getIfNormalFolder(defaultFolder + param);
	}
	
	public static String parseFolderPath(String param, String defaultFolder) {
		String path = getCleverPath(param);
		if(path != null) {
			return path;
		}
		
		if (PanaceaBox.isMacOrLinuxOrUnix()){
			File file = FileUtil.getIfNormalFolder(param);
			if(file != null) {
				return file.getAbsolutePath();
			}
		}
		
		File file = FileUtil.getIfNormalFolder(defaultFolder + param);
		if(file != null) {
			return file.getAbsolutePath();
		}
		
		if(param.startsWith("\\\\")) {
			file = FileUtil.getIfNormalFolder(param);
			if(file != null) {
				return file.getAbsolutePath();
			}
		}
		
		return null;
	}
	
	public static String[] splitFolderAndFile(String filepath) {
		String temp = filepath.replaceAll("/", "\\\\");
		filepath = temp;
		int idxOfLastSeparator = filepath.lastIndexOf("\\");
		
		if(idxOfLastSeparator < 0) {
			return new String[] {null, filepath};
		}
		
		String folder = filepath.substring(0, idxOfLastSeparator);
		String filename = filepath.substring(idxOfLastSeparator + 1);
		
		return new String[] {folder, filename};
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
	
	/***
	 * 
	 * @return C:
	 *         D:
	 */
	public static List<String> availableDiskDetails() {
		List<String> items = new ArrayList<>();
		String template = "{0} {3}% available {1} out of {2}";
		FileSystemView fsv = FileSystemView.getFileSystemView();
		
		for(char flag = 'A'; flag <= 'Z'; flag++) {
			String folderName = flag + ":\\";
			File file = FileUtil.getIfNormalFolder(folderName);
			if(file == null) {
				continue;
			}
			
			String displayName = organizeSystemDisplayName(fsv.getSystemDisplayName(file));
			long free = file.getFreeSpace();
			long total = file.getTotalSpace();
			int percentage = (int)(free * 100.0 / total);
			String record = StrUtil.occupy(template, displayName, formatFileSize(free), formatFileSize(total), percentage);
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
	
	/***
	 * CA (C:)
	 * @return C: (CA)
	 */
	public static String organizeSystemDisplayName(String source) {
		String regex = "(.*)\\(([A-Z]:)\\)";
		String[] params = StrUtil.parseParams(regex, source);
		
		if(params == null) {
			throw new IllegalArgumentException(source);
		}

		String template = "{0} ({1})";
		String record = StrUtil.occupy(template, params[1], params[0]); 
		
		return record;
	}
	
	public static String formatFileSize(String fileName) {
		File file = getIfNormalFile(fileName);
		if(file == null) {
			return null;
		}
		
		long size = file.length();
		String value = formatFileSize(size);
		
		return value;
	}
	
	public static String formatFileSize(long sizeInByte) {
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
	public static long parseFileSize(String source) {
		String units = Konstants.FILE_SIZE_UNIT;
		
		String regex = Konstants.REGEX_FLOAT + "([" + units + "])";
		String[] params = StrUtil.parseParams(regex, source.toUpperCase());
		if(params == null) {
			throw new MexException("can't parse file size, try legal examples like 2B, 12M, 38.8G and so on.");
		}
		
		return parseFileSize(params[0], params[1].charAt(0));
	}
	
	public static long parseFileSize(String numberStr, char unit) {
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
				value = formatFileSize((Long)value);
			} else if(value instanceof FileTime) {
				FileTime ft = (FileTime)value;
				value = DateUtil.displayDate(new Date(ft.toMillis()), DateUtil.DATETIME);
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
			temp = temp.replace('\\', '/');
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
						String jack = dir.getAbsolutePath() + "/" + name + goodTail;
						String james = jack.replace('/', '\\');
						matchedFiles.add(james);
					}
					return isMatched;
				}
			});
		}
		
		if(isWindowsStyle) {
			head = head.replace('/', '\\');
			tail = tail.replace('/', '\\');
		}
		
		return matchedFiles;
	}
}
