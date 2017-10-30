package com.sirap.geek.domain;

import com.sirap.basic.domain.MexItem;
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
		
		return false;
	}
	
	public static String getHeader() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(StrUtil.padRight("[Binary", 12));
		sb.append(StrUtil.padRight("Octal", 7));
		sb.append(StrUtil.padRight("Dec", 7));
		sb.append(StrUtil.padRight("Hex", 6));
		sb.append("detail]");
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		String space = StrUtil.repeat(' ', 4);
		StringBuffer sb = new StringBuffer();
		sb.append(binary).append(space);
		sb.append(octal).append(space);
		sb.append(decimal).append(space);
		sb.append(hex).append(space);
		if(base != 8 && base != 10 && base != 12 && base != 13) {
			sb.append('\'').append((char)base).append('\'');
		} else {
			sb.append("#");
		}
		if(info != null) {
			sb.append(", ").append(info);
		}
		
		return sb.toString();
	}
}
