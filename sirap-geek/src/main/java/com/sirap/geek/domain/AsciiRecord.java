package com.sirap.geek.domain;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class AsciiRecord extends MexItem {

	private int base;
	private String binary;
	private String octal;
	private String decimal;
	private String hex;
	private String info;

	public AsciiRecord(int base) {
		this.base = base;
	}
	
	public String getBinary() {
		return binary;
	}

	public void setBinary(String binary) {
		this.binary = binary;
	}
	
	public String getOctal() {
		return octal;
	}

	public void setOctal(String octal) {
		this.octal = octal;
	}

	public String getDecimal() {
		return decimal;
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	public String getHex() {
		return hex;
	}

	public void setHex(String hex) {
		this.hex = hex;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public boolean isMatched(String keyWord) {
		if(keyWord.length() < 1) {
			return false;
		}
		
		if(StrUtil.contains(binary, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(octal, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(decimal, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(hex, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(info, keyWord)) {
			return true;
		}
		
		if(StrUtil.equals((char)base + "", keyWord)) {
			return true;
		}
		
		if(StrUtil.equals(quote(), keyWord)) {
			return true;
		}
		
		return false;
	}
	
	public static ValuesItem COLUMNS = ValuesItem.of("[Binary", "Octal", "Decimal", "Hex", "Char", "About]");
	
	public String quote() {
		String temp = "";
		if(base != 8 && base != 10 && base != 12 && base != 13) {
			temp = StrUtil.occupy("'{0}'", (char)base);
		} else {
			temp = StrUtil.occupy("'{0}'", '#');
		}
		
		return temp;
	}
	
	public List toList(String options) {
		return Lists.newArrayList(binary, octal, decimal, hex, quote(), niceInfo());
	}
	
	private String niceInfo() {
		if(info == null) {
			info = "";
		}
		
		return info;
	}
	
	@Override
	public String toString() {
		return StrUtil.connectWithSpace(toList());
	}
}
