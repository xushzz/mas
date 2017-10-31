package com.sirap.titus.extractor;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class BibleChapterFromSirapExtractor extends Extractor<MexObject> {

	public static final String HOME = "https://gitee.com/thewire/bible/raw/master/niv";
	
	/***
	 * 19%20Psalms/001.txt
	 * @param fullBookName
	 * @param chapter
	 */
	public BibleChapterFromSirapExtractor(String urlInfo) {
		printFetching = true;
		readIntoSourceList = true;
		String url = StrUtil.useSlash(HOME, urlInfo.replace(" ", "%20"));
		setUrl(url);
	}
	
	@Override
	protected void parseContent() {
		for(String item : sourceList) {
			mexItems.add(new MexObject(item));
		}
	}
}
