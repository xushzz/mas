package com.sirap.db;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.StrUtil;
import com.sirap.db.adjustor.QueryAdjustorMySql;
import com.sirap.db.adjustor.QueryAdjustorOracle;
import com.sirap.db.adjustor.QueryAdjustorSqlServer;
import com.sirap.db.adjustor.QuerySqlAdjustor;
import com.sirap.db.parser.SchemaNameParser;
import com.sirap.db.parser.SchemaParserMySql;
import com.sirap.db.parser.SchemaParserOracle;
import com.sirap.db.parser.SchemaParserSqlServer;

public class DBFactory {

	public static QuerySqlAdjustor getQuerySqlAdjustor(String dbType) {
		if(StrUtil.equals(Konstants.DB_TYPE_MYSQL, dbType)) {
			return new QueryAdjustorMySql();
		}
		
		if(StrUtil.equals(Konstants.DB_TYPE_ORACLE, dbType)) {
			return new QueryAdjustorOracle();
		}
		
		if(StrUtil.equals(Konstants.DB_TYPE_SQLSERVER, dbType)) {
			return new QueryAdjustorSqlServer();
		}
		
		return null;
	}
	
	public static SchemaNameParser getSchemaNameParser(String dbType) {
		if(StrUtil.equals(Konstants.DB_TYPE_MYSQL, dbType)) {
			return new SchemaParserMySql();
		}
		
		if(StrUtil.equals(Konstants.DB_TYPE_ORACLE, dbType)) {
			return new SchemaParserOracle();
		}
		
		if(StrUtil.equals(Konstants.DB_TYPE_SQLSERVER, dbType)) {
			return new SchemaParserSqlServer();
		}
		
		return null;
	}
}
