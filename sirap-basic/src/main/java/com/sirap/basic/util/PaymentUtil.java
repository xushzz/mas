package com.sirap.basic.util;

import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.PaymentItem;

public class PaymentUtil {
	
	public static final String URL_DONATION = "https://gitee.com/thewire/todos/raw/master/high/images/donation.txt";

	public static PaymentItem getActive(final String type) {
		Extractor<PaymentItem> nick = new Extractor<PaymentItem>() {

			
			@Override
			public String getUrl() {
				useList();
				return URL_DONATION;
			}
			
			@Override
			protected void parse() {
				String regex = "((wxp|HTTPS)://[^\\|]+)\\|(.+)";
				for(String line : sourceList) {
					String temp = line.trim();
					if(StrUtil.startsWith(temp, "active")) {
						Matcher ma = createMatcher(regex, temp);
						while(ma.find()) {
							item = new PaymentItem();
							item.setUrl(ma.group(1));
							item.setRemark(ma.group(3));
						}
						if(StrUtil.contains(item.getType(), type)) {
							break;
						}
					}
				}
			}
		};
		
		return nick.process().getItem();
	}
}
