package com.sirap.common.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class ForexRateRecord extends MexItem implements Comparable<ForexRateRecord> {
	private String code;
	private String displayName = "";
	private String value;
	
	public ForexRateRecord(String name, String displayName, String value) {
		this.code = name;
		this.displayName = displayName;
		this.value = value;
	}
	
	public ForexRateRecord(String name, String value) {
		this.code = name;
		this.value = value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setName(String name) {
		this.code = name;
	}

	public String getName() {
		return code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public BigDecimal toPrice() {
		BigDecimal price = MathUtil.toBigDecimal(value);
		return price;
	}
	
	public String getDisplayAmount() {
		return MathUtil.formatNumber(value, 2);
	}
	
	@Override
	public boolean isMatched(String kw) {
		if(kw.equalsIgnoreCase(code)) {
			return true;
		}
		
		if(StrUtil.contains(displayName, kw)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ForexRateRecord other = (ForexRateRecord) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ForexPrice [name=" + code + ", displayName=" + displayName + ", value=" + value + "]";
	}

	@Override
	public int compareTo(ForexRateRecord o) {
		BigDecimal bd1 = toPrice();
		BigDecimal bd2 = o.toPrice();
		if(bd1 == null) {
			return -1;
		}
		if(bd2 == null) {
			return -1;
		}
		return bd1.compareTo(bd2);
	}
	
	@Override
	public String toPrint(String options) {
		int maxLenOfCurrency = OptionUtil.readIntegerPRI(options, "maxLenOfCurrency", 25);
		int maxLenOfAmount = OptionUtil.readIntegerPRI(options, "maxLenOfAmount", 25);
		StringBuffer sb = new StringBuffer();
		sb.append(code);
		sb.append("  ").append(StrUtil.extend(displayName, maxLenOfCurrency));
		sb.append("  ").append(StrUtil.extendLeftward(getDisplayAmount(), maxLenOfAmount, " "));
		
		return sb.toString();
	}
	
	@Override
	public List<String> toPDF() {
		List<String> list = new ArrayList<String>();
		list.add(code);
		list.add(displayName);
		list.add(getDisplayAmount());
		
		return list;
	}
}
