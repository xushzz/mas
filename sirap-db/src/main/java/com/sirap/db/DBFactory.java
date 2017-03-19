package com.sirap.db;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.StrUtil;
import com.sirap.db.adjustor.QueryAdjustorMySql;
import com.sirap.db.adjustor.QueryAdjustorOracle;
import com.sirap.db.adjustor.QuerySqlAdjustor;
import com.sirap.db.parser.SchemaNameParser;
import com.sirap.db.parser.SchemaParserMySql;
import com.sirap.db.parser.SchemaParserOracle;

public class DBFactory {

	public static QuerySqlAdjustor getQuerySqlAdjustor(String dbType) {
		if(StrUtil.equals(Konstants.DB_TYPE_MYSQL, dbType)) {
			return new QueryAdjustorMySql();
		}
		
		if(StrUtil.equals(Konstants.DB_TYPE_ORACLE, dbType)) {
			return new QueryAdjustorOracle();
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
		
		return null;
	}
}
