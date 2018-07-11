package com.sirap.common.framework;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.MexMap;
import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

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
		innerProperties = new MexMap(readAsResource(KONFIG_FILE));
		innerProperties.setType("Zoo Inner Config");
	}
	
	private List<String> readAsResource(String path) {
		InputStream ins = IOUtil.streamByClassLoader(path);
		return IOUtil.readLinesFromStream(ins, Konstants.CODE_UTF8);
	}
	
	protected void loadUserConfigDetail() {
		
		if(userConfigFile == null) {
			C.pl("[Configuration] User config file unavailable, please check program arguments.");
		} else {
			File file = FileUtil.getIfNormalFile(userConfigFile);
			if(file != null) {
				MexMap mm = new MexMap(IOUtil.readFileIntoList(userConfigFile));
				List<TypedKeyValueItem> items = mm.detectCircularItems();
				if(!EmptyUtil.isNullOrEmpty(items)) {
					String msg = "\n[Configuration] Circular items found in [{0}]:\n" + StrUtil.connectWithLineSeparator(items);
					XXXUtil.alert(msg, file.getAbsolutePath());
				} else {
					userProperties.setType("Zoo User Config");
					userProperties.clear();
					userProperties.putAll(mm);
				}
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
