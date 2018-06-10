package com.sirap.extractor.konstants;

import com.sirap.basic.thirdparty.TrumpHelper;

public class MeituKonstants {
	private static final String XXX = TrumpHelper.decodeBySIRAP("C7779439BF728F46F7A881024D5D0D83", "final");
	public static final String HOME_PAGE = "http://www." + XXX + ".com";
	public static final int ALBUMS_PER_PAGE = 40;
}
