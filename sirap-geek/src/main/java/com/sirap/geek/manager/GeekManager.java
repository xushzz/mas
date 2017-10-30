package com.sirap.geek.manager;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.geek.domain.AsciiRecord;
import com.sirap.geek.domain.CharsetCode;

public class GeekManager {
	
	private static GeekManager instance;
	
	private GeekManager() {}
	
	public static GeekManager g() {
		if(instance == null) {
			instance = new GeekManager();
		}
		
		return instance;
	}
	
	public List<MexItem> asciiAll() {
		int[] range = {0, 127};
		return ascii(range);
	}
	
	public List<MexItem> ascii(int[] range) {
		if(range.length != 2) {
			XXXUtil.alert("The range should have two elments.");
		}
		
		List<MexItem> items = new ArrayList<>();
		for(int i = range[0]; i <= range[1]; i++) {
			AsciiRecord item = new AsciiRecord(i);
			String binary = StrUtil.padLeft(Integer.toBinaryString(i), 8, "0");
			String octal = StrUtil.padLeft(Integer.toOctalString(i), 3, "0");
			String decimal = StrUtil.padLeft(Integer.toString(i), 3, "0");
			String hex = StrUtil.padLeft(Integer.toHexString(i), 2, "0").toUpperCase();
			String info = XCodeUtil.ASCII_INFO.get(i);
			
			item.setBinary(binary);
			item.setOctal(octal);
			item.setDecimal(decimal);
			item.setHex(hex);
			item.setInfo(info);
			
			items.add(item);
		}

		return items;
	}
	
	public List<CharsetCode> allCodingNames() {
		List<CharsetCode> items = new ArrayList<>();
		
		Iterator<String> it = Charset.availableCharsets().keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			CharsetCode code = new CharsetCode(key);
			items.add(code);
		}
		
		items.add(new CharsetCode(Konstants.CODE_UNICODE));
		
		return items;
	}
	
	public List<CharsetCode> searchCharsetNames(String criteria) {
		MexFilter<CharsetCode> filter = new MexFilter<CharsetCode>(criteria, allCodingNames());
		List<CharsetCode> items = filter.process();
		
		return items;
	}
	
	public List<String> encodeStringByUnicodeUTF8GBK(String source) {
		List<String> charsets = StrUtil.split("Unicode,UTF-8,GBK");
		return encodeStringByCharset(source, charsets);
	}
	
	public List<String> encodeStringByCharset(String source, List<String> charsets) throws MexException {
		List<String> items = new ArrayList<>();
		int maxLen = StrUtil.maxLengthOf(charsets);
		for(int k = 0; k < charsets.size(); k++) {
			String code = charsets.get(k);
			StringBuffer sb = new StringBuffer();
			String prefix = StrUtil.padRight(code, maxLen);
			sb.append(prefix).append("    ");
			for(int i = 0; i < source.length(); i++) {
				char ch = source.charAt(i);
				String value = XCodeUtil.encode2HexChars(ch, code, true);
				if(!EmptyUtil.isNullOrEmpty(value)) {
					sb.append(value);
				}
			}
			items.add(sb.toString());
		}
		
		return items;
	}
}
