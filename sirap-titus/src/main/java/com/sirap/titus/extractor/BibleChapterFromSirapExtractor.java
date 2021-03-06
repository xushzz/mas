package com.sirap.titus.extractor;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.StrUtil;

public class BibleChapterFromSirapExtractor extends Extractor<MexObject> {

	public static final String HOME = "https://gitee.com/thewire/bible/raw/master/niv";
	
	/***
	 * 19%20Psalms/001.txt
	 * @param fullBookName
	 * @param chapter
	 */
	public BibleChapterFromSirapExtractor(String urlInfo) {
		printFetching = true;
		String url = StrUtil.useSlash(HOME, urlInfo.replace(" ", "%20"));
		setUrl(url).useList();
	}
	
	@Override
	protected void parse() {
		for(String item : sourceList) {
			mexItems.add(new MexObject(item));
		}
	}
}
