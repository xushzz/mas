package com.sirap.common.extractor.impl;

import java.util.Date;

import com.sirap.basic.domain.MexItem;
import com.sirap.common.extractor.Extractor;

public abstract class WorldTimeExtractor extends Extractor<MexItem> {
	
	public WorldTimeExtractor() {
		setUrl(getUrl());
		printExceptionIfNeeded = false;
	}
	
	protected Date datetime;
	
	public Date getDatetime() {
		return datetime;
	}
}
