package com.sirap.basic.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.geek.util.JsonUtil;

public class FastJsonTest {
	
	String dir = "D:/KDB/tasks/0423_JsonParse/";
	//dir = "E:/KDB/tasks/0726_JsonBox/A1";

	public void readM1() {
		String fileName = dir + "M1.txt";
		String source = IOUtil.readFileWithoutLineSeparator(fileName);
		Object obj = JsonUtil.parseObject(source);
		C.pl(JsonUtil.getPrettyText(obj));
		String path = "keepLog";
		path = "@2/kings@ss";
		C.pl(JsonUtil.readObjectByPath(obj, path));
		//C.pl(lee.readObject(obj, "changeSet"));
        //C.pl(source);
	}
	
	@Test
	public void readF1() {
		String fileName = dir + "F1.txt";
		String source = IOUtil.readFileWithoutLineSeparator(fileName);
        C.pl(source);
		Object obj = JsonUtil.parseObject(source);
		C.pl(JsonUtil.getPrettyText(obj));
	}
	public void readE1() {
		String fileName = dir + "E1.txt";
		String source = IOUtil.readFileWithoutLineSeparator(fileName);
		Object obj = JsonUtil.parseObject(source);
		C.pl(JsonUtil.getPrettyText(obj));
		String path = "keepLog";
		path = "list@3";
		C.pl(JsonUtil.readObjectByPath(obj, path));
		C.pl(JsonUtil.readObjectByPath(obj, "changeSet"));
        //C.pl(source);
	}
	
	public void read() {
		String fileName = dir + "Z4.txt";
		String source = IOUtil.readFileWithoutLineSeparator(fileName);
		Object obj = JsonUtil.parseObject(source);
		C.pl(JsonUtil.getPrettyText(obj));
		String path = "rate/type";
		path = "comments@1/a@30@4/phone";
		path = "rate@2";
//		path = "comments@1";
//		path = "comments@2/a@3";
//		path = "/comments@2/a@3";
//		path = "@1@2";
		path = "rate/type";
		C.pl(JsonUtil.readObjectByPath(obj, path));
        //C.pl(source);
	}
	
	@SuppressWarnings("rawtypes")
	public void print() {
//		User jack = MockData.getUser();
//		C.pl(jack);
//		String json = JSON.toJSON(jack).toString();
//		D.sink(json);
//		Map map = JsonUtil.toMap(json);
//		C.pl(map);
	}
	
	public void zhuan() {
	//  转换成对象    
//        String jsonstring = "{\"a\":51,\"b\":0}";    
//        User u1 = JSON.parseObject(jsonstring, new TypeReference<User>(){}); 
//        C.pl(u1);
//        D.sink(JSON.toJSON(u1));
//        User u2 = JSON.parseObject(jsonstring, User.class);  
//        C.pl(u2);
//        // 转换成对象数组     
//        String jsonstring2 = "[{\"a\":51,\"b\":0}]";    
//        User[] usa2 = JSON.parseObject(jsonstring2, new TypeReference<User[]>(){});    
//        List list = Arrays.asList(usa2); 
//        // 转换成ArrayList
//        ArrayList<User> list2 = JSON.parseObject(jsonstring2, new TypeReference<ArrayList<User>>(){}); 
//        
//        // 转换成ArrayList(默认)    list3  与 list4  效果相同
//        ArrayList<JSONObject> list3 = JSON.parseObject(jsonstring2, new ArrayList<User>().getClass()); 
//        ArrayList<JSONObject> list4 = JSON.parseObject(jsonstring2, ArrayList.class); 
//        for (int i = 0; i < list4.size(); i++) { //  推荐用这个
//        	JSONObject io = list4.get(i);
//        	System.out.println(io.get("a") + "======adn====="+io.get("b"));
//		}
	}
	public void convert() {
		MexedObject mo = new MexedObject("ak47");
		List list = StrUtil.split("a,c,b,d,e,f");
		Map map = new HashMap();
		map.put("A", 1917);
		map.put(19, "yuzhe");
//		list.add(map);
//		list.add(mo);
		list.clear();
		list.add(new String[]{"a", "c"});
//		map.put("sink", list);
//["a","c","b","d","e","f",{"19":"yuzhe","A":1917},{"pseudoOrder":0,"string":"ak47","obj":"ak47"},["a","c"]]
//alibaba json, changes the order.
		//C.pl(JSONArray.toJSON(list));
		C.pl(JsonUtil.getRawText(list));
	}
}
