package com.sirap.common.framework.command.target;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.third.http.HttpHelper;

public class TargetWeb extends Target {

	private String url;
	private String subject;
	
	public TargetWeb(String subject, String url) {
		this.subject = subject;
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public void export(List records, String options, boolean withTimestamp) {
		List newList = Lists.newArrayList();
		if(isFileRelated()) {
			for(Object obj : records) {
				File file = FileUtil.of(obj);
				if(file != null) {
					newList.add(file);
            	} else {
            		newList.add(obj);
            	}
			}
		} else {
			newList = toStringList(records, options);
		}
		
		String temp = HttpHelper.sendStringsAndFiles(newList, url, subject);
		String msg = StrUtil.occupy("Sent to {0} :\n {1}", url, temp);
		C.pl2(msg);
	}
	
	@Override
	public String toString() {
		return D.jst(this);
	}
}
