package com.sirap.basic.json;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.JsonUtil;
import com.sirap.basic.util.StrUtil;

public class JsonBoxTest {
	
	String dir = "D:/KDB/tasks/0423_JsonParse/";
	String dirSat = "D:/KDB/tasks/0803_Statistics/";

	@Test
	public void parseIP() {
		String fileName = dirSat + "M1.txt";
		String source = IOUtil.readFileWithoutLineSeparator(fileName);
		C.pl(retrieveTargetIps(source));
	}
	
	public static Set<String> retrieveTargetIps(String json) {
		Set<String> allIps = new HashSet<>();
		
		List list = JsonUtil.toList(json);
		for(Object obj : list) {
			if(obj instanceof Map) {
				Map map = (Map)obj;
				Object targetIp = map.get("ip");
				if(targetIp != null) {
					allIps.add(targetIp + "");
				}
			}
		}
		
		return allIps;
	}
	
	//@Test
	public void arrays() {
		String[] sArr = {"A", "B", "C"};
		Integer[] iArr = {1,43,8733};
		int[] iArr2 = {1,43,8733};
		Object obj = sArr;
		C.pl(iArr instanceof Integer[]);
		C.pl(obj instanceof Integer[]);
		if(obj instanceof String[]) {
			obj = Arrays.asList(iArr);
			C.pl(obj);
		}
		List list = Arrays.asList(iArr);
		C.pl(list);
	}
	
	//@Test
	public void convert() {
		MexedObject mo = new MexedObject(null);
		List list = StrUtil.split("a,c,b,d,e,f");
		Map map = new HashMap();
		map.put("A", 1917);
		map.put(19, "yuzhe");
		list.add(map);
		list.add(mo);
		list.add(new String[]{"a", "c"});
//		map.put("sink", list);
		C.pl(JsonUtil.getPrettyText(list));
		C.pl(JsonUtil.getPrettyText(map));
	}
	
	//@Test
	public void last() {
		String value = "abcd\n\nfurious";
		value = value.replaceAll("\n$", "");
		C.pl(value);
	}
	
	public void parse() {
		String fileName = dir + "z3.txt";
		String source = IOUtil.readFileWithoutLineSeparator(fileName);
		C.pl(JsonUtil.parseObject(source));
//		C.pl(JsonUtil.parseMap(source));
	}
	
	//@Test
	public void read() {
		String fileName = dir + "E3.txt";
		String source = IOUtil.readFileWithoutLineSeparator(fileName);
//		source = "fsfds";
//		source = "{\"a\":10}";
//		source = null;
		source = "[{\"id\":\"0375\",s\"city\":\"平顶山\"},{\"isd\":\"0377\",\"city\":\"南阳\"}]";
//		JsonBox box = new JsonBox(source);
//		Map map = JsonUtil.toMap(source);
//		List list = JsonUtil.toList(source);
//		D.ts(box.isLegalJson());
//		D.ts();
//		D.ts(box.getKing());
		Object stuff = JsonUtil.isLegalJson(source);
		C.pl();
		C.pl(stuff);
//		D.ts();
//		D.pl(box.getGrandMap());
//		D.ts();
//		D.ts(box.getJsonText());
//		D.ts();
//		D.ts(box.getFinalText());
	}
}
