package com.sirap.common.command.explicit;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.MatrixCalendar;
import com.sirap.basic.component.RioCalendar;
import com.sirap.basic.domain.MexedFile;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.email.EmailCenter;
import com.sirap.basic.math.FormulaCalculator;
import com.sirap.basic.math.MexCalculator;
import com.sirap.basic.math.MexColorConverter;
import com.sirap.basic.math.MexNumberConverter;
import com.sirap.basic.math.Sudoku;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.search.TextSearcher;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.basic.util.NetworkUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.domain.LocalSearchEngine;
import com.sirap.common.domain.SiteSearchEngine;
import com.sirap.common.domain.TZRecord;
import com.sirap.common.extractor.Extractor;
import com.sirap.common.framework.AppBase;
import com.sirap.common.framework.Janitor;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.common.framework.command.target.TargetEmail;
import com.sirap.common.framework.command.target.TargetPDF;
import com.sirap.common.manager.TimeZoneManager;

public class CommandSirap extends CommandBase {
	
	private static final String KEY_LIST_STORAGE = ".";
	private static final String KEY_EMAIL_CONFIGURATION = "mc";
	private static final String KEY_EMAIL_SETUP = "ms";
	private static final String KEY_VERSION = "ver";
	private static final String KEY_DATETIME_SERVER = "d,now";
	private static final String KEY_DATETIME_USER = "du";
	private static final String KEY_TIMEZONE_DISPLAY = "z\\.(.{1,20})";
	private static final String KEY_USER_TIMEZONE_SET = "u([+-](1[0-2]|[0-9]))";
	private static final String KEY_USER_SETTING = "u";
	private static final String KEY_CONFIGURATION = "c";
	private static final String KEY_USER_CONFIGURATION = "/";
	private static final String KEY_CALENDAR = "ca";
	private static final String KEY_CAPTURE_SCREEN = "s";
	private static final String KEY_CAPTURE_LAST = "ll";
	private static final String KEY_CAPTURE_SOUND_ONOFF_SWITCH = "sx";
	private static final String KEY_GENERATEDFILE_AUTOOPEN_ONOFF_SWITCH = "ox";
	private static final String KEY_EMAIL_ENABLED_SWITCH = "mx";
	private static final String KEY_TIMESTAMP_ENABLED_SWITCH = "tx";
	private static final String KEY_LOCALE = "l=";
	private static final String KEY_PERMUTATION = "p=";
	private static final String KEY_HOST = "host";
	private static final String KEY_MAC = "mac";
	private static final String LENGTH_OF = "l\\.";
	private static final String KEY_TO_DATE = "td";
	private static final String KEY_TO_LONG = "tl";
	private static final String KEY_ASSIGN_CHARSET = "gbk,utf8,utf-8,gb2312";
	private static final String KEY_MAXMIN = "max,min";

	{
		helpMeanings.put("image.formats", Konstants.IMG_FORMATS);
		helpMeanings.put("guest.quits", KEY_EXIT);
		helpMeanings.put("guest.escaper", InputAnalyzer.EXPORT_ESACPE);
	}
	
	@Override
	public boolean handle() {
		
		if(is(KEY_EMAIL_CONFIGURATION)) {
			String value = isEmailEnabled() ? "Enabled" : "Disabled";
			C.pl2(value + ", " + EmailCenter.g().getEmailInfo());
			return true;
		}

		if(is(KEY_EMAIL_SETUP)) {
			if(!isEmailEnabled()) {
				C.pl2("Email should be enabled to perform email setup.");
			} else {
				String passcode = SimpleKonfig.g().getSecurityPasscode();
				MexUtil.setupEmailCenter(EmailCenter.g(), passcode);
			}
			return true;
		}
		
		if(is(KEY_EMAIL_ENABLED_SWITCH)) {
			boolean flag = !isEmailEnabled();
			g().setEmailEnabled(flag);
			String value = flag ? "enabled" : "disabled";
			C.pl2("Email " + value + ", " + EmailCenter.g().getEmailInfo());
			
			return true;
		}
		
		if(is(KEY_GENERATEDFILE_AUTOOPEN_ONOFF_SWITCH)) {
			boolean flag = !g().isGeneratedFileAutoOpen();
			g().setGeneratedFileAutoOpen(flag);
			String value = flag ? "on" : "off";
			C.pl2("Generated-file auto-open is " + value + ".");
			
			return true;
		}
		
		if(is(KEY_TIMESTAMP_ENABLED_SWITCH)) {
			boolean flag = !g().isExportWithTimestampEnabled();
			g().setExportWithTimestampEnabled(flag);
			String value = flag ? "on" : "off";
			C.pl2("Export with timestamp is " + value + ".");
			
			return true;
		}
		
		if(is(KEY_CALENDAR)) {
			int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
			List<String> records = displayMonthCalendar(currentMonth);
			
			setIsPrintTotal(false);
			export(records);
			
			return true;
		}
		
		if(is(KEY_CALENDAR + KEY_2DOTS)) {
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			List<String> records = displayCalendarsOfYear(currentYear);
			
			if(records.size() > 0) {
				records.remove(records.size() - 1);
				setIsPrintTotal(false);
				export(records);
			}
			
			return true;
		}
		
		singleParam = parseParam(KEY_CALENDAR + "([\\d&]+)");
		if(singleParam != null) {
			List<String> totalRecords = new ArrayList<String>();
			List<String> yearMonthList = StrUtil.split(singleParam, '&');
			for(String info:yearMonthList) {
				String singleMonth = StrUtil.parseParam("(\\d{1,2})", info);
				if(singleMonth != null) {
					int month = Integer.parseInt(singleMonth);
					if(month < 1 || month > 12) {
						continue;
					}
					
					List<String> temp = displayMonthCalendar(month);
					if(!EmptyUtil.isNullOrEmpty(temp)) {
						totalRecords.addAll(temp);
						totalRecords.add("");
					}
					
					continue;
				}
				
				String[] monthOfYear = StrUtil.parseParams("(\\d{4})(\\d{1,2})", info);
				if(monthOfYear != null) {
					Integer year = MathUtil.toInteger(monthOfYear[0]);
					if(year == null) {
						continue;
					}
					
					Integer month = MathUtil.toInteger(monthOfYear[1]);
					if(month == null) {
						continue;
					}
					
					List<String> temp = displayMonthCalendar(year, month);
					if(!EmptyUtil.isNullOrEmpty(temp)) {
						totalRecords.addAll(temp);
						totalRecords.add("");
					}
					continue;
				}
				
				String singleYear = StrUtil.parseParam("(\\d{4})", info);
				if(singleYear != null) {
					Integer year = MathUtil.toInteger(singleYear);
					if(year != null) {
						List<String> temp = displayCalendarsOfYear(year);
						if(!EmptyUtil.isNullOrEmpty(temp)) {
							totalRecords.addAll(temp);
						}
					}
					
					continue;
				}
			}
			
			if(totalRecords.size() > 0) {
				totalRecords.remove(totalRecords.size() - 1);
				setIsPrintTotal(false);
				export(totalRecords);
			}
			
			return true;
		}
		
		if(isIn(KEY_DATETIME_SERVER)) {
			export(DateUtil.displayDateWithGMT(new Date(), DateUtil.HOUR_Min_Sec_AM_WEEK_DATE, g().getLocale()));
			
    		return true;
		}

		if(isIn(KEY_DATETIME_USER)) {
			Date dateUser = DateUtil.getTZRelatedDate(g().getTimeZoneUser(), new Date());
			export(DateUtil.displayDateWithGMT(dateUser, DateUtil.HOUR_Min_Sec_AM_WEEK_DATE, g().getLocale(), g().getTimeZoneUser()));
    		
			return true;
		}
		
		singleParam = parseParam(KEY_TIMEZONE_DISPLAY);
		if(singleParam != null) {
			List<TZRecord> records = TimeZoneManager.g().getTimeZones(singleParam, g().getLocale(), false);
			if(!EmptyUtil.isNullOrEmpty(records)) {
				if(target instanceof TargetPDF) {
					int[] cellsWidth = {1, 1};
					int[] cellsAlign = {0, 0};
					PDFParams pdfParams = new PDFParams(cellsWidth, cellsAlign);
					target.setParams(pdfParams);
					List<List<String>> items = CollectionUtil.items2PDFRecords(records);
					export(items);
				} else {
					export(CollectionUtil.items2PrintRecords(records));	
				}
				
	    		return true;
			}
		}
		
		params = parseParams("(s|c)(|\\d{1,2})(|\\s.*?)");
		if(params != null) {
			String type = params[0];
			String strDelay = params[1];
			String nameInfo = params[2];
			int delay = MathUtil.toInteger(strDelay, 0);
			if(delay == 0) {
				String defDelayStr = SimpleKonfig.g().getUserValueOf("capture.delay");
				Integer defDelay = MathUtil.toInteger(defDelayStr);
				if(defDelay != null && defDelay > 0) {
					int maxDelay = 99;
					if(defDelay > maxDelay) {
						delay = maxDelay;
					} else {
						delay = defDelay;
					}
				}
			}
			
			String[] filenameAndFormat = generateImageFilenamePrefixAndFormat(nameInfo, Konstants.IMG_BMP);
			String filename = filenameAndFormat[0];
			String format = filenameAndFormat[1];
			
			String filePath = IOUtil.takePhoto(filename, getCaptureSound(), format, delay, KEY_CAPTURE_SCREEN.equals(type));
			if(filePath != null) {
				C.pl(" => " + filePath);
				tryToOpenGeneratedImage(filePath);
			}
			
			if(target instanceof TargetEmail) {
				export(FileUtil.getIfNormalFile(filePath));
			}
			
			return true;
		}
		
		params = parseParams(KEY_CAPTURE_LAST + "(|\\s+(.*?))");
		if(params != null) {
			String llPath = g().getUserValueOf("ll.folder");
			String folderPath = llPath != null ? llPath : screenShotPath();
			String key = params[1];
			String filePath = lastModified(folderPath, key);
			if(filePath != null) {
				FileOpener.open(filePath);
			}
		}
		
		
		params = parseParams("(s|c)(|\\d{1,2})\\+(\\d{1,4})(|\\s.*?)");
		if(params != null) {
			String type = params[0];
			String strDelay = params[1];
			int count = Integer.valueOf(params[2]);
			if(count > 0) {
				String nameInfo = params[3];
				int delay = MathUtil.toInteger(strDelay, 0);
				if(delay == 0) {
					String defDelayStr = SimpleKonfig.g().getUserValueOf("capture.delay");
					Integer defDelay = MathUtil.toInteger(defDelayStr);
					if(defDelay != null && defDelay > 0) {
						int maxDelay = 99;
						if(defDelay > maxDelay) {
							delay = maxDelay;
						} else {
							delay = defDelay;
						}
					}
				}
				
				String[] filenameAndFormat = generateImageFilenamePrefixAndFormat(nameInfo, Konstants.IMG_BMP);
				String filename = filenameAndFormat[0];
				String format = filenameAndFormat[1];

				String filePath = IOUtil.takeConsecutivePhotos(filename, getCaptureSound(), format, delay, count, KEY_CAPTURE_SCREEN.equals(type));
				if(filePath != null) {
					tryToOpenGeneratedImage(filePath);
				}
				
				if(target instanceof TargetEmail) {
					export(FileUtil.getIfNormalFile(filePath));
				}
				
				return true;
			}
		}
		
		if(is(KEY_CAPTURE_SOUND_ONOFF_SWITCH)) {
			boolean flag = !g().isCaptureSoundOn();
			g().setCaptureSoundOn(flag);
			String value = flag ? "on" : "off";
			C.pl2("Capture sound turned " + value + ".");
			
			return true;
		}
		
		if(is(KEY_VERSION)) {
			C.pl2(versionAndCopyright());
			return true;
		}
		
		if(is(KEY_LIST_STORAGE)) {
			File file = FileUtil.getIfNormalFolder(storage());
			if(file != null) {
				String path = file.getAbsolutePath();
				if(path != null) {
					List<String> records = listDirectory(path);
					export(records);
				}
				return true;
			}
		}

		if(is(KEY_USER_SETTING)) {
			C.pl2(getSystemInfo());
			return true;
		}
		
		if(is(KEY_LOCALE)) {
			C.pl2("en,zh,en_GB, en_US, es_ES, zh_CN, zh_TW, ja_JP, it_IT");
			return true;
		}
		
		singleParam = parseParam(KEY_USER_TIMEZONE_SET);
		if(singleParam != null) {
			resetTimeZone(Integer.parseInt(singleParam));
			return true;
		}
		
		singleParam = parseParam(KEY_LOCALE + "(.*?)");
		if(singleParam != null) {
			Locale locale = DateUtil.parseLocale(singleParam);
			if(locale != null) {
				resetLocale(locale);
				return true;
			}
		}
		
		String math = MexCalculator.evaluate(command, true);
		if(math != null) {
			List<String> results = new ArrayList<String>();
			results.add(command + "=" + math);
			export(results);
			return true;
		}
		
		math = FormulaCalculator.evaluate(command);
		if(math != null) {
			List<String> results = new ArrayList<String>();
			results.add("x=" + math);
			export(results);
			return true;
		}
		
		List<String[]> solutions = Sudoku.evaluate(command);
		if(!EmptyUtil.isNullOrEmpty(solutions)) {
			boolean isMultiple = solutions.size() > 1;
			List<String> results = new ArrayList<String>();
			for(int i = 0; i < solutions.size(); i++) {
				if(isMultiple) {
					results.add("Solution " + (i+1) + ">");
				}
				String[] matrix = solutions.get(i);
				
				for(int m = 0; m < matrix.length; m++) {
					results.add(matrix[m].replace("", " "));
				}
			}
			export(results);
			
			return true;
		}
		
		params = parseParams("(h|hex|d|dec|o|oct|b|bin|)=([a-f|\\d|,|\\s]+)");
		if(params != null) {
			MexNumberConverter salim = new MexNumberConverter(params[0], params[1]);
			List<String> results = salim.getResult();
			export(results);
			return true;
		}
		
		singleParam = parseParam("#([a-f|\\d|,|\\s]+)");
		if(singleParam != null) {
			MexColorConverter salim = new MexColorConverter(singleParam);
			List<String> results = salim.getResult();
			if(!EmptyUtil.isNullOrEmpty(results)) {
				export(results);
				return true;
			}
		}
		
		params = parseParams(KEY_PERMUTATION + "(.+?)\\s*,\\s*(\\d+)");
		if(params != null) {
			String p0 = params[0].trim();
			String p1 = params[1].trim();
			Integer targetSize = MathUtil.toInteger(p1);
			if(targetSize != null) {
				Integer numberOfSamples = MathUtil.toInteger(p0);
				if(numberOfSamples != null) {
					String result = MathUtil.permutationWithLimit(numberOfSamples, targetSize, 99);
					if(result != null) {
						C.pl2("Permutation(" + numberOfSamples + "," + targetSize + ")=" + result);
					}
				}
				
				String source = p0;
				List<String> records = MathUtil.permutation(source, targetSize);
				if(!EmptyUtil.isNullOrEmpty(records)) {
					records.add(C.getTotal(records.size()));
					setIsPrintTotal(false);
					export(records);
				}
				
				return true;
			}
		}
		
		String userConfig = KEY_USER_CONFIGURATION;
		if(PanaceaBox.isMac()) {
			userConfig = ";" + KEY_USER_CONFIGURATION;
		}
		params = parseParams(userConfig + "(|(.*?))");
		if(params != null) {
			String criteria = params[1];
			String userConfigFile = g().getUserConfigFileName();
			if(userConfigFile != null) {
				List<String> records = IOUtil.readFileIntoList(userConfigFile, g().getCharsetInUse());
				exportItems(records, criteria);
				
				return true;
			}
		}
		
		if(is(KEY_CONFIGURATION + KEY_REFRESH)) {
			g().refresh();
			C.pl2("Configuration refreshed.");
			
			return true;
		}
		
		String regexDateStr = "(|\\d{4})\\.(|\\d{1,2})\\.(|\\d{1,2})";
		params = parseParams(regexDateStr + "\\s*-\\s*" + regexDateStr);
		if(params != null) {
			Date d1 = DateUtil.construct(params[0], params[1], params[2]);
			Date d2 = DateUtil.construct(params[3], params[4], params[5]);
			int dayDiff = DateUtil.dayDiff(d1, d2);
			String d1Str = DateUtil.displayDateCompact(d1);
			String d2Str = DateUtil.displayDateCompact(d2);
			export(d1Str + " - " + d2Str + " = " + dayDiff);
			
			return true;
		}
		
		params = parseParams(regexDateStr + "\\s*([+-])\\s*(\\d{1,5})");
		if(params != null) {
			Date d1 = DateUtil.construct(params[0], params[1], params[2]);
			String operator = params[3];
			int dayDiffAbs = MathUtil.toInteger(params[4]);
			int dayDiff = dayDiffAbs;
			if(StrUtil.equals(operator, "-")) {
				dayDiff *= -1;
			}
			
			Date d2 = DateUtil.add(d1, Calendar.DAY_OF_YEAR, dayDiff);

			String d1Str = DateUtil.displayDateCompact(d1);
			String d2Str = DateUtil.displayDateCompact(d2);
			export(d1Str + " " + operator + " " + dayDiffAbs + " = " + d2Str);
			
			
			return true;
		}
		
		List<SiteSearchEngine> sites = getSiteSearchEngines();
		for(SiteSearchEngine engine:sites) {
			boolean isMatched = conductSiteSearch(engine);
			if(isMatched) {
				return true;
			}
		}
		
		List<LocalSearchEngine> locals = getLocalSearchEngines();
		for(LocalSearchEngine engine:locals) {
			boolean isMatched = conductLocalSearch(engine);
			if(isMatched) {
				return true;
			}
		}
		
		if(handleHttpRequest(command)) {
			return true;
		}
		
		if(is(KEY_HOST)) {
			String result = NetworkUtil.getLocalhostNameIpMac();
			export(result);
			return true;
		}
		
		if(is(KEY_MAC)) {
			List<String> items = NetworkUtil.getLocalMacItems();
			export(items);
			return true;
		}
		
		singleParam = parseParam(KEY_HOST + " (.*?)");
		if(singleParam != null) {
			String result = NetworkUtil.getHostByName(singleParam);
			export(result);
			
			return true;
		}
		
		singleParam = parseParam(KEY_TO_DATE + "\\.(-?\\d{0,14})");
		if(singleParam != null) {
			Long milliSecondsSince1970 = Long.parseLong(singleParam);
			
			List<String> items = new ArrayList<>();
			items.add(DateUtil.convertLongToDateStr(milliSecondsSince1970, DateUtil.HOUR_Min_Sec_Milli_AM_WEEK_DATE));
			items.add(DateUtil.convertLongToDateStr(milliSecondsSince1970, DateUtil.DATETIME_ALL_TIGHT));
			
			export(items);
			
			return true;
		}
		
		if(is(KEY_TO_LONG)) {
			long value = DateUtil.convertDateStrToLong(null);
			export(value);
			
			return true;
		}
		
		singleParam = parseParam(KEY_TO_LONG + "\\.(\\d{8,17})");
		if(singleParam != null) {
			long value = DateUtil.convertDateStrToLong(singleParam);
			export(value);
			
			return true;
		}
		
		singleParam = parseParam(LENGTH_OF + "(.+?)");
		if(singleParam != null) {
			int len = singleParam.length();
			String value = "len = " + len;
			export(value);
			
			return true;
		}
		
		if(isIn(KEY_MAXMIN)) {
			List<String> items = new ArrayList<>();
			items.add(maxmin("Max of Long", Long.MAX_VALUE));
			items.add(maxmin("Min of Long", Long.MIN_VALUE));
			items.add(maxmin("Max of Integer", Integer.MAX_VALUE));
			items.add(maxmin("Min of Integer", Integer.MIN_VALUE));
			items.add(maxmin("Max of Short", Short.MAX_VALUE));
			items.add(maxmin("Min of Short", Short.MIN_VALUE));
			items.add(maxmin("Max of Byte", Byte.MAX_VALUE));
			items.add(maxmin("Min of Byte", Byte.MIN_VALUE));
			
			export(items);
			
			return true;
		}
		
		if(isIn(KEY_ASSIGN_CHARSET)) {
			String charset = command.replace("-", "").toUpperCase();
			g().setCharsetInUse(charset);
			C.pl2("Charset in use: " + charset);
			
			return true;
		}
				
		return false;
	}
	
	private List<SiteSearchEngine> getSiteSearchEngines() {
		List<SiteSearchEngine> engines = new ArrayList<SiteSearchEngine>();
		List<String> engineRecords = g().getValuesByKeyword("site.search.");
		List<String> userDefinedEngines = g().getUserValuesByKeyword("site.search.");
		engineRecords.addAll(userDefinedEngines);
		for(String engineInfo:engineRecords) {
			SiteSearchEngine se = new SiteSearchEngine();
			boolean flag = se.parse(engineInfo);
			if(flag) {
				engines.add(se);
			}
		}
		
		return engines;
	}
	
	private List<LocalSearchEngine> getLocalSearchEngines() {
		List<LocalSearchEngine> engines = new ArrayList<LocalSearchEngine>();
		List<String> engineRecords = g().getValuesByKeyword("local.search.");
		List<String> userDefinedEngines = g().getUserValuesByKeyword("local.search.");
		engineRecords.addAll(userDefinedEngines);
		for(String engineInfo:engineRecords) {
			LocalSearchEngine se = new LocalSearchEngine();
			boolean flag = se.parse(engineInfo);
			if(flag) {
				engines.add(se);
			}
		}
		
		return engines;
	}
	
	private boolean conductSiteSearch(SiteSearchEngine engine) {
		String regex = engine.getPrefix() + " (.+?)";
		String singleParam = parseParam(regex);
		if(singleParam != null) {
			String urlTemplate = engine.getUrlTemplate();
			String url = StrUtil.occupy(urlTemplate, Extractor.encodeURLParam(singleParam));
			FileOpener.playThing(url, "page.viewer");
			C.pl(url);
			String motto = engine.getMotto();
			if(!EmptyUtil.isNullOrEmpty(motto)) {
				C.pl(engine.getMotto());
			}
			C.pl();
			
			return true;
		}
		
		return false;
	}
	
	private boolean conductLocalSearch(LocalSearchEngine engine) {
		String engineName = engine.getPrefix();
		boolean isSpaceMandatory = engine.isUseSpace();
		String regex = engineName + (isSpaceMandatory ? "\\s(.+?)" : "(.+?)");
		String criteria = parseParam(regex);
		if(criteria != null) {
			String folders = engine.getFolders();
			String suffixes = engine.getSuffixes();
			boolean printSource = engine.isPrintSource();
			boolean useCache = engine.isUseCache();
			List<MexedObject> list = null;
			if(useCache) {
				list = TextSearcher.searchWithCache(engineName, folders, suffixes, criteria, printSource, g().getCharsetInUse());
			} else {
				list = TextSearcher.search(folders, suffixes, criteria, printSource, g().getCharsetInUse());
			}
			
			export(list);
			
			return true;
		}
		
		return false;
	}
	
	private String[] generateImageFilenamePrefixAndFormat(String nameInfo, String defFormat) {

		String suffix = "";
		String format = "";
		if(EmptyUtil.isNullOrEmpty(nameInfo)) {
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
		
		String filenamePrefix = dir + "{0}" + FileUtil.generateLegalFileName(suffix);
		
		return new String[] {filenamePrefix, format};
	}
	
	private String getCaptureSound() {
		if(!g().isCaptureSoundOn()) {
			return null;
		}
		
		File file = parseFile(g().getUserValueOf("capture.sound"));
		if(file == null) {
			return null;
		}
		
		return file.getAbsolutePath();
	}
	
	@SuppressWarnings("unchecked")
	private List<String> displayMonthCalendar(int year, int month) {
		if(month < 1 || month > 12) {
			return Collections.EMPTY_LIST;
		}
		
		RioCalendar rioCal = new RioCalendar(year, month);
		rioCal.setLocale(g().getLocale());
		boolean isGood = rioCal.generate();
		if(isGood) {
			return rioCal.getRecords();
		}
		
		return Collections.EMPTY_LIST;
	}
	
	private List<String> displayMonthCalendar(int month) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		return displayMonthCalendar(currentYear, month);
	}
	
	private List<String> displayCalendarsOfYear(int year) {
		List<List<String>> grandList = new ArrayList<List<String>>();
		for(int m = 1; m <= 12; m++) {
			RioCalendar rioCal = new RioCalendar(year, m);
			boolean isGood = rioCal.generate();
			if(isGood) {
				List<String> records = rioCal.getRecords();
				grandList.add(records);
			} else {
				break;
			}
		}

		MatrixCalendar jamie = new MatrixCalendar(grandList, MatrixCalendar.MatrixMode.THREE);
		List<String> results = jamie.getResults();
		
		return results;
	}
	
	protected String getSystemInfo() {

		StringBuffer sb = new StringBuffer();
		sb.append(AppBase.USERNAME);
		sb.append(" ").append(g().getSystemInfo());
		
		Date expirationDate = Janitor.g().getExpirationDate();
		if(expirationDate != null) {
			String expDateStr = DateUtil.displayDate(Janitor.g().getExpirationDate(), "yyyyMMdd");
			sb.append(" expire@").append(expDateStr);
		}

		return sb.toString();
	}
	
	protected boolean resetTimeZone(Integer value) {
		int currentTZ = g().getTimeZoneUser();
		boolean isChanged = currentTZ != value;
		
		if(isChanged) {
			g().setTimeZoneUser(value);
			C.pl2("TimeZone reset as GMT" + StrUtil.signValue(value));
		}
		
		return isChanged;
	}

	private void resetLocale(Locale locale) {
		g().setLocale(locale);
		C.pl2("Locale reset as " + locale);
	}
	
	public String lastModified(String folderPath, final String criteria) {
		TreeMap<String, String> mapDateStr = new TreeMap<String, String>();
		TreeMap<Long, MexedObject> mapLastModified = new TreeMap<Long, MexedObject>();
		File folder = FileUtil.getIfNormalFolder(folderPath);
		if(folder == null) {
			String msg = "Invalid path [" + folderPath + "].";
			C.pl2(msg);
			return null;
		}
		
		List<MexedFile> list = new ArrayList<MexedFile>();
		folder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				if(!file.isFile()) {
					return false;
				}
				
				list.add(new MexedFile(file));
				
				mapLastModified.put(file.lastModified(), new MexedObject(file.getAbsolutePath()));
								
				return true;
			}
		});

		String filePath = null;
		if(criteria == null) {
			String regex = "^\\d{8}_\\d{6}";
			Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			
			for(MexedFile file : list) {
				String fileName = file.getName();
				String fileAbsPath = file.getPath();
				Matcher m = ptn.matcher(fileName);
				if(m.find()) {
					String dateStr = m.group(0);
					mapDateStr.put(dateStr, fileAbsPath);
				}
			}
			
			Map.Entry<String, String> entry = mapDateStr.lastEntry();
			if(entry != null) {
				filePath = entry.getValue();
				C.pl(filePath);
			} else {
				Map.Entry<Long,  MexedObject> entry2 = mapLastModified.lastEntry();
				if(entry2 != null) {
					filePath = entry2.getValue().getString();
					C.pl(filePath);
				} else {
					exportEmptyMsg();
				}
			}
		} else {
			MexFilter<MexedObject> filter = new MexFilter<MexedObject>(criteria, new ArrayList<MexedObject>(mapLastModified.values()));
			List<MexedObject> filePaths = filter.process();
			
			int size = filePaths.size();
			if(size == 1) {
				filePath = filePaths.get(0).getString();
				C.pl(filePath);
			} else {
				if(!filePaths.isEmpty()) {
					filePath = filePaths.get(size - 1).getString();
				} 
				export(filePaths);
			}
		}
		
		return filePath;
	}
	
	private String maxmin(String displayName, long value) {
		String str = String.valueOf(value);
		int len = str.length();
		if(value < 0) {
			len--;
		}
		
		String temp = "{0} is {1}, {2} chars.";
		String result = StrUtil.occupy(temp, displayName, str, len);
		
		return result;
	}
}