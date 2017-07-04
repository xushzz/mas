package com.sirap.farm;

import java.util.Locale;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.LocaleUtil;

public class LoTest {
	
	@Test
	public void parseLocale() {

		String sb = "Japanese, , , , ,";
		sb = "Japanes";
		sb = "Japanes ";
		sb = "Japanes, ";
        String value = sb.toString().replaceAll("[\\s,]+$", "");
        D.pl(value);
		
		String regex = "([a-z]{2})(_([A-Z]{2})|)";
//		C.pl(StrUtil.isRegexMatched(regex, "es"));
//		C.pl(StrUtil.isRegexMatched(regex, "es_XX"));
//		C.pl(StrUtil.isRegexMatched(regex, "es_XXX"));
//		Locale lo = LocaleUtil.parseLocale("de_GK");
//		C.pl(LocaleUtil.doesExist(lo));
//		D.pl(LocaleUtil.parseLocale("es")); 
//		D.pl(LocaleUtil.parseLocale("es_XX"));
//		D.pl(LocaleUtil.parseLocale("es_XX_NINJA)A")); 
//		D.pl(LocaleUtil.parseLocale("es_XXK")); 
//		D.pl(LocaleUtil.parseLocale("es_XXK_SINGHA")); 
	}
}
