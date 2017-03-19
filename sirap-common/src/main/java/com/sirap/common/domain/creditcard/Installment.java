package com.sirap.common.domain.creditcard;

import java.math.BigDecimal;
import java.util.Date;

import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;

public class Installment {
	private Date start;
	private BigDecimal pool;
	private int times;
	private BigDecimal pie;
	private BigDecimal juice;
	BigDecimal amount;

	public boolean parse(String source) {
		//INS_20140620_48162.30_12_4013.53_317.87_4331.40
		String regex = "INS_(\\d{4})(\\d{2})(\\d{2})_([\\d|\\.]+)_(\\d{1,2})_([\\d|\\.]+)_([\\d|\\.]+)_([\\d|\\.]+)";
		String[] params = StrUtil.parseParams(regex, source);
		if(params != null) {
			start = DateUtil.construct(params[0], params[1], params[2]);
			pool = MathUtil.toBigDecimal(params[3]);
			times = Integer.parseInt(params[4]);
			pie = MathUtil.toBigDecimal(params[5]);
			juice = MathUtil.toBigDecimal(params[6]);
			amount = MathUtil.toBigDecimal(params[7]);
			
			return true;
		}
		
		return false;
	}
	
	public boolean isPeriod(Date when) {
		int monthDiff = DateUtil.monthDiff(when, start);
		if(monthDiff < times && monthDiff >= 0) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return "Installment [start=" + displayDate(start) + ", pool=" + pool + ", times="
				+ times + ", pie=" + pie + ", juice=" + juice + ", amount="
				+ amount + "]";
	}
	
	public String displayDate(Date date) {
		if(date == null) {
			return null;
		}
		
		return DateUtil.displayDate(date, DateUtil.DATE_TIGHT);
	}
}
