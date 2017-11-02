package com.sirap.common.command.explicit;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.MatrixCalendar;
import com.sirap.basic.component.RioCalendar;
import com.sirap.basic.component.comparator.MexFileComparator;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.domain.MexTextSearchRecord;
import com.sirap.basic.email.EmailCenter;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.search.TextSearcher;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.ImageUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.basic.util.NetworkUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.domain.SiteSearchEngine;
import com.sirap.common.domain.TZRecord;
import com.sirap.common.domain.TextSearchEngine;
import com.sirap.common.extractor.Extractor;
import com.sirap.common.framework.App;
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
	private static final String KEY_HOST = "host";
	private static final String KEY_MAC = "mac";
	private static final String KEY_ASSIGN_CHARSET = "gbk,utf8,utf-8,gb2312,unicode";

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
		
		solo = parseSoloParam(KEY_CALENDAR + "([\\d&]+)");
		if(solo != null) {
			List<String> totalRecords = new ArrayList<String>();
			List<String> yearMonthList = StrUtil.split(solo, '&');
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

		if(is(KEY_DATETIME_USER)) {
			Date dateUser = DateUtil.getTZRelatedDate(g().getTimeZoneUser(), new Date());
			export(DateUtil.displayDateWithGMT(dateUser, DateUtil.HOUR_Min_Sec_AM_WEEK_DATE, g().getLocale(), g().getTimeZoneUser()));
    		
			return true;
		}
		
		solo = parseSoloParam(KEY_TIMEZONE_DISPLAY);
		if(solo != null) {
			List<TZRecord> records = TimeZoneManager.g().getTimeZones(solo, g().getLocale(), false);
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
			
			String filePath = ImageUtil.takePhoto(filename, getCaptureSound(), format, delay, KEY_CAPTURE_SCREEN.equals(type));
			if(filePath != null) {
				String info = "";
				if(OptionUtil.readBooleanPRI(options, "d", false)) {
					info += " " + FileUtil.formatFileSize(filePath);
					info += " " + ImageUtil.readImageWidthHeight(filePath, "*");
				}

				C.pl(" => " + filePath + info);
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

				String filePath = ImageUtil.takeConsecutivePhotos(filename, getCaptureSound(), format, delay, count, KEY_CAPTURE_SCREEN.equals(type));
				if(filePath != null) {
					String info = "";
					if(OptionUtil.readBooleanPRI(options, "d", false)) {
						info += " " + FileUtil.formatFileSize(filePath);
						info += " " + ImageUtil.readImageWidthHeight(filePath, "x");
					}
					C.pl("detail:" + info);
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

					exportWithOptions(allFiles, tempOptions);
				}
				return true;
			}
		}

		if(is(KEY_USER_SETTING)) {
			C.pl2(getSystemInfo());
			return true;
		}
		
		solo = parseSoloParam(KEY_USER_TIMEZONE_SET);
		if(solo != null) {
			resetTimeZone(Integer.parseInt(solo));
			return true;
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
				exportByCriteria(records, criteria);
				
				return true;
			}
		}
		
		if(is(KEY_CONFIGURATION + KEY_REFRESH)) {
			g().refresh();
			C.pl2("Configuration refreshed.");
			
			return true;
		}
		
		List<SiteSearchEngine> sites = getSiteSearchEngines();
		for(SiteSearchEngine engine:sites) {
			boolean isMatched = conductSiteSearch(engine);
			if(isMatched) {
				return true;
			}
		}
		
		List<TextSearchEngine> locals = getTextSearchEngines();
		for(TextSearchEngine engine:locals) {
			boolean isMatched = conductTextSearch(engine);
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
		
		solo = parseSoloParam(KEY_HOST + "\\s+(.*?)");
		if(solo != null) {
			String result = NetworkUtil.getHostByName(solo);
			export(result);
			
			return true;
		}
		
		if(isIn(KEY_ASSIGN_CHARSET)) {
			String charset = command.replace("-", "").toUpperCase();
			g().setCharsetInUse(charset);
			C.pl2("Charset in use: " + command);
			
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
	
	private List<TextSearchEngine> getTextSearchEngines() {
		List<TextSearchEngine> engines = new ArrayList<TextSearchEngine>();
		List<String> items = g().getUserValuesByKeyword("text.search.");
		for(String engineInfo : items) {
			TextSearchEngine se = new TextSearchEngine();
			boolean flag = se.parse(engineInfo);
			if(flag) {
				engines.add(se);
			}
		}
		
		return engines;
	}
	
	private boolean conductSiteSearch(SiteSearchEngine engine) {
		String regex = engine.getPrefix() + " (.+?)";
		String singleParam = parseSoloParam(regex);
		if(singleParam != null) {
			String urlTemplate = engine.getUrlTemplate();
			String url = StrUtil.occupy(urlTemplate, Extractor.encodeURLParam(singleParam));
			if(FileOpener.playThing(url, "page.viewer", true)) {
				C.pl(url);
				String motto = engine.getMotto();
				if(!EmptyUtil.isNullOrEmpty(motto)) {
					C.pl(engine.getMotto());
				}
				C.pl();
				
				return true;
			}
		}
		
		return false;
	}
	
	private boolean conductTextSearch(TextSearchEngine engine) {
		String regex = engine.getPrefix() + "\\s(.+?)";
		String contentCriteria = parseSoloParam(regex);
		if(contentCriteria != null) {
			String folders = engine.getFolders();
			String fileCriteria = engine.getFileCriteria();
			String engineOptions = engine.getOptions();
			List<MexTextSearchRecord> list = TextSearcher.search(folders, fileCriteria, contentCriteria, g().getCharsetInUse());
			String finalOptions = OptionUtil.mergeOptions(options, engineOptions);
			exportWithOptions(list, finalOptions);
			
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
		
		String key = "capture.sound";
		String sound = g().getUserValueOf(key);
		if(EmptyUtil.isNullOrEmpty(sound)) {
			C.pl("[silent] can't find sound with key " + key);
			return null;
		}
		
		File file = parseFile(sound);
		if(file == null) {
			C.pl("[silent] can't find sound at " + sound);
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
		sb.append(App.USERNAME);
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
	
	public String lastModified(String folderPath, final String criteria) {
		TreeMap<String, String> mapDateStr = new TreeMap<String, String>();
		TreeMap<Long, MexObject> mapLastModified = new TreeMap<Long, MexObject>();
		File folder = FileUtil.getIfNormalFolder(folderPath);
		if(folder == null) {
			String msg = "Invalid path [" + folderPath + "].";
			C.pl2(msg);
			return null;
		}
		
		List<MexFile> list = new ArrayList<MexFile>();
		folder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				if(!file.isFile()) {
					return false;
				}
				
				list.add(new MexFile(file));
				
				mapLastModified.put(file.lastModified(), new MexObject(file.getAbsolutePath()));
								
				return true;
			}
		});

		String filePath = null;
		if(criteria == null) {
			String regex = "^\\d{8}_\\d{6}";
			Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			
			for(MexFile file : list) {
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
				Map.Entry<Long,  MexObject> entry2 = mapLastModified.lastEntry();
				if(entry2 != null) {
					filePath = entry2.getValue().getString();
					C.pl(filePath);
				} else {
					exportEmptyMsg();
				}
			}
		} else {
			List<MexObject> filePaths = CollectionUtil.filter(new ArrayList<MexObject>(mapLastModified.values()), criteria);
			
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
}