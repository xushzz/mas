package com.sirap.db;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.util.MatrixUtil;

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
		return exportLiteralStrings(false, false, ", ");
	}
	
	public List<String> exportLiteralStrings(boolean rotate, boolean pretty, String connector) {
		List<List> total = Lists.newArrayList();
		if(printColumnName && columnNames != null) {
			total.add(columnNames);
		}
		total.addAll(records);
		
		if(rotate) {
			total = MatrixUtil.rotate(total);
		}
		
		if(pretty) {
			return MatrixUtil.prettyMatrixLines(total, connector);
		} else {
			return MatrixUtil.lines(total, connector);
		}
	}
	
	public List<List> exportListItems() {
		return exportListItems(false);
	}
	
	public List<List> exportListItems(boolean rotate) {
		List<List> total = Lists.newArrayList();
		if(printColumnName && columnNames != null) {
			total.add(columnNames);
		}
		total.addAll(records);
		
		if(rotate) {
			total = MatrixUtil.rotate(total);
		}
		
		return total;
	}
}
