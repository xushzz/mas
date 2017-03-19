package com.sirap.common.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexItem;

@SuppressWarnings("serial")
public class InstallmentRecord extends MexItem {
	private String order;
	private BigDecimal capital;
	private BigDecimal plusByAverageInterest;
	private BigDecimal plusByAverageCapital;
	
	public InstallmentRecord(BigDecimal capital, BigDecimal plusByAverageInterest, BigDecimal plusByAverageCapital) {
		this.capital = capital;
		this.plusByAverageInterest = plusByAverageInterest;
		this.plusByAverageCapital = plusByAverageCapital;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(order).append(" ");
		sb.append(capital).append("\t");
		sb.append(plusByAverageInterest).append("\t");
		sb.append(plusByAverageCapital);
		
		return sb.toString();	
	}
		
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public List<String> toPDF() {
		List<String> list = new ArrayList<String>();
		list.add(pseudoOrder + ")");
		list.add(capital.toString());
		list.add(plusByAverageInterest.toString());
		list.add(plusByAverageCapital.toString());
		
		return list;
	}
}
