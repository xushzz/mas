package com.sirap.basic.util;

import java.util.List;

import org.junit.Test;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.tool.C;

public class MexUtilTest {
	
	@Test
	public void parse() {
		String fullFileName = "D:\\Mas\\test\\20160519_234904.txt";
//		fullFileName = "D:\\Mas\\test\\20160519_234806.txt";
		List list = MexUtil.readMexItems(fullFileName);
		C.list(list);
	}
	
//	@Test
	public void saveAsNew() {
		List<String> items = StrUtil.splitByRegex("a,b;c,d.e");
		List<MexedObject> list = CollectionUtil.toMexedObjects(items);
		String fullFileName = "D:\\Mas\\test\\" + DateUtil.timestamp() + ".txt";
		MexUtil.saveAsNew(list, fullFileName);
//		MexUtil.saveAsMex(list, fullFileName);
		C.list(list);
	}
}
