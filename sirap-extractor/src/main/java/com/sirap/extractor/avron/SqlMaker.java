package com.sirap.extractor.avron;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;

public class SqlMaker {
	public static List<String> areacodes() {
		String pathA = "E:/Mas/exp/existed.txt";
		String pathB = "E:/Mas/exp/guangxi.txt";
		
		List<String> listA = IOUtil.readFileIntoList(pathA);
		List<String> listB = IOUtil.readFileIntoList(pathB);

		String template = "insert into T_DIC_AREACODE(CODE_REGION,REGIONNAME,PCODE,LEVEL) values('{0}','{1}','{2}',{3})";
		//450000000000, 广西壮族自治区, 00, 1
		int countA = 0;
		int countB = 0;
		List<String> finallist = Lists.newArrayList();
		for(String line : listB) {
			if(!StrUtil.isRegexFound("^\\d{12}", line)) {
				continue;
			}
			
			List<String> items = StrUtil.split(line);
			if(items.size() < 4) {
				continue;
			}
			
			String code = items.get(0);
			if(listA.indexOf(code) >= 0) {
				countB++;
//				D.pl("existed " + code);
				//continue;
			}
			
			String temp = StrUtil.occupy(template, items.get(0), items.get(1), items.get(2), Integer.parseInt(items.get(3)+""));
			finallist.add(temp);
			countA++;
		}
		
		D.pl("-- countA: " + countA + ", countB:" + countB);
		return finallist;
	}
}
