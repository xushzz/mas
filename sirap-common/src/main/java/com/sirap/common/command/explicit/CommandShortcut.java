package com.sirap.common.command.explicit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.framework.Janitor;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.Stash;

public class CommandShortcut extends CommandBase {
	
	private static final String KEY_TERMINAL_COMMAND = "c\\.";
	private static final String KEY_DYNAMIC = "(\\S+)\\s(.+)";

	@Override
	public boolean handle() {
		
		solo = parseParam(KEY_TERMINAL_COMMAND + "(.{3,}?)");
		if(solo != null) {
			executeInternalCmd(solo);
			return true;
		}
		
		String dosPrefix = g().getUserValueOf("dos.prefix");
		if(!EmptyUtil.isNullOrEmpty(dosPrefix)) {
			List<String> items = StrUtil.split(dosPrefix);
			for(String item : items) {
				if(StrUtil.equalsCaseSensitive(command, item) || command.startsWith(item + " ")) {
					String temp = command;
					String path = OptionUtil.readString(options, "p");
					if(!EmptyUtil.isNullOrEmpty(path)) {
						temp = translatePath(temp, path);
						C.pl(temp);
					}
					executeInternalCmd(temp);
					return true;
				}
			}
		}
		
		params = parseParams(KEY_DYNAMIC);
		if(params != null) {
			String dynamite = getDynamicCommand(params[0]);
			if(dynamite != null) {
				String temp = StrUtil.occupy(dynamite, params[1]);
				C.pl(command + "=" + temp);
				Janitor.g().process(temp);
				
				return true;
			}
		}
		
		TypedKeyValueItem entry = SimpleKonfig.g().getUserConfigEntry(command);
		if(entry != null) {
			String shortcutCommand = entry.getValueX();
			C.pl(entry.getKey() + "=" + shortcutCommand);
			boolean notYetStashed = Stash.g().read(Stash.KEY_USER_INPUT_TARGET) == null;
			if(notYetStashed) {
				boolean hasSpecifiedTarget = !EmptyUtil.isNullOrEmpty(target.getValue());
				if(hasSpecifiedTarget) {
					Stash.g().place(Stash.KEY_USER_INPUT_TARGET, target);
				}
			}
			Janitor.g().process(shortcutCommand);
			
			return true;
		}

		return false;
	}
	
	//nin {0} = E:/KDB/statics/wx 3 {0}
	private String getDynamicCommand(String prefix) {
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
