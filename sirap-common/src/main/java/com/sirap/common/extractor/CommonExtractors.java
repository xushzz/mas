package com.sirap.common.extractor;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.thread.MasterItemOriented;
import com.sirap.basic.thread.WorkerItemOriented;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.ThreadUtil;
import com.sirap.basic.util.WebReader;

public class CommonExtractors {
	
	public static void setWorldTime(final MexObject mDate) {
		ThreadUtil.executeInNewThread(new Runnable() {
			@Override
			public void run() {
				Date date = WebReader.dateOfWebsite(DateUtil.NTP_SITE);
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
	
	public static Map<String, Date> internetTimes(List<String> urls) {
		MasterItemOriented<String, Date> master = new MasterItemOriented<>(urls, new WorkerItemOriented<String, Date>() {
			@Override
			public Date process(String url) {
				Date date = WebReader.dateOfWebsite(url);
				
				return date;
			}
		});

		return master.getResults();
	}
}
