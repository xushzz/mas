package com.sirap.db;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.util.StrUtil;

@SuppressWarnings("rawtypes")
public class QueryWatcher {
	
	private boolean printColumnName;
	private String sql;
	private List<String> columnNames;
	private List<List<Object>> records;
	
	public QueryWatcher() {}
	
	public QueryWatcher(String sql) {
		this.sql = sql;
	}
	
	public boolean isPrintColumnName() {
		return printColumnName;
	}

	public void setPrintColumnName(boolean printColumnName) {
		this.printColumnName = printColumnName;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}
	
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}
	
	public List<List<Object>> getRecords() {
		return records;
	}
	public void setRecords(List<List<Object>> records) {
		this.records = records;
	}
	
	public List<String> exportLiteralStrings() {
		List<String> allRecords = new ArrayList<>();
		
		if(printColumnName && columnNames != null) {
			allRecords.add(columnNames.toString());
		}
		
		if(records != null) {
			for(List<Object> items : records) {
				String item = StrUtil.connect(items, ", ");
				allRecords.add(item);
			}
		}
		
		return allRecords;
	}
	
	public List<List> exportListItems() {
		List<List> allRecords = new ArrayList<>();
		
		if(printColumnName && columnNames != null) {
			allRecords.add(columnNames);
		}
		
		if(records != null) {
			for(List<Object> items : records) {
				allRecords.add(items);
			}
		}
		
		return allRecords;
	}
}
