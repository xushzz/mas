package com.sirap.common.component;

import java.util.List;

import com.sirap.basic.domain.MexItem;

public abstract class MexItemsFetcher {
	public abstract List<MexItem> body();
	public MexItem header;
	public MexItem footer;

	public String fixCriteria(String criteria) {
		return criteria;
	}
	
	public abstract void handle(List<MexItem> items);
}
