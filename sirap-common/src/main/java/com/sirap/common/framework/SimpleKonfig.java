package com.sirap.common.framework;

import java.io.File;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.thirdparty.TrumpHelper;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.LocaleUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.domain.CommandRecord;
import com.sirap.third.email.base.EmailCenter;
import com.sirap.third.email.base.EmailServerItem;

public class SimpleKonfig extends Konfig {

	private static SimpleKonfig instance;
	
	private String storage;
	private List<CommandRecord> commandNodes;
	private List<CommandBase> commandInstances;
	private Locale locale;
	private boolean isEmailEnabled;
	private boolean isExportWithTimestampEnabled;
	private boolean isAsianFontWhenPDF;
	private boolean isPrintTopInfoWhenPDF;
	private boolean isPrintGreyRowWhenPDF;
	private boolean isRemoteEnabled;
	private boolean isHistoryEnabled;
	private boolean isCaptureSoundOn;
	private boolean isGeneratedFileAutoOpen;
	private boolean isSuckOptionsEnabled = true;
	private String securityPasscode = "mike";
	private String charsetInUse;
	private boolean isFromWeb;
	
	public void refresh() {
		loadAndSetValues();
	}
	
	public static void init(String params) {
		instance = new SimpleKonfig();
		
		instance.originalStorage = OptionUtil.readString(params, KEY_STORAGE, false);
		String tempPasscode = OptionUtil.readString(params, KEY_PASSCODE, false);
		if(!EmptyUtil.isNullOrEmpty(tempPasscode)) {
			String temp2 = TrumpHelper.decodeMixedTextBySIRAP(tempPasscode, "true", true);
			instance.securityPasscode = temp2;
		}
		instance.userConfigFile = OptionUtil.readString(params, KEY_USERCONFIG, false);
		
		instance.loadAndSetValues();
	}
	
	private void loadAndSetValues() {
		loadInnerConfigDetail();
		loadUserConfigDetail();
		
		setValues();
		decodeUserConfig();
		initEmail();
	}
	
	private void decodeUserConfig() {
		getUserProps().recoverValues(securityPasscode);
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
	
	public boolean isFromWeb() {
		return isFromWeb;
	}
	
	public void setFromWeb(boolean flag) {
		isFromWeb = flag;
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
		if(EmptyUtil.isNullOrEmpty(commandNodes)) {
			XXXUtil.alert("Can't not find any available commands in user config.");
		}
		commandInstances = createCommandInstances();
		
		String temp = getUserValueOf("locale.in_use");
		if(!EmptyUtil.isNullOrEmpty(temp)) {
			locale = LocaleUtil.of(temp);
			if(locale == null) {
				locale = Locale.getDefault();
			}
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
		isFromWeb = isYes("opertion.fromweb");
	}
	
	public int getTimeoutMinutes() {
		int timeoutMinutes = getUserNumberValueOf("timeout.minutes");

		return timeoutMinutes;
	}

	public String getStorageWithSeparator() {
		return storage + "/";
	}

	public String getStorage() {
		return storage;
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
	
	public List<ValuesItem> getUserStatus() {
		List<ValuesItem> items = Lists.newArrayList();
		String serverTZ = ZoneId.systemDefault().toString();
		String serverDiff = DateUtil.tzoneOffsetInHour(serverTZ);
		items.add(ValuesItem.of("Local timezone", StrUtil.occupy("{0} (GMT{1})", serverTZ, serverDiff)));
		items.add(ValuesItem.of("Local charset", Charset.defaultCharset()));
		items.add(ValuesItem.of("Local locale", Locale.getDefault()));
		items.add(ValuesItem.of("User", AkaBase.USERNAME));
		
		String userTZ = getUserValueOf("user.timezone");
		String userDiff = null;
		if(EmptyUtil.isNullOrEmpty(userTZ)) {
			items.add(ValuesItem.of("User timezone", Konstants.FAKED_EMPTY));
		} else {
			userDiff = DateUtil.tzoneOffsetInHour(userTZ);
			items.add(ValuesItem.of("User timezone", StrUtil.occupy("{0} (GMT{1})", userTZ, userDiff)));
		}
		
		items.add(ValuesItem.of("User charset", charsetInUse));
		items.add(ValuesItem.of("User locale", locale));
		items.add(ValuesItem.of("Email", getDisplayEnableSign(isEmailEnabled)));
		items.add(ValuesItem.of("Remote control", getDisplayEnableSign(isRemoteEnabled)));
		items.add(ValuesItem.of("Capture sound", getDisplayEnableSign(isCaptureSoundOn)));
		items.add(ValuesItem.of("Auto open", getDisplayEnableSign(isGeneratedFileAutoOpen)));
		items.add(ValuesItem.of("Use timestamp", getDisplayEnableSign(isExportWithTimestampEnabled)));
		items.add(ValuesItem.of("Suck option", getDisplayEnableSign(isSuckOptionsEnabled)));
		
		String expiry = Konstants.FAKED_EMPTY;
		Date date = Janitor.g().getExpirationDate();
		if(date != null) {
			expiry = DateUtil.strOf(date, DateUtil.GMT, getLocale());
		}
		items.add(ValuesItem.of("Expiration", expiry));
		
		return items;
	}

	public void setLocale(Locale locale) {
		XXXUtil.shouldBeNotnull(locale);
		this.locale = locale;
	}
	
	private List<CommandBase> createCommandInstances() {

		List<CommandBase> commandList = Lists.newArrayList();
		for(CommandRecord item : commandNodes) {
			try {
				Object instance = Class.forName(item.getClassName()).newInstance();
				if(CommandBase.class.isInstance(instance)) {
					commandList.add((CommandBase)instance);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				XXXUtil.alert("Can't create instance by : " + item);
			}
		}
		
		return commandList;
	}

	private List<CommandRecord> parseCommandNodes() {
		HashMap<String, String> greatMap = getInnerProps().getKeyValuesByPartialKeyword("command.node.");
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
	
	public List<CommandBase> getCommandInstances() {
		return commandInstances;
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
	
	public boolean isSuckOptionsEnabled() {
		return isSuckOptionsEnabled;
	}

	public void setSuckOptionsEnabled(boolean flag) {
		this.isSuckOptionsEnabled = flag;
	}

	public boolean isYes(String key) {
		boolean flag = StrUtil.equals(Konstants.FLAG_YES, getUserValueOf(key));
		return flag;
	}
	
	public boolean isNo(String key) {
		boolean flag = StrUtil.equals(Konstants.FLAG_NO, getUserValueOf(key));
		return flag;
	}

	public void setViewerOn(boolean isViewerOn) {
		this.isGeneratedFileAutoOpen = isViewerOn;
	}
	
	public String pathOf(String key, String defFolderName) {
		return pathOf(key, defFolderName, true);
	}
	
	public String pathOf(String key, String defFolderName, boolean toCreate) {
		String path = null;
		String temp = getUserValueOf(key);
		if(EmptyUtil.isNullOrEmpty(temp)) {
			path = getStorageWithSeparator() + defFolderName;
		} else if(FileUtil.startWithDiskName(temp)) {
			path = temp;
		} else {
			path = getStorageWithSeparator() + temp;
		}
		if(toCreate) {
			FileUtil.makeDirectoriesIfNonExist(path);
		}

		return FileUtil.unixSeparator(path) + "/";
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
