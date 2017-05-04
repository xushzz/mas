package com.sirap.common.component;

import java.util.Date;
import java.util.Locale;

import com.sirap.basic.component.MexedTimer;
import com.sirap.basic.component.MomentCalculator;
import com.sirap.basic.util.DateUtil;
import com.sirap.common.manager.AlarmManager;

public abstract class Alarm extends MexedTimer implements Comparable<Alarm> {

	public static final String STATUS_CANCELLED = "cancelled";

	public static final String TYPE_DELAY = "+";
	public static final String TYPE_FIXED = "@";
	public static final String TYPE_REPEAT_NO_DELAY = "#";
	public static final String TYPE_REPEAT_DELAY = "##";
	
	private boolean isActive = true; 
	private String order;
	private String action;
	private String type;
	private String source;
	private MomentCalculator jamie = null;
	private Date targetMoment;
	
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Alarm(String type, String source, String action) {
		this.action = action;
		this.type = type;
		this.source = source;
		boolean toCheckRange = TYPE_FIXED.equals(type);
		this.targetMoment = parseMoment(toCheckRange, source);
		tick();
	}
	
	public Alarm(Date theMoment, String action) {
		this.action = action;
		this.targetMoment = theMoment;
	}

	private Date parseMoment(boolean isRangeCheck, String source) {
		jamie = new MomentCalculator(isRangeCheck, source);
		Date theMoment = jamie.getTargetMoment();
		
		return theMoment;
	}
	
	private boolean isRepeat() {
		boolean flag = TYPE_REPEAT_NO_DELAY.equals(type) || TYPE_REPEAT_DELAY.equals(type);
		return flag;
	}
	
	private void tick() {
		if(targetMoment == null) {
			return;
		}
		
		long diffInMilli = targetMoment.getTime() - (new Date()).getTime();
		if(isRepeat()) {
			int min = 3000;
			if(diffInMilli < min) {
				diffInMilli = min;
			}
			setPeriodMills(diffInMilli);
			if(TYPE_REPEAT_DELAY.equals(type)) {
				setDelayMillis(diffInMilli);
			} else {
				setDelayMillis(1);
			}
		} else {
			if(diffInMilli < 0) {
				diffInMilli = 0;
			}
			setDelayMillis(diffInMilli);
		}
		
		startTimer();
		AlarmManager.g().addAlarm(this);
	}
	
	public boolean isValid() {
		return targetMoment != null;
	}
	
	public void cancel() {
		cancelTimer();
		isActive = false;
	}
	
	@Override
	protected void timerAction()  {
		execute();
		if(isRepeat()) {
			targetMoment = jamie.recalculateTargetMoment();
		} else {
			cancel();
			AlarmManager.g().removeAlarm(this);
		}
    }
	
	protected abstract void execute(); 
	
	public String display() {
		return display(Locale.US);
	}
	
	public String display(Locale locale) {
		StringBuffer sb = new StringBuffer();
		sb.append(order).append(") ");
		sb.append(DateUtil.displayDate(targetMoment, DateUtil.HOUR_Min_Sec_AM_WEEK_DATE, locale));
		sb.append(" ").append(type).append(source).append(" => ").append(action);
		return sb.toString();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(targetMoment).append("\t");
		sb.append(action);
		
		return sb.toString();
	}
	
	@Override
	public int compareTo(Alarm o) {
		return targetMoment.compareTo(o.targetMoment);
	}
}
