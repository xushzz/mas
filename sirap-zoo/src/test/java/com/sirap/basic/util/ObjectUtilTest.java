package com.sirap.basic.util;

import java.util.List;

import org.junit.Test;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexedFile;
import com.sirap.basic.tool.C;

public class ObjectUtilTest {
	
	@Test
	public void ming() {
		Object obj = StrUtil.class;
		C.pl(ObjectUtil.isStaticClass(obj));
		//C.pl(ObjectUtil.isStaticClass(new Driver()));
	}
	
	public void readSupers() throws Exception {
		String source = "com.sirap.basic.domain.MexedFile";
		source = "java.lang.String";
		C.pl(ObjectUtil.isInherit(source, MexItem.class));
		List list = ObjectUtil.getSuperClasses(MexedFile.class);
		C.list(list);
	}
	
//	@Test
	public void saveObj() {
//		String dir = "E:\\Klose\\nomas2\\";
//		String file = dir + "tr2.data";
//		TRRecord r = new TRRecord();
//		r.setId("walter");
//		r.setName("white");
//		Object obj = IOUtil.saveObject(r, file);
//		M.pl("saveObj=>" + obj);
	}
	
//	@Test(enabled=false, dependsOnMethods="saveObj")
	public void readObj() {
		String dir = "E:\\Klose\\nomas2\\";
		String file = dir + "tr2.data";
		Object obj = IOUtil.readObject(file);
		C.pl("readObj=>" + obj);
	}
}
