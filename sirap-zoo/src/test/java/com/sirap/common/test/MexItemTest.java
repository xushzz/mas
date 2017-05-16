package com.sirap.common.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.common.domain.Link;
import com.sirap.common.domain.TZRecord;

public class MexItemTest {
	
//	@Test
	public void plain() {
		List<MexItem> items = new ArrayList<MexItem>();
		items.add(new Link("http://img.ivsky.com/img/tupian/t/201010/06/taiguo.png"));
		C.list(items);
	}
	
	public  void link() {
		Set<MexItem> s1 = new HashSet<MexItem>();
		s1.add(new Link("A", "a1"));
		s1.add(new Link("A", "a1"));
		s1.add(new Link("A", "a1"));
		D.pl(s1);
	}
	
	public static void main(String[] args) {
		MexItemTest james = new MexItemTest();
		james.match();
	}
	
	public void match(){
		TZRecord tz = new TZRecord("China");

		C.pl(tz.isMatched("hin"));
		C.pl(tz.isMatched("^hin$"));
		C.pl(tz.isMatched("^China$"));
		C.pl(tz.isMatched("^Chin"));
		C.pl(tz.isMatched("ina$"));
	}
}
