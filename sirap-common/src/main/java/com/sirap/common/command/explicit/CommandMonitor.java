package com.sirap.common.command.explicit;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sirap.basic.domain.MexFile;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.TrumpUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.component.KeysReader;
import com.sirap.common.domain.CommandRecord;
import com.sirap.common.domain.InputRecord;
import com.sirap.common.domain.LoginRecord;
import com.sirap.common.extractor.RemoteSecurityExtractor;
import com.sirap.common.framework.Konfig;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.target.TargetPDF;
import com.sirap.common.manager.CommandHistoryManager;
import com.sirap.common.manager.LoginHistoryManager;

public class CommandMonitor extends CommandBase {

	private static final String KEY_KEYS_READER = "k";
	private static final String KEY_KEYS_CONFIG = "kc";
	private static final String KEY_SYSTEM_CONFIG = "sc";
	private static final String KEY_NODES_COFIG = "n..";
	private static final String KEY_COMMAND_HISTORY = "\\\\";
	private static final String KEY_LOGIN_HISTORY = "lh";
	private static final String KEY_SECURITY_ENCODE = "jiami";
	private static final String KEY_SECURITY_DECODE = "jiemi";
	private static final String KEY_LOGIN_HISTORY_DISTRIBUTION = "ld";
	private static final String KEY_ECHO = "ah";

	private static final int[] CH_cellsWidth = {12, 38};
	private static final int[] CH_cellsAlign = {0, 0};
	private static final PDFParams CH_PDF_PARAMS = new PDFParams(CH_cellsWidth, CH_cellsAlign);
	
	private static final int[] LH_cellsWidth = {3, 1, 3};
	private static final int[] LH_cellsAlign = {0, 1, 2};
	private static final PDFParams LH_PDF_PARAMS = new PDFParams(LH_cellsWidth, LH_cellsAlign);
	
	@Override
	public boolean handle() {
		solo = parseSoloParam(KEY_ECHO + "\\s(.+?)");
		if(solo != null) {
			List<String> sysProps = new ArrayList(System.getProperties().entrySet());
			List<String> sysEnvis = new ArrayList(System.getenv().entrySet());
			
			String criteria = solo;
			if(criteria.equalsIgnoreCase("-s")) {
				export(sysProps);
				return true;
			} else if(criteria.equalsIgnoreCase("-e")) {
				export(sysEnvis);
				return true;
			}
			
			List<String> records = new ArrayList<String>();
			records.addAll(sysProps);
			records.addAll(sysEnvis);
			
			if(StrUtil.isRegexMatched("-(se|es)", criteria)) {
				export(records);
			} else {
				if(!records.isEmpty()) {
					export(CollUtil.filterMix(records, criteria, isCaseSensitive()));
				} else {
					export(StrUtil.split(criteria, '\\'));
				}
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
					String content = IOUtil.readFileWithRegularLineSeparator(filePath);
					String text = TrumpUtil.encodeBySIRAP(content, passcode);
					C.pl("File, SIRAP generates " + text.length() + " chars. ");
					export(text);
				} else {
					String text = TrumpUtil.encodeBySIRAP(param, passcode);
					C.pl("SIRAP generates " + text.length() + " chars. ");
					export(text);
				}
				
				return true;
			} else {
				String text = TrumpUtil.encodeBySIRAP(param, passcode);
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
					String content = IOUtil.readFileWithoutLineSeparator(filePath);
					String text = TrumpUtil.decodeBySIRAP(content, passcode, true);
					List<String> items = StrUtil.split(text, '\n');
					export(items);
				} else {
					XXXUtil.alert("Not a text file: " + filePath);
				}
				
				return true;
			} else {
				String text = TrumpUtil.decodeMixedTextBySIRAP(param, passcode, true);
				if(StrUtil.equals(param, text)) {
					text = TrumpUtil.decodeBySIRAP(param, passcode, true);
				} 

				List<String> items = StrUtil.split(text, '\n');
				export(items);
			}
			
			return true;
		}

		if(g().isHistoryEnabled()) {
			solo = parseSoloParam(KEY_LOGIN_HISTORY + "\\s(.+?)");
			if(solo != null) {
				List<LoginRecord> records = LoginHistoryManager.g().search(solo);
				if(target instanceof TargetPDF) {
					target.setParams(LH_PDF_PARAMS);
					List<List<String>> items = CollUtil.items2PDFRecords(records);
					export(items);
				} else {
					export(CollUtil.items2PrintRecords(records));
				}
				return true;
			}

			params = parseParams(KEY_LOGIN_HISTORY + "(|\\d{1,4})");
			if(params != null) {
				int count = MathUtil.toInteger(params[0], 20);
				List<LoginRecord> records = LoginHistoryManager.g().getLoginRecords(count);
				if(target instanceof TargetPDF) {
					target.setParams(LH_PDF_PARAMS);
					List<List<String>> items = CollUtil.items2PDFRecords(records);
					export(items);
				} else {
					export(CollUtil.items2PrintRecords(records));
				}
				
				return true;
			}
			
			if (is(KEY_LOGIN_HISTORY + KEY_2DOTS)) {
				List<LoginRecord> records = LoginHistoryManager.g().getAllInputRecords();
				if(target instanceof TargetPDF) {
					target.setParams(LH_PDF_PARAMS);
					List<List<String>> items = CollUtil.items2PDFRecords(records);
					export(items);
				} else {
					export(CollUtil.items2PrintRecords(records));
				}
				
				return true;
			}
			
			solo = parseSoloParam(KEY_COMMAND_HISTORY + "(.*)");
			if(solo != null) {
				noCollect();
				List<InputRecord> records = CommandHistoryManager.g().getAllRecords();
				if(!OptionUtil.readBooleanPRI(options, "all", false)) {
					if(EmptyUtil.isNullOrEmpty(solo)) {
						records = CommandHistoryManager.g().getNRecords(20);
					} else {
						if(OptionUtil.readBooleanPRI(options, "n", false)) {
							int count = MathUtil.toInteger(solo, 20);
							records = CollUtil.last(records, count);
						} else {
							records = CommandHistoryManager.g().search(solo);
						}
					}
				}
				
				if(target instanceof TargetPDF) {
					target.setParams(CH_PDF_PARAMS);
					List<List<String>> items = CollUtil.items2PDFRecords(records);
					export(items);
				} else {
					export(CollUtil.items2PrintRecords(records));
				}
				
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

			solo = parseSoloParam(KEY_LOGIN_HISTORY_DISTRIBUTION + "\\s(.+?)");
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
		
		params = parseParams(KEY_KEYS_CONFIG + "(|\\s+(.*?))");
		if(params != null) {
			String criteria = params[1];
			List<String> list = FileUtil.readResourceFilesIntoList(Konfig.KEYS_FILE);
			
			export(CollUtil.filterMix(list, criteria, isCaseSensitive()));
			
			return true;
		}
		
		params = parseParams(KEY_SYSTEM_CONFIG + "(|\\s+(.*?))");
		if(params != null) {
			String criteria = params[1];
			List<String> list = FileUtil.readResourceFilesIntoList(SimpleKonfig.KONFIG_FILE);
			List<String> recordsExtra = FileUtil.readResourceFilesIntoList(Konfig.EXTRA_FILE);
			list.addAll(recordsExtra);
			
			export(CollUtil.filterMix(list, criteria, isCaseSensitive()));
			
			return true;
		}
		
		if(is(KEY_NODES_COFIG)) {
			List<CommandRecord> items = g().getCommandNodes();
			export(items);
			
			return true;
		}

		solo = parseSoloParam(KEY_KEYS_READER + "\\s(.+?)");
		if(solo != null) {
			List<String> folders = StrUtil.splitByRegex(solo);
			String methods = g().getValueOf("keys.reader.methods");
			List<String> methodList = StrUtil.splitByRegex(methods);
			List<String> keys = new ArrayList<String>();
			if(!methodList.isEmpty()) {
				List<MexFile> mexItems = FileUtil.scanFolders(folders, false, ".java$");
				for(MexFile item : mexItems) {
					KeysReader pycelle = new KeysReader(item.getFile(), methodList);
					keys.addAll(pycelle.readKeysFromFile(g().getCharsetInUse()));
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
		
		if(is(KEY_SECURITY_ENCODE +KEY_SECURITY_DECODE)) {
			noCollect();
			export(g().getSecurityPasscode());
			
			return true;
		}
		
		return false;
	}
}