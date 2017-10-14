package com.sirap.common.framework;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.email.EmailCenter;
import com.sirap.basic.email.EmailServerItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.TrumpUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.domain.CommandRecord;
import com.sirap.common.domain.TZRecord;
import com.sirap.common.manager.TimeZoneManager;

public class SimpleKonfig extends Konfig {

	private static SimpleKonfig instance;
	
	private String storage;
	private List<CommandRecord> commandNodes;
	private Locale locale;
	private String areaZoneUser;
	private int timeZoneUser = Short.MIN_VALUE;
	private boolean isEmailEnabled;
	private boolean isExportWithTimestampEnabled;
	private boolean isAsianFontWhenPDF;
	private boolean isPrintTopInfoWhenPDF;
	private boolean isPrintGreyRowWhenPDF;
	private boolean isRemoteEnabled;
	private boolean isHistoryEnabled;
	private boolean isCaptureSoundOn;
	private boolean isGeneratedFileAutoOpen;
	private String securityPasscode = "mike";
	private String charsetInUse;
	
	public void refresh() {
		loadAndSetValues();
	}
	
	public static void init(String params) {
		instance = new SimpleKonfig();
		
		instance.originalStorage = OptionUtil.readString(params, KEY_STORAGE);
		String tempPasscode = OptionUtil.readString(params, KEY_PASSCODE);
		if(!EmptyUtil.isNullOrEmpty(tempPasscode)) {
			String temp2 = TrumpUtil.decodeMixedTextBySIRAP(tempPasscode, "true", true);
			instance.securityPasscode = temp2;
		}
		instance.userConfigFile = OptionUtil.readString(params, KEY_USERCONFIG);
		
		instance.loadAndSetValues();
	}
	
	private void loadAndSetValues() {
		loadSystemConfigDetail();
		loadUserConfigDetail();
		
		setValues();
		decodeUserConfig();
		initEmail();
	}
	
	private void decodeUserConfig() {
		getUserProps().recoverValues(securityPasscode);
		List<String> items = getUserProps().detectCircularItems();
		if(!EmptyUtil.isNullOrEmpty(items)) {
			C.pl("[Configuration] Circular items found in [" + instance.userConfigFile + "] as following:");
			C.list(items);
			C.pl("User configuration is set to be empty.");
			getUserProps().getContainer().clear();
		}
	}
	
	private void initEmail() {
		String username = getUserValueOf("email.sender");
		String password = getUserValueOf("email.sender.pwd");
		String receiver = getUserValueOf("email.receiver");
		
		Map<String, EmailServerItem> servers = getExtraServers();
		
		EmailCenter.g().config(username, password, receiver, servers);
	}

	public static SimpleKonfig g() {
		XXXUtil.nullCheck(instance, "SimpleKonfig instance");
		
		return instance;
	}
	
	public boolean isEmailEnabled() {
		return isEmailEnabled;
	}

	public void setEmailEnabled(boolean flag) {
		isEmailEnabled = flag;
	}
	
	public boolean isExportWithTimestampEnabled(String options) {
		return OptionUtil.readBooleanPRI(options, "ts", isExportWithTimestampEnabled);
	}

	public boolean isExportWithTimestampEnabled() {
		return isExportWithTimestampEnabled;
	}

	public void setExportWithTimestampEnabled(boolean flag) {
		isExportWithTimestampEnabled = flag;
	}
	
	public boolean isPrintTopInfoWhenPDF() {
		return isPrintTopInfoWhenPDF;
	}

	public void setPrintTopInfoWhenPDF(boolean isPrintTopInfoWhenPDF) {
		this.isPrintTopInfoWhenPDF = isPrintTopInfoWhenPDF;
	}
	
	public boolean isAsianFontWhenPDF() {
		return isAsianFontWhenPDF;
	}

	public void setAsianFontWhenPDF(boolean isAsianFontWhenPDF) {
		this.isAsianFontWhenPDF = isAsianFontWhenPDF;
	}

	public boolean isPrintGreyRowWhenPDF() {
		return isPrintGreyRowWhenPDF;
	}

	public void setPrintGreyRowWhenPDF(boolean isPrintGreyRowWhenPDF) {
		this.isPrintGreyRowWhenPDF = isPrintGreyRowWhenPDF;
	}

	@Override
	protected void setValues() {
		if(EmptyUtil.isNullOrEmpty(originalStorage)) {
			XXXUtil.alert("[Configuration] Storage should be specified.");
		}
		
		storage = originalStorage.replace("/", File.separator);
		FileUtil.makeDirectoriesIfNonExist(storage);

		commandNodes = parseCommandNodes();
		
		locale = DateUtil.parseLocale(getUserValueOf("locale.in_use"));
		if(locale == null) {
			locale = Locale.US;
		}
		charsetInUse = isYes("use.gbk") ? Konstants.CODE_GBK : Konstants.CODE_UTF8;
		isCaptureSoundOn = isYes("capture.sound.on");
		isGeneratedFileAutoOpen = isYes("generatedfile.autoopen.on");
		isEmailEnabled = isYes("email.enabled");
		isRemoteEnabled = isYes("remote.enabled");
		isHistoryEnabled = isYes("history.enabled");
		isExportWithTimestampEnabled = !isNo("timestamp.enabled");
		isPrintTopInfoWhenPDF = !isNo("pdf.topinfo.print");
		isPrintGreyRowWhenPDF = !isNo("pdf.table.grayrow");
		isAsianFontWhenPDF = isYes("pdf.font.asian");
		isExportWithTimestampEnabled = !isNo("timestamp.enabled");
	}
	
	public int getTimeoutMinutes() {
		int timeoutMinutes = getUserNumberValueOf("timeout.minutes");

		return timeoutMinutes;
	}

	public String getStorageWithSeprator() {
		return storage + File.separator;
	}

	public String getUserConfigFileName() {
		return userConfigFile;
	}

	public String getPasswordEncrypted(String type) {
		String passwordEncrypted = getUserValueOf("password.encrypted." + type);

		return passwordEncrypted;
	}

	public Locale getLocale() {
		return locale;
	}
	
	public String getSystemInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append(getDisplaySignInt("GMT", getTimeZoneUser()));
		if(areaZoneUser != null) {
			sb.append("@").append(areaZoneUser);
		}
		sb.append(" " + charsetInUse);
		//sb.append(" " + getDisplaySignInt("timeout", timeoutMinutes));
		sb.append(" email" + getDisplayEnableSign(isEmailEnabled));
		sb.append(" remote" + getDisplayEnableSign(isRemoteEnabled));
		sb.append(" sound" + getDisplayEnableSign(isCaptureSoundOn));
		sb.append(" autoOpen" + getDisplayEnableSign(isGeneratedFileAutoOpen));
		sb.append(" timestamp" + getDisplayEnableSign(isExportWithTimestampEnabled));
		//sb.append(" " + getDisplaySignInt("server", DateUtil.TIMEZONE_JVM));
		
		return sb.toString();
	}
	
	public int getTimeZoneUser() {
		if(timeZoneUser == Short.MIN_VALUE) {
			setUserZoneInfo(getUserValueOf("timezone.user"));
		}
		
		return timeZoneUser;
	}
	
	public void setUserZoneInfo(String source) {
		String temp = source;
		if(EmptyUtil.isNullOrEmpty(temp)) {
			timeZoneUser = DateUtil.TIMEZONE_JVM;
			return;
		}
		
		Integer timezone = MathUtil.toInteger(temp);
		if(timezone != null && timezone <=12 && timezone>=-12) {
			timeZoneUser = timezone;
			return;
		}
		
		List<TZRecord> list = TimeZoneManager.g().getTZRecordsById(temp);
		if(list != null && list.size() == 1) {
			TZRecord item = list.get(0);
			timeZoneUser = item.getDiff();
			areaZoneUser = item.getId();
			return;
		}

		timeZoneUser = DateUtil.TIMEZONE_JVM;
		
		if(list.size() > 1) {
			C.pl("Uncanny, multiple timezones matched: ");
			C.listSome(list, 4);
		}
	}

	public void setTimeZoneUser(int timeZone) {
		areaZoneUser = null;
		this.timeZoneUser = timeZone;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	private List<CommandRecord> parseCommandNodes() {
		HashMap<String, String> greatMap = getProps().getKeyValuesByPartialKeyword("command.node.");
		HashMap<String, String> userMap = getUserProps().getKeyValuesByPartialKeyword("command.node.");
		
		if(!EmptyUtil.isNullOrEmpty(userMap)) {
			greatMap.putAll(userMap);
		}
		
		Iterator<String> it = greatMap.keySet().iterator();
		List<CommandRecord> items = new ArrayList<>();
		while(it.hasNext()) {
			String key = it.next();
			String value = greatMap.get(key);
			if(StrUtil.startsWith(value, "#")) {
				continue;
			}
			
			CommandRecord item = new CommandRecord();
			if(item.parse(value)) {
				item.setKey(key);
				items.add(item);
			} else {
				continue;
			}
		}

		Collections.sort(items);
		
		return items;
	}

	public List<String> getCommandClassNames() {
		List<String> items = new ArrayList<>();
		for(CommandRecord item : commandNodes) {
			items.add(item.getClassName());
		}
		
		return items;
	}
	
	public List<CommandRecord> getCommandNodes() {
		return commandNodes;
	}

	public boolean isRemoteEnabled() {
		return isRemoteEnabled;
	}

	public void setRemoteEnabled(boolean flag) {
		this.isRemoteEnabled = flag;
	}

	public boolean isCaptureSoundOn() {
		return isCaptureSoundOn;
	}

	public void setCaptureSoundOn(boolean flag) {
		this.isCaptureSoundOn = flag;
	}
	
	public boolean isGeneratedFileAutoOpen() {
		return isGeneratedFileAutoOpen;
	}

	public void setGeneratedFileAutoOpen(boolean flag) {
		this.isGeneratedFileAutoOpen = flag;
	}
	
	public boolean isYes(String key) {
		boolean flag = Konstants.FLAG_YES.equalsIgnoreCase(getUserValueOf(key));
		return flag;
	}
	
	public boolean isNo(String key) {
		boolean flag = Konstants.FLAG_NO.equalsIgnoreCase(getUserValueOf(key));
		return flag;
	}

	public void setViewerOn(boolean isViewerOn) {
		this.isGeneratedFileAutoOpen = isViewerOn;
	}
	
	public String pathWithSeparator(String key, String defFolderName) {
		return pathWithSeparator(key, defFolderName, true);
	}
	
	public String pathWithSeparator(String key, String defFolderName, boolean toCreate) {
		String path = null;
		String temp = getUserValueOf(key);
		if(EmptyUtil.isNullOrEmpty(temp)) {
			path = getStorageWithSeprator() + defFolderName;
		} else if(FileUtil.startWithDiskName(temp)) {
			path = temp;
		} else {
			path = getStorageWithSeprator() + temp;
		}
		path = path.replace("/", File.separator);
		if(toCreate) {
			FileUtil.makeDirectoriesIfNonExist(path);
		}

		return path + File.separator;
	}

	public boolean isHistoryEnabled() {
		return isHistoryEnabled;
	}
	
	public String getSecurityPasscode() {
		return securityPasscode;
	}
	
	public void setSecurityPasscode(String securityPasscode) {
		this.securityPasscode = securityPasscode;
	}

	private Map<String, EmailServerItem> getExtraServers() {
		Map<String, EmailServerItem> servers = new HashMap<>();
		List<String> items = SimpleKonfig.g().getUserValuesByKeyword("email.server.");
		for(String item : items) {
			String[] arr= item.split(",");
			if(arr.length != 3) {
				continue;
			}
			
			String domain = arr[0].trim();
			String smtp = arr[1].trim();
			String port = arr[2].trim();
			
			servers.put(domain, new EmailServerItem(domain, smtp, port));
		}
		
		return servers;
	}

	public String getCharsetInUse() {
		return charsetInUse;
	}

	public void setCharsetInUse(String charsetInUse) {
		this.charsetInUse = charsetInUse;
	}
	
}
