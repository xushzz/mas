package com.sirap.common.command;

import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.component.Alarm;
import com.sirap.common.framework.Janitor;
import com.sirap.common.manager.AlarmManager;

public class CommandTask extends CommandBase {

	private static final String KEY_ALARM_SET = "al(@|\\+|#)";
	private static final String KEY_ALARM_SHOW = "al";
	private static final String KEY_ALARM_CANCEL = "ac(|[\\d|,]+)";
	private static final String KEY_TASK = "tk";
	
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
					C.pl("[Begin => " + actionStr + "]");
					List<String> actions = StrUtil.split(actionStr, ';');
					executeActions(actions);
					C.pl("[End => " + actionStr + "]");
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
		
		singleParam = parseParam(KEY_ALARM_CANCEL);
		if(singleParam != null) {
			List<String> orderIds = StrUtil.split(singleParam);
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
		
		singleParam = StrUtil.parseParam(KEY_TASK + "\\s(.*?)", input);
		if(singleParam != null) {
			List<String> tasks = readRecordsFromFile(singleParam);
			if(tasks == null) {
				List<String> actions = StrUtil.split(singleParam, ';');
				executeActions(actions);
			} else {
				executeActions(tasks);
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
			String info = "[Action " + (i + 1) + "/" + actions.size() + "] ";
			C.pl(info +  action);
			Janitor.g().process(action.trim());
		}
	}
}
