package com.sirap.extractor.manager;

import java.util.List;

import com.sirap.basic.domain.MexObject;

public abstract class RssExtractorManager {
	public abstract List<MexObject> readAllRss();
	public abstract List<MexObject> fetchRssByType(final String type);
}
