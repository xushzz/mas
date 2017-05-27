package com.sirap.geek;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.SecurityUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.geek.domain.AsciiRecord;
import com.sirap.geek.domain.CharsetCode;
import com.sirap.geek.manager.GeekManager;
import com.sirap.geek.util.QRCodeUtil;
	
public class CommandXCode extends CommandBase {

	private static final String KEY_ASCII_SHORT = "asc";
	private static final String KEY_ASCII_ALL = "ascii";
	private static final String KEY_QRCODE_ENCODE = "qrc";
	private static final String KEY_QRCODE_DECODE = "qrx";	
	private static final String KEY_TO_BASE64 = "t64";
	private static final String KEY_FROM_BASE64 = "f64";
	private static final String KEY_DIGEST_ALGORITHMS = "(SHA1|SHA-224|SHA224|SHA-256|SHA256|MD2|SHA|SHA-512|SHA512|MD5)";
	private static final String KEY_URL_ENCODE = "url";
	private static final String KEY_URL_DECODE = "urx";
	private static final String KEY_ENCODE = "cde";
	private static final String KEY_SWAP = "swap";

	public boolean handle() {
		
		params = parseParams(KEY_QRCODE_ENCODE + "\\s(.*?)(|///(.*?))");
		if(params != null) {
			String nameInfo = params[2];
			if(nameInfo == null) {
				nameInfo = "";
			}

			String content = params[0];
			File file = parseFile(content);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					content = IOUtil.readFileWithRegularLineSeparator(filePath);
					C.pl("Encode text file.");
					if(EmptyUtil.isNullOrEmpty(nameInfo)) {
						nameInfo = FileUtil.extractFilenameWithoutExtension(filePath);
					}
				}
			}

			String[] filenameAndFormat = generateQRCodeImageFilenameAndFormat(nameInfo, content, "png");
			String filepath = filenameAndFormat[0];
			String format = filenameAndFormat[1];
			
			String filePath = QRCodeUtil.createImage(content, filepath, format, 200, 200);
			if (filePath != null) {
				C.pl(filePath);
				tryToOpenGeneratedImage(filePath);
			}
			
			return true;
		}

		String param = parseParam(KEY_QRCODE_DECODE + "\\s+(.+?)");
		if(param != null) {
			File file = parseFile(param);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				String content = QRCodeUtil.decodeImage(filePath);
				export(content);
				
				return true;
			}
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

		singleParam = parseParam(KEY_TO_BASE64 + "\\s(.+?)");
		if(singleParam != null) {
			String result = XCodeUtil.toBase64(singleParam);
			export(result);
			
			return true;
		}
		
		singleParam = parseParam(KEY_FROM_BASE64 + "\\s(.+?)");
		if(singleParam != null) {
			String result = XCodeUtil.fromBase64(singleParam);
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
			
			boolean isFile = FileUtil.isNormalFile(what);
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
		
		singleParam = parseParam(KEY_URL_ENCODE + "\\s(.+?)");
		if(singleParam != null) {
			String charset = Konstants.CODE_UTF8;
			String value = XCodeUtil.urlEncodeUTF8(singleParam);
			C.pl("Encode with charset: " + charset);
			export(value);
			
			return true;
		}
		
		singleParam = parseParam(KEY_URL_DECODE + "\\s(.+?)");
		if(singleParam != null) {
			String charset = Konstants.CODE_UTF8;
			String value = XCodeUtil.urlDecodeUTF8(singleParam);
			C.pl("Decode with charset: " + charset);
			export(value);
			
			return true;
		}
		
		if(is(KEY_ENCODE + KEY_2DOTS)) {
			List<CharsetCode> items = GeekManager.g().allCodingNames();
			export(items);
			
			return true;
		}
		
		singleParam = parseParam(KEY_ENCODE + "-([\\S]+)");
		if(singleParam != null) {
			String criteria = singleParam;
			List<CharsetCode> codes = GeekManager.g().searchCharsetNames(criteria);
			
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
		
		if(is(KEY_SWAP)) {
			String[] codeAndImage = IOUtil.generateCaptcha(4, screenShotPath());
			String code = null;
			String filePath = null;
			if(codeAndImage != null) {
				code = codeAndImage[0];
				filePath = codeAndImage[1];
			}
			
			if(filePath != null) {
				C.pl(code + ", " + filePath);
				tryToOpenGeneratedImage(filePath);
			}
			
			if(target instanceof TargetConsole) {
				return true;
			}
			
			export(FileUtil.getIfNormalFile(filePath));
			
			return true;
		}
		
		return false;
	}
}