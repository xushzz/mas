package com.sirap.common.command.explicit;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.domain.MemoryRecord;
import com.sirap.common.domain.creditcard.CreditKard;
import com.sirap.common.framework.command.target.TargetPDF;
import com.sirap.common.manager.ForexManager;
import com.sirap.common.manager.KardManager;
import com.sirap.common.manager.LoanManager;
import com.sirap.common.manager.MemorableDayManager;
	
public class CommandFinancial extends CommandBase {

	private static final String KEY_CREDIT_KARD = "kk";
	private static final String KEY_MEMORABLE = "mm";
	private static final String KEY_DAY_CHECK = "dc";
	private static final String KEY_FOREX = "\\$([a-z]{3})([\\-\\d\\.]{1,99})(|/|[a-z,]+)";
	private static final String KEY_LOAN = "loan";

	public boolean handle() {

		if(is(KEY_CREDIT_KARD)) {
			String filePath = cardPath();
			List<CreditKard> cards = KardManager.g().allCards(filePath);
			if(!EmptyUtil.isNullOrEmpty(cards)) {
				export(cards);
			}

			return true;
		}
		
		String param = parseParam(KEY_CREDIT_KARD + " ([a-zA-Z]+)");
		if(param != null) {
			String filePath = cardPath();
			KardManager.g().analyzeCards(param, filePath);
			return true;
		}
		
		if(is(KEY_DAY_CHECK)) {
			MemoryKeeper lucy = new MemoryKeeper() {
				public List<MemoryRecord> readRecords() {
					return MemorableDayManager.g(filePath).searchWithNDays(30);
				}
			};
			
			if(lucy.handle()) {
				return true;
			}
		}
		
		String[] params = parseParams(KEY_MEMORABLE + "(|\\d{1,4})");
		if(params != null) {
			final Integer count = MathUtil.toInteger(params[0], 20);
			MemoryKeeper lucy = new MemoryKeeper() {
				public List<MemoryRecord> readRecords() {
					List<MemoryRecord> records = MemorableDayManager.g(filePath).getMemoryRecords(count);
					return records;
				}
			};
			
			if(lucy.handle()) {
				return true;
			}
			
			return true;
		}
		
		if(is(KEY_MEMORABLE + KEY_2DOTS)) {
			MemoryKeeper lucy = new MemoryKeeper() {
				public List<MemoryRecord> readRecords() {
					List<MemoryRecord> records = MemorableDayManager.g(filePath).getAllRecords();
					return records;
				}
			};
			
			if(lucy.handle()) {
				return true;
			}
		}
		
		String singleParam = parseParam(KEY_MEMORABLE + "\\s(.+?)");
		if(singleParam != null) {
			MemoryKeeper lucy = new MemoryKeeper() {
				public List<MemoryRecord> readRecords() {
					List<MemoryRecord> records = MemorableDayManager.g(filePath).search(singleParam);
					return records;
				}
			};
			
			if(lucy.handle()) {
				return true;
			}
		}
		
		params = parseParams(KEY_FOREX);
		if(params != null) {
			String name = params[0];
			String amount = params[1];
			String currencies = params[2];
			BigDecimal bd = MathUtil.toBigDecimal(amount);
			if(bd != null) {
				if(target instanceof TargetPDF) {
					int[] cellsWidth = {1, 4, 2};
					int[] cellsAlign = {1, 0, 2};
					PDFParams pdfParams = new PDFParams(cellsWidth, cellsAlign);
					target.setParams(pdfParams);
					List<List<String>> records = ForexManager.g().convert4PDF(name, amount, currencies);
					export(records);
				} else {
					List<String> records = ForexManager.g().convert(name, amount, currencies);
					export(records);
				}
				return true;
			}
		}
		
		params = parseParams(KEY_LOAN + "\\s+([\\d\\.]{1,20})\\s*,\\s*(\\d{1,3})\\s*,\\s*([\\d\\.]+)");
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
		return pathWithSeparator("storage.card", "xyk");
	}
	
	abstract class MemoryKeeper {
		protected String filePath;
		
		public MemoryKeeper() {
			init();
		}
		
		private void init() {
			File file = parseFile(g().getUserValueOf("file.memory"));
			if(file == null) {
				throw new MexException("Memory file missing, pleace check user config [file.memory].");
			}
			
			filePath = file.getAbsolutePath();
		}
		
		protected boolean handle() {
			if(filePath == null) {
				return false;
			}
			
			List<MemoryRecord> records = readRecords();
			export(CollectionUtil.items2PrintRecords(records));
			
			return true;
		}
		
		public abstract List<MemoryRecord> readRecords();
	}
}
