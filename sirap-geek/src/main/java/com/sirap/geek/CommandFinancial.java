package com.sirap.geek;

import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.domain.creditcard.CreditKard;
import com.sirap.common.manager.KardManager;
import com.sirap.common.manager.LoanManager;

public class CommandFinancial extends CommandBase {

	private static final String KEY_CREDIT_KARD = "kk";
	private static final String KEY_LOAN = "loan";

	@Override
	public boolean handle() {

		if(is(KEY_CREDIT_KARD)) {
			String filePath = cardPath();
			List<CreditKard> cards = KardManager.g().allCards(filePath);
			if(!EmptyUtil.isNullOrEmpty(cards)) {
				export(cards);
			}

			return true;
		}
		
		solo = parseParam(KEY_CREDIT_KARD + " ([a-zA-Z]+)");
		if(solo != null) {
			String filePath = cardPath();
			KardManager.g().analyzeCards(solo, filePath);
			return true;
		}
		
		regex = KEY_LOAN + "\\s+" + Konstants.REGEX_FLOAT + "\\s*,\\s*(\\d{1,3})\\s*,\\s*" + Konstants.REGEX_FLOAT;
		params = parseParams(regex);
		if(params != null) {
			Double principal = MathUtil.toDouble(params[0]);
			Integer period = MathUtil.toInteger(params[1]);
			Double monthlyRateInPercentage = MathUtil.toDouble(params[2]); 
			
			List<String> items = LoanManager.g().calculate(principal, period, monthlyRateInPercentage);
			export(items);
			
			return true;
		}

		return false;
	}

	public String cardPath() {
		return pathOf("storage.card", "xyk");
	}
}
