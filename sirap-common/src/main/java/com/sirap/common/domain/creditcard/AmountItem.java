package com.sirap.common.domain.creditcard;

import java.math.BigDecimal;
import java.util.Date;

import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.MathUtil;

public class AmountItem {
	String type;
	BigDecimal amount;
	Date when;
	
	public AmountItem(int limit, Date at) {
		this.amount = MathUtil.toBigDecimal(limit);
		this.when = at;
	}
	
	public AmountItem(String limit, Date at) {
		this.amount = MathUtil.toBigDecimal(limit);
		this.when = at;
	}
	
	public boolean isPeriod(BillPeriod peace) {
		return DateUtil.isDayBetweenPeriod(when, peace.billFrom, peace.billTo);
	}

	@Override
	public String toString() {
		return "NumberDateItem [type=" + type + ", amount=" + amount + ", when=" + DateUtil.displayDate(when, DateUtil.DATE_TIGHT) + "]";
	}
}