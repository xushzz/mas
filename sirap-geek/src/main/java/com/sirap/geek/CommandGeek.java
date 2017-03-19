package com.sirap.geek;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.math.HiredDaysCalculator;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.util.CodeUtil;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.JsonUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.SecurityUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.extractor.Extractor;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.geek.domain.AsciiRecord;
import com.sirap.geek.domain.CharsetCode;
import com.sirap.geek.git.GithubIssuesExtractor;
import com.sirap.geek.jenkins.JenkinsBuildRecord;
import com.sirap.geek.jenkins.JenkinsManager;
import com.sirap.geek.maven.MavenManager;
	
public class CommandGeek extends CommandBase {

	private static final String KEY_PATH = "path";
	private static final String KEY_MVN = "mvn";
	private static final String KEY_DEPS = "deps";
	private static final String KEY_ISSUE = "iss";
	private static final String KEY_JENKINS = "jk";
	private static final String KEY_JSON = "js";
	private static final int KEY_JSON_MIN_LEN = 9;
	private static final String KEY_RAW_JSON = "rjs";
	private static final String KEY_MAXMIN = "maxmin";
	private static final String KEY_HIRED = "((hired|hdays)(|-))";
	private static final String KEY_ASCII_SHORT = "asc";
	private static final String KEY_ASCII_ALL = "ascii";
	private static final String KEY_ENCODE = "cd";
	private static final String KEY_TO_BASE64 = "t64";
	private static final String KEY_FROM_BASE64 = "f64";
	private static final String KEY_DIGEST_ALGORITHMS = "(SHA1|SHA-224|SHA224|SHA-256|SHA256|MD2|SHA|SHA-512|SHA512|MD5)";

	public boolean handle() {
		
		singleParam = parseParam(KEY_PATH + " (.*?)");
		if(singleParam != null) {
			List<String> items = IOUtil.echoPath();
			List<MexedObject> result = CollectionUtil.search(items, singleParam);
			
			export(result);
			
			return true;
		}

		if(is(KEY_PATH)) {
			List<String> items = IOUtil.echoPath();
			export(items);
			
			return true;
		}
		
		if(is(KEY_MVN)) {
			List<String> items = MavenManager.g().getMavenInfo();
			export(items);
			
			return true;
		}

		if(StrUtil.startsWith(command, KEY_MVN + " ")) {
			executeInternalCmd(command);
			
			return true;
		}
		
		singleParam = parseParam(KEY_DEPS + "\\s(.*?)");
		if(singleParam != null) {
			String filepath = null;
			File file = FileUtil.getIfNormalFile(singleParam);
			if(file != null) {
				filepath = file.getAbsolutePath();
			} else {
				File folder = FileUtil.getIfNormalFolder(singleParam);
				if(folder != null) {
					String temp = folder.getAbsolutePath() + File.separatorChar + "pom.xml";
					if(FileUtil.exists(temp)) {
						filepath = temp;
					}
				}
			}
			
			if(filepath != null) {
				List<String> result = MavenManager.g().getDependencies(filepath);
				export(result);

				return true;
			}
		}
		
		singleParam = parseParam(KEY_ISSUE + " ([A-Za-z0-9\\-_]+/[A-Za-z0-9\\-_]+)");
		if(singleParam != null) {
			Extractor<MexItem> frank = new GithubIssuesExtractor(singleParam);
			frank.process();
			List<MexItem> items = frank.getMexItems();
			export(items);
			
			return true;
			
		}
		
		if(is(KEY_JENKINS)) {
			String key = "jenkins.url";
			String url = SimpleKonfig.g().getUserValueOf(key);
			XXXUtil.nullCheck(url, key);
			
			export(url);
		}
		
		singleParam = parseParam(KEY_JENKINS + " ([A-Za-z0-9\\-_]+)");
		if(singleParam != null) {
			JenkinsBuildRecord record = JenkinsManager.g().getLatestBuildRecord(singleParam);
			export(record);
			
			return true;
		}
		
		params = parseParams(KEY_JENKINS + " ([A-Za-z0-9\\-_]+)\\.([1-9]\\d{0,3})");
		if(params != null) {
			String jobName = params[0];
			String numberStr = params[1];
			Integer buildNumber = MathUtil.toInteger(numberStr);
			List<JenkinsBuildRecord> records = JenkinsManager.g().getLatestKBuildRecords(jobName, buildNumber);
			export(records);
			
			return true;
		}
		
		params = parseParams(KEY_JENKINS + " ([A-Za-z0-9\\-_]+)#([1-9]\\d{0,3})");
		if(params != null) {
			String jobName = params[0];
			String numberStr = params[1];
			JenkinsBuildRecord record = JenkinsManager.g().getBuildRecordByNumber(jobName, numberStr);
			export(record);
			
			return true;
		}
		
		singleParam = parseParam(KEY_JSON + " " + KEY_HTTP);
		if(singleParam != null) {
			String source = IOUtil.readURL(singleParam);
			String text = JsonUtil.getPrettyText(source);
			export(text);
			
			return true;
		}
		
		if(command.length() >= KEY_JSON_MIN_LEN) {
			try {
				String tempJson = JsonUtil.getPrettyText(command);
				export(tempJson);
				return true;
			} catch (MexException ex) {
				//
			}
		}
		
		singleParam = parseParam(KEY_JSON + " (.+?)");
		if(singleParam != null) {
			File file = parseFile(singleParam);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					String source = IOUtil.readFileWithoutLineSeparator(filePath);
					String text = JsonUtil.getPrettyText(source);
					export(text);
				} else {
					XXXUtil.alert("Not a text file: " + filePath);
				}
				
				return true;
			} else {
				String text = JsonUtil.getPrettyText(singleParam);
				export(text);
			}
			
			return true;
		}
		
		singleParam = parseParam(KEY_RAW_JSON + " " + KEY_HTTP);
		if(singleParam != null) {
			String source = IOUtil.readURL(singleParam);
			String text = JsonUtil.getRawText(source);
			export(text);
			
			return true;
		}
		
		singleParam = parseParam(KEY_RAW_JSON + " (.+?)");
		if(singleParam != null) {
			File file = parseFile(singleParam);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					String source = IOUtil.readFileWithoutLineSeparator(filePath);
					String text = JsonUtil.getRawText(source);
					export(text);
				} else {
					XXXUtil.alert("Not a text file: " + filePath);
				}
				
				return true;
			} else {
				String text = JsonUtil.getRawText(singleParam);
				export(text);
			}
			
			return true;
		}
		
		params = parseParams(KEY_HIRED + " (.+?)");
		if(params != null) {
			File file = parseFile(params[3]);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					List<String> records = IOUtil.readFileIntoList(filePath);
					boolean byDays = StrUtil.equals("hdays", params[1]);
					boolean descend = StrUtil.equals("-", params[2]);

					HiredDaysCalculator cong = new HiredDaysCalculator(records);
					List<String> list = null;
					if(byDays) {
						list = cong.orderByHiredDays(descend);
					} else {
						list = cong.orderByHiredDate(descend);
					}
					
					setIsPrintTotal(false);
					export(list);
				} else {
					XXXUtil.alert("Not a text file: " + filePath);
				}
				
				return true;
			}
			
			return true;
		}
		
		if(is(KEY_ASCII_SHORT)) {
			List<MexItem> items = new ArrayList<>();
			MexedObject header = new MexedObject((AsciiRecord.getHeader()));
			items.add(header);
			
			int[][] ranges= {{'0', '9'}, {'A', 'Z'}, {'a', 'z'}};
			for(int i = 0; i < ranges.length; i++) {
				int[] range = ranges[i];
				items.addAll(GeekManager.g().ascii(range));
			}
			
			items.add(header);

			
			export(items);
			
			return true;
		}
		
		if(isIn(KEY_ASCII_ALL + "," + KEY_ASCII_SHORT + KEY_2DOTS)) {
			List<MexItem> items = GeekManager.g().asciiAll();
			MexedObject header = new MexedObject((AsciiRecord.getHeader()));
			items.add(0, header);
			items.add(header);
			
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_ASCII_SHORT + "\\s(.+?)");
		if(singleParam != null) {
			List<MexItem> records = GeekManager.g().asciiAll();
			
			MexFilter<MexedObject> filter = new MexFilter<MexedObject>(singleParam, CollectionUtil.toMexedObjects(records));
			List<MexedObject> items = filter.process();
			
			if(!EmptyUtil.isNullOrEmpty(items)) {
				items.add(0, new MexedObject((AsciiRecord.getHeader())));
			}

			export(items);
			
			return true;
		}
		
		if(is(KEY_MAXMIN)) {
			List<String> items = new ArrayList<>();
			items.add(maxmin("Max of Long", Long.MAX_VALUE));
			items.add(maxmin("Min of Long", Long.MIN_VALUE));
			items.add(maxmin("Max of Integer", Integer.MAX_VALUE));
			items.add(maxmin("Min of Integer", Integer.MIN_VALUE));
			items.add(maxmin("Max of Short", Short.MAX_VALUE));
			items.add(maxmin("Min of Short", Short.MIN_VALUE));
			items.add(maxmin("Max of Byte", Byte.MAX_VALUE));
			items.add(maxmin("Min of Byte", Byte.MIN_VALUE));
			
			export(items);
			
			return true;
		}
		
		if(is(KEY_ENCODE + KEY_2DOTS)) {
			List<CharsetCode> items = GeekManager.g().allCodingNames();
			export(items);
			
			return true;
		}
		
		params = parseParams(KEY_ENCODE + "-([\\S]+)\\s(.+?)");
		if(params != null) {
			String criteria = params[0];
			String content = params[1];
			List<CharsetCode> codes = GeekManager.g().searchCodingNames(criteria);
			
			List<String> charsets = new ArrayList<>();
			for(CharsetCode mexCode : codes) {
				String codeName = mexCode.getName();
				charsets.add(codeName);
			}
			List<String> items = GeekManager.g().encodeStringByCharset(content, charsets);
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_ENCODE + "-([\\S]+)");
		if(singleParam != null) {
			String criteria = singleParam;
			List<CharsetCode> codes = GeekManager.g().searchCodingNames(criteria);
			
			List<String> charsets = new ArrayList<>();
			for(CharsetCode mexCode : codes) {
				String codeName = mexCode.getName();
				charsets.add(codeName);
			}
			
			export(codes);
			
			return true;
		}
		
		singleParam = parseParam(KEY_ENCODE + "\\s(.+?)");
		if(singleParam != null) {
			List<String> items = GeekManager.g().encodeStringByUnicodeUTF8GBK(singleParam);
			export(items);
			return true;
		}
		
		params = parseParams("([a-z0-9\\-_]{3,30})\\s(.+?)");
		if(params != null) {
			String accurateCharset = "^" + params[0] + "$";
			List<CharsetCode> mexCodes = GeekManager.g().searchCodingNames(accurateCharset);
			if(mexCodes.size() == 1) {
				String code = mexCodes.get(0).getName();
				String content = params[1];
				String value = CodeUtil.replaceHexChars(content, code);
				export(value);
				
				return true;
			}
			
		}

		singleParam = parseParam(KEY_TO_BASE64 + "\\s(.+?)");
		if(singleParam != null) {
			String result = CodeUtil.toBase64(singleParam);
			export(result);
			
			return true;
		}
		
		singleParam = parseParam(KEY_FROM_BASE64 + "\\s(.+?)");
		if(singleParam != null) {
			String result = CodeUtil.fromBase64(singleParam);
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
			
			boolean isFile = FileUtil.exists(what);
			String result = null;
			if(isFile) {
				result = SecurityUtil.digestFile(what, algo);
			} else {
				result = SecurityUtil.digest(what, algo);
			}
			
			String prefix = "";
			if(isFile) {
				prefix = "File, ";
			}
			
			List<String> items = new ArrayList<>();
			items.add(result);
			items.add(prefix + algo.toUpperCase() + " generates " + result.length() + " chars.");
			
			export(items);
			return true;
		}
		
		return false;
	}
	
	private String maxmin(String displayName, long value) {
		String str = String.valueOf(value);
		int len = str.length();
		if(value < 0) {
			len--;
		}
		
		String temp = "{0} is {1}, {2} chars.";
		String result = StrUtil.occupy(temp, displayName, str, len);
		
		return result;
	}
}
