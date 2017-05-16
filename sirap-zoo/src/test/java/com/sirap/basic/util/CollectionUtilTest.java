package com.sirap.basic.util;

import java.util.List;

import org.testng.annotations.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.StrUtil;

public class CollectionUtilTest {
	
	@Test
	public void treeMap() {
		
	}
	
	public void top() {
		String source = "a,b,c,d,e,f";
		List<String> list = StrUtil.split(source);
		C.list(CollectionUtil.last(list, -30));
	}
}
