package com.sirap.geek;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.Person;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IDCardUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.ThreadUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.geek.manager.HiredDaysCalculator;

public class CommandPirate extends CommandBase {
	
	private static final String KEY_HIRED = "((hired|hdays)(|-))";
	private static final String KEY_ID_SFZ = "sfz";
	private static final String KEY_BEEP_K = "b(\\d{1,2})";
	private static final String KEY_BEEP_HOUR = "bmw";

	public boolean handle() {
		
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
		
		solo = parseSoloParam(KEY_ID_SFZ + "\\s(\\d{1,6}|[\\D]{1,100})");
		if(solo != null) {
			String source = g().getUserValueOf("sfz.source");
			if(!EmptyUtil.isNullOrEmpty(source)) {
				boolean isText = FileOpener.isTextFile(source);
				if(isText) {
					String path = parseFile(source).getAbsolutePath();
					List<String> allAreas = IOUtil.readFileIntoList(path);
					export2(allAreas, solo);
				} else {
					C.pl2("Not a text file: " + source);
				}
			}
			
			return true;
		}
		
		solo = parseSoloParam(KEY_ID_SFZ + "\\s((\\d{17})(|\\d|X))");
		if(solo != null) {
			sfz(solo);
			return true;
		}
		
		try {
			sfz(command);
			return true;
		} catch (MexException ex) {
			
		}
		
		solo = parseSoloParam(KEY_BEEP_K);
		if(solo != null) {
			Integer count = MathUtil.toInteger(solo);
			beepKTimes(count);
			
			C.pl();
			return true;
		}
		
		if(is(KEY_BEEP_HOUR)) {
			int currentHour = DateUtil.getHour();
			if(currentHour == 0) {
				currentHour = 24;
			}
			beepKTimes(currentHour);
			
			C.pl();
			return true;
		}
		
		return false;
	}
	
	private void beepKTimes(int count) {
		for(int i = 0; i < count; i++) {
			String temp = "Beep {0}/{1} ..." + Konstants.BEEP;
			String value = StrUtil.occupy(temp, i + 1, count);
			C.pl(value);
			ThreadUtil.sleepInSeconds(1);
		}
	}
	
	private void sfz(String param) {
		String code = IDCardUtil.checkCodeChina(param) + "";
		List<String> items = new ArrayList<>();
		String msg = "";
		if(param.length() == 18) {
			if(StrUtil.endsWith(param, code)) {
				msg = param + " is a right ID.";
			} else {
				String temp = param.replaceAll(".$", code);
				msg = "Wrong, should be " + temp ;
			}
		} else {
			msg = "ID should be " + param + code ;
		}
		items.add(msg);
		
		Person saul = IDCardUtil.readBasicInfo(param);
		String areaCode = saul.getAreaCode();
		
		int monthDiff = DateUtil.monthDiff(new Date(), saul.getBirthDate());
		BigDecimal age = MathUtil.divide(monthDiff, 12, 1);
		
		StringBuffer bf = new StringBuffer();
		bf.append(saul.displayGender()).append(", ");
		bf.append(age).append(", ");
		bf.append(saul.getDateOfBirth());
		
		String source = g().getUserValueOf("sfz.source");
		if(!EmptyUtil.isNullOrEmpty(source)) {
			boolean isText = FileOpener.isTextFile(source);
			if(isText) {
				String path = parseFile(source).getAbsolutePath();
				List<String> allAreas = IOUtil.readFileIntoList(path);
				String areaName = IDCardUtil.getAreaInfo(areaCode, allAreas);
				saul.setAreaName(areaName);
			}
		}
		bf.append(", ").append("from ").append(saul.getAreaName());
		items.add(bf.toString());
		
		export(items);
	}
}


