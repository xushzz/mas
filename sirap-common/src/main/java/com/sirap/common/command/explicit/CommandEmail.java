package com.sirap.common.command.explicit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.email.EmailCenter;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.target.TargetAnalyzer;
import com.sirap.common.framework.command.target.TargetConsole;

public class CommandEmail extends CommandBase {
	private static final String KEY_SEND_EMAIL = "x";
	private static final String KEY_EMAIL_ENABLED_SWITCH = "mx";
	private static final String KEY_EMAIL_CONFIGURATION = "mc";
	private static final String KEY_EMAIL_SETUP = "ms";

	public boolean handle() {

		if(is(KEY_EMAIL_CONFIGURATION)) {
			String value = isEmailEnabled() ? "Enabled" : "Disabled";
			export(value + ", " + EmailCenter.g().getEmailInfo());
			return true;
		}

		if(is(KEY_EMAIL_SETUP)) {
			if(!isEmailEnabled()) {
				C.pl2("Email should be enabled to perform email setup.");
			} else {
				String passcode = SimpleKonfig.g().getSecurityPasscode();
				MexUtil.setupEmailCenter(EmailCenter.g(), passcode);
			}
			return true;
		}
		
		if(is(KEY_EMAIL_ENABLED_SWITCH)) {
			boolean flag = !isEmailEnabled();
			g().setEmailEnabled(flag);
			String value = flag ? "enabled" : "disabled";
			C.pl2("Email " + value + ", " + EmailCenter.g().getEmailInfo());
			
			return true;
		}
		
		//send email, string, content of text file, or given file as attachment
		solo = parseParam(KEY_SEND_EMAIL + "\\s(.*?)");
		if(solo != null) {
			if(target instanceof TargetConsole) {
				if(isEmailEnabled()) {
					target = TargetAnalyzer.createTargetEmail(EmailCenter.DEFAULT_RECEIVER, command);
				} else {
					C.pl2("Email currently disabled.");
					return true;
				}
			}
			
			final List<Object> objs = new ArrayList<Object>();
			List<String> items = StrUtil.split(solo, ';');
			for(String item:items) {
				if(EmptyUtil.isNullOrEmptyOrBlank(item)) {
					continue;
				}
				
				item = item.trim();
				
				String[] fileParams = StrUtil.parseParams("(#{1,2}|)(.+?)", item);
				if(fileParams == null) {
					continue;
				}
				
				String type = fileParams[0];
				if(type.isEmpty()) {
					objs.add(item);
					continue;
				}
				
				String value = fileParams[1];
				File file = parseFile(value);
				if(file != null) {
					String filePath = file.getAbsolutePath();
					if(FileOpener.isTextFile(filePath)) {
						String cat = IOUtil.charsetOfTextFile(filePath);
						if(OptionUtil.readBooleanPRI(options, "x", false)) {
							cat = switchChartset(cat);
						}
						List<String> txtContent = FileOpener.readTextContent(filePath, true, cat);
						if(type.length() == 1) {
							objs.add(file);
						} else if(type.length() == 2) {
							for(String line:txtContent) {
								File lineFile = parseFile(line.trim());
								if(lineFile != null) {
									objs.add(lineFile);
								} else {
									objs.add(line);
								}
							}
						}
					} else {
						objs.add(file);
					}
					continue;
				}

				File folder = parseFolder(value);
				if(folder != null) {
					folder.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							File subFile = FileUtil.getIfNormalFile(dir + File.separator + name);
							if(subFile != null) {
								objs.add(subFile);
							}
							return false;
						}
					});
					continue;
				}
				
				objs.add(item);
			}

			export(objs);
			
			return true;
		}
		
		return false;
	}
}
