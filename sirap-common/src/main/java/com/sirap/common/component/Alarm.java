package com.sirap.common.component;

import java.util.Date;
import java.util.Locale;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.MexedTimer;
import com.sirap.basic.component.MomentCalculator;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.common.manager.AlarmManager;

public abstract class Alarm extends MexedTimer implements Comparable<Alarm> {

	public static final String STATUS_CANCELLED = "cancelled";

	public static final String TYPE_DELAY = "+";
	public static final String TYPE_FIXED = "@";
	public static final String TYPE_REPEAT_NOW = "#";
	public static final String TYPE_REPEAT_DELAY = "##";
	
	private boolean isActive = true; 
	private String order;
	private String action;
	private String type;
	private String face;
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

	public Alarm(String type, String face, String action) {
		this.type = type;
		this.face = face;
		this.action = action;
		
		if(sharpHourAlarm()) {
			this.targetMoment = DateUtil.nextSharpHour(1);
		} else {
			boolean toCheckRange = TYPE_FIXED.equals(type);
			this.targetMoment = parseMoment(toCheckRange, face);
		}
		tick();
	}
	
	public boolean sharpHourAlarm() {
		boolean flag = face.isEmpty() && ("#".equals(type) || "##".equals(type));
		return flag;
	}
	
	public Alarm(Date theMoment, String action) {
		this.action = action;
		this.targetMoment = theMoment;
	}

	private Date parseMoment(boolean isRangeCheck, String face) {
		jamie = new MomentCalculator(isRangeCheck, face);
		Date theMoment = jamie.getTargetMoment();
		
		return theMoment;
	}
	
	private boolean isRepeat() {
		boolean flag = TYPE_REPEAT_NOW.equals(type) || TYPE_REPEAT_DELAY.equals(type);
		return flag;
	}
	
	private void tick() {
		if(targetMoment == null) {
			return;
		}
		
		long diffInMilli = targetMoment.getTime() - (new Date()).getTime();
		if(sharpHourAlarm()) {
			setDelayMillis(diffInMilli);
			if("##".equals(type)) {
				setPeriodMills(Konstants.MILLI_PER_HOUR);
			}
		} else {
			if(isRepeat()) {
				int least = 3000;
				if(diffInMilli < least) {
					C.pl("Minimum repeat interval would be changed to " + least + " milliseconds.");
					diffInMilli = least;
				}
				setPeriodMills(diffInMilli);
				setDelayMillis(diffInMilli);
				if(TYPE_REPEAT_NOW.equals(type)) {
					execute();
				}
			} else {
				if(diffInMilli < 0) {
					diffInMilli = 0;
				}
				setDelayMillis(diffInMilli);
			}
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
		sb.append(" ").append(type).append(face).append(" => ").append(action);
		return sb.toString();
	}

	@Override
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
