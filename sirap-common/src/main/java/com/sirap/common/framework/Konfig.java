package com.sirap.common.framework;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.sirap.basic.component.MexedMap;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public abstract class Konfig {

	protected static String SYSTEM_CONFIG_FILE_TEMPLATE = "/Config_{0}.properties";
	public static String EXTRA_FILE = "/Extra.properties";
	public static String KEYS_FILE = "/Keys.txt";

	protected String originalStorage;
	protected String originalSystemConfigName;

	protected String systemConfigFile;
	protected String userConfigFile;
	protected MexedMap systemProperties = new MexedMap();
	protected MexedMap userProperties = new MexedMap();

	protected void initKonfig(String systemConfig, String userConfig) {
		loadSystemConfigDetail(systemConfig);
		loadUserConfigDetail(userConfig);
		setValues();
	}
	
	protected void initKonfig(String systemConfig) {
		loadSystemConfigDetail(systemConfig);
		setValues();
	}
	
	private void loadSystemConfigDetail(String systemConfig) {
		systemConfigFile = systemConfig;
		systemProperties.getContainer().clear();
		
		InputStream is = getClass().getResourceAsStream(systemConfigFile);
		if(is == null) {
			XXXUtil.alert("[Configuration] System config file unavailable, please check [" + systemConfigFile + "].");
		}
		
		MexedMap temp = IOUtil.readKeyValuesIntoMexedMap(is);
		if(temp != null) {
			systemProperties = temp;
		}
		
		is = getClass().getResourceAsStream(EXTRA_FILE);
		if(is == null) {
			C.pl("[Configuration] Extra config file unavailable, please check [" + EXTRA_FILE + "].");
		} else {
			temp = IOUtil.readKeyValuesIntoMexedMap(is);
			if(temp != null) {
				systemProperties.getContainer().putAll(temp.getContainer());
			}
		}
	}
	
	private void loadUserConfigDetail(String userConfig) {
		userConfigFile = userConfig;
		userProperties.getContainer().clear();
		
		if(userConfigFile == null) {
			C.pl("[Configuration] User config file unavailable, please check program arguments.");
		} else {
			File file = FileUtil.getIfNormalFile(userConfigFile);
			if(file != null) {
				MexedMap temp = IOUtil.createMexedMapByRegularFile(file.getAbsolutePath());
				if(temp != null) {
					List<String> items = temp.detectCircularItems();
					if(!EmptyUtil.isNullOrEmpty(items)) {
						C.pl("[Configuration] Circular items found in [" + userConfigFile + "] as following:");
						C.list(items);
					} else {
						userProperties = temp;
					}
				}
			} else {
				C.pl("[Configuration] User config file unavailable, please check [" + userConfigFile + "].");
			}
		}
	}
	
	protected abstract void setValues();

	public MexedMap getProps() {
		return systemProperties;
	}

	public MexedMap getUserProps() {
		return userProperties;
	}

	public void setProps(MexedMap properties) {
		this.systemProperties = properties;
	}

	public int getNumberValueOf(String key) {
		return systemProperties.getNumber(key, 0);
	}

	public int getNumberValueOf(String key, int defaulIfNull) {
		return systemProperties.getNumber(key, defaulIfNull);
	}

	public String getValueOf(String key) {
		return systemProperties.get(key, null);
	}

	public String getValueOf(String key, String defaulIfNull) {
		return systemProperties.get(key, defaulIfNull);
	}
	
	public List<String> getValuesByKeyword(String keyword) {
		return systemProperties.getValuesByKeyword(keyword);
	}

	public int getUserNumberValueOf(String key) {
		return userProperties.getNumber(key, 0);
	}

	public int getUserNumberValueOf(String key, int defaulIfNull) {
		return userProperties.getNumber(key, defaulIfNull);
	}

	public String getUserValueOf(String key) {
		return userProperties.get(key, null);
	}

	public String getUserValueOf(String key, String defaulIfNull) {
		return userProperties.get(key, defaulIfNull);
	}
	
	public List<String> getUserValuesByKeyword(String keyword) {
		return userProperties.getValuesByKeyword(keyword);
	}
	
	public String getDisplaySignInt(String type, int tz) {
		String str = type + StrUtil.signValue(tz);
		return str;
	}
	
	public String getDisplayEnableSign(boolean flag) {
		return flag ? "+":"-";
	}
}
