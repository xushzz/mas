package com.sirap.common.framework;

import java.io.File;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.MexMap;
import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;

public abstract class Konfig {

	public static String KONFIG_FILE = "Konfig.properties";
	public static String KEYS_FILE = "Keys.txt";
	
	public static final String KEY_STORAGE = "storage";
	public static final String KEY_USERCONFIG = "userConfig";
	public static final String KEY_PASSCODE = "passcode";
	
	protected String originalStorage;
	protected String userConfigFile;
	protected MexMap innerProperties = new MexMap();
	protected MexMap userProperties = new MexMap();

	protected void loadInnerConfigDetail() {
		List<String> list = IOUtil.readLinesFromStreamByClassLoader(Konfig.KONFIG_FILE, Konstants.CODE_UTF8);
		innerProperties = new MexMap(list);
		innerProperties.setType("Zoo Inner Config");
	}
	
	protected void loadUserConfigDetail() {
		
		if(userConfigFile == null) {
			C.pl("[Configuration] User config file unavailable, please check program arguments.");
		} else {
			File file = FileUtil.getIfNormalFile(userConfigFile);
			if(file != null) {
				userProperties = new MexMap(IOUtil.readLines(userConfigFile, Konstants.CODE_UTF8));
				userProperties.setType("Zoo User Config");
			} else {
				C.pl("[Configuration] User config file unavailable, please check [" + userConfigFile + "].");
			}
		}
	}
	
	protected abstract void setValues();

	public MexMap getInnerProps() {
		return innerProperties;
	}

	public MexMap getUserProps() {
		return userProperties;
	}

	public int getNumberValueOf(String key) {
		return innerProperties.getNumber(key, -7);
	}

	public int getNumberValueOf(String key, int defaulIfNull) {
		return innerProperties.getNumber(key, defaulIfNull);
	}

	public String getValueOf(String key) {
		return innerProperties.get(key, null);
	}

	public String getValueOf(String key, String defaulIfNull) {
		return innerProperties.get(key, defaulIfNull);
	}
	
	public List<String> getValuesByKeyword(String keyword) {
		return innerProperties.getValuesByKeyword(keyword);
	}

	public int getUserNumberValueOf(String key) {
		return userProperties.getNumber(key, -17);
	}

	public int getUserNumberValueOf(String key, int defaulIfNull) {
		return userProperties.getNumber(key, defaulIfNull);
	}

	public String getUserValueOf(String key) {
		return userProperties.get(key, null);
	}

	public TypedKeyValueItem getUserConfigEntry(String key) {
		return userProperties.getEntryIgnorecase(key);
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
		return flag ? "on" : "off";
	}
}
