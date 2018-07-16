package com.sirap.common.framework;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.SatoUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class JanitorHelper {
	
	private static final String KEY_DYNAMIC = "(\\S+)\\s(.+)";
	
	public static String findAliasFromUserProperties(String command) {
		String[] params = StrUtil.parseParams(KEY_DYNAMIC, command);
		if(params != null) {
			String dynamite = getDynamicCommand(params[0]);
			if(dynamite != null) {
				String alias = StrUtil.occupy(dynamite, params[1]);
				C.pl("[Dynamic] " + dynamite + " = " + alias);
				return alias;
			}
		}
		
		TypedKeyValueItem entry = SimpleKonfig.g().getUserConfigEntry(command);
		if(entry != null) {
			String alias = entry.getValueX();
			C.pl("[Alias] " + entry.getKey() + " = " + alias);
			return alias;
		}
		
		return null;
	}
	
	public static String findCoinsFromSatos(String origin) {
		String niceinput = origin;
    	try {
			List<TypedKeyValueItem> items = Lists.newArrayList();
			items.addAll(SatoUtil.systemPropertiesAndEnvironmentVaribables());
			items.addAll(SimpleKonfig.g().getUserProps().listOf());
			String after = SatoUtil.occupyCoins(niceinput, items);
			if(!StrUtil.equals(niceinput, after)) {
				C.pl("[Coin] " + niceinput + " = " + after);
				niceinput = after;
			}
			return niceinput;
		} catch (MexException ex) {
			XXXUtil.alert(ex);
		}
		return null;
	}
	
	//nin {0} = E:/KDB/statics/wx 3 {0}
	private static String getDynamicCommand(String prefix) {
		HashMap<String, String> map = SimpleKonfig.g().getUserProps().getKeyValuesByPartialKeyword(prefix);
		Iterator<String> it = map.keySet().iterator();
		
		while(it.hasNext()) {
			String key = it.next();
			String temp = key.replace(prefix, "").trim();
			String regex = "\\{\\d{1,2}\\}";
			if(StrUtil.isRegexMatched(regex, temp)) {
				String value = map.get(key);
				
				return value;
			}
		}
		
		return null;
	}
}
