package com.sirap.basic.db.adjustor;

public class QueryAdjustorMySql extends QuerySqlAdjustor {
	@Override
	public String showTop(String sql, String topKKK, String kkk) {
		
		String temp = sql.replace(topKKK, " limit " + kkk);
		return temp;
	}
}
