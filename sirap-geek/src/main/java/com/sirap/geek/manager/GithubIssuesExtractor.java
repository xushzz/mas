package com.sirap.geek.manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class GithubIssuesExtractor extends Extractor<MexItem> {

	public static final String URL_TEMPLATE = "https://github.com/{0}/issues?q=";

	public GithubIssuesExtractor(String repo) {
		String url = StrUtil.occupy(URL_TEMPLATE, repo);
		printFetching = true;
		setUrl(url);
	}
	
	@Override
	protected void parseContent() {
		String regex = "/(\\d+)\" class=\"Box-row-link.*?\">(.*?)</a>";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
		while(m.find()) {
			String issue = m.group(1);
			String content = m.group(2).trim();
			String result = "#" + issue + " " + content;
			mexItems.add(new MexedObject(result));
		}
	}
}
