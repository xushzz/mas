package com.sirap.basic.db;

import com.sirap.basic.component.DBKonstants;
import com.sirap.basic.db.adjustor.QueryAdjustorMySql;
import com.sirap.basic.db.adjustor.QueryAdjustorOracle;
import com.sirap.basic.db.adjustor.QueryAdjustorSqlServer;
import com.sirap.basic.db.adjustor.QuerySqlAdjustor;
import com.sirap.basic.db.parser.SchemaNameParser;
import com.sirap.basic.db.parser.SchemaParserMySql;
import com.sirap.basic.db.parser.SchemaParserOracle;
import com.sirap.basic.db.parser.SchemaParserSqlServer;
import com.sirap.basic.util.StrUtil;

public class DBFactory {

	public static QuerySqlAdjustor getQuerySqlAdjustor(String dbType) {
		if(StrUtil.equals(DBKonstants.DB_TYPE_MYSQL, dbType)) {
			return new QueryAdjustorMySql();
		}
		
		if(StrUtil.equals(DBKonstants.DB_TYPE_ORACLE, dbType)) {
			return new QueryAdjustorOracle();
		}
		
		if(StrUtil.equals(DBKonstants.DB_TYPE_SQLSERVER, dbType)) {
			return new QueryAdjustorSqlServer();
		}
		
		return null;
	}
	
	public static SchemaNameParser getSchemaNameParser(String dbType) {
		if(StrUtil.equals(DBKonstants.DB_TYPE_MYSQL, dbType)) {
			return new SchemaParserMySql();
		}
		
		if(StrUtil.equals(DBKonstants.DB_TYPE_ORACLE, dbType)) {
			return new SchemaParserOracle();
		}
		
		if(StrUtil.equals(DBKonstants.DB_TYPE_SQLSERVER, dbType)) {
			return new SchemaParserSqlServer();
		}
		
		return null;
	}
}
