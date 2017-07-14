package com.sirap.common.domain;

import java.util.List;

import com.sirap.basic.component.MexedOption;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.OptionUtil;

@SuppressWarnings("serial")
public class LocalSearchEngine extends MexItem {
	private String prefix;
	private String folders;
	private String suffixes;
	private boolean printSource;
	private boolean useCache;
	private boolean useSpace = true;

	private static final String KEY_PRINTSOURCE = "printsource";
	private static final String KEY_USECACHE = "usecache";
	private static final String KEY_USESPACE = "usespace";
	
	public LocalSearchEngine() {
		
	}
	
	public LocalSearchEngine(String prefix, String folders, String suffixes) {
		this.prefix = prefix;
		this.folders = folders;
		this.suffixes = suffixes;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getFolders() {
		return folders;
	}

	public void setFolders(String folders) {
		this.folders = folders;
	}

	public String getSuffixes() {
		return suffixes;
	}

	public void setSuffixes(String suffixes) {
		this.suffixes = suffixes;
	}
	
	public boolean isPrintSource() {
		return printSource;
	}

	public void setPrintSource(boolean printSource) {
		this.printSource = printSource;
	}
	
	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}	
	
	public boolean isUseSpace() {
		return useSpace;
	}

	public void setUseSpace(boolean useSpace) {
		this.useSpace = useSpace;
	}

	@Override
	public boolean parse(String source) {
		String[] info = source.split("#");
		if(info.length < 3) {
			return false;
		}

		setPrefix(info[0].trim());
		setFolders(info[1].trim());
		setSuffixes(info[2].trim());
		
		String optionsStr = null;
		if(info.length >= 4) {
			optionsStr = info[3].trim();
			List<MexedOption> options = OptionUtil.parseOptions(optionsStr);
			Object value = OptionUtil.readOption(options, KEY_PRINTSOURCE);
			if(value instanceof Boolean) {
				setPrintSource((Boolean)value);
			}
			value = OptionUtil.readOption(options, KEY_USECACHE);
			if(value instanceof Boolean) {
				setUseCache((Boolean)value);
			}
			value = OptionUtil.readOption(options, KEY_USESPACE);
			if(value instanceof Boolean) {
				setUseSpace((Boolean)value);
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(prefix).append("\t");
		sb.append(folders).append("\t");
		sb.append(suffixes).append("\t");
		sb.append(printSource);
		
		return sb.toString(); 
	}
}
