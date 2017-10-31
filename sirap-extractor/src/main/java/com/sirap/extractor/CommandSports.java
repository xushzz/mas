package com.sirap.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.extractor.domain.SportsMatchItem;
import com.sirap.extractor.manager.Extractors;

public class CommandSports extends CommandBase {
	private static final String KEY_UEFA = "uefa";
	private static final String KEY_CHINA = "china";
	private static final String KEY_SPORTS_TABLE = "-t";
	private static final String KEY_SPORTS_SCHEDULE = "-s";
	private static final String KEY_SPORTS_GOALS = "-g";
	private static final String KEY_LEAGUES;
	private static final String KEY_BIG5_CHINA;
	private static final String KEY_BIG5_CHINA_NAMES;

	public static final Map<String, Integer> BIG5_CHINA_IDS = Maps.newHashMap();
	static {
		BIG5_CHINA_IDS.put("eng", 2);
		BIG5_CHINA_IDS.put("esp", 3);
		BIG5_CHINA_IDS.put("ita", 4);
		BIG5_CHINA_IDS.put("ger", 5);
		BIG5_CHINA_IDS.put("fra", 6);
		BIG5_CHINA_IDS.put("china", 128);

		List<String> items = new ArrayList<>(BIG5_CHINA_IDS.keySet());
		items.add("\\d{1,3}");
		KEY_BIG5_CHINA = "(" + StrUtil.connect(items, "|") + ")";
	}

	public static final Map<String, int[]> BIG5_LEVELS = Maps.newHashMap();
	static {
		BIG5_LEVELS.put("England", new int[]{3,1,2,3});
		BIG5_LEVELS.put("Spain", new int[]{3,1,2,3});
		BIG5_LEVELS.put("Italy", new int[]{2,1,2,3});
		BIG5_LEVELS.put("Germany", new int[]{3,1,2,3});
		BIG5_LEVELS.put("France", new int[]{2,1,1,3});
	}
	
	public static final Map<String, String> BIG5_NAMES = Maps.newHashMap();
	static {
		BIG5_NAMES.put("eng", "England");
		BIG5_NAMES.put("esp", "Spain");
		BIG5_NAMES.put("ita", "Italy");
		BIG5_NAMES.put("ger", "Germany");
		BIG5_NAMES.put("fra", "France");

		List<String> items = new ArrayList<>(BIG5_NAMES.keySet());
		KEY_BIG5_CHINA_NAMES = "(" + StrUtil.connect(items, "|") + ")";
	}

	public static final Map<String, Integer> LEAGUE_IDS = Maps.newHashMap();
	static {
		LEAGUE_IDS.putAll(BIG5_CHINA_IDS);
		LEAGUE_IDS.put("uefa", 9);
		
		List<String> items = new ArrayList<>(LEAGUE_IDS.keySet());
		items.add("\\d{1,9}");
		KEY_LEAGUES = "(" + StrUtil.connect(items, "|") + ")";
	}

	public boolean handle() {
		params = parseParams(KEY_LEAGUES + KEY_SPORTS_GOALS + "(|\\s+\\S+|\\.\\.)");
		if(params != null) {
			String idInfo = params[0].toLowerCase();
			String criteria = params[1];
			Integer id = MathUtil.toInteger(idInfo);
			if(id == null) {
				id = LEAGUE_IDS.get(idInfo);
			}
			if(id == null) {
				C.pl2("Illegal id " + id + ", must be one of " + KEY_LEAGUES);
			} else {
				List<MexObject> items = Extractors.fetchHupuFootballScorers(id);
				if(StrUtil.equals(KEY_2DOTS, criteria)) {
					exportWithDefaultOptions(items);
				} else {
					int topN = OptionUtil.readIntegerPRI(options, "n", 10);
					if(topN > 0) {
						items = CollectionUtil.top(items, topN + 1);
					}
					
					if(EmptyUtil.isNullOrEmpty(criteria)) {
						export(items);
					} else {
						export(CollectionUtil.filter(items, criteria));
					}
				}
			}
			
			return true;
		}
		
		params = parseParams(KEY_BIG5_CHINA_NAMES + KEY_SPORTS_TABLE + "(|\\s+\\S+)");
		if(params != null) {
			String nameInfo = params[0].toLowerCase();
			String criteria = params[1];
			String name = BIG5_NAMES.get(nameInfo);
			if(name == null) {
				C.pl2("Illegal name " + name + ", must be one of " + KEY_BIG5_CHINA_NAMES);
			} else {
				List<MexObject> items = Extractors.fetchHupuFootballBig5Table(name);
				if(OptionUtil.readBooleanPRI(options, "mark", true)) {
					int[] levels = BIG5_LEVELS.get(name);
					if(levels != null) {
						tableWithLevels(items, levels);
					}
				}
				if(EmptyUtil.isNullOrEmpty(criteria)) {
					export(items);
				} else {
					export(CollectionUtil.filter(items, criteria));
				}
			}
			
			return true;
		}
		
		params = parseParams(KEY_BIG5_CHINA + KEY_SPORTS_SCHEDULE + "(|\\s+\\S+|\\.\\.)");
		if(params != null) {
			String idInfo = params[0].toLowerCase();
			String criteria = params[1];
			Integer id = MathUtil.toInteger(idInfo);
			if(id == null) {
				id = BIG5_CHINA_IDS.get(idInfo);
			}
			if(id == null) {
				C.pl2("Illegal id " + id + ", must be one of " + BIG5_CHINA_IDS);
			} else {
				List<SportsMatchItem> items = Extractors.fetchHupuFootballSchedule(id);
				if(StrUtil.equals(KEY_2DOTS, criteria)) {
					exportWithDefaultOptions(items);
				} else if(EmptyUtil.isNullOrEmpty(criteria)) {
					String today = DateUtil.displayNow("yyyy-MM-dd");
					Integer lastK = OptionUtil.readInteger(options, "L");
					List<SportsMatchItem> todayItems = null;
					if(lastK != null && lastK > 0) {
						todayItems = CollectionUtil.filter(items, "<=" + today);
						exportWithDefaultOptions(CollectionUtil.last(todayItems, lastK));
					} else {
						int nextK = OptionUtil.readIntegerPRI(options, "N", 10);
						if(nextK > 0) {
							todayItems = CollectionUtil.filter(items, ">=" + today);
							exportWithDefaultOptions(CollectionUtil.top(todayItems, nextK));
						} 
					}
				} else {
					exportWithDefaultOptions(CollectionUtil.filter(items, criteria));
				}
			}
			
			return true;
		}
		
		solo = parseSoloParam(KEY_CHINA + KEY_SPORTS_TABLE + "(|\\s\\S+)");
		if(solo != null) {
			List<MexObject> items = Extractors.fetchHupuFootballChinaTable();
			if(EmptyUtil.isNullOrEmpty(solo)) {
				export(items);
			} else {
				export(CollectionUtil.filter(items, solo));
			}
			
			return true;
		}
		
		solo = parseSoloParam(KEY_UEFA + KEY_SPORTS_TABLE + "(|\\s\\S+)");
		if(solo != null) {
			List<MexObject> items = Extractors.fetchUefaChampionsTable();
			if(EmptyUtil.isNullOrEmpty(solo)) {
				export(items);
			} else {
				export(CollectionUtil.filter(items, solo));
			}
			
			return true;
		}
		
		solo = parseSoloParam(KEY_UEFA + KEY_SPORTS_SCHEDULE + "(|\\s+\\S+|\\.\\.)");
		if(solo != null) {
			String criteria = solo;
			List<SportsMatchItem> items = Extractors.fetchUefaChampionsSchedule();
			if(StrUtil.equals(KEY_2DOTS, criteria)) {
				export(items);
			} else if(EmptyUtil.isNullOrEmpty(criteria)) {
				String today = DateUtil.displayNow("yyyy-MM-dd");
				Integer lastK = OptionUtil.readInteger(options, "L");
				List<SportsMatchItem> todayItems = null;
				if(lastK != null && lastK > 0) {
					todayItems = CollectionUtil.filter(items, "<=" + today);
					export(CollectionUtil.last(todayItems, lastK));
				} else {
					int nextK = OptionUtil.readIntegerPRI(options, "N", 16);
					if(nextK > 0) {
						todayItems = CollectionUtil.filter(items, ">=" + today);
						export(CollectionUtil.top(todayItems, nextK));
					} 
				}
			} else {
				export(CollectionUtil.filter(items, criteria));
			}
		}

		return false;
	}

	public static void tableWithLevels(List<MexObject> items, int[] levels) {
		char[] cars = {8364, 8730, 8721, 8595};
		for(MexObject item : items) {
			String order = StrUtil.findFirstMatchedItem("#(\\d{1,2})", item.getString());
			if(order == null) {
				continue;
			}
			
			int number = Integer.parseInt(order);
			int levelA = levels[0];
			if(number <= levelA) {
				item.setObj(item.getString().replace('#', cars[0]));
				continue;
			}
			
			int levelB = levelA + levels[1];
			if(number <= levelB) {
				item.setObj(item.getString().replace('#', cars[1]));
				continue;
			}
			
			int levelC = levelB + levels[2];
			if(number <= levelC) {
				item.setObj(item.getString().replace('#', cars[2]));
				continue;
			}
			
			int levelD = items.size() - levels[3];
			if(number >= levelD) {
				item.setObj(item.getString().replace('#', cars[3]));
				continue;
			}
		}
	}
}
