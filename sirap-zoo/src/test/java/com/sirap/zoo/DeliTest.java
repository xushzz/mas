package com.sirap.zoo;

import org.junit.Test;

import com.sirap.basic.tool.C;

public class DeliTest {
	
	@Test
	public void square() {
		for(int i = 2; i <= 24; i++) {
			C.pl("175.25");
			C.pl("168.36");
		}
	}
	
//	@Test
	public void roomNumber() {
		for(int i = 2; i <= 26; i++) {
			for(int k = 1; k <= 2; k++) {
				if(k == 4) {
					continue;
				}
				String temp = i + "0" + k;
				C.pl(temp);
			}
		}
	}
}
