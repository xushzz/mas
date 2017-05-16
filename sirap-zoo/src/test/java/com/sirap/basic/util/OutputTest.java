package com.sirap.basic.util;

import java.util.Arrays;

import org.junit.Test;

import com.sirap.basic.tool.D;
import com.sirap.basic.util.DateUtil;

public class OutputTest {

	@Test
	public void abc() {
		D.pl("same", "sandman");
	}
	
	@Test
	public void plc() {
		String[] a = {"A", "B", "C"};
		Object[] b = {1, 3, "ASD", a, DateUtil.displayNow(), 2.365};
//		D.pl(b);
		D.ls(Arrays.asList(b));
	}
}
