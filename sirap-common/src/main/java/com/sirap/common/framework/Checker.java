package com.sirap.common.framework;

import java.util.Date;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.extractor.CommonExtractors;
import com.sirap.common.manager.CommandHistoryManager;

public abstract class Checker {

	private Date serverTimeWhenLogin;
	private Date expirationDate;
	private MexObject wrappedWorldTimeWhenLogin;
	private MexObject wrappedExpirationDate;
	
	protected void initLoginTime() {
		serverTimeWhenLogin = new Date();
		wrappedWorldTimeWhenLogin = new MexObject(new Date());
		CommonExtractors.setWorldTime(wrappedWorldTimeWhenLogin);
		
		Date expDate = null;
		wrappedExpirationDate = new MexObject(expDate);
		CommonExtractors.setUserExpiration(wrappedExpirationDate, AppBase.USERNAME);
	}

    protected boolean askAndCheckPassword() {
    	int max = getMaxAttempts();
    	if(max <= 0) {
    		return false;
    	}
    	
    	int count = 0;
    	while(true) {
			if(count >= max) {
				break;
			}
			
			count++;
			
			String input = MexUtil.askForHiddenInput("Password: ");
			CommandHistoryManager.g().collect(input);
			if(verify(input)) {
				return true;
			} else {
				int left = max - count;
				String suffix = left > 1 ? "s" : "";
				String asia = "Wrong password, {0} attempt{1} left.";
				C.pl(StrUtil.occupy(asia, left, suffix));
			}
    	}
    	
    	return false;
    }
    
    protected int getMaxAttempts() {
    	return 1;
    }
    
    protected abstract boolean verify(String input);
    
    public Date getExpirationDate() {
    	if(expirationDate == null && wrappedExpirationDate != null) {
    		expirationDate = (Date)wrappedExpirationDate.getObj();
    	}
    	return expirationDate;
    }
    
    private Date getCurrentWorldDate() {
    	Date serverTimeNow = new Date();
    	Date worldTimeWhenLogin = (Date)wrappedWorldTimeWhenLogin.getObj();
    	if(worldTimeWhenLogin == null) {
    		return DateUtil.hourDiff(serverTimeNow, -1 * DateUtil.TIMEZONE_JVM);
    	}

    	long diff = serverTimeNow.getTime() - serverTimeWhenLogin.getTime();
    	Date worldTimeNow = new Date(worldTimeWhenLogin.getTime() + diff);
    	
    	return worldTimeNow;
    }

	protected boolean calculateExpiration() {
    	if(getExpirationDate() == null) {
			return true;
		}
    	
		Date worldTime = getCurrentWorldDate();
		
		if(worldTime.before(expirationDate)) {
			int dayDiff = DateUtil.dayDiff(expirationDate, worldTime);
			if(dayDiff > 3) {
				return true;
			}
			
			int number;
			String unit, plural = "";
			
			if(dayDiff > 0) {
				number = dayDiff;
				unit = "day";
				if(dayDiff > 1) {
					plural = "s";
				}
			} else {
				int hourDiff = DateUtil.hourDiff(expirationDate, worldTime);
				number = hourDiff;
				unit = "hour";
				if(hourDiff > 1) {
					plural = "s";
				}
			}
			
			String key = "license.toExpire";
			String toExpire = SimpleKonfig.g().getValueOf(key);
			if(toExpire != null) {
				printLicenseInfo(StrUtil.occupy(toExpire, AppBase.USERNAME, number, unit, plural));
			} else {
				XXXUtil.alert("No valid expiration alert [" + key +"] provided.");
			}
			
			return true;
		} else {
			String key = "license.expired";
			String display = DateUtil.displayDate(expirationDate, DateUtil.DATE_US);
			String expired = SimpleKonfig.g().getValueOf(key);
			if(expired != null) {
				printLicenseInfo(StrUtil.occupy(expired, AppBase.USERNAME, display));
			} else {
				XXXUtil.alert("No valid expiration alert [" + key +"] provided.");
			}
			
			return false;
		}
	}
	
	private void printLicenseInfo(String info) {
		C.pl(info);
		String donation = SimpleKonfig.g().getValueOf("donation");
		if(donation != null) {
			C.pl(donation);
		}
		C.pl();
	}
}
