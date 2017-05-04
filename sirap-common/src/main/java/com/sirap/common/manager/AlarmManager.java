package com.sirap.common.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.common.component.Alarm;

public class AlarmManager {
	
	private static AlarmManager instance;
	private int count;
	
	private List<Alarm> alarms = new ArrayList<Alarm>();
	
	private AlarmManager() {
		
	}

	public static AlarmManager g() {
		if(instance == null) {
			instance = new AlarmManager();
		}
		
		return instance;
	}

	public List<Alarm> getAlarms() {
		Collections.sort(alarms);
		return alarms;
	}
	
	public void removeAlarm(Alarm alarm) {
		alarms.remove(alarm);
	}

	public void addAlarm(Alarm alarm) {
		alarms.add(alarm);
		count++;
		alarm.setOrder(count + "");
	}

	/***
	 * @param isShowMsg
	 * @param orderIdList, means ALL alarms if null
	 * @return
	 */
	public int cancelAlarms(boolean isShowMsg, List<String> orderIdList) {
		int size = alarms.size();
		if(size == 0) {
			if(isShowMsg) {
				C.pl2("Currently no alarm.");
			}
			return -1;
		}
		
		Collections.sort(alarms);

		int countOfCancelled = 0;
		for(Alarm al : alarms) {
			String order = al.getOrder();
			if(orderIdList != null && orderIdList.indexOf(order) < 0) {
				continue;
			}
			C.pl("Cancelled [" + al.display() + "]");
			al.cancel();
			countOfCancelled++;
		}
		
		for(int i = size - 1; i >= 0; i--) {
			Alarm al = alarms.get(i);
			if(!al.isActive()) {
				alarms.remove(i);
			}
		}
		
		return countOfCancelled;
	}
}