package com.sirap.geek;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.data.HttpData;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexZipEntry;
import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.ScreenCaptor;
import com.sirap.basic.util.ArisUtil;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.ImageUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.SatoUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.FileSizeInputAnalyzer;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.geek.jenkins.JenkinsBuildRecord;
import com.sirap.geek.jenkins.JenkinsManager;
import com.sirap.geek.manager.GithubIssuesExtractor;
import com.sirap.geek.manager.MavenManager;
import com.sirap.geek.util.GeekExtractors;

public class CommandDev extends CommandBase {

	private static final String KEY_ECHO = "echo";
	private static final String KEY_SPLIT = "ah";
	private static final String KEY_PWD_USER_DIR = "pwd";
	private static final String KEY_TILDE_USER_HOME = "~";
	private static final String KEY_MAVEN = "maven";
	private static final String KEY_DEPS = "deps";
	private static final String KEY_ISSUE = "iss";
	private static final String KEY_JENKINS = "jk";
	private static final String KEY_JSON = "js";
	private static final int KEY_JSON_MIN_LEN = 9;
	private static final String KEY_PAIR_KEY_VALUE = "pa";
	private static final String KEY_TO_UPPERCASE = "up";
	private static final String KEY_TO_LOWERCASE= "lo";
	private static final String KEY_ZIP = FileUtil.SUFFIXES_ZIP.replace(';', '|');
	private static final String KEY_UUID = "uuid";
	private static final String KEY_CHANGE_FILESEPARATOR = "sw";
	private static final String KEY_SIZE = "size";
	private static final String KEY_HTTP_STATUS_CODES = "https";
	
	public boolean handle() {
		//Satoshi Nakamoto
		solo = parseParam(KEY_ECHO + "(|\\s.*?)");
		if(solo != null) {
			if(StrUtil.equals(solo, "path")) {
				//do whole match if and only PATH
				solo = "^" + solo + "$";
			}
			List<TypedKeyValueItem> items = Lists.newArrayList();
			boolean showAll = OptionUtil.readBooleanPRI(options, "a", false);
			if(showAll || OptionUtil.readBooleanPRI(options, "s", true)) {
				List<TypedKeyValueItem> sato = SatoUtil.SYSTEM_PROPERTIES;
				if(!EmptyUtil.isNullOrEmpty(solo)) {
					sato = CollUtil.filter(sato, solo);
				}
				items.addAll(sato);
			}
			if(showAll || OptionUtil.readBooleanPRI(options, "e", true)) {
				List<TypedKeyValueItem> sato = SatoUtil.ENVIRONMENT_VARIABLES;
				if(!EmptyUtil.isNullOrEmpty(solo)) {
					sato = CollUtil.filter(sato, solo);
				}
				items.addAll(sato);
			}
			if(showAll || OptionUtil.readBooleanPRI(options, "u", false)) {
				List<TypedKeyValueItem> sato = g().getUserProps().listOf();
				if(!EmptyUtil.isNullOrEmpty(solo)) {
					sato = CollUtil.filter(sato, solo);
				}
				items.addAll(sato);
			}
			if(showAll || OptionUtil.readBooleanPRI(options, "i", false)) {
				List<TypedKeyValueItem> sato = g().getInnerProps().listOf();
				if(!EmptyUtil.isNullOrEmpty(solo)) {
					sato = CollUtil.filter(sato, solo);
				}
				items.addAll(sato);
			}
			Collections.sort(items);
			
			Boolean showValueItemInLines = OptionUtil.readBoolean(options, "l");
			if(showValueItemInLines == null) {
				showValueItemInLines = items.size() == 1;
			}
			if(showValueItemInLines) {
				List<String> gras = Lists.newArrayList();
				String sepStr = OptionUtil.readString(options, "s");
				String sep = File.pathSeparator;
				if(!EmptyUtil.isNullOrEmpty(sepStr)) {
					sep = sepStr;
				}
				for(TypedKeyValueItem item : items) {
					List<String> sons = item.valueItemInLines(sep);
					if(sons.size() > 1) {
						if(OptionUtil.readBooleanPRI(options, "sort", true)) {
							CollUtil.sortIgnoreCase(sons);
						}
						gras.add(item.toPrint(OptionUtil.mergeOptions("-v", options)));
						gras.addAll(sons);
					} else {
						gras.add(item.toPrint(options));
					}
				}
				
				export(gras);
			} else {
				export(items);
			}
			
			return true;
		}

		solo = parseParam(KEY_SPLIT + "\\s(.+)");
		if(solo != null) {
			String sepStr = OptionUtil.readString(options, "s");
			String sep = ",";
			if(!EmptyUtil.isNullOrEmpty(sepStr)) {
				sep = sepStr;
			}
			List<String> items = StrUtil.split(solo, sep);
			if(OptionUtil.readBooleanPRI(options, "s", false)) {
				CollUtil.sortIgnoreCase(items);
			}
			
			export(items);
			
			return true;
		}

		if(is(KEY_PWD_USER_DIR)) {
			String value = System.getProperty("user.dir");
			export(value);
			
			return true;
		}
		
		if(is(KEY_TILDE_USER_HOME)) {
			String value = System.getProperty("user.home");
			export(value);
			
			return true;
		}
		
		if(is(KEY_MAVEN)) {
			List<String> items = MavenManager.g().getMavenInfo();
			export(items);
			
			return true;
		}
		
		if(is(KEY_DEPS)) {
			String mvnCommand = "mvn dependency:list";
			String path = OptionUtil.readString(options, "p");
			if(!EmptyUtil.isNullOrEmpty(path)) {
				mvnCommand = translatePath(mvnCommand, path);
			}
			List<String> items = MavenManager.g().getDependencies(mvnCommand);
			if(target.isFileRelated()) {
				export(CollUtil.toFileList(items));
			} else {
				export(items);
			}

			return true;
		}
		
		solo = parseParam(KEY_ISSUE + " ([A-Za-z0-9\\-_]+/[A-Za-z0-9\\-_]+)");
		if(solo != null) {
			Extractor<MexItem> frank = new GithubIssuesExtractor(solo);
			frank.process();
			List<MexItem> items = frank.getItems();
			export(items);
			
			return true;
			
		}
		
		if(is(KEY_JENKINS)) {
			String key = "jenkins.url";
			String url = SimpleKonfig.g().getUserValueOf(key);
			XXXUtil.nullCheck(url, key);
			
			export(url);
		}
		
		solo = parseParam(KEY_JENKINS + " ([A-Za-z0-9\\-_]+)");
		if(solo != null) {
			JenkinsBuildRecord record = JenkinsManager.g().getLatestBuildRecord(solo);
			export(record);
			
			return true;
		}
		
		params = parseParams(KEY_JENKINS + " ([A-Za-z0-9\\-_]+)\\.([1-9]\\d{0,3})");
		if(params != null) {
			String jobName = params[0];
			String numberStr = params[1];
			Integer buildNumber = MathUtil.toInteger(numberStr);
			List<JenkinsBuildRecord> records = JenkinsManager.g().getLatestKBuildRecords(jobName, buildNumber);
			export(records);
			
			return true;
		}
		
		params = parseParams(KEY_JENKINS + " ([A-Za-z0-9\\-_]+)#([1-9]\\d{0,3})");
		if(params != null) {
			String jobName = params[0];
			String numberStr = params[1];
			JenkinsBuildRecord record = JenkinsManager.g().getBuildRecordByNumber(jobName, numberStr);
			export(record);
			
			return true;
		}
		
		solo = parseParam(KEY_JSON + " " + KEY_HTTP_WWW);
		if(solo != null) {
			String source = IOUtil.readURL(solo);
			if(OptionUtil.readBooleanPRI(options, "r", false)) {
				export(JsonUtil.getRawText(source));
			} else {
				export(JsonUtil.getPrettyTextInLines(source));
			}
			
			return true;
		}
		
		if(command.length() >= KEY_JSON_MIN_LEN) {
			try {
				if(OptionUtil.readBooleanPRI(options, "r", false)) {
					export(JsonUtil.getRawText(command));
				} else {
					export(JsonUtil.getPrettyTextInLines(command));
				}
				return true;
			} catch (MexException ex) {
				//
			}
		}
		
		solo = parseParam(KEY_JSON + " (.+?)");
		if(solo != null) {
			File file = parseFile(solo);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					String source = IOUtil.readFileWithoutLineSeparator(filePath);
					if(OptionUtil.readBooleanPRI(options, "r", false)) {
						export(JsonUtil.getRawText(source));
					} else {
						export(JsonUtil.getPrettyTextInLines(source));
					}
				} else {
					XXXUtil.alert("Not a text file: " + filePath);
				}
				
				return true;
			} else {
				if(OptionUtil.readBooleanPRI(options, "r", false)) {
					export(JsonUtil.getRawText(solo));
				} else {
					export(JsonUtil.getPrettyTextInLines(solo));
				}
			}
			
			return true;
		}
		
		solo = parseParam(KEY_PAIR_KEY_VALUE + "\\s(.+)");
		if(solo != null) {
			List<String> pairs = StrUtil.parseUrlParams(solo);
			export(pairs);
			
			return true;
		}
		
		solo = parseParam(KEY_TO_LOWERCASE + "\\.(.+)");
		if(solo != null) {
			C.pl("To lower case, " + solo.length() + " chars.");
			String result = solo.toLowerCase();
			export(result);
			
			return true;
		}
		
		solo = parseParam(KEY_TO_UPPERCASE + "\\.(.+)");
		if(solo != null) {
			C.pl("To upper case, " + solo.length() + " chars.");
			String result = solo.toUpperCase();
			export(result);
			
			return true;
		}
		
		regex = StrUtil.occupy("(.+\\.({0}))!/(.+)", KEY_ZIP);
		params = parseParams(regex);
		if(params != null) {
			String whatfile = params[0];
			String whatentry = params[2];
			File jar = parseFile(whatfile);
			if(jar != null) {
				List<String> items = ArisUtil.readZipEntry(jar.getAbsolutePath(), whatentry);
				export(items);
				return true;
			}
		}
		
		InputAnalyzer sean = new FileSizeInputAnalyzer(input);
		regex = StrUtil.occupy("(-?)(.+\\.({0}))\\s+(.+)", KEY_ZIP);
		String[] crazy = StrUtil.parseParams(regex, sean.getCommand());
		if(crazy != null) {
			this.command = sean.getCommand();
			this.target = sean.getTarget();
			this.options = sean.getOptions();
			boolean showSize = !crazy[0].isEmpty();
			String fileInfo = crazy[1];
			String criteria = crazy[3];
			List<String> files = FileUtil.explodeAsterisk(fileInfo);
			List<MexZipEntry> allItems = new ArrayList<>();
			for(String onefile : files) {
				File jar = parseFile(onefile);
				if(jar != null) {
					String filepath = jar.getAbsolutePath();
					List<MexZipEntry> items = ArisUtil.parseZipEntries(filepath);
					allItems.addAll(items);
				}
			}
			String tempOptions = showSize ? "+size" : "";
			if(options != null) {
				tempOptions += "," + options;
			}
			
			if(KEY_2DOTS.equals(criteria)) {
				export(allItems, tempOptions);
			} else {
				MexFilter<MexZipEntry> filter = new MexFilter<MexZipEntry>(criteria, allItems, isCaseSensitive(tempOptions));
				List<MexZipEntry> result = filter.process();
				export(result, tempOptions);
			}
			
			return true;
		}
		
		if(is(KEY_UUID)) {
			String value = UUID.randomUUID().toString().replace("-", "");
			export(value);
			
			return true;
		}
		
		solo = parseParam(KEY_UUID + "(\\d{1,3})");
		if(solo != null) {
			List<String> items = new ArrayList<>();
			int count = Integer.parseInt(solo);
			for(int i = 0; i < count; i++) {
				items.add(UUID.randomUUID().toString().replace("-", ""));
			}
			
			export(items);
			return true;
		}
		
		solo = parseParam(KEY_CHANGE_FILESEPARATOR + "\\s+(.+)");
		if(solo != null) {
			char windows = '\\';
			char unix = '/';
			List<String> items = new ArrayList<>();
			String unixStyle = solo.replace(windows, unix);
			if(!StrUtil.equals(unixStyle, solo)) {
				items.add(unixStyle);
			}
			
			String windowsStyle = solo.replace(unix, windows);
			if(!StrUtil.equals(windowsStyle, solo)) {
				items.add(windowsStyle);
			}
			
			items.add(FileUtil.shellStyle(unixStyle));
			
			export(items);
			return true;
		}
		
		solo = parseParam(KEY_SIZE + "([1-9]|)");
		if(solo != null) {
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			List<String> items = Lists.newArrayList();
			items.add("full: " + (int)dim.width + " x " + (int)dim.height);
			if(!solo.isEmpty()) {
				ImageUtil.countDown(Integer.parseInt(solo));
				C.pl();
			}
			RenderedImage image = (new ScreenCaptor()).captureCurrentWindow();
			items.add("acti: " + (int)image.getWidth() + " x " + (int)image.getHeight());
			export(items);
			return true;
		}
		
		solo = parseParam(KEY_HTTP_STATUS_CODES + "\\s(.+?)");
		if(solo != null) {
			List<ValuesItem> items = CollUtil.filter(GeekExtractors.fetchHttpResponseCodes(), solo);
			String concise = HttpData.EGGS.get(solo);
			if(concise != null) {
				items.add(new ValuesItem(concise));
			}
			export(items);
			return true;
		}
		
		if(is(KEY_HTTP_STATUS_CODES)) {
			export(GeekExtractors.fetchHttpResponseCodes());
			return true;
		}
		
		return false;
	}
}
