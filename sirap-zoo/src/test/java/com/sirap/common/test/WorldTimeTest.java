package com.sirap.common.test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.DateUtil;
import com.sirap.common.domain.WeatherRecord;
import com.sirap.common.extractor.WorldTimeBJTimeOrgExtractor;
import com.sirap.common.extractor.WorldTimeExtractor;
import com.sirap.extractor.impl.NationalWeatherExtractor;

public class WorldTimeTest {
	
	@Test
	public void weather() {
		NationalWeatherExtractor xiu = new NationalWeatherExtractor();
		xiu.process();
		List<WeatherRecord> items = xiu.getMexItems();
		C.list(items);
	}
	
	public void local() {
		C.pl(ZoneId.SHORT_IDS);
		LocalDateTime bjTime = LocalDateTime.of(2016, 8, 31, 21, 53, 43);
		LocalDateTime grMeantime = bjTime.minusHours(8);
		Instant instant = bjTime.atZone(ZoneId.of("CTT")).toInstant();
		Date d2 = Date.from(instant);
		C.pl(bjTime);
		C.pl(d2);
		C.pl(grMeantime);
	}
	
//	@Test
	public void bjTimeOrg() {
		C.pl(DateUtil.construct(2016,  6,  15, 22, 27, 33));
		WorldTimeExtractor frank = new WorldTimeBJTimeOrgExtractor();
		frank.process();
		D.pl("bjTimeOrg", frank.getDatetime());
	}

	//@Test
	public void baidu() {
//		WorldTimeExtractor frank = new WorldTimeBaiduExtractor();
//		frank.process();
//		D.pl("baidu", frank.getDatetime());
	}

	/**
	 * http://mib168.iteye.com/blog/800569
	 */
	@Test
	public void calendar() {
		Calendar al = Calendar.getInstance();
		C.pl(al);
		C.pl(al.get(Calendar.YEAR));
		C.pl(al.get(Calendar.MONTH));
		C.pl(al.get(Calendar.DATE));
		C.pl(al.get(Calendar.HOUR_OF_DAY));
		C.pl(al.get(Calendar.MINUTE));
		C.pl(al.get(Calendar.SECOND));
		C.pl(al.get(Calendar.MILLISECOND));
		
		int nowDay = al.get(Calendar.DAY_OF_MONTH);
		al.set(Calendar.DATE, nowDay + 3);
		C.pl(al);
		C.pl(al.get(Calendar.YEAR));
		C.pl(al.get(Calendar.MONTH));
		C.pl(al.get(Calendar.DATE));
		C.pl(al.get(Calendar.HOUR_OF_DAY));
		C.pl(al.get(Calendar.MINUTE));
		C.pl(al.get(Calendar.SECOND));
		C.pl(al.get(Calendar.MILLISECOND));
		
	}
}
