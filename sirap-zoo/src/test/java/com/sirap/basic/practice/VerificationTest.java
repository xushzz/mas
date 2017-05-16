package com.sirap.basic.practice;


import org.junit.Test;

import com.sirap.basic.tool.CaptchaGenerator;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.RandomUtil;


public class VerificationTest {

	@Test
    public void generate() {
		String text = RandomUtil.letters(4);
		String filePath = "D:/" + DateUtil.timestamp() + "_captcha.jpeg";
    	CaptchaGenerator james = new CaptchaGenerator(text);
    	james.writeImageTo(filePath);
    	long start = System.currentTimeMillis();
    	D.pl(filePath);
    	PanaceaBox.openFile(filePath);
    	long end = System.currentTimeMillis();
    	D.pl(end-start);
    }
}