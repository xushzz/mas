package com.sirap.common.manager;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.creditcard.BillPeriod;
import com.sirap.common.domain.creditcard.CreditKard;
import com.sirap.common.domain.creditcard.CreditKardFile;

public class KardManager {
	private static KardManager instance;
	
	public static KardManager g() {
		if(instance == null) {
			instance = new KardManager();
		}
		
		return instance;
	}
	
	public void project(int year, int month, List<String> records) {
		String monthStr = StrUtil.extendLeftward(month + "", 2, "0");
		project(year + monthStr, records);
	}

	public List<CreditKard> allCards(String filePath) {
		List<CreditKard> cards = new ArrayList<>();
		File file = new File(filePath);
		File[] files = file.listFiles();
		for(int i = 0; i < files.length; i++) {
			File f = files[i];
			List<String> records = IOUtil.readFileIntoList(f.getAbsolutePath());
			CreditKardFile ccFile = new CreditKardFile(records);
			cards.add(ccFile.getCard());
		}
		
		return cards;
	}
	
	public void analyzeCards(String card, String filePath) {
		File file = new File(filePath);
		File[] files = file.listFiles();
		for(int i = 0; i < files.length; i++) {
			File f = files[i];
			List<String> records = IOUtil.readFileIntoList(f.getAbsolutePath());
			CreditKardFile ccFile = new CreditKardFile(records);
			CreditKard kard = ccFile.getCard();
			if(kard == null) {
				continue;
			}
			
			String acronym = kard.getAcronym();
			if(StrUtil.contains(f.getName(), card, 3) || acronym.equalsIgnoreCase(card)) {
				C.pl(kard);
				projectNextN(3, records);
				C.pl();
			}
		}
	}
	
	private void projectNextN(int n, List<String> records) {
		for(int i = 0; i < n; i++) {
			Date d = DateUtil.nextMonth(i);
			String yearMonth = DateUtil.displayDate(d, "yyyyMM");
			project(yearMonth, records);
		}
	}
	
	public List<String> project(String yearMonth, List<String> records) {
		List<String> items = new ArrayList<>();
		CreditKardFile ccFile = new CreditKardFile(records);
		if(!ccFile.isValid()) {
			C.pl("Invalid card file:\n" + records);
			return items;
		}
		CreditKard card = ccFile.getCard();
		items.add(card.toString());
		BillPeriod peace = card.billdayOfMonth(yearMonth);
		BigDecimal amount = ccFile.calcSpend(peace);
		C.pl(yearMonth + " => " + amount);
		
		return items;
	}
}
