package com.sirap.common.component;

import java.util.List;

import com.sirap.basic.domain.MexItem;

public abstract class MexItemsFetcher<T extends MexItem> {
	public abstract List<T> body();
	public T header;
	public T footer;

	public String fixCriteria(String criteria) {
		return criteria;
	}
	
	public abstract void handle(List<T> items);
}
