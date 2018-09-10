package com.sirap.basic.domain;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.XXXUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LongOrLat {
	public static final String TYPE_LONGITUDE = "lon";
	public static final String TYPE_LATITUDE = "lat";
	public static final String DEGREE = XCodeUtil.urlDecodeUTF8("%C2%B0");
	public static final String MINUTE = XCodeUtil.urlDecodeUTF8("%E2%80%B2");
	public static final String SECOND = XCodeUtil.urlDecodeUTF8("%E2%80%B3");

	private String type;
	private double decimal;
	private int degree;
	private int minute;
	private double second;
	private String flag;
	
	public LongOrLat(String typeParam, double decimalParam) {
		this.type = typeParam;
		this.decimal = Math.abs(decimalParam);
		this.flag = flagOf(typeParam, decimalParam);
		this.degree = (int)decimal;
		
		double temp = (decimal - degree) * 60;
		this.minute = (int)temp;
		temp = temp - minute;
		this.second = temp * 60;
	}
	
	public LongOrLat(String typeParam, int degreeParam, int minuteParam, double secondParam) {
		this.type = typeParam;
		this.degree = Math.abs(degreeParam);
		this.minute = Math.abs(minuteParam);
		this.second = Math.abs(secondParam);
		this.flag = flagOf(typeParam, degreeParam);
		this.decimal = toDecimal(degree, minute, second);
	}
	
	public String toDegreeMinuteSecondNEWS(String... units) {
		StringBuffer sb = StrUtil.sb();
		sb.append(degree).append(units[0]);
		sb.append(minute).append(units[1]);
		sb.append(MathUtil.toPrettyString(second, 2)).append(units[2]);
		sb.append(flag);
		
		return sb.toString();
	}
	
	public String getTypedDMS() {
		StringBuffer sb = StrUtil.sb();
		sb.append(type).append(" ");
		if(isNegative()) {
			sb.append("-");
		}
		sb.append(degree).append(" ");
		sb.append(minute).append(" ");
		sb.append(MathUtil.toPrettyString(second, 2));
		
		return sb.toString();
	}
	
	private double toDecimal(int degree, int minute, double second) {
		double positive = Math.abs(degree);
		
		double temp = second / 60.0;
		temp = (minute + temp) / 60.0; 
		temp = positive + temp;
		
		return temp;
	}
	
	private String flagOf(String type, double decimalParam) {
		String temp = null;
		if(isLatitude()) {
			temp = decimalParam > 0 ? "N" : "S";
		} else if(isLongitude()) {
			temp = decimalParam > 0 ? "E" : "W";
		} else {
			XXXUtil.alerto("Invalid type [{0}], shoule be either {1} or {2}", type, TYPE_LONGITUDE, TYPE_LATITUDE);
		}
		
		return temp;
	}
	
	public boolean isLongitude() {
		return StrUtil.equals(type, TYPE_LONGITUDE);
	}

	public boolean isLatitude() {
		return StrUtil.equals(type, TYPE_LATITUDE);
	}
	
	public boolean isNegative() {
		return StrUtil.isRegexMatched("[SW]", flag);
	}
	
	public String getSignedDecimal() {
		String pretty = MathUtil.toPrettyString(decimal, 7);
		if(isNegative()) {
			pretty = "-" + pretty;
		}
		
		return pretty;
	}
	
	public String getTypedDecimal() {
		String pretty = getSignedDecimal();
		
		if(type != null) {
			return type + " " + pretty;
		} else {
			return pretty;
		}
	}
	
	public boolean parse(String record) {
		boolean flag = parseDmsNews(record);
		if(flag) {
			return true;
		}
		
		flag = parseDecimalNews(record);
		if(flag) {
			return true;
		}
		
		return false;
	}
	
	public boolean parseDecimalNews(String decimalNEWS) {
		StringBuffer sb = StrUtil.sb();
		sb.append(Konstants.REGEX_FLOAT);
		sb.append("([NEWS])");
		
		String regex = sb.toString();
		String[] params = StrUtil.parseParams(regex, decimalNEWS);
		if(params == null) {
			return false;
		}
		
		decimal = Double.parseDouble(params[0]);
		if(!params[1].isEmpty()) {
			flag = params[1];
			type = typeOf(flag);
		}
		
		return true;
	}
	
	public boolean parseDmsNews(String degreeMinuteSecondNEWS) {
		StringBuffer sb = StrUtil.sb();
		sb.append("(\\d+)");
		sb.append("(\\s|" + DEGREE + ")");
		sb.append("(\\d+)");
		sb.append("(\\s|'|" + MINUTE + ")");
		sb.append("(|");
		sb.append(Konstants.REGEX_FLOAT);
		sb.append("(|\"|" + SECOND + ")");
		sb.append(")");
		sb.append("(|[NEWS])");
		
		String regex = sb.toString();
		String[] params = StrUtil.parseParams(regex, degreeMinuteSecondNEWS);
		if(params == null) {
			return false;
		}
		
		degree = Integer.parseInt(params[0]);
		minute = Integer.parseInt(params[2]);
		String secondStr = params[5];

		if(secondStr != null) {
			second = MathUtil.toDouble(secondStr);
		}
		
		decimal = toDecimal(degree, minute, second);

		flag = params[7];
		
		type = typeOf(flag);
		
		return true;
	}
	
	private String typeOf(String flag) {
		String type = null;
		
		if(StrUtil.isRegexMatched("[NS]", flag)) {
			type = TYPE_LATITUDE;
		} else if(StrUtil.isRegexMatched("[EW]", flag)) {
			type = TYPE_LONGITUDE;
		}

		return type;
	}
}
