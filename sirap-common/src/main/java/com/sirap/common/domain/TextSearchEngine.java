package com.sirap.common.domain;

import com.sirap.basic.domain.MexItem;

@SuppressWarnings("serial")
public class TextSearchEngine extends MexItem {
	private String prefix;
	private String folders;
	private String fileNameCriteria;
	private String options;
	
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
	
	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
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
			setOptions(info[3].trim());
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(prefix).append("\t");
		sb.append(folders).append("\t");
		sb.append(fileNameCriteria).append("\t");
		sb.append(options);
		
		return sb.toString(); 
	}
}
