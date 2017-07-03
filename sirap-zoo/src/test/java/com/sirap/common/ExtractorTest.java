package com.sirap.common;

import org.junit.Test;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.IOUtil;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.impl.EnglishDictionaryExtractor;
import com.sirap.extractor.impl.MobilePhoneLocationExtractor;

public class ExtractorTest {
	
	@Test
	public void diction() {
		String va = "badass";
		va = "fake";
		va = "obama";
		va = "de facto";
//		va = "USA"; // lacking of title
//		va = "U.S.A.";
		
		Extractor<MexedObject> xie = new EnglishDictionaryExtractor(va);
		xie.process();
		C.list(xie.getMexItems());
	}
	
//	@Test
	public void ok() {
		String url = "http://www.00cha.com/shouji/?mobile=15838400995";
		C.pl(IOUtil.readURL(url));
	}
	public void ip138() {
		String haoma = "1369229";
		Extractor<MexedObject> frank = new MobilePhoneLocationExtractor(haoma);
		frank.process();
		MexedObject mo = frank.getMexItem();
		C.pl(mo);
	}
}
