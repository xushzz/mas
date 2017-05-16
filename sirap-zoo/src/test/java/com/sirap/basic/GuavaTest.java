package com.sirap.basic;

import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.StrUtil;

public class GuavaTest {
	
	{
		
	}

	@Test
	public void join() {
		List<String> items = StrUtil.split("a,c,wd,e,f,g");
		C.pl(items);
		Iterator<String> it = items.iterator();
		C.pl(Joiner.on('-').join(it));
		String[] subdirs = { "usr", "local", "lib" };
		String dir = Joiner.on("/").join(subdirs);
		C.pl(dir);
		C.pl(File.separator);
		int[] numbers = { 1, 2, 3, 4, 5 };
		String numbersAsString = Joiner.on(";").join(Ints.asList(numbers));
		C.pl(numbersAsString);
	}
	
	public void charmat() {
		C.pl(CharMatcher.inRange('d', 'f').matchesAllOf("a"));
		C.pl(CharMatcher.inRange('d', 'f').matchesAllOf("e"));
		C.pl(CharMatcher.inRange('d', 'f').matchesAllOf("k"));
		C.pl(CharMatcher.DIGIT.retainFrom("some text 8,9,98,893 and more"));
		C.list(StrUtil.extractIntegers("some text 8,9,98,893 and more"));
		C.pl(CharMatcher.DIGIT.removeFrom("some text 89983 and more"));
	}
	@Test
	public void list() {
		int a = 10, b = 39;
		int compare = Ints.compare(a, b);
		C.pl(Longs.compare(a, b));
		C.pl(compare);
		List<Integer> list = Lists.newArrayList(1,2,3,433);
		C.list(list);
		ImmutableList<String> of = ImmutableList.of("a", "b", "c", "d");
		C.pl(of);
		C.pl(StrUtil.split("a,b,c,de,f"));
	}
	public void asset() {
		HashSet<Integer> setA = newHashSet(1, 2, 3, 4, 5);
		HashSet<Integer> setB = newHashSet(4, 5, 6, 7, 8);

		SetView<Integer> union = Sets.union(setA, setB);
		C.pl("union:");
		for (Integer integer : union)
			C.pl(integer);

		SetView<Integer> difference = Sets.difference(setA, setB);
		C.pl("difference:");
		for (Integer integer : difference)
			C.pl(integer);

		SetView<Integer> difference2 = Sets.difference(setB, setA);
		C.pl("difference2:");
		for (Integer integer : difference2)
			C.pl(integer);

		SetView<Integer> intersection = Sets.intersection(setA, setB);
		C.pl("intersection:");
		for (Integer integer : intersection)
			C.pl(integer);
	}
}
