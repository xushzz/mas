package com.sirap.farm;

import java.util.List;

import org.junit.Test;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.search.MexFilter2;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.StrUtil;

public class MexFilterTest {
	
	@Test
	public void ref() {
	}
	
	@Test
	public void what() {
		List items = StrUtil.split("a1,a2,b2,bb3a,b4,ca5,c6,c7,cccNick");
		List<MexedObject> mexItems = CollectionUtil.toMexedObjects(items);
		String sa = "a&bb|ccc";
//		sa = "&bb|ccc";
//		sa = "ccc";
//		sa = "a|(b&(k|c)|ni)&cc";
//		sa = "&(bb|c)&a";
//		sa = "a";
//		sa = "a|b|c|d|e|f";
//		sa = "&&&&";
//		sa = "||||";
//		sa = "&()|";
		sa = "&(((a)))|b";
		sa = "&()()|b";
		sa = "&()&()|b";
		sa = "&))&((|b";
		sa = "&(k1)&(k2)|b";
		sa = "&)k1)&(k2(|b";
		sa = "&)k1)&)k2)|b";
		sa = "&(k1(&(k2(|b";
		sa = "&ab(k1|bk&na)&(k2(|b";
		sa = "ab(k1|bk&na)";//@list1@
		// "ab@list1@"
//		sa = "ac&bc&(bb|c)&a&(bb|c)&a";
//		sa = "a&b";
//		sa = "b&A";
//		sa = "(bb|c)&a";

		MexFilter2<MexedObject> m2 = new MexFilter2<MexedObject>(sa, mexItems);
		List<MexedObject> result = m2.process();
		C.list(result);
	}
}
