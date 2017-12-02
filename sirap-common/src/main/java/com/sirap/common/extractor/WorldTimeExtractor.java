package com.sirap.common.extractor;

import java.util.Date;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexItem;

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
