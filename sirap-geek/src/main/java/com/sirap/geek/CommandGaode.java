package com.sirap.geek;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.geek.domain.DistrictItem;
import com.sirap.geek.manager.GaodeManager;
import com.sirap.geek.manager.GaodeUtils;

public class CommandGaode extends CommandBase {
	private static final String KEY_GAODE = "gao";
	private static final String KEY_GAODE_LIST = "list";
	private static final String KEY_GAODE_GEO = "geo";
	private static final String KEY_GAODE_REGEO = "regeo";

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
			boolean showUpperLevels = OptionUtil.readBooleanPRI(options, "up", false);
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
		}
		
		params = parseParams(KEY_GAODE_LIST + "\\s+(\\S+)(|\\s+[0-3]?)");
		if(params != null) {
			String keyword = params[0];
			String level = "0";
			if(!params[1].isEmpty()) {
				level = params[1];
			}
			
			String result = GaodeUtils.districtsOf(keyword, level);
			export(result);
			return true;
		}
		
		params = parseParams(KEY_GAODE_GEO + "\\s+(\\S+)(|\\s+\\S+)");
		if(params != null) {
			String address = params[0];
			String city = params[1];
			
			String result = GaodeUtils.geocodeOf(address, city);
			export(result);
			return true;
		}
		
		params = parseParams(KEY_GAODE_REGEO + "\\s+(\\S+)(|\\s+\\d+)");
		if(params != null) {
			String location = params[0];
			String radius = "1000";
			if(!params[1].isEmpty()) {
				radius = params[1];
			}
			
			String result = GaodeUtils.regeocodeOf(location, radius);
			export(result);
			return true;
		}
		
		return false;
	}
}
