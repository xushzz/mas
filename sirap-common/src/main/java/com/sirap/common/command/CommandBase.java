package com.sirap.common.command;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.TimestampIDGenerator;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.output.ConsoleParams;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.Stash;
import com.sirap.common.framework.command.Exporter;
import com.sirap.common.framework.command.target.Target;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.common.framework.command.target.TargetFolder;

public abstract class CommandBase {

	public static final String KEY_2DOTS = "..";
	public static final String KEY_LOAD = "load";
	public static final String KEY_EQUALS = "=";
	public static final String KEY_REFRESH = "r";
	public static final String KEY_EXIT = "q,e,quit,exit";
	public static final String KEY_HTTP_WWW = "((https?://|www\\.)[\\S]{4,}?)";
	public Map<String, Object> helpMeanings = new HashMap<>();
	
	protected String input;
	protected String command;
	protected String options;
	public Target target;
	protected boolean collectInput = true;
	
	protected String solo;
	protected String[] params;
	protected String regex;
	
	public CommandBase() {
	}
	
	public void noCollect() {
		collectInput = false;
	}
	
	public boolean isToCollect() {
		return collectInput;
	}
	
	public void setInstructions(String input) {
		this.input = input;
		this.command = input;
		this.target = new TargetConsole();
	}
	
	public void setInstructions(String input, String command, String options, Target target) {
		this.input = input;
		this.command = command;
		this.options = options;
		this.target = target;
	}
	
	public SimpleKonfig g() {
		return SimpleKonfig.g();
	}
	
	public boolean handle() throws Exception {
		return false;
	}
	
	public boolean process() {
		try {
			boolean flag = handle();
			
			if(isDebug()) {
				D.ts(getClass());
			}
			if(!flag) {
				return false;
			}
		} catch (MexException ex) {
			StringBuilder stv = new StringBuilder();
			if(isDebug()) {
				if(ex.getOrigin() != null) {
					stv.append(XXXUtil.getStackTrace(ex.getOrigin()));
				} else {
					stv.append(XXXUtil.getStackTrace(ex));
				}
			} else {
				if(ex.getOrigin() != null) {
					stv.append(ex.getOrigin());
				} else {
					stv.append(ex);
				}
			}
			export(stv);
			
		} catch (Exception ex) {
			StringBuilder stv = new StringBuilder();
			if(isDebug()) {
				stv.append(XXXUtil.getStackTrace(ex));
			} else {
				stv.append(ex);
			}
			export(stv);
		}
		
		return true;
	}
	
	public boolean isCaseSensitive() {
		return OptionUtil.readBooleanPRI(options, "case", false);
	}
	
	public boolean isCaseSensitive(String tempOptions) {
		return OptionUtil.readBooleanPRI(tempOptions, "case", false);
	}
	
	public boolean isDebug() {
		boolean debug = OptionUtil.readBooleanPRI(options, "debug", false);
		debug = debug || g().isYes("debug");
		return debug;
	}
	
	@SuppressWarnings({ "rawtypes"})
	public void export(List list) {
		export(list, options);
	}
	
	@SuppressWarnings("rawtypes")
	public void export2(List list, String criteria) {
		List list2 = CollUtil.filterMix(list, criteria, isCaseSensitive());
		export(list2, options);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List applyMexCriteriaOnMexItems(List rawItems, String mexCriteria, boolean isCaseSensitive) {
		List<MexItem> mexItems = Lists.newArrayList();
		List nonItems = Lists.newArrayList();
		for(Object obj : rawItems) {
			if(obj instanceof MexItem) {
				mexItems.add((MexItem)obj);
			} else if(obj instanceof String) {
				mexItems.add(new MexObject(obj));
			} else {
				nonItems.add(obj);
			}
		}

		List<MexItem> after = CollUtil.filter(mexItems, mexCriteria, isCaseSensitive);
		
		nonItems.addAll(after);
		
		return nonItems;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List toPrintIfMex(List items, String options) {
		List records = new ArrayList<String>();
		
		for(Object item : items) {
			if(item instanceof MexItem) {
				MexItem mi = (MexItem)item;
				records.add(mi.toPrint(options));
			} else {
				records.add(item);
			}
		}
		
		return records;
	}

	@SuppressWarnings({"rawtypes" })
	public void export(List list, String finalOptions) {
		XXXUtil.nullCheck(list, "list");
		List newList = list;
		String mexCriteria = OptionUtil.readString(finalOptions, "z");
		Target where = whereToShot();
		if(!EmptyUtil.isNullOrEmpty(mexCriteria)) {
			newList = applyMexCriteriaOnMexItems(list, mexCriteria, isCaseSensitive(finalOptions));
		}
		newList = toPrintIfMex(newList, finalOptions);
		if(EmptyUtil.isNullOrEmpty(newList)) {
			exportEmptyMsg();
		} else {
			Exporter.exportList(input, newList, where, finalOptions);
		}
	}
	
	private Target whereToShot() {
		Object item = Stash.g().readAndRemove(Stash.KEY_USER_INPUT_TARGET);
		if(item != null) {
			if(item instanceof Target) {
				Target directInputTarget = (Target)item;
				return directInputTarget;
			} else {
				XXXUtil.alert("Uncanny, stash non-Target stuff with key " + Stash.KEY_USER_INPUT_TARGET);
			}
		}
		
		return target;
	}
	
	public void setIsPrintTotal(boolean isPrintTotal) {
		if(target instanceof TargetConsole) {
			TargetConsole console = (TargetConsole)target;
			ConsoleParams params = console.getParams();
			params.setPrintTotal(isPrintTotal);
		}
	}

	public void exportEmptyMsg() {
		Exporter.exportList(input, Lists.newArrayList("The result is empty."), whereToShot(), options);		
	}

	@SuppressWarnings("rawtypes")
	public void export(Object content) {
		if(content == null) {
			exportEmptyMsg();
			return;
		}
		
		if(content instanceof List) {
			export((List)content);
			return;
		}
		
		List<Object> list = new ArrayList<Object>();
		list.add(content);
		
		export(list);
	}

	public boolean isIn(String keys) {
		String[] keyArr = keys.split(",");
		return StrUtil.existsIgnoreCase(keyArr, command);
	}

	public boolean isIn(String... keyArr) {
		return StrUtil.existsIgnoreCase(keyArr, command);
	}

	public boolean is(String key) {
		return key.equalsIgnoreCase(command);
	}

	public String parseSoloParam(String regex) {
		return StrUtil.parseParam(regex, command);
	}

	public String[] parseParams(String regex) {
		return StrUtil.parseParams(regex, command);
	}
	
	protected boolean isSingleParamNotnull() {
		return solo != null;
	}
	
	protected boolean isParamsNotnull() {
		return params != null;
	}
	
	public File parseFile(String param) {
		if(StrUtil.containsNoneOfAplhanumeric(param)) {
			return null;
		}
		return FileUtil.parseFile(param, storage());
	}
	
	public File parseFolder(String param) {
		if(StrUtil.containsNoneOfAplhanumeric(param)) {
			return null;
		}
		return FileUtil.parseFolder(param, storage());
	}
	
	public String parseFolderPath(String param) {
		if(PanaceaBox.isWindows() && StrUtil.containsNoneOfAplhanumeric(param)) {
			return null;
		}
		return FileUtil.parseFolderPath(param, storage());
	}
	
	public boolean isEmailEnabled() {
		return g().isEmailEnabled();
	}
	
	public String storage() {
		return g().getStorage();
	}
	
	public String pathOf(String key, String defFolderName) {
		return g().pathOf(key, defFolderName);
	}
	
	public void tryToOpenGeneratedImage(String filePath) {
		if(!FileUtil.exists(filePath)) {
			return;
		}
		
		if(SimpleKonfig.g().isGeneratedFileAutoOpen()) {
			C.pl2("View photo");
			FileOpener.playThing(filePath, "image.viewer");
		} else {
			C.pl();
		}
	}
	
	protected String versionAndCopyright() {
		String version = g().getValueOf("version", "Version info unavailable.");
		String copyright = g().getValueOf("copyright", "Copyright info unavailable.");
		return version + ". " + copyright;
	}
	
	public String miscPath() {
		return pathOf("storage.misc", Konstants.FOLDER_MISC);
	}
	
	protected String screenShotPath() {
		return pathOf("storage.screenshot", Konstants.FOLDER_SCREENSHOT);
	}
	
	protected String equiHttpProtoclIfNeeded(String url) {
		String temp = url;
		String key = "http";
		if(!StrUtil.startsWith(url, "http")) {
			temp = key + "://" + url;
		}
		
		return temp;
	}
	
	protected boolean handleHttpRequest(String source) {
		String temp = StrUtil.parseParam(KEY_HTTP_WWW, source);
		if(temp == null) {
			return false;
		}
		
		String url = equiHttpProtoclIfNeeded(temp);
		Boolean toViewPage = OptionUtil.readBoolean(options, "web");
		if(toViewPage != null) {
			 if(toViewPage) {
				 viewPage(url);
			 } else {
				 downloadFile(url);
			 }
			 
			 return true;
		}
		
		boolean toDownload = FileOpener.isPossibleNormalFile(url);
		if(toDownload) {
			downloadFile(url);
		} else {
			viewPage(url);
		}
		
		return true;
	}
	
	private void downloadFile(String url) {
		String httpUrl = url;
		String unique = "";
		boolean useUniqueFilename = g().isExportWithTimestampEnabled(options);
		if(useUniqueFilename) {
			unique = TimestampIDGenerator.nextId() + "_";
		}
		String jack = FileUtil.generateFilenameByUrl(httpUrl);
		if(EmptyUtil.isNullOrEmpty(jack)) {
			jack = RandomUtil.letters(7);
		}
		String fileName = unique + jack;
		String storage = pathOf("storage.misc", Konstants.FOLDER_MISC);
		String filePath = StrUtil.useSeparator(storage, fileName);
		if(FileUtil.exists(filePath)) {
			C.pl("Existed => " + filePath);
		} else {
			FileUtil.makeDirectoriesIfNonExist(storage);
			C.pl("Fetching... " + httpUrl);
			boolean flag = IOUtil.downloadNormalFile(httpUrl, filePath, false);
			if(flag) {
				C.pl2("Saved => " + filePath);
			}
		}

		if(g().isGeneratedFileAutoOpen()) {
			FileOpener.open(filePath);
		}
		
		if(target instanceof TargetConsole) {
			return;
		}
		
		if(target.isFileRelated()) {
			export(FileUtil.getIfNormalFile(filePath));
		} else {
			export(filePath);
		}
	}
	
	private void viewPage(String url) {
		FileOpener.playThing(url, "page.viewer", true);
		C.pl2("View Page.");
	}
	
	protected String[] generateQRCodeImageFilenameAndFormat(String nameInfo, String defName, String defFormat) {
		String suffix = "";
		String format = "";
		if(EmptyUtil.isNullOrEmpty(nameInfo)) {
			suffix = defName;
			format = getConfigedImageFormat(defFormat);
		} else {
			String[] suffixAndFormt = parseImageFormat(nameInfo);
			if(suffixAndFormt != null) {
				suffix = suffixAndFormt[0];
				format = suffixAndFormt[1];
			} else {
				suffix = nameInfo;
				format = getConfigedImageFormat(defFormat);
			}
		}
		
		if(!EmptyUtil.isNullOrEmpty(suffix)) {
			suffix = "_" + suffix;
		}
		
		String dir = getImageLocation();
		String temp = "{0}{1}QR{2}.{3}";
		String ts = "";
		if(g().isExportWithTimestampEnabled(options)) {
			ts = DateUtil.timestamp() + "_";
		}
		String filePath = StrUtil.occupy(temp, dir, ts, FileUtil.generateLegalFileName(suffix), format);
		
		return new String[] {filePath, format};
	}
	
	protected String getConfigedImageFormat(String defFormat) {
		String[] keyArr = Konstants.IMG_FORMATS.split(",");
		String format = g().getUserValueOf("capture.format", defFormat);
		if(StrUtil.existsIgnoreCase(keyArr, format)) {
			return format;
		}
		
		return Konstants.IMG_BMP;
	}

	protected String getTargetLocation(String defaultLocation) {
		String dir = defaultLocation;
		if(target instanceof TargetFolder) {
			dir = ((TargetFolder) target).getPath() + File.separator;
		} else {
			String targetStr = target.getValue();
			if(!EmptyUtil.isNullOrEmpty(targetStr)) {
				File folder = parseFolder(targetStr);
				if(folder != null) {
					dir = folder.getAbsolutePath() + File.separator; 
				}
			}
		}
		
		return dir;
	}

	protected String getImageLocation() {
		String dir = screenShotPath();
		dir = getTargetLocation(dir);
		
		return dir;
	}
	
	protected String getExportLocation() {
		String dir = pathOf("storage.export", Konstants.FOLDER_EXPORT);
		dir = getTargetLocation(dir);
		
		return dir;
	}
	
	protected String[] parseImageFormat(String suffixAndFormat) {
		int idxDot = suffixAndFormat.lastIndexOf('.');
		String extension = null;
		if(idxDot >= 0) {
			extension = suffixAndFormat.substring(idxDot + 1);
		}
		
		String[] keyArr = Konstants.IMG_FORMATS.split(",");
		if(StrUtil.existsIgnoreCase(keyArr, extension)) {
			String suffix = suffixAndFormat.substring(0, idxDot);
			return new String[] {suffix, extension};
		}
		
		return null;
	}
	
	protected List<String> downloadFiles(String destination, List<String> links) {
		return downloadFiles(destination, links, null);
	}
	
	protected List<String> downloadFiles(String destination, List<String> links, String suffixWhenObscure) {
		int threads = SimpleKonfig.g().getUserNumberValueOf("threads.download");
		boolean useUniqueFilename = g().isExportWithTimestampEnabled(options);
		return IOUtil.downloadFiles(destination, links, suffixWhenObscure, threads, useUniqueFilename);
	}
	
	protected void checkTooBigToHandle(File file, String maxSize) {
		long fileSize = file.length();
		long limitSizeInByte = FileUtil.parseFileSize(maxSize);
		if(file.length() > limitSizeInByte) {
			String msgTemp = "File size {0} is larger than maximum {1}, refuse to handle.";
			String msg = StrUtil.occupy(msgTemp, FileUtil.formatFileSize(fileSize), FileUtil.formatFileSize(limitSizeInByte));
			XXXUtil.alert(msg);
		}
	}
	

	protected List<String> getCommandNames() {
		List<String> keys = new ArrayList<String>();
		List<String> commandNodes = SimpleKonfig.g().getCommandClassNames();
		for(String node:commandNodes) {
			String param = StrUtil.parseParam(".*?\\.Command([a-z]*?)$", node);
			if(param == null) {
				continue;
			}
			keys.add(param);
		}
		return keys;
	}
	
	protected void executeInternalCmd(String conciseCommand) {
		Boolean printAlong = OptionUtil.readBoolean(options, "ing");
		String zoo = OptionUtil.readString(options, "z");
		if(printAlong == null) {
			printAlong = zoo == null;
		}
		String newCommand = "cmd /c " + conciseCommand;
		if(PanaceaBox.isMac()) {
			newCommand = conciseCommand;
		}
		List<String> result = PanaceaBox.executeAndRead(newCommand, printAlong);
		if(EmptyUtil.isNullOrEmpty(result)) {
			export("Command [" + conciseCommand + "] executed.");
		} else {
			if(printAlong) {
				if(result.size() > 5) {
					C.total(result.size());
				}
				
				C.pl();
			} else {
				export(result);
			}
		}
	}
	
	protected Map<String, Object> createMexItemParams(String key, Object value) {
		Map<String, Object> params = new HashMap<>();
		params.put(key, value);
		
		return params;
	}
	
	protected String switchChartset(String key) {
		Map<String, String> ma = Maps.newConcurrentMap();
		ma.put(Konstants.CODE_GBK, Konstants.CODE_UTF8);
		ma.put(Konstants.CODE_UTF8, Konstants.CODE_GBK);
		
		return ma.get(key);
	}
	
	protected String translatePath(String dosCommand, String path) {
		String gitWithSpace = "git ";
		if(StrUtil.startsWith(dosCommand, gitWithSpace)) {
			String gitFile = StrUtil.useSeparator(path, ".git");
			String info = "--work-tree=" + gitFile + " --git-dir=" + gitFile + " ";
			return dosCommand.replace(gitWithSpace, gitWithSpace + info);
		}
		
		String mvnWithSpace = "mvn ";
		if(StrUtil.startsWith(dosCommand, mvnWithSpace)) {
			String info = " -f";
			if(StrUtil.endsWith(path, ".xml")) {
				info += path;
			} else {
				info += StrUtil.useSeparator(path, "pom.xml");
			}
			return dosCommand + info;
		}
		
		return dosCommand;
	}
}
