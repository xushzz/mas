package com.sirap.geek;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.geek.domain.DistrictItem;
import com.sirap.geek.manager.GaodeManager;
import com.sirap.geek.manager.GaodeUtils;
import com.sirap.geek.manager.TencentUtils;

public class CommandGaode extends CommandBase {
	private static final String KEY_GAODE = "gao";
	private static final String KEY_GAODE_INPUTTIPS = "gin";
	private static final String KEY_GAODE_SEARCH = "gas";
	private static final String KEY_GAODE_GEO = "geo";
	
	private static Map<String, String> LAKES = Maps.newConcurrentMap();
	static {
		LAKES.put("deli", "108.392544,22.828986");
		LAKES.put("tam", "116.397573,39.908743");
		LAKES.put("jia", "108.904706,24.777411");
	}

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
			List<DistrictItem> list2 = CollUtil.filter(items, solo, isCaseSensitive());
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
			export(items);
			
			return true;
		}
		
		solo = parseParam(KEY_GAODE_SEARCH + "\\s+(.+?)");
		if(solo != null) {
			String loc = LAKES.get(solo);
			String ack = loc != null ? loc : solo;
			List<String> lines = Lists.newArrayList();
			if(GaodeUtils.isCoordination(ack)) {
				String location = ack;
				String keywords = OptionUtil.readString(options, "k", "");
				String types = OptionUtil.readString(options, "t", "");
				String radius = OptionUtil.readString(options, "r", "");
				lines = GaodeUtils.searchPlaceAround(location, keywords, types, radius);
			} else {
				String keywords = ack, types = "";
				String kParam = OptionUtil.readString(options, "k", "");
				if(!kParam.isEmpty()) {
					keywords = kParam;
					types = ack;
				}
				String tParam = OptionUtil.readString(options, "t", "");
				if(!tParam.isEmpty()) {
					types = tParam;
				}
				String city = OptionUtil.readString(options, "c", "");
				lines = GaodeUtils.searchPlaceText(keywords, types, city);
			}

			export(lines);
			
			return true;
		}
		
		solo = parseParam(KEY_GAODE_GEO + "\\s+(.+?)");
		if(solo != null) {
			String loc = LAKES.get(solo);
			String ack = loc != null ? loc : solo;
			
			boolean showJson = OptionUtil.readBooleanPRI(options, "j", false);
			List<String> lines = Lists.newArrayList();
			if(GaodeUtils.isCoordination(ack)) {
				String location = ack;
				if(OptionUtil.readBooleanPRI(options, "r", false)) {
					export(KEY_GAODE_GEO + " " + GaodeUtils.reverseLongAndLat(location));
					
					return true;
				}
				String dest = OptionUtil.readString(options, "d");
				if(dest != null) {
					String mike = LAKES.get(dest);
					String destLocation = mike != null ? mike : dest;
					destLocation = destLocation.replace("+", ",");
					if(GaodeUtils.isCoordination(destLocation)) {
						String distance = GaodeUtils.distance(location, destLocation);
						export(distance);
					} else {
						export("Not a valid location: " + dest);
					}
					
					return true;
				}
				String radius = OptionUtil.readString(options, "r", "1000");
				lines = GaodeUtils.regeocodeOf(ack, radius);
				String regex = StrUtil.occupy("\"{0}\"\\s*:\\s*\"([^\"]+)\"", "formatted_address");
				String formattedAddress = StrUtil.findFirstMatchedItem(regex, StrUtil.connect(lines));
				if(formattedAddress == null) {
					lines.addAll(TencentUtils.regeocodeOf(GaodeUtils.reverseLongAndLat(location)));
				} else {
					lines.add(StrUtil.occupy("\"location\":\"{0}\"", location));
				}
			} else {
				String address = ack;
				String city = OptionUtil.readString(options, "c", "");
				lines = GaodeUtils.geocodeOf(address, city);
			}

			if(!showJson) {
				lines = prettyFormatOf(lines);
			}
			
			export(lines);
			if(OptionUtil.readBooleanPRI(options, "w", false)) {
				String variables = generateVariables(lines);
				generatePicker(variables);
			} 
			
			return true;
		}
		
		return false;
	}
	
	private String generateVariables(List<String> lines) {
		String regexTemp = "\"{0}\"\\s*:\\s*\"([^\"]+)\"";
		String[] keys = {"formatted_address", "location"};
		String regexA = StrUtil.occupy(regexTemp, keys[0]);
		String regexB = StrUtil.occupy(regexTemp, keys[1]);
		String oneline = StrUtil.connect(lines);
		String address = StrUtil.findFirstMatchedItem(regexA, oneline);
		String location = StrUtil.findFirstMatchedItem(regexB, oneline);
		if(EmptyUtil.isNullOrEmpty(address) || EmptyUtil.isNullOrEmpty(location)) {
			XXXUtil.alert("Result contains no valid {0} and {1}", keys[0], keys[1]);
		}
		
		String temp = "location = \"{0}\"; address = \"{1}\";";
		
		return StrUtil.occupy(temp, location, address);
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
		List<String> items = Lists.newArrayList();
		String regex = "\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"";
		for(String line : lines) {
			if(StrUtil.isRegexFound(regex, line)) {
				items.add(line.trim());
			}
		}
		
		return items;
	}
}
