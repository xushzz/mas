package com.sirap.executor;

import java.util.Date;
import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.executor.ssh.SSHCommandExecutor;

public class CommandExecutor extends CommandBase {

	private static final String KEY_SSH = "ssh";
	private static final String KEY_CHEF_CLIENT_EXECUTE = "!";
	private static final String KEY_CHEF_RECIPE_DISPLAY = "=";
	
	@Override
	public boolean handle() {
		singleParam = parseParam(KEY_SSH + "\\s(.{1,})");
		if(singleParam != null) {
			String sshCommand = singleParam;
			List<String> items = SSHCommandExecutor.g().execute(sshCommand);
			
			export(items);
			
			return true;
		}
		
		if(is("d." + KEY_SSH)) {
			SSHCommandExecutor instance = SSHCommandExecutor.g();
			C.pl("Fetching time from " + instance.getIp() + " ...");
			String result = SSHCommandExecutor.g().getMilliSecondsFrom1970();
			Long milliSecondsSince1970 = Long.parseLong(result);
			Date date = new Date(milliSecondsSince1970);
			String str = DateUtil.displayDate(date, DateUtil.HOUR_Min_Sec_AM_WEEK_DATE);
			export(str);
			
			return true;
		}
		
		params = parseParams("(!|=|)" + "(\\S+)::(\\S+)");
		if(params != null) {
			String cookbook = params[1];
			String recipe = params[2];
			if(StrUtil.equals(KEY_CHEF_CLIENT_EXECUTE, params[0])) {
				String sshCommand = "chef-client -r " + cookbook + "::" + recipe;
				C.pl("ssh " + sshCommand);
				List<String> items = SSHCommandExecutor.g().execute(sshCommand);
				export(items);
				
				return true;
			}
			
			String chefrepo = g().getUserValueOf("ssh.chefrepo");
			String recipeTempalte = "cat "+ chefrepo + "/cookbooks/{0}/recipes/{1}.rb";
			String sshCommand = StrUtil.occupy(recipeTempalte, cookbook, recipe);
			C.pl("ssh " + sshCommand);
			List<String> items = SSHCommandExecutor.g().execute(sshCommand);
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
}
