package com.sirap.basic.component;

import java.util.Timer;
import java.util.TimerTask;

public abstract class MexedTimer {
	
	private Timer timer;
	private TimerTask timerTask;
	private long delayMilliSeconds = -1;
	private long periodMilliSeconds = -1;
    
	protected void setDelayMinutes(double minutes) {
		this.delayMilliSeconds = (long)(minutes * Konstants.MILLI_PER_MINUTE);
	}

	protected void setDelaySeconds(long seconds) {
		this.delayMilliSeconds = seconds * Konstants.MILLI_PER_SECOND;
	}

	protected void setDelayMillis(long milliSeconds) {
		this.delayMilliSeconds = milliSeconds;
	}
	
	protected void setPeriodSeconds(long seconds) {
		this.periodMilliSeconds = seconds * Konstants.MILLI_PER_SECOND;
	}
	
	protected void setPeriodMills(long periodMilliseconds) {
		this.periodMilliSeconds = periodMilliseconds;
	}
	
	protected boolean isTimerInvalid() {
		return delayMilliSeconds < 0;
	}

	protected abstract void timerAction();
	
	protected final TimerTask createTimerTask()  {
    	return new TimerTask() {
 			public void run() {
 				timerAction();
 			}
     	};
    }
	
	protected void startTimer() {
		if(isTimerInvalid()) {
			return;
		}
    	timer = new Timer();
    	timerTask = createTimerTask();
    	if(periodMilliSeconds > 0) {
        	timer.schedule(timerTask, delayMilliSeconds, periodMilliSeconds);
    	} else {
        	timer.schedule(timerTask, delayMilliSeconds);
    	}
    }
	
	public boolean isTimerActive() {
		return timer != null;
	}
    
    public void cancelTimer() {
    	if(!isTimerActive() || isTimerInvalid()) {
			return;
		}
    	timer.cancel();
    }
    
    protected void cancelTimerTask() {
    	if(isTimerInvalid()) {
			return;
		}
    	timerTask.cancel();
    }
    
    protected void updateTimerTask() {
    	if(isTimerInvalid()) {
			return;
		}
    	cancelTimerTask();
    	timerTask = createTimerTask();
    	if(periodMilliSeconds > 0) {
        	timer.schedule(timerTask, delayMilliSeconds, periodMilliSeconds);
    	} else {
        	timer.schedule(timerTask, delayMilliSeconds);
    	}
    }
}
