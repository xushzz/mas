package com.sirap.geek;

import java.io.File;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.DateUtil;
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
					List<String> records = IOUtil.readFileIntoList(filePath, g().getCharsetInUse());
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
		
		singleParam = parseParam(KEY_ID_SFZ + "\\s+(.+?)");
		if(singleParam != null) {
			sfz(singleParam);
			return true;
		}
		
		try {
			sfz(command);
			return true;
		} catch (MexException ex) {
			
		}
		
		singleParam = parseParam(KEY_BEEP_K);
		if(singleParam != null) {
			Integer count = MathUtil.toInteger(singleParam);
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
		String info = "";
		if(param.length() == 18) {
			if(StrUtil.endsWith(param, code)) {
				info = param + " is a right ID.";
			} else {
				String temp = param.replaceAll(".$", code);
				info = "Wrong, right ID should be " + temp ;
			}
		} else {
			info = "ID should be " + param + code ;
		}
		
		export(info);
	}
}
