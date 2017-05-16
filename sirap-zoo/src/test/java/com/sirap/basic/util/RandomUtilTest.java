package com.sirap.basic.util;

import org.testng.annotations.Test;

import com.sirap.basic.tool.D;

public class RandomUtilTest {
	@Test
	public void testDigits() {
		int len = 5;
		D.ts(RandomUtil.digits(len));
		D.ts(RandomUtil.digitsStartWithNoZero(len));
		D.ts(RandomUtil.letters(len));
		D.ts(RandomUtil.letters(len, true));
		D.ts(RandomUtil.alphanumeric(len));
		D.ts(RandomUtil.chars(len, "adamsmith"));
	}
}
