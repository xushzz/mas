package com.sirap.basic.db.adjustor;

import java.util.regex.Matcher;

import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class QueryAdjustorSqlServer extends QuerySqlAdjustor {

	@Override
	public String showCurrentSchemaTables() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT a.name 'TABLE_NAME', cast(b.rows as varchar(10)) 'TABLE_ROWS' FROM sysobjects AS a");
		sb.append(" INNER JOIN sysindexes AS b ON a.id = b.id");
		sb.append(" WHERE (a.type = 'u') AND (b.indid IN (0, 1))");
		sb.append(" union all");
		sb.append(" select name, CONVERT(varchar(100), crdate, 121) as whenCreated");
		sb.append(" from sysobjects where xtype in ('P','V')");
		
		return sb.toString();
	}

	@Override
	public String showDatabases() {
		String temp = "SELECT Name 'SCHEMA_NAME' FROM Master..SysDatabases ORDER BY Name";
		return temp;
	}

	/***
	 * select dbms_metadata.get_ddl('TABLE','TABLE_NAME') from dual;
	 * select dbms_metadata.get_ddl('INDEX','INDEX_NAME') from dual;
	 */
	@Override
	public String showCreation(String sql, String type, String name) {
		String tempQuery = "select text from syscomments s1 join sysobjects s2 on s1.id=s2.id  where name='{0}'";
		String querySql = StrUtil.occupy(tempQuery, name);
		
		return querySql;
	}

	@Override
	public String showLimit(String sql, String limitKKK, String kkk) {
		
		String regex = "^select"; 
		Matcher ma = StrUtil.createMatcher(regex, sql);
		String temp = sql.replace(limitKKK, "");
		if(ma.find()) {
			String value = ma.group();
			temp = temp.replace(value, value + " top " + kkk);
		} else {
			XXXUtil.alert("There is not a select sentence: " + sql);
		}
		
		return temp;
	}

	@Override
	public String showTop(String sql, String temp, String kkk) {
		return showLimit(sql, temp, kkk);
	}
}
