package com.sirap.basic.db.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.KeyValuesItem;

public class ResultSetKeyValuesAnalyer extends ResultSetAnalyzer<List<KeyValuesItem>> {

	@Override
	public List<KeyValuesItem> analyze(ResultSet rocks) throws Exception {
		List<KeyValuesItem> records = Lists.newArrayList();
		
		ResultSetMetaData meta = rocks.getMetaData();
    	int count = meta.getColumnCount();
    	
    	List<String> keys = Lists.newArrayList();
    	for(int i = 1; i <= count; i++) {
    		keys.add(meta.getColumnName(i));
    	}

        while (rocks.next()) {
        	KeyValuesItem loris = new KeyValuesItem();
        	for(int i = 1; i <= count; i++) {
        		loris.add(keys.get(i - 1), rocks.getObject(i));
        	}
        	records.add(loris);
        }
		
		return records;		
	}
}
