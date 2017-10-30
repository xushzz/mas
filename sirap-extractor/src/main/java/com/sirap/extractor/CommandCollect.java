package com.sirap.extractor;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.thread.Master;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.domain.WeatherRecord;
import com.sirap.common.extractor.Extractor;
import com.sirap.common.framework.command.target.TargetPDF;
import com.sirap.extractor.domain.ZhihuRecord;
import com.sirap.extractor.impl.EnglishDictionaryExtractor;
import com.sirap.extractor.impl.FindJarExtractor;
import com.sirap.extractor.impl.IcibaTranslationExtractor;
import com.sirap.extractor.impl.MobilePhoneLocationExtractor;
import com.sirap.extractor.impl.NationalWeatherExtractor;
import com.sirap.extractor.impl.TulingExtractor;
import com.sirap.extractor.impl.WeixinSearchExtractor;
import com.sirap.extractor.impl.WikiSummaryExtractor;
import com.sirap.extractor.impl.XRatesForexRateExtractor;
import com.sirap.extractor.impl.ZhihuSearchExtractor;
import com.sirap.extractor.manager.BaiduExtractorManager;
import com.sirap.extractor.manager.Extractors;
import com.sirap.extractor.manager.FinancialTimesChineseExtractorManager;
import com.sirap.extractor.manager.ForexManager;
import com.sirap.extractor.manager.RssExtractorManager;
import com.sirap.extractor.manager.WeatherManager;

public class CommandCollect extends CommandBase {

	private static final String KEY_WEATHER = "w";	
	private static final String KEY_CAR = "car";	
	private static final String KEY_PHONE_MOBILE = "@";
	private static final String KEY_DICTONARY = "ia";
	private static final String KEY_TRANSLATE = "i";
	private static final String KEY_FOREX = "\\$([a-z]{3})" + Konstants.REGEX_FLOAT + "(|/|[a-z,]+)";
	private static final String KEY_TULING_ASK = "tl\\*";
	private static final String KEY_ZHIHU_ASK = "\\*";
	private static final String KEY_BAIDU_BAIKE_SUMMARY = "bk";
	private static final String KEY_WIKI_SUMMARY = "wk";
	private static final String KEY_RSS = "rss";
	private static final String KEY_JAR= "jar";
	private static final String KEY_WEIXIN_SEARCH= "wei";
	private static final String KEY_THIS_DAY_IN_HISTORY_CHINESE = "this";
	private static final String KEY_THIS_DAY_IN_HISTORY = "hist";
	private static final String KEY_NOBEL_PRIZE = "nobel";

	{
		helpMeanings.put("money.forex.url", XRatesForexRateExtractor.URL_X_RATES);
		helpMeanings.put("tuling.url", TulingExtractor.HOMEPAGE);
		helpMeanings.put("china.weather.url", NationalWeatherExtractor.HOMEPAGE);
		helpMeanings.put("phone.char.url", MobilePhoneLocationExtractor.HOMEPAGE);
		helpMeanings.put("iciba.url", IcibaTranslationExtractor.HOMEPAGE);
		helpMeanings.put("dictionary.url", EnglishDictionaryExtractor.HOMEPAGE);
		helpMeanings.put("zhihu.url", ZhihuSearchExtractor.HOMEPAGE);
	}
	
	public boolean handle() {

		if(is(KEY_CAR + KEY_2DOTS)) {
			export(Extractors.fetchCarList());
			
			return true;
		}
		
		solo = parseSoloParam(KEY_CAR + "\\s([^\\.]+)");
		if(solo != null) {
			List<MexObject> items = Extractors.fetchCarList();
			export(CollectionUtil.filter(items, solo));
			
			return true;
		}

		if(is(KEY_WEATHER + KEY_2DOTS)) {
			List<WeatherRecord> items = WeatherManager.g().allRecords();
			export(items);
			
			return true;
		}
		
		solo = parseSoloParam(KEY_WEATHER + "\\.([^\\.]+)");
		if(solo != null) {
			List<WeatherRecord> items = WeatherManager.g().search(solo);
			export(items);
			
			return true;
		}
		
		solo = parseSoloParam(KEY_PHONE_MOBILE + "(.+?)");
		if(solo != null) {
			String number = StrUtil.takeDigitsOnly(solo);

			if(number.length() >= 7) {
				String detail = getMobilePhoneLocation(number);
				if(EmptyUtil.isNullOrEmpty(detail)) {
					detail = "no detail.";
				}
				String value = number + " " + detail;
				export(value);
			}
			
			return true;
		}
				
		solo = parseSoloParam(KEY_TRANSLATE + "\\s+(.+?)");
		if(solo != null) {
			List<MexObject> items = getTranslation(solo);
			export(items);
			
			return true;
		}
		
		solo = parseSoloParam(KEY_DICTONARY + "\\s+(.+?)");
		if(solo != null) {
			List<MexObject> items = lookupDictionary(solo);
			export(items);
			
			return true;
		}
		
		params = parseParams(KEY_FOREX);
		if(params != null) {
			String name = params[0];
			String amount = params[1];
			String currencies = params[2];
			Double bd = MathUtil.toDouble(amount);
			if(bd != null) {
				if(target instanceof TargetPDF) {
					int[] cellsWidth = {1, 4, 2};
					int[] cellsAlign = {1, 0, 2};
					PDFParams pdfParams = new PDFParams(cellsWidth, cellsAlign);
					target.setParams(pdfParams);
					List<List<String>> records = ForexManager.g().convert4PDF(name, amount, currencies);
					export(records);
				} else {
					List<String> records = ForexManager.g().convert(name, amount, currencies);
					export(records);
				}
				return true;
			}
		}
		
		solo = parseSoloParam(KEY_TULING_ASK + "(.+?)");
		if(solo != null) {
			String key = g().getUserValueOf("tuling.key", "e8c190a005adc401867efd1ad2602f70");
			Extractor<MexObject> mike = new TulingExtractor(key, solo);
			mike.process();
			String tulingInChinese = StrUtil.utf8ToWhatever("\\uE59BBE\\uE781B5");
			export(tulingInChinese + ": " + mike.getMexItem());
			
			return true;
		}
		
		solo = parseSoloParam(KEY_ZHIHU_ASK + "(.+?)");
		if(solo != null) {
			Extractor<ZhihuRecord> mike = new ZhihuSearchExtractor(solo);
			mike.process();
			export(mike.getMexItems());
			
			return true;
		}
		
		params = parseParams(KEY_BAIDU_BAIKE_SUMMARY + "\\s(\\*?)(.+?)");
		if(params != null) {
			boolean withOtherSameNames = !params[0].isEmpty() || OptionUtil.readBooleanPRI(options, "all", false);
			String keywordOrUrl = params[1];
			List<MexObject> items = BaiduExtractorManager.g().fetchBaiduSummary(keywordOrUrl, withOtherSameNames);
			export(items);
			
			return true;
		}
		
		solo = parseSoloParam(KEY_WIKI_SUMMARY + "\\s(.+?)");
		if(solo != null) {
			Extractor<MexObject> mike = new WikiSummaryExtractor(solo);
			mike.process();
			export(mike.getMexItems());
			
			return true;
		}

		if(is(KEY_RSS)) {
			Object result = ObjectUtil.execute(sourceOfRss(), "readAllRss", new Class[0], new Object[0]);
			List<MexObject> items = (List<MexObject>)result;
			items = CollectionUtil.reverseOrder(items);
			export(items);
			
			return true;
		}
		
		solo = parseSoloParam(KEY_RSS + "\\s(.+?)");
		if(solo != null) {
			Object result = ObjectUtil.execute(sourceOfRss(), "readAllRss", new Class[0], new Object[0]);
			List<MexObject> items = (List<MexObject>)result;
			items = CollectionUtil.reverseOrder(CollectionUtil.filter(items, solo));
			export(items);
			
			return true;
		}
		
		regex = KEY_RSS + "\\.(.+?)";
		solo = parseSoloParam(regex);
		if(solo != null) {
			Object result = ObjectUtil.execute(sourceOfRss(), "fetchRssByType", new Class[]{String.class}, new Object[]{solo});
			List<MexObject> items = (List<MexObject>)result;
			items = CollectionUtil.reverseOrder(items);
			
			export(items);
			
			return true;
		}
		
		solo = parseSoloParam(KEY_JAR + "\\s+(.+?)");
		if(solo != null) {
			Extractor<MexObject> mike = new FindJarExtractor(solo);
			mike.process();
			export(mike.getMexItems());
			
			return true;
		}
		
		solo = parseSoloParam(KEY_WEIXIN_SEARCH + "\\s+(.+?)");
		if(solo != null) {
			Extractor<MexObject> mike = new WeixinSearchExtractor(solo);
			mike.process();
			export(mike.getMexItems());
			
			return true;
		}
		
		solo = parseSoloParam(KEY_THIS_DAY_IN_HISTORY_CHINESE + "(bc\\d{1,4}|\\d{1,4})");
		if(solo != null) {
			if(!StrUtil.startsWith(solo, "bc")) {
				int currentYear = Integer.parseInt(DateUtil.displayNow("yyyy"));
				XXXUtil.checkYearRange(Integer.parseInt(solo), currentYear);
			}
			export(Extractors.fetchHistoryEventsByYear(solo));
			return true;
		}

		params = parseParams(KEY_THIS_DAY_IN_HISTORY_CHINESE + "(\\d{1,2})[\\./\\-](\\d{1,2})");
		if(params != null) {
			int month = Integer.parseInt(params[0]);
			int day = Integer.parseInt(params[1]);
			XXXUtil.checkMonthDayRange(month, day);
			String month2 = StrUtil.padLeft(month + "", 2, "0");
			String day2 = StrUtil.padLeft(day + "", 2, "0");
			String urlParam = month2 + "-" + day2;
			export(Extractors.fetchHistoryEventsByDay(urlParam));
			return true;
		}

		if(is(KEY_THIS_DAY_IN_HISTORY_CHINESE)) {
			String urlParam = DateUtil.displayNow("MM-dd");
			export(Extractors.fetchHistoryEventsByDay(urlParam));
			return true;
		}

		if(is(KEY_THIS_DAY_IN_HISTORY_CHINESE + ".load")) {
			int[] maxDays = DateUtil.MAX_DAY_IN_MONTH_LEAP_YEAR;
			List<MexObject> urlParams = Lists.newArrayList();
			for(int k = 1; k <= 12; k++) {
				String month = StrUtil.padLeft("" + k, 2, "0");
				int max = maxDays[k - 1];
				for(int i = 1; i <= max; i++) {
					String day = StrUtil.padLeft("" + i, 2, "0");
					urlParams.add(new MexObject(month + "-" + day));
				}
			}
			
			String location = getTargetLocation(storage());

			HistoryEventsFetcher dinesh = new HistoryEventsFetcher(location);
			Master<MexObject> master = new Master<MexObject>(urlParams, dinesh);

			master.sitAndWait();
			C.pl2("Done with downloading, check " + location);

			return true;
		}

		params = parseParams(KEY_THIS_DAY_IN_HISTORY + "(\\d{1,2})[\\./\\-](\\d{1,2})");
		if(params != null) {
			int month = Integer.parseInt(params[0]);
			int day = Integer.parseInt(params[1]);
			XXXUtil.checkMonthDayRange(month, day);
			String month2 = DateUtil.getJanuaryLikeMonth(month, true).toLowerCase();
			String month3 = StrUtil.padLeft(month + "", 2, "0");
			String day2 = StrUtil.padLeft(day + "", 2, "0");
			String urlParam = month2 + "-" + day2;
			String monthDay = month3 + "/" + day2;
			export(Extractors.fetchHistoryEventsByDay2(urlParam, monthDay));
			return true;		
		}

		if(is(KEY_THIS_DAY_IN_HISTORY)) {
			String urlParam = DateUtil.displayNow("MMMM-dd").toLowerCase();
			String monthDay = DateUtil.displayNow("MM/dd");
			export(Extractors.fetchHistoryEventsByDay2(urlParam, monthDay));
			return true;
		}

		if(is(KEY_NOBEL_PRIZE + KEY_2DOTS)) {
			export(Extractors.fetchAllNobelPrizes());
			
			return true;
		}
		
		solo = parseSoloParam(KEY_NOBEL_PRIZE + "\\s([^\\.]+)");
		if(solo != null) {
			List<MexObject> items = Extractors.fetchAllNobelPrizes();
			export(CollectionUtil.filter(items, solo));
			
			return true;
		}

		return false;
	}
	
	public static String getMobilePhoneLocation(String phoneNumber) {
		Extractor<MexObject> frank = new MobilePhoneLocationExtractor(phoneNumber);
		frank.process();
		MexObject mo = frank.getMexItem();
		
		String value = null;
		if(mo != null) {
			value = mo.getString().trim();
		}
		
		return value;
	}
	
	public static List<MexObject> getTranslation(String word) {
		Extractor<MexObject> frank = new IcibaTranslationExtractor(word);
		frank.process();
		List<MexObject> items = frank.getMexItems();
		
		return items;
	}
	
	public static List<MexObject> lookupDictionary(String word) {
		Extractor<MexObject> frank = new EnglishDictionaryExtractor(word);
		frank.process();
		List<MexObject> items = frank.getMexItems();
		
		return items;
	}
	
	private RssExtractorManager sourceOfRss() {
		String where = g().getUserValueOf("rss.source", "baidu");
		RssExtractorManager instance;
		if(StrUtil.equals("baidu", where)) {
			instance = BaiduExtractorManager.g();
		} else {
			instance = FinancialTimesChineseExtractorManager.g();
		}
		
		return  instance;
	}
}
