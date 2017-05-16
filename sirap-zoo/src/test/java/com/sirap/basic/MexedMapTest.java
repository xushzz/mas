package com.sirap.basic;

import org.junit.Test;

import com.sirap.basic.component.MexedMap;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.IOUtil;

public class MexedMapTest {
	
	@Test
	public void detect() {
		String url = "D:/KDB/tasks/0530_MexedMap/likes.txt";
		MexedMap mm = IOUtil.createMexedMapByRegularFile(url);
		C.pl(mm.detectCircularItems());
		C.list(mm.listEntries());
	}
}
