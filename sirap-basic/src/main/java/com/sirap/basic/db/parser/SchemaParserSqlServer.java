package com.sirap.basic.db.parser;


public class SchemaParserSqlServer extends SchemaNameParser {

	/***
	 * jdbc:sqlserver://localhost:1433;DatabaseName=laos;integratedSecurity=TRUE;
	 */
	@Override
	public String getRegex() {
//		String regex = ":\\d+:([^/?]{1,99})";
		String regex = ";DatabaseName=([^;]+);?";
		
		return regex;
	}
	
}
