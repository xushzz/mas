package com.sirap.geek;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.thirdparty.ShiroHelper;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.LocaleUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.MatrixUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.SecurityUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.MexItemsFetcher;
import com.sirap.geek.domain.AsciiRecord;
import com.sirap.geek.domain.CharsetCode;
import com.sirap.geek.manager.GeekManager;
	
public class CommandXCode extends CommandBase {

	private static final String KEY_ASCII_SHORT = "asc";
	private static final String KEY_TO_BASE64 = "t64";
	private static final String KEY_FROM_BASE64 = "f64";
	private static final String KEY_DIGEST_ALGORITHMS = "(SM3|SHA|SHA1|SHA224|SHA256|SHA512|MD2|MD5)";
	private static final String KEY_URL_ENCODE = "url";
	private static final String KEY_URL_DECODE = "urx";
	private static final String KEY_ENCODE = "cde";
	private static final String KEY_DECODE = "cdx";
	private static final String KEY_ISO = "iso";
	private static final String KEY_CURRENCY = "ccy";
	private static final String KEY_MONTHS = "mon";
	private static final String KEY_WEEKS = "wee";

	public boolean handle() {
		
		flag = searchAndProcess(KEY_ASCII_SHORT, new MexItemsFetcher<MexItem>() {
			
			@Override
			public void handle(List<MexItem> items) {
				exportMatrix(items);
			}
			
			@Override
			public List<MexItem> body() {
				header = AsciiRecord.COLUMNS;
				footer = AsciiRecord.COLUMNS;
				return GeekManager.g().asciiAll();
			}
		});
		if(flag) return true;
		
		if(is(KEY_ASCII_SHORT)) {
			List<MexItem> items = Lists.newArrayList(AsciiRecord.COLUMNS);
			int[][] ranges= {{'0', '9'}, {'A', 'Z'}, {'a', 'z'}};
			for(int i = 0; i < ranges.length; i++) {
				int[] range = ranges[i];
				items.addAll(GeekManager.g().ascii(range));
			}
			items.add(AsciiRecord.COLUMNS);
			
			exportMatrix(items);
		}
		
		solo = parseParam(KEY_TO_BASE64 + "\\s(.+?)");
		if(solo != null) {
			String result = XCodeUtil.toBase64(solo);
			export(result);
			
			return true;
		}
		
		solo = parseParam(KEY_FROM_BASE64 + "\\s(.+?)");
		if(solo != null) {
			String result = XCodeUtil.fromBase64(solo);
			export(result);
			
			return true;
		}

		params = parseParams(KEY_DIGEST_ALGORITHMS + "\\s(.+?)");
		if(params != null) {
			String temp = params[0];
			String what = params[1];
			String algo = temp;

			if(StrUtil.isRegexMatched("SHA\\d{3}", algo)) {
				StringBuffer sb = new StringBuffer(algo);
				sb.insert(3, "-");
				algo = sb.toString();
			}
			
			String salt = OptionUtil.readString(options, "s");
			int count = OptionUtil.readIntegerPRI(options, "i", 1);
			
			List<String> whatsaltcount = StrUtil.splitByRegex(what, "\\s+");
			if(!whatsaltcount.isEmpty()) {
				what = whatsaltcount.get(0);
				if(whatsaltcount.size() > 1) {
					salt = whatsaltcount.get(1);
					if(whatsaltcount.size() > 2) {
						String tempCount = whatsaltcount.get(2);
						count = MathUtil.toInteger(tempCount, count);
					}
				}
			}
			
			File normal = parseFile(what);
			boolean isFile = normal != null;
			String result = null;
			
			
			if(salt != null) {
				Object source = isFile ? normal : what;
				if(StrUtil.equals(salt, ".")) {
					salt = null;
				}
				result = ShiroHelper.hash(algo, source, salt, count);
			} else {
				if(isFile) {
					result = SecurityUtil.digestFile(normal.getAbsolutePath(), algo);
				} else {
					result = SecurityUtil.digest(what, algo);
				}
			}
			
			String prefix = "";
			if(isFile) {
				prefix = "File, ";
			}
			
			List<String> items = new ArrayList<>();
			if(salt != null) {
				items.add("salt: " + salt);
			}
			if(count > 1) {
				items.add("times: " + count);
			}
			items.add(result);
			items.add(prefix + algo.toUpperCase() + " generates " + result.length() + " chars.");
			
			export(items);
			return true;
		}
		
		solo = parseParam(KEY_URL_ENCODE + "\\s(.+?)");
		if(solo != null) {
			String charset = g().getCharsetInUse();
			String value = XCodeUtil.urlEncode(solo, charset);
			C.pl("Encode with charset: " + charset);
			export(value);
			
			return true;
		}
		
		solo = parseParam(KEY_URL_DECODE + "\\s(.+?)");
		if(solo != null) {
			String charset = g().getCharsetInUse();
			String value = XCodeUtil.urlDecode(solo, charset);
			C.pl("Decode with charset: " + charset);
			export(value);
			
			return true;
		}
		
		if(is(KEY_ENCODE + KEY_2DOTS)) {
			List<CharsetCode> items = GeekManager.g().allCodingNames();
			export(items);
			
			return true;
		}
		
		solo = parseParam(KEY_ENCODE + "-([\\S]+)");
		if(solo != null) {
			String criteria = solo;
			List<CharsetCode> codes = GeekManager.g().searchCharsetNames(criteria);
			
			List<String> charsets = new ArrayList<>();
			for(CharsetCode mexCode : codes) {
				String codeName = mexCode.getName();
				charsets.add(codeName);
			}
			
			export(codes);
			
			return true;
		}
		
		solo = parseParam(KEY_ENCODE + "\\s(.+?)");
		if(solo != null) {
			List<String> items = GeekManager.g().encodeStringByUnicodeUTF8GBK(solo);
			export(items);
			return true;
		}
		
		params = parseParams(KEY_ENCODE + "-([\\S]+)\\s(.+?)");
		if(params != null) {
			String criteria = params[0];
			String content = params[1];
			List<CharsetCode> codes = GeekManager.g().searchCharsetNames(criteria);
			
			List<String> charsets = new ArrayList<>();
			for(CharsetCode mexCode : codes) {
				String codeName = mexCode.getName();
				charsets.add(codeName);
			}
			List<String> items = GeekManager.g().encodeStringByCharset(content, charsets);
			export(items);
			
			return true;
		}
		
		solo = parseParam(KEY_DECODE + "\\s(.+?)");
		if(solo != null) {
			List<String> items = GeekManager.g().decodeStringByUnicodeUTF8GBK(solo);
			export(items);
			return true;
		}
		
		params = parseParams("([a-z0-9\\-_]{3,30})\\s(.+?)");
		if(params != null) {
			String accurateCharset = "^" + params[0] + "$";
			List<CharsetCode> mexCodes = GeekManager.g().searchCharsetNames(accurateCharset);
			if(mexCodes.size() == 1) {
				String code = mexCodes.get(0).getName();
				String content = params[1];
				String value = XCodeUtil.replaceHexChars(content, code);
				export(value);
				
				return true;
			}
		}
		
		flag = searchAndProcess(KEY_MONTHS, new MexItemsFetcher<MexItem>() {
			
			@Override
			public void handle(List<MexItem> items) {
				exportMatrix(items);
			}
			
			@Override
			public List<MexItem> body() {
				return LocaleUtil.getMonths(LocaleUtil.LOCALES);
			}
		});
		if(flag) return true;
		
		flag = searchAndProcess(KEY_WEEKS, new MexItemsFetcher<MexItem>() {
			
			@Override
			public void handle(List<MexItem> items) {
				exportMatrix(items);
			}
			
			@Override
			public List<MexItem> body() {
				return LocaleUtil.getWeeks(LocaleUtil.LOCALES);
			}
		});
		if(flag) return true;
		
		if(is(KEY_ISO)) {
			boolean inMatrix;
			Map<String, Set<String>> mars;
			if(OptionUtil.readBooleanPRI(options, "l", false)) {
				inMatrix = OptionUtil.readBooleanPRI(options, "mat", false);
				mars = LocaleUtil.groupByLangs();
			} else {
				inMatrix = OptionUtil.readBooleanPRI(options, "mat", true);
				mars = LocaleUtil.groupByCountries();
			}
			Iterator<String> it = mars.keySet().iterator();
			List<ValuesItem> list = Lists.newArrayList();
			while(it.hasNext()) {
				String key = it.next();
				Set<String> va = mars.get(key);
				String countries = StrUtil.connectWithCommaSpace(Lists.newArrayList(va));
				list.add(ValuesItem.of(key, va.size(), countries));
			}
			
			Collections.sort(list, new Comparator<ValuesItem>() {

				@Override
				public int compare(ValuesItem a, ValuesItem b) {
					int countA = ((Integer)a.getByIndex(1)).intValue();
					int countB = ((Integer)b.getByIndex(1)).intValue();
					
					return countB - countA;
				}
			});

			if(inMatrix) {
				exportMatrix(list);
			} else {
				export(list);
			}
			
			return true;
		}
		
		flag = searchAndProcess(KEY_CURRENCY, new MexItemsFetcher<ValuesItem>() {
			
			@Override
			public void handle(List<ValuesItem> items) {
				exportMatrix(items);
			}
			
			@Override
			public List<ValuesItem> body() {
				List<Locale> localesInColumns = LocaleUtil.langsOf(g().getUserValueOf("iso.locales"));
				ValuesItem vi = ValuesItem.of("Code", "Symbol");
				
				for(Locale kid : localesInColumns) {
					vi.add(kid.getDisplayName());
		    	}
				
				header = vi;

				return LocaleUtil.getAllCurrencies(localesInColumns);
			}
		});
		if(flag) return true;

		flag = searchAndProcess(KEY_ISO, new MexItemsFetcher<ValuesItem>() {
			@Override
			public void handle(List<ValuesItem> items) {
				exportMatrix(items);
			}
			@Override
			public List<ValuesItem> body() {
				List<Locale> localesInColumns = LocaleUtil.langsOf(g().getUserValueOf("iso.locales"));
				
				ValuesItem vi = ValuesItem.of("Locale", "Code");
				
				for(Locale kid : localesInColumns) {
					vi.add(kid.getDisplayLanguage());
					vi.add(kid.getDisplayCountry());
		    	}
				
				header = vi;
				footer = header;
				
				return LocaleUtil.getLocaleRecords(localesInColumns);
			}
		});
		if(flag) return true;
	
		solo = parseParam(KEY_ISO + "=\\s*(.+?)");
		if(solo != null) {
			List<Locale> localesInColumns = LocaleUtil.langsOf(g().getUserValueOf("iso.locales"));
			List<ValuesItem> mexLocales = LocaleUtil.getLocaleRecords(localesInColumns);
			
			ValuesItem goodItem = null;
			List<ValuesItem> matchedItems = Lists.newArrayList();
			String[] arr = {"^" + solo + "$", solo};
			for(String criteria : arr) {
				matchedItems = Colls.filter(mexLocales, criteria);
				if(matchedItems.size() == 1) {
					goodItem = matchedItems.get(0);
					break;
				}
			}

			if(goodItem != null) {
				g().setLocale(LocaleUtil.of(goodItem.getByIndex(0) + ""));
				C.pl2("Locale set as " + goodItem.toPrint("c=  "));
			} else {
				if(EmptyUtil.isNullOrEmpty(matchedItems)) {
					C.pl("[" + solo + "] is not a valid locale, please check from:");
					C.listSome(mexLocales, 7);
				} else {
					C.pl("[" + solo + "] is not a valid locale, do you mean one of this?");
					exportMatrix(MatrixUtil.matrixOf(matchedItems));
				}
			}
			
			return true;
		}
				
		return false;
	}
}
