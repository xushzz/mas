package com.sirap.db.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import com.google.common.collect.Lists;

public class ResultSetStringMatrixAnalyer extends ResultSetAnalyzer<List<List<String>>> {

	private boolean getColumn = true;

	public ResultSetStringMatrixAnalyer() {
		
	}
	
	public ResultSetStringMatrixAnalyer(boolean getColumn) {
		this.getColumn = getColumn;
	}
	
	@Override
	public List<List<String>> analyze(ResultSet rocks) throws Exception {
		List<List<String>> records = Lists.newArrayList();
		
		ResultSetMetaData meta = rocks.getMetaData();
    	int count = meta.getColumnCount();
    	
    	List<String> items = Lists.newArrayList();
    	if(getColumn) {
	    	for(int i = 1; i <= count; i++) {
	    		items.add(meta.getColumnName(i));
	    	}
	    	records.add(items);
    	}

        while (rocks.next()) {
        	items = Lists.newArrayList();
        	for(int i = 1; i <= count; i++) {
        		items.add(rocks.getObject(i) + "");
        	}
        	records.add(items);
        }
		
		return records;		
	}
}
