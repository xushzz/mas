package com.sirap.basic.db.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.db.QueryWatcher;

public class ResultSetMingAnalyzer extends ResultSetAnalyzer<QueryWatcher> {

	@Override
	public QueryWatcher analyze(ResultSet rest) throws Exception {
		QueryWatcher ming = new QueryWatcher();
    	ResultSetMetaData meta = rest.getMetaData();
    	int count = meta.getColumnCount();
    	
    	if(true) {
    		List<String> items = new ArrayList<>();
	    	for(int i = 1; i <= count; i++) {
	    		items.add(meta.getColumnName(i));
	    	}
	    	ming.setColumnNames(items);
    	}

    	List<List> records = new ArrayList<>(); 
        while (rest.next()) {
        	List<Object> items = new ArrayList<>();
        	for(int i = 1; i <= count; i++) {
        		items.add(rest.getObject(i));
        	}
        	records.add(items);
        }
        ming.setRecords(records);
        
        return ming;
	}
}
