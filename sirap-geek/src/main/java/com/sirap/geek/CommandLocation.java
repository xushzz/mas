package com.sirap.geek;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.data.CityData;
import com.sirap.basic.domain.LocationItem;
import com.sirap.basic.domain.LongOrLat;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.LonglatUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.MatrixUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.geek.data.LonglatData;
import com.sirap.geek.domain.DistrictItem;
import com.sirap.geek.manager.GaodeManager;
import com.sirap.geek.manager.GaodeUtils;
import com.sirap.geek.manager.TencentUtils;
import com.sirap.third.http.HttpHelper;

public class CommandLocation extends CommandBase {
	private static final String KEY_GAODE = "gao";
	private static final String KEY_GAODE_INPUTTIPS = "gin";
	private static final String KEY_GAODE_IP = "gip";
	private static final String KEY_GAODE_SEARCH = "gas";
	private static final String KEY_GAODE_GEO = "geo";
	private static final String KEY_LONGLAT = "(lon|lat)";
	
	public boolean handle() {
		boolean toRefresh = OptionUtil.readBooleanPRI(options, "r", false);

		if(is(KEY_GAODE + KEY_2DOTS)) {
			boolean fromAmap = OptionUtil.readBooleanPRI(options, "amap", false);
			if(fromAmap) {
				export(GaodeUtils.fetchAllDistricts());
			} else {
				export(GaodeManager.g().getAllDistricts(toRefresh));
			}
			
			return true;
		}
		
		solo = parseParam(KEY_GAODE + "\\s+(.+?)");
		if(solo != null) {
			boolean showUpperLevels = OptionUtil.readBooleanPRI(options, "p", false);
			Integer nextKLevel = OptionUtil.readInteger(options, "n");
			if(nextKLevel == null && OptionUtil.readBooleanPRI(options, "n", false)) {
				nextKLevel = 1;
			}
			List<DistrictItem> items = GaodeManager.g().getAllDistricts(toRefresh);
			List<DistrictItem> list2 = Colls.filter(items, solo, isCaseSensitive());
			List<MexItem> finals = Lists.newArrayList();
			if(showUpperLevels | nextKLevel != null) {
				boolean theFirstItem = true;
				for(DistrictItem item : list2) {
					if(!theFirstItem) {
						finals.add(new MexObject("===="));
					}
					theFirstItem = false;
					if(showUpperLevels) {
						finals.addAll(GaodeManager.g().getUpperDistrictsOf(item));
					}
					if(nextKLevel != null) {
						if(nextKLevel <= 0) {
							nextKLevel = 1;
						}
						finals.addAll(GaodeManager.g().getLowerDistrictsOf(item, nextKLevel));
					}
				}
				export(finals);
			} else {
				export(list2);
			}
			
			return true;
		}

		solo = parseParam(KEY_GAODE_INPUTTIPS + "\\s+(.+?)");
		if(solo != null) {
			List<ValuesItem> items = GaodeUtils.tipsOf(solo);
			useLowOptions("+so,c=#s");
			export(items);
			
			return true;
		}
		
		solo = parseParam(KEY_GAODE_SEARCH + "\\s+(.+?)");
		if(solo != null) {
			String city = OptionUtil.readString(options, "c", "");
			String location = GaodeUtils.fetchLonglat(solo, city);
			if(location == null) {
				boolean isDistance = dealWithDistance(solo, city);
				if(isDistance) {
					return true;
				}
			}
			
			List<ValuesItem> lines = Lists.newArrayList();
			if(location != null) {
				String keywords = OptionUtil.readString(options, "k", "");
				String types = OptionUtil.readString(options, "t", "");
				String radius = OptionUtil.readString(options, "r", "");
				lines = GaodeUtils.searchPlaceAround(location, keywords, types, radius);
			} else {
				String keywords = solo, types = "";
				String kParam = OptionUtil.readString(options, "k", "");
				if(!kParam.isEmpty()) {
					keywords = kParam;
					types = solo;
				}
				String tParam = OptionUtil.readString(options, "t", "");
				if(!tParam.isEmpty()) {
					types = tParam;
				}
				lines = GaodeUtils.searchPlaceText(keywords, types, city);
			}

			useLowOptions("+so,c=#s");
			export(lines);
			
			return true;
		}

		if(is(KEY_GAODE_GEO)) {
			List<List> matrix = MatrixUtil.matrixOf(LonglatData.EGGS);
			useLowOptions("c=#s2");
			exportMatrix(matrix);
			
			return true;
		}

		if(is(KEY_GAODE_GEO + KEY_2DOTS)) {
			List<List> matrix = MatrixUtil.matrixOf(Lists.newArrayList(CityData.EGGS.values()));
			useLowOptions("c=#s2");
			exportMatrix(matrix);
			
			return true;
		}
		
		solo = parseParam(KEY_GAODE_GEO + "\\s+(.+?)");
		if(solo != null) {
			String longCommaLat = null;
			LocationItem item = LonglatUtil.longAndlatOfDMS(solo);
			if(item != null) {
				longCommaLat = item.longCommaLat();
			}
			String city = OptionUtil.readString(options, "c", "");
			if(longCommaLat == null) {
				longCommaLat = GaodeUtils.fetchLonglat(solo, city);
			}
			
			if(longCommaLat == null) {
				boolean isDistance = dealWithDistance(solo, city);
				if(isDistance) {
					return true;
				}
			} else {
				if(OptionUtil.readBooleanPRI(options, "r", false)) {
					C.pl(KEY_GAODE_GEO + " " + GaodeUtils.reverseLonglat(longCommaLat));
					longCommaLat = GaodeUtils.reverseLonglat(longCommaLat);
				}
			}

			boolean showJson = OptionUtil.readBooleanPRI(options, "j", false);
			if(showJson) {
				if(longCommaLat != null) {
					export(TencentUtils.regeocodeOf(GaodeUtils.reverseLonglat(longCommaLat)));
				} else {
					export(GaodeUtils.geocodeOf(solo, city));
				}
				
				return true;
			}
			
			String niceAddress = null;
			boolean showDetail = true;
			boolean showWebsite = OptionUtil.readBooleanPRI(options, "w", false);
			String rawJson;
			if(longCommaLat != null) {
				rawJson = TencentUtils.regeocodeOfRaw(GaodeUtils.reverseLonglat(longCommaLat));
				String rawGao = GaodeUtils.regeocodeOfRaw(longCommaLat);
				if(showWebsite) {
					niceAddress = TencentUtils.niceAddressByRawJson(rawJson);
					showDetail = TencentUtils.isInGreatDetail(rawJson);
				}
			} else {
				rawJson = GaodeUtils.geocodeOfRaw(solo, city);
				if(showWebsite) {
					longCommaLat = StrUtil.findFirstMatchedItem(JsonUtil.createRegexKey("location"), rawJson);
					String formattedAddress = StrUtil.findFirstMatchedItem(JsonUtil.createRegexKey("formatted_address"), rawJson);
					String province = StrUtil.findFirstMatchedItem(JsonUtil.createRegexKey("province"), rawJson);
					if(formattedAddress != null) {
						niceAddress = formattedAddress.replaceAll("^" + province, "");
					}
				}
			}
			
			List<String> lines = JsonUtil.getPrettyTextInLines(rawJson);
			lines.add(0, StrUtil.occupy("param_location: {0}", longCommaLat));
			List<String> pretty = prettyFormatOf(lines);
			export(pretty);
			
			if(showWebsite) {
				String temp = "?zoom={0}&location={1}&address={2}";
				int zoom = showDetail ? 15 : 7;
				if(!StrUtil.isPositive(longCommaLat)) {
					longCommaLat = "";
				}
				if(!StrUtil.isPositive(niceAddress)) {
					niceAddress = "";
				}
				String requestParams = StrUtil.occupy(temp, zoom, longCommaLat, niceAddress);
				String site = g().getUserValueOf("picker.site", HttpHelper.URL_AKA10_PICKER);
				String pickerUrl = site + requestParams;
				viewPage(pickerUrl);
			}

			return true;
		}
		
		solo = parseParam(KEY_GAODE_GEO + "XXX\\s+(.+?)");
		if(solo != null) {
			String location = null;
			LocationItem item = LonglatUtil.longAndlatOfDMS(solo);
			if(item != null) {
				location = item.longCommaLat();
			}
			String city = OptionUtil.readString(options, "c", "");
			if(location == null) {
				location = GaodeUtils.fetchLonglat(solo, city);
			}
			
			if(location == null) {
				boolean isDistance = dealWithDistance(solo, city);
				if(isDistance) {
					return true;
				}
			}

			boolean showJson = OptionUtil.readBooleanPRI(options, "j", false);
			boolean appendParam = false;
			String textAddress = null;
			List<String> lines = Lists.newArrayList();
			if(location != null) {
				if(OptionUtil.readBooleanPRI(options, "r", false)) {
					export(KEY_GAODE_GEO + " " + GaodeUtils.reverseLonglat(location));
					location = GaodeUtils.reverseLonglat(location);
					//return true;
				}
				
				String radius = OptionUtil.readString(options, "r", "1000");
				boolean useTencent = OptionUtil.readBooleanPRI(options, "tx", true);
				
				double[] dog = LonglatUtil.longlatOf(location);
				boolean couldBeChina = true;
				if(dog != null) {
					couldBeChina = LonglatUtil.isInsideOf(dog, LonglatUtil.RANGE_CHINA_LONG, LonglatUtil.RANGE_CHINA_LAT);
				}
				
				if(useTencent || !couldBeChina) {
					lines = TencentUtils.regeocodeOf(GaodeUtils.reverseLonglat(location));
					textAddress = StrUtil.findFirstMatchedItem(JsonUtil.createRegexKey("address"), StrUtil.connect(lines));
					appendParam = true;
				} else {
					lines = GaodeUtils.regeocodeOf(location, radius);
					String temp = StrUtil.connect(lines);
					String formattedAddress = StrUtil.findFirstMatchedItem(JsonUtil.createRegexKey("formatted_address"), temp);
					if(formattedAddress != null) {
						String province = StrUtil.findFirstMatchedItem(JsonUtil.createRegexKey("province"), temp);
						textAddress = formattedAddress.replaceAll("^" + province, "");
					} else {
						appendParam = true;
						lines = TencentUtils.regeocodeOf(GaodeUtils.reverseLonglat(location));
						temp = StrUtil.connect(lines);
						textAddress = StrUtil.findFirstMatchedItem(JsonUtil.createRegexKey("address"), temp);
					}
				}
			} else {
				lines = GaodeUtils.geocodeOf(solo, city);
				String temp = StrUtil.connect(lines);
				location = StrUtil.findFirstMatchedItem(JsonUtil.createRegexKey("location"), temp);
				String formattedAddress = StrUtil.findFirstMatchedItem(JsonUtil.createRegexKey("formatted_address"), temp);
				String province = StrUtil.findFirstMatchedItem(JsonUtil.createRegexKey("province"), temp);
				if(formattedAddress != null) {
					textAddress = formattedAddress.replaceAll("^" + province, "");
				}
			}
			
			if(!showJson) {
				lines = prettyFormatOf(lines);
			}

			if(appendParam) {
				lines.add(0, StrUtil.occupy("param_location: {0}", location));
			}
			
			export(lines);
			if(OptionUtil.readBooleanPRI(options, "w", false)) {
				String temp = "?location={0}&address={1}";
				String requestParams = StrUtil.occupy(temp, location, textAddress);
				String site = g().getUserValueOf("picker.site", HttpHelper.URL_AKA10_PICKER);
				String pickerUrl = site + requestParams;
				viewPage(pickerUrl);
			}
			if(OptionUtil.readBooleanPRI(options, "ww", false)) {
				String temp = "location = \"{0}\"; address = \"{1}\";";
				String variables = StrUtil.occupy(temp, location, textAddress);
				generatePicker(variables);
			} 

			return true;
		}
		
		solo = parseParam(KEY_GAODE_IP + "(|\\s+.+)");
		if(solo != null) {
			List<String> lines = null;
			if(solo.isEmpty()) {
				lines = GaodeUtils.locationOfIp();
			} else {
				lines = GaodeUtils.locationOfIp(solo);
			}
			lines = prettyFormatOf(lines);
			
			export(lines);
			
			return true;
		}
		
		LongOrLat item = LonglatUtil.longOrLatOfDMS(command);
		if(item != null) {
			List<String> list = Lists.newArrayList();
			String pretty = MathUtil.toPrettyString(item.getDecimal(), 7);
			if(StrUtil.isRegexMatched("[SW]", item.getFlag())) {
				pretty = "-" + pretty;
			}
			if(item.getType() != null) {
				list.add(item.getType() + " " + pretty);
			} else {
				list.add(LongOrLat.TYPE_LONGITUDE + " " + pretty);
				list.add(LongOrLat.TYPE_LATITUDE + " " + pretty);
			}
			
			export(list);
			return true;
		}

		regex = KEY_LONGLAT + "\\s+(.+)";
		params = parseParams(regex);
		if(params != null) {
			String type = params[0];
			List<String> items = StrUtil.splitByRegex(params[1], "\\s+");
			String msg = "Invalid coordination: " + params[1];
			LongOrLat jack;
			List<String> list = Lists.newArrayList();
			if(items.size() == 1) {
				Double decimal = MathUtil.toDouble(items.get(0));
				if(decimal == null) {
					XXXUtil.alerto(msg);
				}
				jack = new LongOrLat(type, decimal);
				list.add(jack.getTypedDMS());
			} else {
				Integer degree = MathUtil.toInteger(items.get(0));
				if(degree == null) {
					XXXUtil.alerto(msg);
				}
				
				Integer minute = MathUtil.toInteger(items.get(1));
				if(minute == null) {
					XXXUtil.alerto(msg);
				}
				
				double second = 0;
				if(items.size() == 3) {
					Double second2 = MathUtil.toDouble(items.get(2));
					if(second2 == null) {
						XXXUtil.alerto(msg);
					} else {
						second = second2;
					}
				}
				
				jack = new LongOrLat(type, degree, minute, second);
				list.add(jack.getTypedDecimal());
			}
			
			list.add(jack.toDegreeMinuteSecondNEWS(LongOrLat.DEGREE, LongOrLat.MINUTE, LongOrLat.SECOND));
			list.add(jack.toDegreeMinuteSecondNEWS(LongOrLat.DEGREE, "'", "\""));
			list.add(jack.toDegreeMinuteSecondNEWS(" ", " ", ""));
			
			export(list);
			
			return true;
		}
		
		return false;
	}
	
	private void generatePicker(String addressAndLocationVariables) {
		String filePath = "/data/pickerT.html";
		List<String> lines = IOUtil.readLines(filePath, Konstants.CODE_UTF8);
		String newLine = null;
		int index = 0;
		String key = "//PLACEHOLDER";
		for(String line : lines) {
			if(line.trim().startsWith(key)) {
				newLine = line.replaceAll(key + ".+", addressAndLocationVariables);
				break;
			}
			index++;
		}
		if(newLine == null) {
			XXXUtil.alert("Uncanny, not found placeholder: " + key);
		}
		lines.set(index, newLine);
		String fileName = "picker.html";
		String dir = getExportLocation();
		String pcikerFilepath = dir + fileName;
		if(SimpleKonfig.g().isExportWithTimestampEnabled(options)) {
			pcikerFilepath = dir + DateUtil.timestamp() + "_" + fileName;
		}

		IOUtil.saveAsTxtWithCharset(lines, pcikerFilepath, Konstants.CODE_UTF8);
		C.pl2("Exported => " + pcikerFilepath);
		FileOpener.open(pcikerFilepath);
	}
	
	private List<String> prettyFormatOf(List<String> lines) {
		List<String> ignoredKeys = StrUtil.split("status,info,infocode,count,message,request_id");
		return prettyFormatOf(lines, ignoredKeys);
	}
	
	private List<String> prettyFormatOf(List<String> lines, List<String> ignoredKeys) {
		List<String> items = Lists.newArrayList();
		String regex = "\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"";
		for(String line : lines) {
			Matcher ma = StrUtil.createMatcher(regex, line);
			if(ma.find()) {
				String key = ma.group(1);
				if(!ignoredKeys.contains(key)) {
					items.add(line.trim().replaceAll("(\"|,$)", ""));
				}
			}
		}
		
		return items;
	}
	
	private boolean dealWithDistance(String placeInfo, String city) {
		String self = GaodeUtils.fetchLonglat(placeInfo, city);
		if(self != null) {
			return false;
		}
		
		String[] points = StrUtil.parseParams("(.+?)\\s+(.+?)", placeInfo);
		if(points == null) {
			return false;
		}

		String origin = GaodeUtils.fetchLonglat(points[0], city, true);
		String dest = null;
		String msg = null;
		if(origin == null) {
			msg = "Not a valid location: " + points[0];
		} else {
			dest = GaodeUtils.fetchLonglat(points[1], city, true);
			if(dest == null) {
				msg = "Not a valid location: " + points[1];
			}
		}
		
		if(!EmptyUtil.isNullOrEmpty(msg)) {
			export(msg);
			return true;
		}
		
		List<String> lines = Lists.newArrayList();
		boolean isGood = false;
		try {
			int[] info = GaodeUtils.distanceAndDuration(origin, dest);
			String distance = GaodeUtils.niceDistance(info[0]);
			String duration = MathUtil.dhmsStrOfSeconds(info[1]);
			lines.add(StrUtil.occupy("{0}-{1}: {2}, {3}", points[0], points[1], distance, duration));
			isGood = true;
		} catch (Exception ex) {
			lines.add(ex.getMessage());
		}
		if(isGood) {
			int[] info = GaodeUtils.distanceAndDuration(dest, origin);
			String distance = GaodeUtils.niceDistance(info[0]);
			String duration = MathUtil.dhmsStrOfSeconds(info[1]);
			lines.add(StrUtil.occupy("{0}-{1}: {2}, {3}", points[1], points[0], distance, duration));
		} else {
			String prefix = StrUtil.equals(points[0], origin) ? "" : points[0] + " ";
			lines.add("A: " + prefix + GaodeUtils.tieLocation(origin));
			
			prefix = StrUtil.equals(points[1], dest) ? "" : points[1] + " ";
			lines.add("B: " + prefix + GaodeUtils.tieLocation(dest));
		}
		
		export(lines);
		return true;
	}
}
