package com.sirap.common.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.OptionUtil;

@SuppressWarnings("serial")
public class TextSearchEngine extends MexItem {
	private String prefix;
	private String folders;
	private String fileNameCriteria;
	private boolean useCache;
	private boolean useSpace = true;

	private static final String KEY_USECACHE = "usecache";
	private static final String KEY_USESPACE = "usespace";
	
	public TextSearchEngine() {
		
	}
	
	public TextSearchEngine(String prefix, String folders, String fileCriteria) {
		this.prefix = prefix;
		this.folders = folders;
		this.fileNameCriteria = fileCriteria;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getFileCriteria() {
		return fileNameCriteria;
	}

	public void setFileCriteria(String fileCriteria) {
		this.fileNameCriteria = fileCriteria;
	}

	public String getFolders() {
		return folders;
	}

	public void setFolders(String folders) {
		this.folders = folders;
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
		if(info.length < 2) {
			return false;
		}

		setPrefix(info[0].trim());
		setFolders(info[1].trim());
		
		if(info.length >= 3) {
			setFileCriteria(info[2].trim());
		}
		
		if(info.length >= 4) {
			String options = info[3].trim();
			Boolean value = OptionUtil.readBoolean(options, KEY_USECACHE);
			if(value != null) {
				setUseCache(value);
			}
			value = OptionUtil.readBoolean(options, KEY_USESPACE);
			if(value != null) {
				setUseSpace(value);
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(prefix).append("\t");
		sb.append(folders).append("\t");
		sb.append(fileNameCriteria).append("\t");
		sb.append("useCache:" + useCache).append(",").append("useSpace:" + useSpace);
		
		return sb.toString(); 
	}
}
