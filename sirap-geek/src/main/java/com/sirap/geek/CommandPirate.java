package com.sirap.geek;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.Person;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.thirdparty.msoffice.MsExcelHelper;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IDCardUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.ThreadUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.geek.domain.CaroItem;
import com.sirap.geek.manager.HiredDaysCalculator;

public class CommandPirate extends CommandBase {
	
	private static final String KEY_HIRED = "((hired|hdays)(|-))";
	private static final String KEY_ID_SFZ = "sfz";
	private static final String KEY_BEEP_K = "b(\\d{1,2})";
	private static final String KEY_BEEP_HOUR = "bmw";
	private static final String KEY_CARO = "caro";
	private static final String KEY_MATE_JSON = "mate";

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
		
		solo = parseSoloParam(KEY_CARO + "\\s+(.+)");
		if(solo != null) {
			File folder = parseFolder(solo);
			XXXUtil.nullCheck(folder, ":Not a valid folder: " + solo);
			String wikiFolder = OptionUtil.readString(options, "wiki");
			if(wikiFolder == null) {
				String regex = "[/\\\\]([^/\\\\]+)[/\\\\]?$";
				wikiFolder = StrUtil.findFirstMatchedItem(regex, folder.getAbsolutePath());
			}
			List<String> lines = Lists.newArrayList();
			XXXUtil.nullCheck(folder, ":Not a valid folder: " + solo);
			String baseDay = OptionUtil.readString(options, "base");
			Date baseDate = DateUtil.parse("yyyyMMdd", baseDay, false);
			boolean forCaro = false;
			if(baseDate == null) {
				forCaro = true;
				baseDate = DateUtil.construct(2017, 12, 4);
				lines.add("* 我是小鱼儿，女生，这是我爸爸给我做的成长全记录。");
				lines.add("* 我生日：2017/12/4");
				lines.add("* 嗯，冬天，晚上，大南宁，广西省妇幼医院");
			}
			
			List<CaroItem> caros = Lists.newArrayList();
			folder.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String fileName) {
					CaroItem item = new CaroItem();
					if(item.parse(fileName)) {
						caros.add(item);
					}
					return true;
				}
			});
			
			//order by date, ascending
			Collections.sort(caros);
			
			//order by date, descending
			
			if(OptionUtil.readBooleanPRI(options, "new", false)) {
				Collections.reverse(caros);
				if(forCaro) {
					lines.add("### [按时间顺序](https://github.com/acesfullmike/mas/wiki/caroline)");
				}
			} else {
				if(forCaro) {
					lines.add("### [按时间倒序](https://github.com/acesfullmike/mas/wiki/caroline2)");
				}
			}
			for(int i = 0; i < caros.size(); i++) {
				lines.addAll(carolines(i + 1, caros.size(), caros.get(i), baseDate, wikiFolder));
			}
			
			export(lines);
			
			return true;
		}
		
		solo = parseSoloParam(KEY_MATE_JSON + "\\s(.+)");
		if(solo != null) {
			String filePath = solo;
			if(FileUtil.exists(filePath)) {
				List<List<Object>> list = MsExcelHelper.readSheetByIndex(filePath, 0);
				export(jsonDataOfMates(list));
			}
			
			return true;
		}
		
		return false;
	}
	
	private List<String> carolines(int index, int size, CaroItem item, Date birthday, String folderName) {
		Date date = item.getDateInfo();
		int dayDiff = DateUtil.dayDiff(date, birthday) + 1;
		String weekday = DateUtil.displayDate(date, DateUtil.WEEK_DATE, Locale.CHINA);
		String tempA = "# 第 {0} 天, {1}    第{2}张, 共{3}张";
		String lineA = StrUtil.occupy(tempA, dayDiff, weekday, index, size);
		String tempB = "![{0}]({1}/{0})";
		String lineB = StrUtil.occupy(tempB, item.getFileName(), folderName);
		
		List<String> lines = Lists.newArrayList();
		lines.add(lineA);
		lines.add(lineB);
		lines.add("");

		return lines;
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
	
	public String jsonDataOfMates(List<List<Object>> data) {
		Map<String, String> nice = Maps.newConcurrentMap();
		nice.put("广州", "113.254974,23.147856");
		nice.put("南宁", "108.336523,22.813061");
		nice.put("河池", "108.084875,24.692698");
		nice.put("桂林", "110.29185,25.281395");
		nice.put("柳州", "109.406837,24.297981");
		nice.put("深圳", "114.057814,22.543375");
		nice.put("上海", "121.473657,31.230286");
		List<String> newLines = Lists.newArrayList();
		for(int countX = 1; countX < data.size(); countX++) {
			List<Object> line = data.get(countX);
			String template = "{\"name\": \"{0}\",\"location\": \"{1}\",\"phone\": \"{2}\",\"city\": \"{3}\",\"address\": \"{4}\"}";
			Object name = line.get(2);
			String location = line.get(3) + "";
			Object phone = line.get(4);
			String city = line.get(5) + "";
			Object info = line.get(6);
			if(EmptyUtil.isNullOrEmptyOrBlankOrLiterallyNull(city)) {
				city = "南宁";
			}
			if(EmptyUtil.isNullOrEmptyOrBlankOrLiterallyNull(location)) {
				location = nice.get(city);
			}
			if(EmptyUtil.isNullOrEmpty(location)) {
				location = nice.get("南宁");
			}
			String value = StrUtil.occupy(template, name, location, phone, city, info);
			newLines.add(value);
		}
		
		String total = "[" + StrUtil.connect(newLines, ", ") + "]";
		
		return total;
	}
}
