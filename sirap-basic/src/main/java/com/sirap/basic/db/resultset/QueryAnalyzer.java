package com.sirap.basic.db.resultset;

import java.util.List;

@SuppressWarnings("rawtypes")
public abstract class QueryAnalyzer<T extends Object> {
	public T analyze(List rows){
		return null;
	};

	public List<T> getListOf(List rows){
		return null;
	};
}
