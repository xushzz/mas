package com.sirap.basic.json;

import com.sirap.basic.util.RandomUtil;

public class MockData {
	public static User getUser() {
		User yang = new User();
		yang.setA((long)190);
		yang.setB(RandomUtil.letters(3));
		
		return yang;
	}
}
