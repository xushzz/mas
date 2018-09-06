package com.sirap.db.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import com.google.common.collect.Lists;

public class ResultSetObjectMatrixAnalyer extends ResultSetAnalyzer<List<List<Object>>> {

	private boolean getColumn = true;

	public ResultSetObjectMatrixAnalyer() {
		
	}
	
	public ResultSetObjectMatrixAnalyer(boolean getColumn) {
		this.getColumn = getColumn;
	}
	
	@Override
	public List<List<Object>> analyze(ResultSet rocks) throws Exception {
		List<List<Object>> records = Lists.newArrayList();

		ResultSetMetaData meta = rocks.getMetaData();
    	int count = meta.getColumnCount();
    	
    	List<Object> items = Lists.newArrayList();
    	if(getColumn) {
	    	for(int i = 1; i <= count; i++) {
	    		items.add(meta.getColumnName(i));
	    	}
	    	records.add(items);
    	}

        while (rocks.next()) {
        	items = Lists.newArrayList();
        	for(int i = 1; i <= count; i++) {
        		items.add(rocks.getObject(i));
        	}
        	records.add(items);
        }
		
		return records;		
	}
}
