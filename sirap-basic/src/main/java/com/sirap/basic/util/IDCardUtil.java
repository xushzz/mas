package com.sirap.basic.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.Person;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;

public class IDCardUtil {
	
	public static char checkCodeChina(String id17or18) {
		int len = 17;
		String regex = "(\\d{" + len + "})(|\\d|X)";

		String[] params = StrUtil.parseParams(regex, id17or18);
		if(params == null) {
			throw new MexException("illegal ID: " + id17or18);
		}
		
		String bulk = params[0];
		
		int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
		
		XXXUtil.shouldBeEqual(len, weight.length);

		int sum = 0;
		for(int i = 0; i < len; i++) {
			int digit = Integer.parseInt(bulk.charAt(i) + "");
			sum += digit * weight[i];
		}
		
		char[] validate = { '1','0','X','9','8','7','6','5','4','3','2'};
		int mode = sum % validate.length;
		char code = validate[mode];
		
		return code;
	}
	
	/***
	 * Male, 23 years old, born 1923/23/43
	 * @param id17or18
	 * @return
	 */
	public static Person readBasicInfo(String id17or18) {
		String regex = "(\\d{6})(\\d{4})(\\d{2})(\\d{2})\\d{2}(\\d)(|\\d|X)";
		String[] params = StrUtil.parseParams(regex, id17or18);
		
		if(params == null) {
			throw new MexException("illegal ID: " + id17or18);
		}
		
		String areaCode = params[0];
		String year = params[1];
		String month = params[2];
		String day = params[3];
		String gender = params[4];
		
		int monthDiff = DateUtil.monthDiff(new Date(), DateUtil.construct(year, month, day));
		BigDecimal age = MathUtil.divide(monthDiff, 12, 1);
		
		boolean isMale = (Integer.parseInt(gender) % 2) == 1;
		String dateOfBirth = year + "-" + month + "-" + day;
		
		Person saul = new Person();
		saul.setAreaCode(areaCode);
		saul.setDateOfBirth(dateOfBirth);
		saul.setGender(isMale ? 'M' : 'F');
		StringBuffer bf = new StringBuffer();
		bf.append(isMale ? "Male" : "Female").append(", ");
		bf.append(age).append(", ");
		bf.append(dateOfBirth).append(", ");
		bf.append("from ").append(areaCode);
		
		return saul;
	}
	
	public static String getAreaInfo(String areaCode, List<String> allAreas) {
		List<MexItem> items = CollectionUtil.filterRaw(allAreas, areaCode);
		if(items.isEmpty()) {
			return areaCode;
		} else if(items.size() == 1) {
			String record = items.get(0).toString().replace(areaCode, "").trim();
			return record;
		} else {
			C.pl("Multiple records found, totally " + items.size());
			return areaCode;
		}
	}
}
