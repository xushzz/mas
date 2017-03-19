package com.sirap.common.command.explicit;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sirap.basic.component.MexedMap;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.component.KeysReader;
import com.sirap.common.domain.CommandRecord;
import com.sirap.common.domain.InputRecord;
import com.sirap.common.domain.LoginRecord;
import com.sirap.common.extractor.impl.RemoteSecurityExtractor;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.command.target.TargetPDF;
import com.sirap.common.manager.CommandHistoryManager;
import com.sirap.common.manager.LoginHistoryManager;
import com.sirap.security.MrTrump;

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
	
	public boolean handle() {
		String singleParam = parseParam(KEY_ECHO + "\\s(.+?)");
		if(singleParam != null) {
			Properties props = System.getProperties();
			MexedMap mapProps = IOUtil.createMexedMapByProperties(props);
			
			Map<String, String> envs = System.getenv();
			MexedMap mapEnvs = new MexedMap(envs);
			
			String temp = singleParam;
			if(temp.equalsIgnoreCase("s")) {
				List<String> items = mapProps.listEntries();
				items.add("processors=" + Runtime.getRuntime().availableProcessors());
				items.add("threads.copy=" + SimpleKonfig.g().getUserValueOf("threads.copy"));
				items.add("threads.download=" + SimpleKonfig.g().getUserValueOf("threads.download"));
				export(items);
				return true;
			} else if(temp.equalsIgnoreCase("e")) {
				List<String> items = mapEnvs.listEntries();
				export(items);
				return true;
			}
			
			List<String> records = new ArrayList<String>();
			records.addAll(mapProps.getValuesByKeyword(singleParam, true));
			records.addAll(mapEnvs.getValuesByKeyword(singleParam, true));
			
			if(records.isEmpty()) {
				records = StrUtil.split(temp, '\\');
			}
			
			export(records);
			return true;
		}
		
		String[] params = parseParams(KEY_SECURITY_ENCODE + "(|-([a-z0-9]+))\\s(|.+?)");
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
					String text = MrTrump.encodeBySIRAP(content, passcode);
					export(text);
				} else {
					XXXUtil.alert("Not a text file: " + filePath);
				}
				
				return true;
			} else {
				String text = MrTrump.encodeBySIRAP(param, passcode);
				export(text);
			}
			
			return true;
		}
		
		params = parseParams(KEY_SECURITY_DECODE + "(|-([a-z0-9]+))\\s(|.+?)");
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
					String content = IOUtil.readFileWithoutLineSeparator(filePath);
					String text = MrTrump.decodeBySIRAP(content, passcode, true);
					List<String> items = StrUtil.split(text, '\n');
					export(items);
				} else {
					XXXUtil.alert("Not a text file: " + filePath);
				}
				
				return true;
			} else {
				String text = MrTrump.decodeBySIRAP(param, passcode, true);
				List<String> items = StrUtil.split(text, '\n');
				export(items);
			}
			
			return true;
		}

		if(g().isHistoryEnabled()) {
			singleParam = parseParam(KEY_LOGIN_HISTORY + "\\s(.+?)");
			if(singleParam != null) {
				List<LoginRecord> records = LoginHistoryManager.g().search(singleParam);
				if(target instanceof TargetPDF) {
					target.setParams(LH_PDF_PARAMS);
					List<List<String>> items = CollectionUtil.items2PDFRecords(records);
					export(items);
				} else {
					export(CollectionUtil.items2PrintRecords(records));
				}
				return true;
			}

			params = parseParams(KEY_LOGIN_HISTORY + "(|\\d{1,4})");
			if(params != null) {
				int count = MathUtil.toInteger(params[0], 20);
				List<LoginRecord> records = LoginHistoryManager.g().getLoginRecords(count);
				if(target instanceof TargetPDF) {
					target.setParams(LH_PDF_PARAMS);
					List<List<String>> items = CollectionUtil.items2PDFRecords(records);
					export(items);
				} else {
					export(CollectionUtil.items2PrintRecords(records));
				}
				
				return true;
			}
			
			if (is(KEY_LOGIN_HISTORY + KEY_2DOTS)) {
				List<LoginRecord> records = LoginHistoryManager.g().getAllInputRecords();
				if(target instanceof TargetPDF) {
					target.setParams(LH_PDF_PARAMS);
					List<List<String>> items = CollectionUtil.items2PDFRecords(records);
					export(items);
				} else {
					export(CollectionUtil.items2PrintRecords(records));
				}
				
				return true;
			}
			
			params = parseParams("]" + KEY_COMMAND_HISTORY + "(\\d{1,4})");
			if(params != null) {
				noCollect();
				int count = MathUtil.toInteger(params[0], 20);
				List<InputRecord> records = CommandHistoryManager.g().getNRecords(count);
				if(target instanceof TargetPDF) {
					target.setParams(CH_PDF_PARAMS);
					List<List<String>> items = CollectionUtil.items2PDFRecords(records);
					export(items);
				} else {
					export(CollectionUtil.items2PrintRecords(records));
				}
				return true;
			}
			
			singleParam = parseParam("(])" + KEY_COMMAND_HISTORY + KEY_2DOTS);
			if(singleParam != null) {
				noCollect();
				List<InputRecord> records = CommandHistoryManager.g().getAllRecords();
				if(target instanceof TargetPDF) {
					target.setParams(CH_PDF_PARAMS);
					List<List<String>> items = CollectionUtil.items2PDFRecords(records);
					export(items);
				} else {
					export(CollectionUtil.items2PrintRecords(records));
				}
				
				return true;
			}
			
			singleParam = parseParam(KEY_COMMAND_HISTORY + "(.*)");
			if(singleParam != null) {
				noCollect();
				List<InputRecord> records = null;
				if(EmptyUtil.isNullOrEmpty(singleParam)) {
					records = CommandHistoryManager.g().getNRecords(20);
				} else {
					records = CommandHistoryManager.g().search(singleParam);
				}
				
				if(target instanceof TargetPDF) {
					target.setParams(CH_PDF_PARAMS);
					List<List<String>> items = CollectionUtil.items2PDFRecords(records);
					export(items);
				} else {
					export(CollectionUtil.items2PrintRecords(records));
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

			singleParam = parseParam(KEY_LOGIN_HISTORY_DISTRIBUTION + "\\s(.+?)");
			if(singleParam != null) {
				List<LoginRecord> records = LoginHistoryManager.g().search(singleParam);
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
			List<String> list = FileUtil.readResourceFilesIntoList(SimpleKonfig.KEYS_FILE);
			
			exportItems(list, criteria);
			
			return true;
		}
		
		params = parseParams(KEY_SYSTEM_CONFIG + "(|\\s+(.*?))");
		if(params != null) {
			String criteria = params[1];
			List<String> list = FileUtil.readResourceFilesIntoList(g().getSystemConfigFileName());
			List<String> recordsExtra = FileUtil.readResourceFilesIntoList(SimpleKonfig.EXTRA_FILE);
			list.addAll(recordsExtra);
			
			exportItems(list, criteria);
			
			return true;
		}
		
		if(is(KEY_NODES_COFIG)) {
			List<CommandRecord> items = g().getCommandNodes();
			export(items);
			
			return true;
		}

		singleParam = parseParam(KEY_KEYS_READER + "\\s(.+?)");
		if(singleParam != null) {
			List<String> folders = StrUtil.splitByRegex(singleParam);
			String methods = g().getValueOf("keys.reader.methods");
			List<String> methodList = StrUtil.splitByRegex(methods);
			String[] suffixes = {".java"};
			List<String> keys = new ArrayList<String>();
			if(!methodList.isEmpty()) {
				List<File> files = FileUtil.scanFolder(folders, 9, suffixes, false);
				for(File sourceFile : files) {
					KeysReader pycelle = new KeysReader(sourceFile, methodList);
					keys.addAll(pycelle.readKeysFromFile());
				}
			}
			
			if(!EmptyUtil.isNullOrEmpty(keys)) {
				export(keys);
			}
			
			return true;
		}
		
		if(is("guoqi")) {
			export(RemoteSecurityExtractor.URL_SECURITY);
			
			return true;
		}
		
		return false;
	}
}