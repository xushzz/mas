package com.sirap.executor;

import java.io.File;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.Session;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.executor.ssh.SshCommandExecutor;
import com.sirap.executor.ssh.SshConfigItem;
import com.sirap.executor.ssh.SshUtil;

public class CommandUnix extends CommandBase {

	private static final String KEY_SSH = "ssh";
	private static final String KEY_SSH_TOPS = "docker|hadoop";
	private static final String KEY_CHEF_CLIENT_EXECUTE = "!";
	private static final String KEY_CHEF_RECIPE_DISPLAY = "=";
	private static final String KEY_SSH_DATE= KEY_SSH + ".d";
	private static final String KEY_SSH_OFF = KEY_SSH + ".off";
	private static final String KEY_SSH_FTP = "sftp";
	
	@Override
	public boolean handle() {
		solo = parseParam(KEY_SSH_FTP + "\\s(.+?)");
		if(solo != null) {
			String uploadTo = OptionUtil.readString(options, "t");
			if(uploadTo != null) {
				//upload
				File file = parseFile(solo);
				if(file == null) {
					C.pl2("File doesn't exist: " + solo);
				} else {
					String filepath = file.getAbsolutePath();
					String filename = FileUtil.filenameWithExtensionOf(filepath);
					String result = SshCommandExecutor.g().uploadFile(filepath, uploadTo, filename);
					export(result);
				}
			} else {
				//download
				String location = getSshDownloadLocation();
				String[] arr = FileUtil.folderpathAndFilenameOf(solo);
				String filename = arr[1];
				if(g().isExportWithTimestampEnabled(options)) {
					filename = DateUtil.timestamp() + "_" + filename;
				}
				String newfilepath = StrUtil.useSeparator(location, filename);
				SshCommandExecutor.g().downloadFile(arr[0], arr[1], newfilepath);
				
				if(newfilepath != null) {
					String info = newfilepath;
					if(OptionUtil.readBooleanPRI(options, "d", true)) {
						info += " " + FileUtil.formatSize(newfilepath);
					}
					C.pl2("Saved => " + info);

					if(g().isGeneratedFileAutoOpen()) {
						FileOpener.open(newfilepath);
					}
					
					if(target.isFileRelated()) {
						export(FileUtil.getIfNormalFile(newfilepath));
					}
				}
			}
			
			return true;
		}
		
		if(is(KEY_SSH)) {
			SshConfigItem config = SshConfigItem.of(options);
			if(config != null) {
				SshCommandExecutor.create(config);
			}
			
			Session sess = SshCommandExecutor.g().getSession();
			String json = D.jsp(SshCommandExecutor.g().getConfig(), SshConfigItem.class);
			export(StrUtil.occupy("Connected within {0}\n{1}", sess, json));
			
			return true;
		}
		
		regex = StrUtil.occupy("({0}|{1})\\s+(.+)", KEY_SSH_TOPS, KEY_SSH);
		params = parseParams(regex);
		if(params != null) {
			String type = params[0];
			String usercommand = StrUtil.equals(type, KEY_SSH) ? params[1] : command;
			boolean useAlias = OptionUtil.readBooleanPRI(options, "a", true);
			if(useAlias) {
				usercommand = SshUtil.useAlias(usercommand);
			}
			List<String> items = SshCommandExecutor.g().execute(usercommand, isPretty());
			
			export(items);
			
			return true;
		}
		
		if(is(KEY_SSH_DATE)) {
			String result = SshCommandExecutor.g().getMilliSecondsFrom1970(isPretty());
			long millis = Long.parseLong(result);
			List<String> items = new ArrayList<>();
			
			Object[] zones = {ZoneId.systemDefault().toString(), "GMT"};
			for(Object zone : zones) {
				String x2 = DateUtil.strOf(millis, DateUtil.GMT2, zone, locale());
				String t17 = DateUtil.strOf(millis, DateUtil.TIGHT17, zone, locale());
				t17 += " $tz=" + zone;
				items.add(x2 + " #tl." + t17);
			}

			export(items);
			
			return true;
		}
		
		if(is(KEY_SSH_OFF)) {
			SshCommandExecutor.g().closeSessionWithMsg(true);
		}
		
		params = parseParams("(!|=|)" + "(\\S+)::(\\S+)");
		if(params != null) {
			String cookbook = params[1];
			String recipe = params[2];
			if(StrUtil.equals(KEY_CHEF_CLIENT_EXECUTE, params[0])) {
				String sshCommand = "chef-client -r " + cookbook + "::" + recipe;
				C.pl("ssh " + sshCommand);
				List<String> items = SshCommandExecutor.g().execute(sshCommand);
				export(items);
				
				return true;
			}
			
			String chefrepo = g().getUserValueOf("ssh.chefrepo");
			String recipeTempalte = "cat "+ chefrepo + "/cookbooks/{0}/recipes/{1}.rb";
			String sshCommand = StrUtil.occupy(recipeTempalte, cookbook, recipe);
			C.pl("ssh " + sshCommand);
			List<String> items = SshCommandExecutor.g().execute(sshCommand);
			if(StrUtil.equals(KEY_CHEF_RECIPE_DISPLAY, params[0])) {
				export(items);
			} else {
				List<String> chefParams = ChefUtil.extractParams(items, cookbook);
				export(chefParams);
			}
			
			return true;
		}
		
		return false;
	}
	
	private boolean isPretty() {
		boolean pretty = OptionUtil.readBooleanPRI(options, "p", true);
		
		return pretty;
	}
	
	protected String getSshDownloadLocation() {
		String dir = pathOf("storage.ssh", "ftp");
		dir = getTargetLocation(dir);
		
		return dir;
	}
}
