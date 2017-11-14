package com.sirap.common.component;

import java.util.Locale;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.MexedTimer;
import com.sirap.basic.component.MomentCalculator;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.manager.AlarmManager;

public abstract class Alarm extends MexedTimer implements Comparable<Alarm> {

	private boolean activeFlag; 
	private String order;
	private String actionInfo;
	private String timeInfo;
	private MomentCalculator maester;
	
	public boolean isActive() {
		return activeFlag;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
	public Alarm(String timeInfo, String actionInfo) {
		this.timeInfo = timeInfo;
		this.actionInfo = actionInfo;
		maester = new MomentCalculator(timeInfo);
		if(maester.isValidAlarm()) {
			tick();
		}
	}
	
	private void tick() {
		setDelayMillis(maester.howLongHaveToWait());
		if(maester.isRepeatedAlarm()) {
			long interval = maester.getRepeatIntervalMilliSeconds();
			long miniInterval = 3;
			if(interval < miniInterval * Konstants.MILLI_PER_SECOND) {
				String msg = "The repeat interval {0} must be at least {1} seconds.";
				C.pl2(StrUtil.occupy(msg, interval / Konstants.MILLI_PER_SECOND , miniInterval));
				
				return;
			}

			setPeriodMills(interval);
		}
		
		startTimer();
		activeFlag = true;
		AlarmManager.g().addAlarm(this);
	}
	
	public void cancel() {
		cancelTimer();
		activeFlag = false;
	}
	
	@Override
	protected void timerAction()  {
		execute();
		if(maester.isRepeatedAlarm()) {
			maester.recalculateTargetMoment();
			return;
		}

		cancel();
		AlarmManager.g().removeAlarm(this);
    }
	
	protected abstract void execute(); 
	
	public String display() {
		return display(Locale.US);
	}
	
	public String display(Locale locale) {
		StringBuffer sb = new StringBuffer();
		sb.append(order).append(") ");
		sb.append(DateUtil.displayDate(maester.theDate(), DateUtil.HOUR_Min_Sec_AM_WEEK_DATE, locale));
		sb.append(" ");
		if(maester.isRepeatedAlarm()) {
			sb.append("REPEAT ");
		}
		sb.append(timeInfo).append(" => ").append(actionInfo);
		return sb.toString();
	}

	public static String alarmNowInfo(Locale locale) {
		String str = DateUtil.displayDate(DateUtil.HOUR_Min_Sec_AM_WEEK_DATE, locale);
		return "** " + str + " **";
	}
	
	@Override
	public int compareTo(Alarm o) {
		return maester.theDate().compareTo(o.maester.theDate());
	}
}
