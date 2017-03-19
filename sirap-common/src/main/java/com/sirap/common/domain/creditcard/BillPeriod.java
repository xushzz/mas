package com.sirap.common.domain.creditcard;

import java.util.Date;

import com.sirap.basic.util.DateUtil;

public class BillPeriod {
	
	String yearMonth;
	public Date cutDate;
	Date billDate;
	Date billFrom;
	Date billTo;

	@Override
	public String toString() {
		return "BillPeriod [yearMonth=" + yearMonth + ", cutDate=" + DateUtil.displayDate(cutDate, DateUtil.DATE_TIGHT)
				+ ", billDate=" + DateUtil.displayDate(billDate, DateUtil.DATE_TIGHT) + ", billFrom=" + DateUtil.displayDate(billFrom, DateUtil.DATE_TIGHT)
				+ ", billTo=" + DateUtil.displayDate(billTo, DateUtil.DATE_TIGHT) + "]";
	}
}
