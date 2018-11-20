package com.sirap.extractor.images;

import com.sirap.common.domain.Album;

public abstract class WebsiteImageLinksFetcher {
	public abstract Album fetch(String weburl);
}
