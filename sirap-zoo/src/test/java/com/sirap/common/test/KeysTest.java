package com.sirap.common.test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.component.KeysReader;
import com.sirap.common.domain.creditcard.CreditKard;
import com.sirap.common.domain.creditcard.CreditKardFile;
import com.sirap.common.domain.creditcard.Installment;
import com.sirap.common.manager.KardManager;

public class KeysTest {
//	@Test
	public void printKard() {
		parseCreditKard("BASIC_4392260802160521_0320_700_05_18_50000@20150502_0");
		parseCreditKard("BASIC_4392260802160521_0320_700_05_18_50000@20150502_0@19991212");
		parseCreditKard("BASIC_4392260802160521_0320_700_05_18_50000@20150502_0@1999");
		parseCreditKard("BASIC_4392260802160521_0320_700_05_18_50000@20150502");
	}
	
	public void parseCreditKard(String source) {
		CreditKard zhao = new CreditKard();
		if(zhao.parse(source)) {
			C.pl(zhao);
		} else {
			D.sink(source);
		}
	}
	
	public void parseItem() {
		parseNumberDateItem("AVA", null);
	}

	
	public void actionAll() {
		File file = new File("E:\\MasPro\\xyk");
		File[] files = file.listFiles();
		for(int i = 0; i < files.length; i++) {
			File f = files[i];
			List<String> records = IOUtil.readFileIntoList(f.getAbsolutePath());
			CreditKardFile ccFile = new CreditKardFile(records);
			C.pl(ccFile.getCard());
		}
	}
	
	@Test
	public void analyzeCards() {
		KardManager nick = KardManager.g();
		String filePath = "E:\\MasPro\\xyk\\";
		nick.analyzeCards("guang", filePath);
	}
	public void action() {
		KardManager nick = KardManager.g();
		String bank = "Zhaoshang";
		bank = "Guangda";
		bank = "Xingye";
		bank = "zhongxin";
		bank = "Minsheng";
		bank = "PingAN";
		bank = "GuangFa";
		bank = "Guangzhou";
		bank = "GuangNong";
		bank = "Zhongguo";
		List<String> records = IOUtil.readFileIntoList("E:\\MasPro\\xyk\\" + bank + ".txt");
		for(int i = 0; i < 24; i++) {
			//nick.project(2014, i + 1, records);
		}
		nick.project("201508", records);
		nick.project("201509", records);
	}
	
	public void parseKardFile() {
		List<String> records = IOUtil.readFileIntoList("E:\\MasPro\\xyk\\Zhaoshang.txt");
		CreditKardFile zhao = new CreditKardFile(records);
		zhao.parse();
		zhao.print();
	}
	
	private void parseNumberDateItem(String key, String record) {
		record = "AVA_20150808_212.12";
		String regex = key + "_(\\d{4})(\\d{2})(\\d{2})_([\\d|\\.|-]+)";
		String[] params = StrUtil.parseParams(regex, record);
		D.pl(params);
	}
	
	public void printInstall() {
		parseInstall("INS_20140620_48162.30_12_4013.53_317.87_4331.40");
		parseInstall("INS_20141120_36405.36_12_3033.78_240.28_3274.06");
		parseInstall("INS_20150318_35689.24_12_2974.10_235.55_3209.65");
	}
	
	public void parseInstall(String source) {
		Installment zhao = new Installment();
		if(zhao.parse(source)) {
			C.pl(zhao);
		} else {
			D.sink(source);
		}
	}
	
	public void read() {
		String path = "D:\\workspace.luna\\common\\src\\main\\java\\com\\pirate\\common\\command\\CommandWiki.java";
		List<String> list = Arrays.asList("pathWithSeparator");
		KeysReader kev = new KeysReader(new File(path), list);
		List<String> records = kev.readKeysFromFile();
		C.list(records);
	}
}
