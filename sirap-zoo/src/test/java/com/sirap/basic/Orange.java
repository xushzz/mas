package com.sirap.basic;

import org.junit.Test;

import com.sirap.basic.tool.C;

public class Orange extends Apple {
	
	@Test
	public void inherit() {
		Apple a = new Apple();
		C.pl(a.lazy);
		C.pl(lazy);
	}
}
