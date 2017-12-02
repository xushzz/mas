package com.sirap.common.extractor;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

public class RemoteSecurityExtractor extends Extractor<MexItem> {
	
	public static final String URL_SECURITY = "http://blog.sina.com.cn/s/blog_ad2be3220102w5g9.html"; 

	private String username;
	private Date expiration;

	public RemoteSecurityExtractor(String username) {
		setUrl(URL_SECURITY);
		this.username = username;
	}
	
	public RemoteSecurityExtractor(String url, String username) {
		setUrl(url);
		this.username = username;
	}

	@Override
	protected void parseContent() {
		String temp = "exp/{0}/(\\d{8})/exp";
		String regex = StrUtil.occupy(temp, username);
		Matcher m = Pattern.compile(regex).matcher(source);
		while(m.find()) {
			String expStr = m.group(1);
			expiration = DateUtil.parse("yyyyMMdd", expStr, false);
		}
	}
	
	public Date getExpiration() {
		return expiration;
	}
}
