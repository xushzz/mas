package com.sirap.titus.extractor;

import java.util.regex.Matcher;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.extractor.Extractor;
import com.sirap.titus.BibleData;

public class BibleChapterExtractor extends Extractor<MexObject> {

	public BibleChapterExtractor(String chapterLink) {
		printFetching = true;
		setUrl(chapterLink);
	}
	
	public BibleChapterExtractor(String fullBookName, int chapter) {
		printFetching = true;
		String param = fullBookName.toLowerCase().replace(' ', '-');
		String url = StrUtil.useSlash(BibleData.HOMEPAGE, param, chapter + ".html");
		setUrl(url);
	}
	
	private void readBasicInfo() {
		String regex = "<h1>([^><]+)<small>([^><]+)</small>\\s*</h1>";
		Matcher ma = createMatcher(regex);
		if(ma.find()) {
			String bookName = getPrettyText(ma.group(1)).trim();
			String baseInfo = StrUtil.occupy("<{0} {1}>", bookName, ma.group(2).trim());
			mexItems.add(new MexObject(baseInfo));
		} else {
			XXXUtil.info("Can't parse base info with " + regex);
		}
	}
	
	@Override
	protected void parseContent() {
		readBasicInfo();
		
		String regex = "";
		regex += "<h2><a href=\"[^\"]+\">([^><]+)</a></h2>";
		regex += "|";
		regex += "<div id=\"v-\\d+\" class=\"verse font-small\" style=\"[^\"]*\">(.+?)</div>";
		source = source.replace("<span class=\"red-letter\">", "");
		Matcher ma = createMatcher(regex);
		while(ma.find()) {
			String head = ma.group(1);
			String verse = ma.group(2);
			if(head != null) {
				mexItems.add(new MexObject("#" + head));
			} else {
				String temp = verse.replaceAll("<a href=\"javascript:void\\(0\\);\">[^><]+</a>", "");
				temp = HtmlUtil.removeHttpTag(temp).trim();
				temp = StrUtil.reduceMultipleSpacesToOne(temp);
				mexItems.add(new MexObject(temp));
			}
		}
	}
}
