package com.sirap.executor.ssh;

import java.util.Map;

import com.google.common.collect.Maps;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;

public class SshUtil {
	public static final Map<String, String> ALIASES = Maps.newConcurrentMap();
	static {
		ALIASES.put("ll", "ls -lhtr --time-style '+%Y/%m/%d %H:%M:%S'");
		ALIASES.put("la", "ls -alhtr --time-style '+%Y/%m/%d %H:%M:%S'");
	}

	public static String useAlias(String command) {
		String leadword = StrUtil.findFirstMatchedItem("^(\\S+)", command);
		String temp = command;
		if(ALIASES.containsKey(leadword)) {
			String random = RandomUtil.letters(9);
			temp = (random + command).replace(random + leadword, ALIASES.get(leadword));
		}
		
		return temp;
	}
}
