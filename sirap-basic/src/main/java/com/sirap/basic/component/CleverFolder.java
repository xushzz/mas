package com.sirap.basic.component;

import java.io.File;
import java.io.IOException;

import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class CleverFolder extends File {
	
	public CleverFolder(String folderPath) {
		super(folderPath);
	}
	
	public String getCleverFolderPath() {
		if(!exists()) {
			return null;
		}
		
		String path = getPath();
		if(!FileUtil.startWithDiskName(path)) {
			return null;
		}

		String temp = null;
		String[] params = StrUtil.parseParams("([A-Z]:)(|\\.+)", path);
		if(params != null) {
			temp = params[0] + "\\";
			temp = temp.toUpperCase();
			return temp;
		} 
		
		try {
			temp = getCanonicalPath();
		} catch (IOException e) {
			//
		}
		
		return temp;
	}
}
