package com.sirap.common.framework.command.target;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.ObjectUtil;

public class TargetUnix extends Target {
	
	public static final String KEY_CLASS = "com.sirap.executor.ssh.SshCommandExecutor";

	private String path;
	
	public TargetUnix(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/***
	 * com.sirap.executor.ssh.SshCommandExecutor
	 * public String uploadFile(String localfilepath, String unixfolder, String unixfilename) throws MexException {
	 * @param records
	 * @param options
	 * @param withTimestamp
	 */
	@Override
	public void export(List records, String options, boolean withTimestamp) {
		Class<?>[] clazzArr = new Class<?>[3];
		Arrays.fill(clazzArr, String.class);
		try {
			Class<?> classSshExecutor = Class.forName(KEY_CLASS);
			Object instance = ObjectUtil.execute(classSshExecutor, "g");
			for(Object item : records) {
				File file = FileUtil.of(item);
				if(file != null) {
					String unixfilename = file.getName();
					String localfilepath = file.getAbsolutePath();
					String unixfolder = path;
					Object result = ObjectUtil.execute(instance, "uploadFile", clazzArr, localfilepath, unixfolder, unixfilename);
					C.pl2("Uploaded " + result);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	@Override
	public String toString() {
		return D.jst(this);
	}
}
