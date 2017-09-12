package com.sirap.extractor.konstants;

import com.sirap.basic.util.TrumpUtil;

public class MeituKonstants {
	private static final String XXX = TrumpUtil.decodeBySIRAP("C7779439BF728F46F7A881024D5D0D83", "final");
	public static final String HOME_PAGE = "http://www." + XXX + ".com";
	public static final int ALBUMS_PER_PAGE = 40;
}
