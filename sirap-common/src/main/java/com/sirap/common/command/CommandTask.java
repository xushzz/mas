package com.sirap.common.command;

import java.io.File;
import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.component.Alarm;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.Janitor;
import com.sirap.common.manager.AlarmManager;

public class CommandTask extends CommandBase {

	private static final String KEY_ALARM_SET = "al(@|\\+|#{1,2})";
	private static final String KEY_ALARM_SHOW = "al";
	private static final String KEY_ALARM_CANCEL = "ac(|[\\d|,]+)";
	private static final String KEY_TASK = "tk";
	
	@Override
	public boolean handle() {
		
		String regex = KEY_ALARM_SET + "(.*?)(|\\s.*?)";
		params = StrUtil.parseParams(regex, input);
		if(params != null) {
			String type = params[0];
			String faceValue = params[1];
			String temp = params[2].trim();
			if(temp.isEmpty()) {
				temp = g().getUserValueOf("action.def");
			}
			String task = temp;
			if(EmptyUtil.isNullOrEmpty(task)) {
				C.pl2("No action expected.");
				return true;
			}
			final String actionStr = task;
			Alarm al = new Alarm(type, faceValue, actionStr) {
				@Override
				public void execute() {
					C.pl("==================== START of alarm task => " + actionStr + " ====================");
					List<String> actions = StrUtil.split(actionStr);
					executeActions(actions);
					C.pl("==================== END of alarm task => " + actionStr + " ====================");
				}
			};
			
			if(al.isValid()) {
				C.pl(al.display(g().getLocale()));
				C.pl2(alarmNowInfo());
				
				return true;
			}
		}
		
		if(is(KEY_ALARM_SHOW)) {
			List<Alarm> alarms = AlarmManager.g().getAlarms();
			for(Alarm al:alarms) {
				C.pl(al.display(g().getLocale()));
			}
			
			if(alarms.size() > 0) {
				C.pl2(alarmNowInfo());
			} else {
				C.pl2("Currently no alarm.");
			}
			
			return true;
		}
		
		solo = parseSoloParam(KEY_ALARM_CANCEL);
		if(solo != null) {
			List<String> orderIds = solo.isEmpty() ? null : StrUtil.split(solo);			
			int count = AlarmManager.g().cancelAlarms(true, orderIds);
			if(count > 0) {
				C.pl2(alarmNowInfo());
			} else {
				if(count != -1) {
					C.pl2("No alarm has been cancelled.");
				}
			}
			
			return true;
		}
		
		solo = StrUtil.parseParam(KEY_TASK + "\\s(.*?)", input);
		if(solo != null) {
			File file = parseFile(solo);
			if(file != null && FileOpener.isTextFile(file.getAbsolutePath())) {
				List<String> tasks = FileOpener.readTextContent(file.getAbsolutePath());
				executeActions(tasks);
			} else {
				List<String> actions = StrUtil.split(solo);
				D.pl(actions);
				executeActions(actions);
			}

			return true;
		}

		return false;
	}
	
	private String alarmNowInfo() {
		String str = DateUtil.displayDate(DateUtil.HOUR_Min_Sec_AM_WEEK_DATE, g().getLocale());
		return "** " + str + " **";
	}
	
	private void executeActions(List<String> actions) {
		for(int i = 0; i < actions.size(); i++) {
			String action = actions.get(i);
			if(EmptyUtil.isNullOrEmpty(action)) {
				continue;
			}
			C.pl("********** begin of action " + (i + 1) + "/" + actions.size() + ": " + action + " **********");
			Janitor.g().process(action.trim());
			C.pl("********** end of action " + (i + 1) + "/" + actions.size() + ": " + action + " **********");
		}
	}
}
