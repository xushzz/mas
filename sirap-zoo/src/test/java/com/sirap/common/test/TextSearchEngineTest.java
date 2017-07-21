package com.sirap.common.test;

import java.util.List;

import org.junit.Test;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.search.TextSearcher;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.LocalSearchEngine;

public class LocalSearchEngineTest {

	@Test
	public void split() {
		String source = "a|c|D|vdd";
		String[] info = source.split("\\|");
		D.pl(info);
	}
//	@Test
	public void engine() {
		String source = "z>D:/KDB/static>.txt>+printsource";
//		 source = "z>D:/KDB/static>.txt>+printsourCE";
//		source = "z>D:/KDB/static>.txt";
		LocalSearchEngine max = new LocalSearchEngine();
		max.parse(source);
		C.pl(max);
	}
	
	@Test
	public void textSearch() {
		String foldersStr = "E:/GitProjects/SIRAP/mas/scripts";
//		foldersStr = "F:/Docs/Speech"; 
		String suffixesStr = ".bat;ties;txt";
//		suffixesStr = ".bat;gen";
		List<String> folders = StrUtil.splitByRegex(foldersStr);
		String[] suffixes = suffixesStr.split(";");
		String criteria = "capture&sound";
		List<MexedObject> items = TextSearcher.search(folders, suffixes, criteria, false);
		C.listMex(items);
	}
}
