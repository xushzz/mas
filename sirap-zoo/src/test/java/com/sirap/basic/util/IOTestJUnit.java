package com.sirap.basic.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.sirap.basic.component.MexedOption;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;

public class IOTestJUnit {
	@Test
	public void option() {
		String source = "+printSource,-UseParallelGC,mizhihui,ParallelGCThreads=20,motto=Nothing is free";
		source += ",xian=\"zhihui is thinking.\", wh op=liao yang";
		List<String> items = StrUtil.split(source);
		for(String item : items) {
			MexedOption mo = new MexedOption();
			mo.parse(item);
			C.pl(mo);
		}
	}
	
	
	public void regexSafe() {
		List<List<String>> items = StrUtil.findAllMatchedListedItems("(\\d)[a-z](\\d)", "j1h2uue2f3");
		D.sink(items);
		List<String> items2 = StrUtil.findAllMatchedItems("(\\d)[a-z](\\d)", "j1h2uue2f3");
		D.sink(items2);
	}

//	@Test
	public void func2() {
		List<String> items = StrUtil.split("a,b,c,d,e");
		items = Collections.EMPTY_LIST;
		List<MexItem> list = new ArrayList<>();
		list.add(new MexedObject(1912));
		list.add(new MexedObject("opinion"));
		list.add(new MexedObject(new Date()));
		list.add(new MexedObject(-1212));
		String fullFileName = "D:\\" + DateUtil.timestamp() + ".txt";
//		MexUtil.saveAsMex(items, fullFileName);
		MexUtil.saveAsNew(items, fullFileName);
//		IOUtil.saveAsTxt(items, fullFileName);
//		MexUtil.saveAsNew(list, fullFileName);
		
		D.ts();
		
		String origin = "D:\\20160517_230340.txt";
		List list2 = MexUtil.readMexItemsViaUnderlyingClassName(origin);
		C.list(list2);
	}
	
	public void func1() {
		String[] arr = "a;b,c;d,d=e".split(";|,");
		D.pl(arr);
	}
	
	public void readFile() {
		String fileName = "D:/Projects/hub/mas/scripts/SHUN/dev.bat";
		C.pl(IOUtil.readFileWithoutLineSeparator(fileName));
		C.pl(IOUtil.readFileWithRegularLineSeparator(fileName));
	}

	//@Test
	public void extractFilename() {
		String fileName = "D:\\Projects\\hub\\mas\\scripts\\SHUN\\dev.bat";
		fileName = "D:" + File.separator + "abc" + File.separator + "ninja.txt";
		D.pl(FileUtil.extractFilenameWithoutExtension(null));
		D.pl(FileUtil.extractFilenameWithoutExtension("ninB"));
		D.pl(FileUtil.extractFilenameWithoutExtension(File.separator + "ninB"));
		D.pl(FileUtil.extractFilenameWithoutExtension("abc.bat"));
		D.pl(FileUtil.extractFilenameWithoutExtension("b\\abc.bat"));
		D.pl(FileUtil.extractFilenameWithoutExtension(fileName));
		C.pl(File.separator);
		C.pl(File.separatorChar);
	}
}
