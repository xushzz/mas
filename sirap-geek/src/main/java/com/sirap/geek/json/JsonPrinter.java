package com.sirap.geek.json;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sirap.basic.util.StrUtil;

public class JsonPrinter {
	
	private StringBuffer pretty = new StringBuffer();
	private StringBuffer raw = new StringBuffer();
	
	private Object king;
	
	public JsonPrinter(Object king) {
		this.king = king;
	}

	private String space(int depth) {
		return StrUtil.repeat(' ', 4 * depth);
	}
	
	public String getRawText() {
		printRaw(king, 0, false);
		String value = raw.toString();
		
		return value;
	}
	
	public String getPrettyText() {
		printPretty(king, 0, king instanceof List, false);
		String value = pretty.toString();
		value = value.replaceAll("\n$", "");
		
		return value;
	}
	
	@SuppressWarnings("rawtypes")
	private void printPretty(Object element, int depth, boolean fromList, boolean printComma) {
		if(element instanceof Map) {
			Map map = (Map)element;
			Iterator it = map.keySet().iterator();
			
			if(fromList) {
				pretty.append(space(depth));
			}
			pretty.append("{");
			pretty.append("\n");
			
			int count = 0;
			while(it.hasNext()) {
				count++;
				Object key = it.next();
				pretty.append(space(depth +1));
				pretty.append("\"" + key + "\":");
				Object obj = map.get(key);
				boolean isNotLastElement = count < map.size();
				printPretty(obj, depth + 1, false, isNotLastElement);
				boolean isComplex = obj instanceof List || obj instanceof Map;
				if(!isComplex) {
					pretty.append("\n");
				}
			}
			
			pretty.append(space(depth));
			pretty.append("}" + (printComma ? "," : "")).append("\n");
			
			return;
		}
		
		if(element instanceof List) {
			List list = (List)element;
			if(fromList) {
				pretty.append(space(depth));
			}
			pretty.append("[");
			pretty.append("\n");
			
			int count = 0;
			for(Object obj : list) {
				count++;
				boolean isNotLastElement = count < list.size();
				printPretty(obj, depth + 1, true, isNotLastElement);
				boolean isComplex = obj instanceof List || obj instanceof Map;
				if(!isComplex) {
					pretty.append("\n");
				}
			}
			
			pretty.append(space(depth));
			pretty.append("]" + (printComma ? "," : "")).append("\n");
			
			return;
		}
		
		if(fromList) {
			pretty.append(space(depth));
		}
		
		if(element instanceof String) {
			pretty.append("\"" + element + "\"");
		} else {
			pretty.append(element);
		}
		
		if(printComma) {
			pretty.append(",");
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void printRaw(Object element, int depth, boolean printComma) {
		if(element instanceof Map) {
			Map map = (Map)element;
			Iterator it = map.keySet().iterator();

			raw.append("{");
			
			int count = 0;
			while(it.hasNext()) {
				count++;
				Object key = it.next();
				raw.append("\"" + key + "\":");
				Object obj = map.get(key);
				boolean isNotLastElement = count < map.size();
				printRaw(obj, depth + 1, isNotLastElement);
			}
			
			raw.append("}" + (printComma ? "," : ""));
			
			return;
		}
		
		if(element instanceof List) {
			List list = (List)element;
			raw.append("[");
			
			int count = 0;
			for(Object obj : list) {
				count++;
				boolean isNotLastElement = count < list.size();
				printRaw(obj, depth + 1, isNotLastElement);
			}
			
			raw.append("]" + (printComma ? "," : ""));
			
			return;
		}
		
		if(element instanceof String) {
			raw.append("\"" + element + "\"");
		} else {
			raw.append(element);
		}
		
		if(printComma) {
			raw.append(",");
		}
	}
}
