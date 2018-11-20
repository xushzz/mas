//package com.sirap.titus.extractor;
//
//import java.util.regex.Matcher;
//
//import com.sirap.basic.component.Extractor;
//import com.sirap.basic.util.StrUtil;
//import com.sirap.titus.BibleData;
//import com.sirap.titus.BibleVersion;
//
//public class BibleVersionsExtractorx extends Extractor<BibleVersion> {
//
//	public BibleVersionsExtractorx() {
//		printFetching = true;
//		String url = StrUtil.useSlash("https://www.biblegateway.com/");
//		setUrl(url);
//	}
//	
//	@Override
//	protected void parse() {
//		String regex = "<a href=\"[^\"]+/([^\"]+)/\">";
//		regex += "\\s*<h4[^<>]+>([^<>]+)";
//		regex += "<span class=\"text-muted\">([^<>]+)</span></h4>";
//		Matcher m = createMatcher(regex);
//		while(m.find()) {
//			String code = m.group(1);
//			String full = getPrettyText(m.group(2)).trim();
//			String name = m.group(3);
//			mexItems.add(new BibleVersion(code, name, full));
//		}
//	}
//}
