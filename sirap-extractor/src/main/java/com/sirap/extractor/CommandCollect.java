package com.sirap.extractor;

import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.domain.WeatherRecord;
import com.sirap.common.extractor.Extractor;
import com.sirap.common.framework.command.target.TargetPDF;
import com.sirap.extractor.domain.ZhihuRecord;
import com.sirap.extractor.impl.EnglishDictionaryExtractor;
import com.sirap.extractor.impl.EnglishTranslationExtractor;
import com.sirap.extractor.impl.MobilePhoneLocationExtractor;
import com.sirap.extractor.impl.TulingExtractor;
import com.sirap.extractor.impl.ZhihuExtractor;
import com.sirap.extractor.manager.ForexManager;
import com.sirap.extractor.manager.WeatherManager;

public class CommandCollect extends CommandBase {

	private static final String KEY_WEATHER = "w";	
	private static final String KEY_PHONE_MOBILE = "@";
	private static final String KEY_DICTONARY = "ia";
	private static final String KEY_TRANSLATE = "i";
	private static final String KEY_FOREX = "\\$([a-z]{3})" + Konstants.REGEX_FLOAT + "(|/|[a-z,]+)";
	private static final String KEY_TULING_ASK = "tl\\*";
	private static final String KEY_ZHIHU_ASK = "\\*";

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
			List<MexedObject> items = getTranslation(singleParam);
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_DICTONARY + "\\s+(.+?)");
		if(singleParam != null) {
			List<MexedObject> items = lookupDictionary(singleParam);
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
			Extractor<MexedObject> mike = new TulingExtractor(key, singleParam);
			mike.process();
			String tulingInChinese = StrUtil.utf8ToWhatever("\\uE59BBE\\uE781B5");
			export(tulingInChinese + ": " + mike.getMexItem());
			
			return true;
		}
		
		singleParam = parseParam(KEY_ZHIHU_ASK + "(.+?)");
		if(singleParam != null) {
			Extractor<ZhihuRecord> mike = new ZhihuExtractor(singleParam);
			mike.process();
			export(mike.getMexItems());
			
			return true;
		}
		
		return false;
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
	
	public static List<MexedObject> getTranslation(String word) {
		Extractor<MexedObject> frank = new EnglishTranslationExtractor(word);
		frank.process();
		List<MexedObject> items = frank.getMexItems();
		
		return items;
	}
	
	public static List<MexedObject> lookupDictionary(String word) {
		Extractor<MexedObject> frank = new EnglishDictionaryExtractor(word);
		frank.process();
		List<MexedObject> items = frank.getMexItems();
		
		return items;
	}
}
