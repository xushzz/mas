package com.sirap.basic.db.parser;


public class SchemaParserOracle extends SchemaNameParser {

	/***
	 * jdbc:oracle:thin:@localhost:1521:orcle
	 */
	@Override
	public String getRegex() {
		String regex = ":\\d+:([^/?]{1,99})";
		
		return regex;
	}
	
}
