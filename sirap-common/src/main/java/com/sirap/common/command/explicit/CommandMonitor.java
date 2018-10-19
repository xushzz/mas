package com.sirap.common.command.explicit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.thirdparty.TrumpHelper;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.SatoUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.component.KeysReader;
import com.sirap.common.component.MexItemsFetcher;
import com.sirap.common.domain.CommandRecord;
import com.sirap.common.domain.InputRecord;
import com.sirap.common.domain.LoginRecord;
import com.sirap.common.extractor.RemoteSecurityExtractor;
import com.sirap.common.framework.Konfig;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.target.TargetPdf;
import com.sirap.common.manager.CommandHistoryManager;
import com.sirap.common.manager.LoginHistoryManager;

public class CommandMonitor extends CommandBase {

	private static final String KEY_KEYS_READER = "k";
	private static final String KEY_KEYS_CONFIG = "kc";
	private static final String KEY_SYSTEM_CONFIG = "sc";
	private static final String KEY_COMAMND_NODES = "nz";
	private static final String KEY_COMMAND_HISTORY = ";";
	private static final String KEY_LOGIN_HISTORY = "lh";
	private static final String KEY_SECURITY_ENCODE = "jiami";
	private static final String KEY_SECURITY_DECODE = "jiemi";
	private static final String KEY_LOGIN_HISTORY_DISTRIBUTION = "ld";
	private static final String KEY_ECHO = "echo";

	@Override
	public boolean handle() {
		//Satoshi Nakamoto
		solo = parseParam(KEY_ECHO + "(|\\s.*?)");
		if(solo != null) {
			if(StrUtil.equals(solo, "path")) {
				//do whole match if and only PATH
				solo = "^" + solo + "$";
			}
			List<TypedKeyValueItem> items = Lists.newArrayList();
			boolean showAll = OptionUtil.readBooleanPRI(options, "a", false);
			if(showAll || OptionUtil.readBooleanPRI(options, "s", true)) {
				List<TypedKeyValueItem> sato = SatoUtil.SYSTEM_PROPERTIES;
				if(!EmptyUtil.isNullOrEmpty(solo)) {
					sato = Colls.filter(sato, solo);
				}
				items.addAll(sato);
			}
			if(showAll || OptionUtil.readBooleanPRI(options, "e", true)) {
				List<TypedKeyValueItem> sato = SatoUtil.ENVIRONMENT_VARIABLES;
				if(!EmptyUtil.isNullOrEmpty(solo)) {
					sato = Colls.filter(sato, solo);
				}
				items.addAll(sato);
			}
			if(showAll || OptionUtil.readBooleanPRI(options, "u", false)) {
				List<TypedKeyValueItem> sato = g().getUserProps().listOf();
				if(!EmptyUtil.isNullOrEmpty(solo)) {
					sato = Colls.filter(sato, solo);
				}
				items.addAll(sato);
			}
			if(showAll || OptionUtil.readBooleanPRI(options, "i", false)) {
				List<TypedKeyValueItem> sato = g().getInnerProps().listOf();
				if(!EmptyUtil.isNullOrEmpty(solo)) {
					sato = Colls.filter(sato, solo);
				}
				items.addAll(sato);
			}
			Collections.sort(items);
			
			Boolean showValueItemInLines = OptionUtil.readBoolean(options, "l");
			if(showValueItemInLines == null) {
				showValueItemInLines = items.size() == 1;
			}
			if(showValueItemInLines) {
				List<String> gras = Lists.newArrayList();
				String sepStr = OptionUtil.readString(options, "s");
				String sep = File.pathSeparator;
				if(!EmptyUtil.isNullOrEmpty(sepStr)) {
					sep = sepStr;
				}
				for(TypedKeyValueItem item : items) {
					List<String> sons = item.valueItemInLines(sep);
					if(sons.size() > 1) {
						if(OptionUtil.readBooleanPRI(options, "sort", true)) {
							Colls.sortIgnoreCase(sons);
						}
						gras.add(item.toPrint(OptionUtil.mergeOptions("-v", options)));
						gras.addAll(sons);
					} else {
						gras.add(item.toPrint(options));
					}
				}
				
				export(gras);
			} else {
				export(items);
			}
			
			return true;
		}
		
		params = parseParams(KEY_SECURITY_ENCODE + "(|-([a-z0-9]+))\\s(|.+?)");
		if(params != null) {
			noCollect();
			String passcode = params[1];
			if(passcode == null) {
				passcode = SimpleKonfig.g().getSecurityPasscode();				
			}
			
			String param = params[2];
			File file = parseFile(param);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					String content = IOUtil.readString(filePath, charset(), Konstants.NEWLINE);
					String text = TrumpHelper.encodeBySIRAP(content, passcode);
					C.pl("File, SIRAP generates " + text.length() + " chars. ");
					export(text);
				} else {
					String text = TrumpHelper.encodeBySIRAP(param, passcode);
					C.pl("SIRAP generates " + text.length() + " chars. ");
					export(text);
				}
				
				return true;
			} else {
				String text = TrumpHelper.encodeBySIRAP(param, passcode);
				C.pl("SIRAP generates " + text.length() + " chars. ");
				export(text);
			}
			
			return true;
		}
		
		params = parseParams(KEY_SECURITY_DECODE + "(|-([a-z0-9]+))\\s(|.+?)");
		if(params != null) {
			noCollect();
			String passcode = params[1];
			if(passcode == null) {
				passcode = g().getSecurityPasscode();				
			}
			
			String param = params[2];
			File file = parseFile(param);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					String content = IOUtil.readString(filePath);
					String text = TrumpHelper.decodeBySIRAP(content, passcode, true);
					List<String> items = StrUtil.split(text, '\n');
					export(items);
				} else {
					XXXUtil.alert("Not a text file: " + filePath);
				}
				
				return true;
			} else {
				String text = TrumpHelper.decodeMixedTextBySIRAP(param, passcode, true);
				if(StrUtil.equals(param, text)) {
					text = TrumpHelper.decodeBySIRAP(param, passcode, true);
				} 

				List<String> items = StrUtil.split(text, '\n');
				export(items);
			}
			
			return true;
		}

		if(g().isHistoryEnabled()) {
			solo = parseParam(KEY_LOGIN_HISTORY + "\\s(.+?)");
			if(solo != null) {
				List<LoginRecord> records = LoginHistoryManager.g().search(solo);
				D.list(records);
				if(target instanceof TargetPdf) {
//					target.setParams(LH_PDF_PARAMS);
//					List<List<String>> items = Colls.items2PDFRecords(records);
//					export(records);
				}
				exportMatrix(records);
				return true;
			}

			params = parseParams(KEY_LOGIN_HISTORY + "(|\\d{1,4})");
			if(params != null) {
				int count = MathUtil.toInteger(params[0], 20);
				List<LoginRecord> records = LoginHistoryManager.g().getLoginRecords(count);
				export(records);
				
				return true;
			}
			
			if (is(KEY_LOGIN_HISTORY + KEY_2DOTS)) {
				List<LoginRecord> records = LoginHistoryManager.g().getAllInputRecords();
				target.setPdfCellWidths(3, 1, 3);

				exportMatrix(records);
				
				return true;
			}
			
			solo = parseParam(KEY_COMMAND_HISTORY + "(.*)");
			if(solo != null) {
				noCollect();
				List<InputRecord> records = CommandHistoryManager.g().getAllRecords();
				if(!OptionUtil.readBooleanPRI(options, "all", false)) {
					if(EmptyUtil.isNullOrEmpty(solo)) {
						records = CommandHistoryManager.g().getNRecords(20);
					} else {
						if(OptionUtil.readBooleanPRI(options, "n", false)) {
							int count = MathUtil.toInteger(solo, 20);
							records = Colls.last(records, count);
						} else {
							records = CommandHistoryManager.g().search(solo);
						}
					}
				}
				
				target.setPdfCellWidths(12, 38);
				
				exportMatrix(records);
				
				return true;
			}
			
			if(is(KEY_LOGIN_HISTORY_DISTRIBUTION)) {
				String yearMonth = DateUtil.displayDate(new Date(), "yyyy-MM");
				List<LoginRecord> records = LoginHistoryManager.g().search(yearMonth);
				List<String> list = LoginHistoryManager.g().displayDistribution(records);
				setIsPrintTotal(false);
				export(list);
				
				return true;
			}

			solo = parseParam(KEY_LOGIN_HISTORY_DISTRIBUTION + "\\s(.+?)");
			if(solo != null) {
				List<LoginRecord> records = LoginHistoryManager.g().search(solo);
				List<String> list = LoginHistoryManager.g().displayDistribution(records);
				setIsPrintTotal(false);
				export(list);
				
				return true;
			}
			
			if(is(KEY_LOGIN_HISTORY_DISTRIBUTION + KEY_2DOTS)) {
				List<String> list = LoginHistoryManager.g().displayDistribution();
				setIsPrintTotal(false);
				export(list);
				
				return true;
			}
		}

		flag = searchAndProcess(KEY_KEYS_CONFIG, new MexItemsFetcher<MexItem>() {
			@Override
			public void handle(List<MexItem> items) {
				exportMatrix(items);
			}
			
			@Override
			public List<MexItem> body() {
				List<String> list = IOUtil.readLinesFromStreamByClassLoader(Konfig.KEYS_FILE, Konstants.CODE_UTF8);
				Collections.sort(list);
				List<MexItem> items = Lists.newArrayList();
				target.setPdfCellWidths(3, 1, 5);
				target.setPdfCellAligns(0, 1, 0);
				
				for(String line : list) {
					String regex = "(.+?)\\s(\\d+)\\s(.+)";
					Matcher ma = StrUtil.createMatcher(regex, line);
					while(ma.find()) {
						items.add(ValuesItem.of(ma.group(1), ma.group(2), ma.group(3)));
					}
				}
				
				return items;
			}
		});
		if(flag) return true;
		
		params = parseParams(KEY_SYSTEM_CONFIG + "(|\\s+(.*?))");
		if(params != null) {
			String criteria = params[1];
			List<String> list = IOUtil.readLinesFromStreamByClassLoader(Konfig.KONFIG_FILE, Konstants.CODE_UTF8);
			export(Colls.filterMix(list, criteria, isCaseSensitive()));
			
			return true;
		}
		
//		if(is(KEY_NODES_CONFIG)) {
//			List<CommandRecord> items = g().getCommandNodes();
//			export(items);
//			
//			return true;
//		}
//		
		flag = searchAndProcess(KEY_COMAMND_NODES, new MexItemsFetcher<CommandRecord>() {
			@Override
			public void handle(List<CommandRecord> items) {
				exportMatrix(items);
			}
			
			@Override
			public List<CommandRecord> body() {
				return g().getCommandNodes();
			}
		});
		if(flag) return true;

		solo = parseParam(KEY_KEYS_READER + "\\s(.+?)");
		if(solo != null) {
			List<String> folders = StrUtil.splitByRegex(solo);
			String methods = g().getValueOf("keys.reader.methods");
			List<String> methodList = StrUtil.splitByRegex(methods);
			List<String> keys = new ArrayList<String>();
			if(!methodList.isEmpty()) {
				List<MexFile> mexItems = FileUtil.scanFolders(folders, false, ".java$");
				for(MexFile item : mexItems) {
					KeysReader pycelle = new KeysReader(item.getFile(), methodList);
					keys.addAll(pycelle.readKeysFromFile());
				}
			}
			
			if(!EmptyUtil.isNullOrEmpty(keys)) {
				export(keys);
			}
			
			return true;
		}
		
		if(is("guoqi")) {
			noCollect();
			export(RemoteSecurityExtractor.URL_SECURITY);
			
			return true;
		}
		
		if(is(KEY_SECURITY_ENCODE + KEY_SECURITY_DECODE)) {
			noCollect();
			export(g().getSecurityPasscode());
			
			return true;
		}
		
		return false;
	}
}