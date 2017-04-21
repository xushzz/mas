package com.sirap.db.parser;

import com.sirap.db.DBConfigItem;

public abstract class ConfigItemParser {
	public abstract DBConfigItem parse(String source);
}
