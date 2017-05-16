package com.sirap.common.domain.creditcard;

import java.math.BigDecimal;
import java.util.Date;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class CreditKard extends MexItem {
	private String acronym;
	private String number;
	private String expired;
	private String valid;
	private int billday;
	private int interval;
	private BigDecimal fixedLimit;
	private Date fixedFrom;
	private BigDecimal extraLimit;
	private Date extraTo;
	
	public String getAcronym() {
		return acronym;
	}

	public BillPeriod billdayOfMonth(String yearMonth) {
		String regex = "(\\d{4})(\\d{2})";
		String[] params = StrUtil.parseParams(regex, yearMonth);
		if(params == null) {
			return null;
		}
		
		String year = params[0];
		int month = Integer.parseInt(params[1]);
		BillPeriod bill = new BillPeriod();
		bill.yearMonth = yearMonth;
		bill.billDate = DateUtil.construct(year, month, billday);
		bill.cutDate = DateUtil.construct(params[0], params[1], billday + interval);
		bill.billFrom = DateUtil.construct(year, month - 1, billday);
		bill.billTo = DateUtil.construct(year, month, billday - 1);
		
		return bill;
	}
	
	@Override
	public boolean parse(String source) {
		String regex = "([A-Z]{2,3})_(\\d+)_(\\d{2})(\\d{2})_(\\d{3})_(\\d{1,2})_(\\d{2})_([\\d|@]+)_([\\d|@]+)";
		String[] params = StrUtil.parseParams(regex, source);
		if(params != null) {
			acronym = params[0];
			number = params[1];
			String expMonth = params[2];
			String expYear = params[3];
			expired = expMonth + expYear;
			valid = params[4];
			billday = Integer.parseInt(params[5]);
			interval = Integer.parseInt(params[6]);
			AmountItem limitAt = parseLimitAt(params[7]);
			if(limitAt != null) {
				fixedLimit = limitAt.amount;
				fixedFrom = limitAt.when;
			} else {
				return false;
			}
			limitAt = parseLimitAt(params[8]);
			if(limitAt != null) {
				extraLimit = limitAt.amount;
				extraTo = limitAt.when;
			} else {
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	private AmountItem parseLimitAt(String limitAt) {
		String regex = "(\\d+)(|@(\\d{4})(\\d{2})(\\d{2}))";
		String[] params = StrUtil.parseParams(regex, limitAt);
		if(params != null) {
			int limit = Integer.parseInt(params[0]);
			String dateInfo = params[1];
			Date at = null;
			if(!EmptyUtil.isNullOrEmpty(dateInfo)) {
				at = DateUtil.construct(params[2], params[3], params[4]);
			}
			
			return new AmountItem(limit, at);
		}
		
		return null;
	}
	
	@Override
	public void print() {
		C.pl(this);
	}

	@Override
	public String toString() {
		String till = extraTo == null ? "" : ", till=" + displayDate(extraTo);
		return acronym + "[" + number + ", exp=" + expired
				+ ", val=" + valid + ", bill=" + billday + ", itv="
				+ interval + ", fixed=" + fixedLimit + ", from="
				+ displayDate(fixedFrom) + ", extra=" + extraLimit + till + "]";
	}
	
	public String displayDate(Date date) {
		if(date == null) {
			return null;
		}
		
		return DateUtil.displayDate(date, DateUtil.DATE_TIGHT);
	}
}