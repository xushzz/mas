package com.sirap.common;

import java.util.Date;
import java.util.List;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.util.ThreadUtil;
import com.sirap.common.extractor.Extractor;
import com.sirap.common.extractor.impl.EnglishDictionaryExtractor;
import com.sirap.common.extractor.impl.MobilePhoneLocationExtractor;
import com.sirap.common.extractor.impl.RemoteSecurityExtractor;
import com.sirap.common.extractor.impl.WorldTimeBJTimeOrgExtractor;
import com.sirap.common.extractor.impl.WorldTimeExtractor;

public class CommonHelper {
	
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
	
	public static String getMobilePhoneLocation(String phoneNumber) {
		Extractor<MexedObject> frank = new MobilePhoneLocationExtractor(phoneNumber);
		frank.process();
		MexedObject mo = frank.getMexItem();
		
		String value = null;
		if(mo != null) {
			value = mo.getString().trim();
		}
		
		return value;
	}
	
	public static List<MexedObject> getWordTranslation(String word) {
		Extractor<MexedObject> frank = new EnglishDictionaryExtractor(word);
		frank.process();
		List<MexedObject> items = frank.getMexItems();
		
		return items;
	}
}
