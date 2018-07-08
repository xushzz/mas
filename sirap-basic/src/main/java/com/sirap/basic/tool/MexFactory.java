package com.sirap.basic.tool;

import com.sirap.basic.component.Finer;
import com.sirap.basic.util.MathUtil;

public class MexFactory {
	public static Finer createFiner(String[] paramArr, int def) {
		String type = paramArr[0];
		String numberStr = paramArr[1];
		Integer number = MathUtil.toInteger(numberStr, def);
		if(number == null) {
			return null;
		}
		Finer fn = new Finer(type, number);
		return fn;
	}
}
