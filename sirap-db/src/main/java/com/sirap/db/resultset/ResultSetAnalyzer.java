package com.sirap.db.resultset;

import java.sql.ResultSet;

public abstract class ResultSetAnalyzer<T extends Object>  {
	public abstract T analyze(ResultSet rocks) throws Exception;
}
