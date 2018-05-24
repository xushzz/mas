package com.sirap.common.extractor;

import java.util.Date;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.ThreadUtil;

public class CommonExtractors {
	
	public static void setWorldTime(final MexObject mDate) {
		final WorldTimeExtractor frank = new WorldTimeTianqiExtractor();
		ThreadUtil.executeInNewThread(new Runnable() {
			@Override
			public void run() {
				frank.process();
				Date date = frank.getDatetime();
				mDate.setObj(date);
			}
		});
	}

	public static void setUserExpiration(final MexObject mDate, String username) {
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
		WorldTimeExtractor frank = new WorldTimeTianqiExtractor();
		frank.process();
		return frank.getDatetime();
	}
}
