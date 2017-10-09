package com.sirap.geek;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.domain.MexZipEntry;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.ScreenCaptor;
import com.sirap.basic.util.ArisUtil;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.ImageUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.extractor.Extractor;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.FileSizeInputAnalyzer;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.geek.jenkins.JenkinsBuildRecord;
import com.sirap.geek.jenkins.JenkinsManager;
import com.sirap.geek.manager.GithubIssuesExtractor;
import com.sirap.geek.manager.MavenManager;

public class CommandDev extends CommandBase {

	private static final String KEY_PATH = "path";
	private static final String KEY_PRESENT_WORKING_DIRECTORY = "pwd";
	private static final String KEY_MAVEN = "maven";
	private static final String KEY_DEPS = "deps";
	private static final String KEY_ISSUE = "iss";
	private static final String KEY_JENKINS = "jk";
	private static final String KEY_JSON = "js";
	private static final int KEY_JSON_MIN_LEN = 9;
	private static final String KEY_RAW_JSON = "rjs";
	private static final String KEY_PAIR_KEY_VALUE = "pa";
	private static final String KEY_TO_UPPERCASE = "up";
	private static final String KEY_TO_LOWERCASE= "lo";
	private static final String KEY_ZIP = FileUtil.SUFFIXES_ZIP.replace(';', '|');
	private static final String KEY_UUID = "uuid";
	private static final String KEY_CHANGE_FILESEPARATOR = "sw";
	private static final String KEY_SIZE = "size";
	
	public boolean handle() {
		singleParam = parseParam(KEY_PATH + "\\s(.*?)");
		if(singleParam != null) {
			List<String> items = IOUtil.echoPath();
			List<MexObject> result = CollectionUtil.search(items, singleParam);
			
			export(result);
			
			return true;
		}

		if(is(KEY_PATH)) {
			List<String> items = IOUtil.echoPath();
			export(items);
			
			return true;
		}

		if(is(KEY_PRESENT_WORKING_DIRECTORY)) {
			String value = System.getProperty("user.dir");
			export(value);
			
			return true;
		}
		
		if(is(KEY_MAVEN)) {
			List<String> items = MavenManager.g().getMavenInfo();
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_DEPS + "\\s(.*?)");
		if(singleParam != null) {
			String filepath = null;
			File file = FileUtil.getIfNormalFile(singleParam);
			if(file != null) {
				filepath = file.getAbsolutePath();
			} else {
				File folder = FileUtil.getIfNormalFolder(singleParam);
				if(folder != null) {
					String temp = folder.getAbsolutePath() + File.separatorChar + "pom.xml";
					if(FileUtil.exists(temp)) {
						filepath = temp;
					}
				}
			}
			
			if(filepath != null) {
				List<String> result = MavenManager.g().getDependencies(filepath);
				export(result);

				return true;
			}
		}
		
		singleParam = parseParam(KEY_ISSUE + " ([A-Za-z0-9\\-_]+/[A-Za-z0-9\\-_]+)");
		if(singleParam != null) {
			Extractor<MexItem> frank = new GithubIssuesExtractor(singleParam);
			frank.process();
			List<MexItem> items = frank.getMexItems();
			export(items);
			
			return true;
			
		}
		
		if(is(KEY_JENKINS)) {
			String key = "jenkins.url";
			String url = SimpleKonfig.g().getUserValueOf(key);
			XXXUtil.nullCheck(url, key);
			
			export(url);
		}
		
		singleParam = parseParam(KEY_JENKINS + " ([A-Za-z0-9\\-_]+)");
		if(singleParam != null) {
			JenkinsBuildRecord record = JenkinsManager.g().getLatestBuildRecord(singleParam);
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
		
		singleParam = parseParam(KEY_JSON + " " + KEY_HTTP_WWW);
		if(singleParam != null) {
			String source = IOUtil.readURL(singleParam);
			String text = JsonUtil.getPrettyText(source);
			export(text);
			
			return true;
		}
		
		if(command.length() >= KEY_JSON_MIN_LEN) {
			try {
				String tempJson = JsonUtil.getPrettyText(command);
				export(tempJson);
				return true;
			} catch (MexException ex) {
				//
			}
		}
		
		singleParam = parseParam(KEY_JSON + " (.+?)");
		if(singleParam != null) {
			File file = parseFile(singleParam);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					String source = IOUtil.readFileWithoutLineSeparator(filePath);
					String text = JsonUtil.getPrettyText(source);
					export(text);
				} else {
					XXXUtil.alert("Not a text file: " + filePath);
				}
				
				return true;
			} else {
				String text = JsonUtil.getPrettyText(singleParam);
				export(text);
			}
			
			return true;
		}
		
		singleParam = parseParam(KEY_RAW_JSON + " " + KEY_HTTP_WWW);
		if(singleParam != null) {
			String source = IOUtil.readURL(singleParam);
			String text = JsonUtil.getRawText(source);
			export(text);
			
			return true;
		}
		
		singleParam = parseParam(KEY_RAW_JSON + " (.+?)");
		if(singleParam != null) {
			File file = parseFile(singleParam);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					String source = IOUtil.readFileWithoutLineSeparator(filePath);
					String text = JsonUtil.getRawText(source);
					export(text);
				} else {
					XXXUtil.alert("Not a text file: " + filePath);
				}
				
				return true;
			} else {
				String text = JsonUtil.getRawText(singleParam);
				export(text);
			}
			
			return true;
		}
		
		singleParam = parseParam(KEY_PAIR_KEY_VALUE + "\\s(.+)");
		if(singleParam != null) {
			List<String> pairs = StrUtil.parseUrlParams(singleParam);
			export(pairs);
			
			return true;
		}
		
		singleParam = parseParam(KEY_TO_LOWERCASE + "\\.(.+)");
		if(singleParam != null) {
			C.pl("To lower case, " + singleParam.length() + " chars.");
			String result = singleParam.toLowerCase();
			export(result);
			
			return true;
		}
		
		singleParam = parseParam(KEY_TO_UPPERCASE + "\\.(.+)");
		if(singleParam != null) {
			C.pl("To upper case, " + singleParam.length() + " chars.");
			String result = singleParam.toUpperCase();
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
				exportWithOptions(allItems, tempOptions);
			} else {
				MexFilter<MexZipEntry> filter = new MexFilter<MexZipEntry>(criteria, allItems, isCaseSensitive(tempOptions));
				List<MexZipEntry> result = filter.process();
				exportWithOptions(result, tempOptions);
			}
			
			return true;
		}
		
		if(is(KEY_UUID)) {
			String value = UUID.randomUUID().toString().replace("-", "");
			export(value);
			
			return true;
		}
		
		singleParam = parseParam(KEY_UUID + "(\\d{1,3})");
		if(singleParam != null) {
			List<String> items = new ArrayList<>();
			int count = Integer.parseInt(singleParam);
			for(int i = 0; i < count; i++) {
				items.add(UUID.randomUUID().toString().replace("-", ""));
			}
			
			export(items);
			return true;
		}
		
		singleParam = parseParam(KEY_CHANGE_FILESEPARATOR + "\\s+(.+)");
		if(singleParam != null) {
			char windows = '\\';
			char unix = '/';
			List<String> items = new ArrayList<>();
			String temp = singleParam.replace(windows, unix);
			if(!StrUtil.equals(temp, singleParam)) {
				items.add(temp);
			}
			
			temp = singleParam.replace(unix, windows);
			if(!StrUtil.equals(temp, singleParam)) {
				items.add(temp);
			}
			
			export(items);
		}
		
		singleParam = parseParam(KEY_SIZE + "([1-9]|)");
		if(singleParam != null) {
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			List<String> items = Lists.newArrayList();
			items.add("full: " + (int)dim.width + " x " + (int)dim.height);
			if(!singleParam.isEmpty()) {
				ImageUtil.countDown(Integer.parseInt(singleParam));
				C.pl();
			}
			RenderedImage image = (new ScreenCaptor()).captureCurrentWindow();
			items.add("acti: " + (int)image.getWidth() + " x " + (int)image.getHeight());
			export(items);
		}
		
		return false;
	}
}
