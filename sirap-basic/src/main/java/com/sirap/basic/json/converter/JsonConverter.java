package com.sirap.basic.json.converter;

public abstract class JsonConverter<T> {
	public int DENT = 4;
	public abstract String toJson(T anyKindOfObject);
	public abstract String toPrettyJson(T anyKindOfObject, int depth);
}
