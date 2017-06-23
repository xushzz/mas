package com.sirap.common.extractor;

import java.util.Date;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.util.ThreadUtil;

public class CommonExtractors {
	
	public static void setWorldTime(final MexedObject mDate) {
		final WorldTimeExtractor frank = new WorldTimeBJTimeOrgExtractor();
		ThreadUtil.executeInNewThread(new Runnable() {
			@Override
			public void run() {
				frank.process();
				Date date = frank.getDatetime();
				mDate.setObj(date);
			}
		});
	}

	public static void setUserExpiration(final MexedObject mDate, String username) {
		final RemoteSecurityExtractor frank = new RemoteSecurityExtractor(username);
		ThreadUtil.executeInNewThread(new Runnable() {
			@Override
			public void run() {
				frank.process();
				mDate.setObj(frank.getExpiration());
			}
		});
	}
	
	public static Date getWorldTime() {
		WorldTimeExtractor frank = new WorldTimeBJTimeOrgExtractor();
		frank.process();
		return frank.getDatetime();
	}
}
