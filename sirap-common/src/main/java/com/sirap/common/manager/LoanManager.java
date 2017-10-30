package com.sirap.common.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.InstallmentRecord;

public class LoanManager {

	private static LoanManager instance;
	
	public static LoanManager g() {
		if(instance == null) {
			instance = new LoanManager();
		}
		
		return instance;
	}
	
	public List<String> calculate(Double principal, int period, Double monthlyRateInPercentage) {
		double monthlyRateInPureNumber = monthlyRateInPercentage * 0.01;
		BigDecimal capital = MathUtil.divide(principal, period, 2);
		double regularInterest = principal * monthlyRateInPureNumber;
		List<String> items = new ArrayList<>();
		
		double[] totalInterest = new double[2]; 
		
		for(int i = 0; i < period; i++) {
			double decreasedInterest = (principal - i * (capital.doubleValue())) * monthlyRateInPureNumber;
			BigDecimal regularInterestBD = MathUtil.divide(regularInterest, 1, 2);
			BigDecimal decreasedInterestBD = MathUtil.divide(decreasedInterest, 1, 2);

			InstallmentRecord record = new InstallmentRecord(capital, regularInterestBD, decreasedInterestBD);

			String temp = StrUtil.padLeft(i + 1 + "", 2);
			record.setOrder(temp + ")");
			items.add(record.toString());
			
			totalInterest[0] += regularInterest;
			totalInterest[1] += decreasedInterest;
		}
		
		BigDecimal totalCapital =  MathUtil.divide(principal, 1, 2);
		InstallmentRecord record = new InstallmentRecord(totalCapital, MathUtil.divide(totalInterest[0], 1, 2), MathUtil.divide(totalInterest[1], 1, 2));
		record.setOrder("###");
		items.add(record.toString());
		
		double interestDiff = totalInterest[0] - totalInterest[1];
		double diffInPercentage = 100 * interestDiff / totalInterest[0];
		items.add("### diff: " + MathUtil.divide(interestDiff, 1, 2));
		items.add("### perc: " + MathUtil.divide(diffInPercentage, 1, 2) + "%");
		
		return items;
	}
}
