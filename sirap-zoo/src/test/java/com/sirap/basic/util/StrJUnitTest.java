package com.sirap.basic.util;

import org.junit.Test;

import com.sirap.basic.tool.C;

public class StrJUnitTest {
	
	@Test
	public void command() {
		String va = "\\\\";
		String regex = va + "(|\\d{1,4})";
		C.pl(StrUtil.parseParam(regex, "\\20"));
	}
}
