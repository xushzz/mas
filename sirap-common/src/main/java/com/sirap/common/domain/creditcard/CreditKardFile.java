package com.sirap.common.domain.creditcard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

public class CreditKardFile {
	
	public static final String KEY_AVAILABLE = "AVA";
	public static final String KEY_DEBT = "DEB";
	public static final String KEY_SPEND = "OUT";
	
	private CreditKard card;
	private List<String> records = new ArrayList<>();
	private List<AmountItem> availableList = new ArrayList<>();
	private List<AmountItem> debtList = new ArrayList<>();
	private List<AmountItem> spendList = new ArrayList<>();
	private List<Installment> installList = new ArrayList<>();

	public BigDecimal calcSpend(BillPeriod peace) {
		BigDecimal total = new BigDecimal(0);
		List<BigDecimal> sb = new ArrayList<>();
		
		for(AmountItem item: spendList) {
			if(item.isPeriod(peace)) {
				sb.add(item.amount);
				total = total.add(item.amount);
			}
		}
		
		for(Installment item: installList) {
			if(item.isPeriod(peace.cutDate)) {
				sb.add(item.amount);
				total = total.add(item.amount);
			}
		}
		C.pl(StrUtil.connect(sb, "+") + "=" + total);
		return total;
	}
	
	public BigDecimal calcAvailable(BillPeriod peace) {
		BigDecimal total = new BigDecimal(0);
		List<BigDecimal> sb = new ArrayList<>();
		
		for(AmountItem item: spendList) {
			if(item.isPeriod(peace)) {
				sb.add(item.amount);
				total = total.add(item.amount);
			}
		}
		
		for(Installment item: installList) {
			if(item.isPeriod(peace.cutDate)) {
				sb.add(item.amount);
				total = total.add(item.amount);
			}
		}
//		C.pl(StringUtil.connect(sb, "+") + "=" + total);
		return total;
	}
	
	private boolean validFlag;
	
	public CreditKard getCard() {
		return card;
	}

	public List<AmountItem> getAvailableList() {
		return availableList;
	}

	public List<AmountItem> getDebtList() {
		return debtList;
	}

	public List<Installment> getInstallList() {
		return installList;
	}

	public CreditKardFile(List<String> records) {
		this.records = records;
		parse();
	}
	
	public boolean isValid() {
		return validFlag;
	}
	
	public void parse() {
		for(String record : records) {
			AmountItem item = parseNumberDateItem(KEY_AVAILABLE, record);
			if(item != null) {
				availableList.add(item);
				continue;
			}
			
			item = parseNumberDateItem(KEY_DEBT, record);
			if(item != null) {
				debtList.add(item);
				continue;
			}
			
			item = parseNumberDateItem(KEY_SPEND, record);
			if(item != null) {
				spendList.add(item);
				continue;
			}
			
			Installment install = new Installment();
			if(install.parse(record)) {
				installList.add(install);
				continue;
			}
			
			if(card == null) {
				CreditKard ck = new CreditKard();
				if(ck.parse(record)) {
					card = ck;
					validFlag = true;
					continue;
				}
			}
		}
	}
	
	private AmountItem parseNumberDateItem(String key, String record) {
		String regex = key + "_(\\d{4})(\\d{2})(\\d{2})_([\\d|\\.|-]+)";
		String[] params = StrUtil.parseParams(regex, record);
		if(params != null) {
			Date date = DateUtil.construct(params[0], params[1], params[2]);
			AmountItem item = new AmountItem(params[3], date);
			item.type = key;
			
			return item;			
		}
		
		return null;
	}
	
	public void print() {
		C.pl(card);
		C.list(availableList);
		C.list(debtList);
		C.list(spendList);
		C.list(installList);
	}
}
