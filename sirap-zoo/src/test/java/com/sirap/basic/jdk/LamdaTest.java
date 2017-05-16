package com.sirap.basic.jdk;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;

public class LamdaTest {
	
	@Test
	public void mapreduce() {
		List<String> items = StrUtil.split("a,b,c,d,e,f");
		List<String> itemsB = items.stream().map(number -> number + "_" + RandomUtil.alphanumeric(3)).collect(Collectors.toList());
		C.pl(itemsB);
		
		List<Integer> items2 = Arrays.asList(1, 2, 44, 55);
		List<Integer> itemsB2 = items2.stream().map(number -> number * 233).collect(Collectors.toList());

		C.pl(itemsB2);
	}
}
