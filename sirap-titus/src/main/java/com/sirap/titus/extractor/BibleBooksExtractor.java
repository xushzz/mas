package com.sirap.titus.extractor;

import java.util.regex.Matcher;

import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.Link;
import com.sirap.common.extractor.Extractor;
import com.sirap.titus.BibleData;

public class BibleBooksExtractor extends Extractor<Link> {

	public BibleBooksExtractor(String version) {
		printFetching = true;
		setUrl(StrUtil.useSlash(BibleData.HOMEPAGE, version));
	}
	
	@Override
	protected void parseContent() {
		String regex = "<a[^<>]+href=\"([^\"]+)\">\\s*<h4[^<>]+>([^<>]+)</h4>";
		Matcher m = createMatcher(regex);
		while(m.find()) {
			String href = m.group(1);
			String name = getPrettyText(m.group(2));
			mexItems.add(new Link(name, href));
		}
	}
}
