package com.sirap.extractor;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.map.AlinkMap;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.thread.Master;
import com.sirap.basic.thread.Worker;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.Amaps;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.HttpUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.component.MexItemsFetcher;
import com.sirap.common.framework.command.target.TargetPdf;
import com.sirap.extractor.domain.ZhihuRecord;
import com.sirap.extractor.impl.EnglishDictionaryExtractor;
import com.sirap.extractor.impl.FindJarExtractor;
import com.sirap.extractor.impl.IcibaTranslationExtractor;
import com.sirap.extractor.impl.MobilePhoneLocationExtractor;
import com.sirap.extractor.impl.TulingExtractor;
import com.sirap.extractor.impl.WeixinSearchExtractor;
import com.sirap.extractor.impl.WikiSummaryExtractor;
import com.sirap.extractor.impl.XRatesForexRateExtractor;
import com.sirap.extractor.impl.ZhihuSearchExtractor;
import com.sirap.extractor.manager.BaiduExtractorManager;
import com.sirap.extractor.manager.Extractors;
import com.sirap.extractor.manager.FinancialTimesChineseExtractorManager;
import com.sirap.extractor.manager.ForexManager;
import com.sirap.extractor.manager.IcibaManager;
import com.sirap.extractor.manager.RssExtractorManager;

public class CommandCollect extends CommandBase {

	private static final String KEY_WEATHER = "wea";	
	private static final String KEY_CAR_DETAIL = "card";	
	private static final String KEY_CARNO = "car";
	private static final String KEY_PHONE_MOBILE = "@";
	private static final String KEY_DICTONARY = "ia";
	private static final String KEY_TRANSLATE = "i";
	private static final String KEY_FOREX = "#([a-z]{3})" + Konstants.REGEX_FLOAT + "(|/|[a-z,]+)";
	private static final String KEY_TULING_ASK = "tl\\*";
	private static final String KEY_ZHIHU_ASK = "zhihu\\*";
	private static final String KEY_BAIDU_BAIKE_SUMMARY = "bk";
	private static final String KEY_WIKI_SUMMARY = "wk";
	private static final String KEY_RSS = "rss";
	private static final String KEY_JAR= "jar";
	private static final String KEY_WEIXIN_SEARCH= "wei";
	private static final String KEY_THIS_DAY_IN_HISTORY_CHINESE = "this";
	private static final String KEY_THIS_DAY_IN_HISTORY_ENGLISH = "hist";
	private static final String KEY_NOBEL_PRIZE = "nobel";
	private static final String KEY_SINA = "sina";

	{
		helpMeanings.put("money.forex.url", XRatesForexRateExtractor.URL_X_RATES);
		helpMeanings.put("tuling.url", TulingExtractor.HOMEPAGE);
		helpMeanings.put("china.weather.url", "http://www.nmc.cn/publish/forecast/china.html");
		helpMeanings.put("phone.char.url", MobilePhoneLocationExtractor.HOMEPAGE);
		helpMeanings.put("iciba.url", IcibaTranslationExtractor.HOMEPAGE);
		helpMeanings.put("dictionary.url", EnglishDictionaryExtractor.HOMEPAGE);
		helpMeanings.put("zhihu.url", ZhihuSearchExtractor.HOMEPAGE);
	}
	
	public boolean handle() {

		flag = searchAndProcess(KEY_CARNO, new MexItemsFetcher() {
			@Override
			public void handle(List<MexItem> items) {
				exportMatrix(items);
			}
			
			@Override
			public List<MexItem> body() {
				return Extractors.fetchCarNoList();
			}
		});
		if(flag) return true;
		
		solo = parseParam(KEY_CAR_DETAIL + "\\s+(.+)");
		if(solo != null) {
			List<MexItem> items = Extractors.fetchCarList();
			export(Colls.filter(items, solo, isCaseSensitive(), isStayCriteria()));
			
			return true;
		}

		solo = parseParam(KEY_CAR_DETAIL + "-([^\\.]+)");
		if(solo != null) {
			export(Extractors.fetchCarDetail(solo));
			
			return true;
		}

		flag = searchAndProcess(KEY_WEATHER, new MexItemsFetcher() {
			@Override
			public void handle(List<MexItem> items) {
				exportMatrix(items);
			}
			
			@Override
			public String fixCriteria(String criteria) {
				AlinkMap<String, String> cities = Amaps.newLinkHashMap();
				cities.put("sz", "shenzhen");
				cities.put("gz", "guangzhou");
				cities.put("bj", "beijing");
				cities.put("cd", "chengdu");
				cities.put("sh", "shanghai");
				cities.put("dl", "dalian");
				String name = cities.getIgnorecase(criteria);
				return name != null ? name : criteria;
			}
			
			@Override
			public List<MexItem> body() {
				return Extractors.fetchWeather();
			}
		});
		if(flag) return true;
		
		solo = parseParam(KEY_PHONE_MOBILE + "(.+)");
		if(solo != null) {
			String number = StrUtil.takeDigitsOnly(solo);
			if(StrUtil.isRegexMatched("[\\d]{7,30}", solo)) {
				String detail = getMobilePhoneLocation(number);
				if(EmptyUtil.isNullOrEmpty(detail)) {
					detail = "no detail.";
				}
				String value = number + " " + detail;
				export(value);
				
				return true;
			}
		}
				
		solo = parseParam(KEY_TRANSLATE + "\\s+(.+?)");
		if(solo != null) {
			String key = "iciba.source";
			String filePath = g().getUserValueOf(key);
			boolean fetchOnly = false | OptionUtil.readBooleanPRI(options, "force", false);
			if(filePath == null) {
				fetchOnly = true;
			} else if(!FileUtil.exists(filePath)) {
				String msg = "Non-exist path {0} with key {1}.";
				C.pl(StrUtil.occupy(msg, filePath, key));
			}
			
			File file = parseFile(solo);
			if(file != null) {
				String cat = IOUtil.charsetOfTextFile(file.getAbsolutePath());
				if(OptionUtil.readBooleanPRI(options, "x", false)) {
					cat = switchChartset(cat);
				}
				List<String> words = FileOpener.readTextContent(file.getAbsolutePath(), false, cat);
				getBatchTranslation(fetchOnly, words, filePath);
			} else {
				List<String> words = StrUtil.split(solo);
				List<ValuesItem> items = Lists.newArrayList();
				for(String single : words) {
					items.addAll(getTranslation(fetchOnly, single, filePath));
				}
				useLowOptions("c=NL");
				export(items);
			}

			return true;
		}
		
		solo = parseParam(KEY_DICTONARY + "\\s+(.+)");
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
				if(target instanceof TargetPdf) {
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
		
		solo = parseParam(KEY_TULING_ASK + "(.+)");
		if(solo != null) {
			String key = g().getUserValueOf("tuling.key", "e8c190a005adc401867efd1ad2602f70");
			Extractor<MexObject> mike = new TulingExtractor(key, solo);
			mike.process();
			String tulingInChinese = StrUtil.utf8ToWhatever("\\uE59BBE\\uE781B5");
			export(tulingInChinese + ": " + mike.getItem());
			
			return true;
		}
		
		solo = parseParam(KEY_ZHIHU_ASK + "(.+)");
		if(solo != null) {
			Extractor<ZhihuRecord> mike = new ZhihuSearchExtractor(solo);
			mike.process();
			export(mike.getItems());
			
			return true;
		}
		
		params = parseParams(KEY_BAIDU_BAIKE_SUMMARY + "\\s(\\*?)(.+)");
		if(params != null) {
			boolean withOtherSameNames = !params[0].isEmpty() || OptionUtil.readBooleanPRI(options, "all", false);
			String keywordOrUrl = params[1];
			List<MexObject> items = BaiduExtractorManager.g().fetchBaiduSummary(keywordOrUrl, withOtherSameNames);
			export(items);
			
			return true;
		}
		
		solo = parseParam(KEY_WIKI_SUMMARY + "\\s(.+)");
		if(solo != null) {
			String niceKeyword = StrUtil.uppercaseInitials(solo);
			Extractor<MexObject> mike = new WikiSummaryExtractor(niceKeyword);
			mike.process();
			export(mike.getItems());
			
			return true;
		}

		if(is(KEY_RSS)) {
			Object result = ObjectUtil.execute(sourceOfRss(), "readAllRss", new Class[0], new Object[0]);
			List<MexObject> items = (List<MexObject>)result;
			export(Colls.reverse(items));
			
			return true;
		}
		
		solo = parseParam(KEY_RSS + "\\s(.+)");
		if(solo != null) {
			Object result = ObjectUtil.execute(sourceOfRss(), "readAllRss", new Class[0], new Object[0]);
			List<MexObject> items = (List<MexObject>)result;
			export(Colls.reverse(Colls.filter(items, solo)));
			
			return true;
		}
		
		regex = KEY_RSS + "\\.(.+)";
		solo = parseParam(regex);
		if(solo != null) {
			Object result = ObjectUtil.execute(sourceOfRss(), "fetchRssByType", new Class[]{String.class}, new Object[]{solo});
			export(Colls.reverse((List<MexObject>)result));
			
			return true;
		}
		
		solo = parseParam(KEY_JAR + "\\s+(.+)");
		if(solo != null) {
			Extractor<MexObject> mike = new FindJarExtractor(solo);
			mike.process();
			export(mike.getItems());
			
			return true;
		}
		
		solo = parseParam(KEY_WEIXIN_SEARCH + "\\s+(.+)");
		if(solo != null) {
			Extractor<MexObject> mike = new WeixinSearchExtractor(solo);
			mike.process();
			export(mike.getItems());
			
			return true;
		}
		
		solo = parseParam(KEY_THIS_DAY_IN_HISTORY_CHINESE + "(bc\\d{1,4}|\\d{1,4})");
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
			
			String location = getTargetLocation(storageWithSeparator());

			HistoryEventsFetcher dinesh = new HistoryEventsFetcher(location, "thisdays");
			Master<MexObject> master = new Master<MexObject>(urlParams, dinesh);

			master.sitAndWait();
			C.pl2("Done with downloading, check " + location);

			return true;
		}

		params = parseParams(KEY_THIS_DAY_IN_HISTORY_ENGLISH + "(\\d{1,2})[\\./\\-](\\d{1,2})");
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

		if(is(KEY_THIS_DAY_IN_HISTORY_ENGLISH)) {
			String urlParam = DateUtil.displayNow("MMMM-dd").toLowerCase();
			String monthDay = DateUtil.displayNow("MM/dd");
			export(Extractors.fetchHistoryEventsByDay2(urlParam, monthDay));
			return true;
		}
		
		solo = parseParam(KEY_NOBEL_PRIZE + "\\s(.+)");
		if(solo != null) {
			List<MexObject> items = Extractors.fetchAllNobelPrizes();
			export2(items, solo);
			
			return true;
		}
		
		solo = parseParam(KEY_SINA + "\\s(.+)");
		if(solo != null) {
			if(HttpUtil.isHttp(solo)) {
				List<String> items = ExtractorUtil.sinaSlides(solo);
				export(items);
				
				return true;
			}
		}

		return false;
	}
	
	public static String getMobilePhoneLocation(String phoneNumber) {
		Extractor<MexObject> frank = new MobilePhoneLocationExtractor(phoneNumber);
		frank.process();
		MexObject mo = frank.getItem();
		
		String value = null;
		if(mo != null) {
			value = mo.getString().trim();
		}
		
		return value;
	}
	
	public static List<MexObject> lookupDictionary(String word) {
		Extractor<MexObject> frank = new EnglishDictionaryExtractor(word);
		frank.process();
		List<MexObject> items = frank.getItems();
		
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
	
	private List<ValuesItem> getTranslation(boolean fetchOnly, String word, String warehouse) {
		String charset = Konstants.CODE_UTF8;
		List<ValuesItem> items = null;
		if(fetchOnly) {
			ValuesItem vi = IcibaManager.g().fetchFromWebsite(word);
			items = Lists.newArrayList(vi);
		} else {
			//C.pl("Reading... " + filePath);
			boolean isSensitive = OptionUtil.readBooleanPRI(options, "case", false);
			items = IcibaManager.g().readFromDatabase(word, warehouse, charset, isSensitive);
			if(EmptyUtil.isNullOrEmpty(items)) {
				ValuesItem vi = IcibaManager.g().fetchFromWebsite(word);
				if(vi != null) {
					IcibaManager.g().saveToDatabase(vi, warehouse, charset);
					C.pl("Saving... " + warehouse);
					items = Lists.newArrayList(vi);
				}
			}
		}

		return items;
	}
	
	private void getBatchTranslation(boolean fetchOnly, List<String> words, String warehouse) {
		Master<String> george = new Master<String>(words, new Worker<String>() {
			@Override
			public void process(String word) {
				int count = countOfTasks - queue.size();
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "translating...", word);
				getTranslation(fetchOnly, word, warehouse);
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "translated", word);
			}
			
		});
		
		george.sitAndWait();
	}

}
