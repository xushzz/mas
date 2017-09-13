package com.sirap.extractor;

import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.domain.WeatherRecord;
import com.sirap.common.extractor.Extractor;
import com.sirap.common.framework.command.target.TargetPDF;
import com.sirap.extractor.domain.ZhihuRecord;
import com.sirap.extractor.impl.CCTVProgramExtractor;
import com.sirap.extractor.impl.EnglishDictionaryExtractor;
import com.sirap.extractor.impl.FindJarExtractor;
import com.sirap.extractor.impl.IcibaTranslationExtractor;
import com.sirap.extractor.impl.MobilePhoneLocationExtractor;
import com.sirap.extractor.impl.NationalWeatherExtractor;
import com.sirap.extractor.impl.TulingExtractor;
import com.sirap.extractor.impl.WikiSummaryExtractor;
import com.sirap.extractor.impl.XRatesForexRateExtractor;
import com.sirap.extractor.impl.ZhihuSearchExtractor;
import com.sirap.extractor.manager.BaiduExtractorManager;
import com.sirap.extractor.manager.CCTVManager;
import com.sirap.extractor.manager.FinancialTimesChineseExtractorManager;
import com.sirap.extractor.manager.ForexManager;
import com.sirap.extractor.manager.RssExtractorManager;
import com.sirap.extractor.manager.WeatherManager;

public class CommandCollect extends CommandBase {

	private static final String KEY_WEATHER = "w";	
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

		if(is(KEY_WEATHER + KEY_2DOTS)) {
			List<WeatherRecord> items = WeatherManager.g().allRecords();
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_WEATHER + "\\.([^\\.]+)");
		if(singleParam != null) {
			List<WeatherRecord> items = WeatherManager.g().search(singleParam);
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_PHONE_MOBILE + "(.+?)");
		if(singleParam != null) {
			String number = StrUtil.takeDigitsOnly(singleParam);

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
				
		singleParam = parseParam(KEY_TRANSLATE + "\\s+(.+?)");
		if(singleParam != null) {
			List<MexObject> items = getTranslation(singleParam);
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_DICTONARY + "\\s+(.+?)");
		if(singleParam != null) {
			List<MexObject> items = lookupDictionary(singleParam);
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
		
		singleParam = parseParam(KEY_TULING_ASK + "(.+?)");
		if(singleParam != null) {
			String key = g().getUserValueOf("tuling.key", "e8c190a005adc401867efd1ad2602f70");
			Extractor<MexObject> mike = new TulingExtractor(key, singleParam);
			mike.process();
			String tulingInChinese = StrUtil.utf8ToWhatever("\\uE59BBE\\uE781B5");
			export(tulingInChinese + ": " + mike.getMexItem());
			
			return true;
		}
		
		singleParam = parseParam(KEY_ZHIHU_ASK + "(.+?)");
		if(singleParam != null) {
			Extractor<ZhihuRecord> mike = new ZhihuSearchExtractor(singleParam);
			mike.process();
			export(mike.getMexItems());
			
			return true;
		}
		
		params = parseParams(KEY_BAIDU_BAIKE_SUMMARY + "\\s(\\*?)(.+?)");
		if(params != null) {
			boolean withOtherSameNames = !params[0].isEmpty() || OptionUtil.readBoolean(options, "all", false);
			String keywordOrUrl = params[1];
			List<MexObject> items = BaiduExtractorManager.g().fetchBaiduSummary(keywordOrUrl, withOtherSameNames);
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_WIKI_SUMMARY + "\\s(.+?)");
		if(singleParam != null) {
			Extractor<MexObject> mike = new WikiSummaryExtractor(singleParam);
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
		
		singleParam = parseParam(KEY_RSS + "\\s(.+?)");
		if(singleParam != null) {
			Object result = ObjectUtil.execute(sourceOfRss(), "readAllRss", new Class[0], new Object[0]);
			List<MexObject> items = (List<MexObject>)result;
			items = CollectionUtil.reverseOrder(CollectionUtil.filter(items, singleParam));
			export(items);
			
			return true;
		}
		
		regex = KEY_RSS + "\\.(.+?)";
		singleParam = parseParam(regex);
		if(singleParam != null) {
			Object result = ObjectUtil.execute(sourceOfRss(), "fetchRssByType", new Class[]{String.class}, new Object[]{singleParam});
			List<MexObject> items = (List<MexObject>)result;
			items = CollectionUtil.reverseOrder(items);
			
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_JAR + "\\s+(.+?)");
		if(singleParam != null) {
			Extractor<MexObject> mike = new FindJarExtractor(singleParam);
			mike.process();
			export(mike.getMexItems());
			
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
