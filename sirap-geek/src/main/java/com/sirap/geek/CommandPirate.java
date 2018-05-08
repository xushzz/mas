package com.sirap.geek;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.Person;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.json.JsonUtil;
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
import com.sirap.geek.manager.FiveOManager;
import com.sirap.geek.manager.HiredDaysCalculator;

public class CommandPirate extends CommandBase {
	
	private static final String KEY_HIRED = "((hired|hdays)(|-))";
	private static final String KEY_ID_SFZ = "sfz";
	private static final String KEY_BEEP_K = "b(\\d{1,2})";
	private static final String KEY_BEEP_HOUR = "bmw";
	private static final String KEY_CARO = "caro";
	private static final String KEY_MATE_JSON = "mate";
	private static final String KEY_51job = "51job";

	public boolean handle() {
		
		params = parseParams(KEY_HIRED + " (.+?)");
		if(params != null) {
			File file = parseFile(params[3]);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					List<String> records = IOUtil.readLines(filePath, charset());
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
		
		solo = parseParam(KEY_ID_SFZ + "\\s(\\d{1,6}|[\\D]{1,100})");
		if(solo != null) {
			String source = g().getUserValueOf("sfz.source");
			if(!EmptyUtil.isNullOrEmpty(source)) {
				boolean isText = FileOpener.isTextFile(source);
				if(isText) {
					String path = parseFile(source).getAbsolutePath();
					List<String> allAreas = IOUtil.readLines(path);
					export2(allAreas, solo);
				} else {
					C.pl2("Not a text file: " + source);
				}
			}
			
			return true;
		}
		
		solo = parseParam(KEY_ID_SFZ + "\\s((\\d{17})(|\\d|X))");
		if(solo != null) {
			sfz(solo);
			return true;
		}
		
		try {
			sfz(command);
			return true;
		} catch (MexException ex) {
			
		}
		
		solo = parseParam(KEY_BEEP_K);
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
		
		solo = parseParam(KEY_CARO + "\\s+(.+)");
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
		
		solo = parseParam(KEY_51job + "\\s(.+)");
		if(solo != null) {
			String startpage = solo;
			List<String> lines = FiveOManager.g().job51(startpage);
			String name = OptionUtil.readString(options, "n", "xitems");
			lines.set(0, StrUtil.occupy("var {0} = [", name));
			lines.set(lines.size() - 1, "];");
			
			export(lines);
		}
		
		solo = parseParam(KEY_MATE_JSON + "\\s(.+)");
		if(solo != null) {
			File folder = FileUtil.getIfNormalFolder(solo);
			List<String> lines = null;
			if(folder != null) {
				String mexCriteria = OptionUtil.readString(options, "k");
				lines = FileUtil.muse(folder.getAbsolutePath(), mexCriteria);
			}
			File file = FileUtil.getIfNormalFile(solo);
			String type = OptionUtil.readString(options, "t");
			if(file != null) {
				List<List<String>> list = MsExcelHelper.readXlsSheetByIndex(file.getAbsolutePath(), 0);
				boolean askForDonation = OptionUtil.readBooleanPRI(options, "ask", false);
				if(EmptyUtil.isNullOrEmpty(type)) {
					lines = jsonDataOfMates(list, askForDonation);
				} else {
					lines = sqlDataOfMates(list, askForDonation, type);
				}
			}

			if(lines == null) {
				XXXUtil.alert("Neither a valid folder nor a valid path: {0}", solo);
			}
			
			if(EmptyUtil.isNullOrEmpty(type)) {
				String name = OptionUtil.readString(options, "n", "xitems");
				lines.set(0, StrUtil.occupy("var {0} = [", name));
				lines.set(lines.size() - 1, "];");
			}

			export(lines);
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
			File file = parseFile(source);
			if(file == null) {
				C.pl2("Not a valid path: " + source);
			} else {
				boolean isText = FileOpener.isTextFile(source);
				if(isText) {
					String path = file.getAbsolutePath();
					List<String> allAreas = IOUtil.readFileIntoList(path);
					String areaName = IDCardUtil.getAreaInfo(areaCode, allAreas);
					saul.setAreaName(areaName);
				}
			}
		}
		bf.append(", ").append("from ").append(saul.getAreaName());
		items.add(bf.toString());
		
		export(items);
	}
	
	public List<String> jsonDataOfMates(List<List<String>> data, boolean askForDonation) {
		String digao = "108.037172,24.686088";
		String sftech = "113.940025,22.524648";
		List<String> newLines = Lists.newArrayList();
		String template = "{\"name\": \"{0}\",\"location\": \"{1}\",\"phone\": \"{2}\",\"city\": \"{3}\",\"address\": \"{4}\"}";
		for(int countX = 2; countX < data.size(); countX++) {
			List<String> line = data.get(countX);
			String name = line.get(1) + "";
			if(StrUtil.contains(name + "", "-")) {
				continue;
			}
			String phone = line.get(2) + "";
			if(StrUtil.isRegexMatched("[\\-]+", phone.trim())) {
				phone = "电话";
			}
			String city = line.get(4) + "";
			String location = line.get(5) + "";
			if(StrUtil.contains(location, "-")) {
				location = digao;
			}
			if(StrUtil.contains(city, "-")) {
				city = "城市";
			}
			String info = line.get(3) + "";
			info = info.replaceAll("-", "") + " [" + location + "]";
			String value = StrUtil.occupy(template, name, location, phone, city, info);
			value = value.replace("\n", "");
			newLines.add(value);
		}
		if(askForDonation) {
			String value = StrUtil.occupy(template, "Carospop", sftech, "donation", "打赏", "精准打赏九点九，系统建设到久久");
			newLines.add(value);
		}
		String json = "[" + StrUtil.connect(newLines, ", ") + "]";
		return JsonUtil.getPrettyTextInLines(json);
	}
	
	public List<String> sqlDataOfMates(List<List<String>> data, boolean askForDonation, String type) {
		String digao = "108.037172,24.686088";
		List<String> newLines = Lists.newArrayList();
		String template = "insert into fish_item values(null, '{0}', '{1}', '{2}', '{3}', '{4}', '{5}', now());";
		for(int countX = 2; countX < data.size(); countX++) {
			List<String> line = data.get(countX);
			String name = line.get(1) + "";
			if(StrUtil.contains(name, Konstants.SHITED_FACE)) {
				continue;
			}
			String phone = line.get(2) + "";
			if(StrUtil.contains(phone, Konstants.SHITED_FACE)) {
				phone = "电话";
			}
			String city = line.get(4) + "";
			String location = line.get(5) + "";
			if(StrUtil.contains(location, Konstants.SHITED_FACE)) {
				location = digao;
			}
			if(StrUtil.contains(city, Konstants.SHITED_FACE)) {
				city = "城市";
			}
			String info = line.get(3) + "";
			info = info.replaceAll("-", "") + " [" + location + "]";
			String value = StrUtil.occupy(template, type, name, location, phone, city, info);
			value = value.replace("\n", "");
			newLines.add(value);
		}
		
		return newLines;
	}
}
