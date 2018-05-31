package com.sirap.basic.json.converter;

import com.sirap.basic.domain.MexItem;

public class JsonConverterMexItem extends JsonConverter<MexItem> {
	
	@Override
	public String toJson(MexItem ant) {
		return ant.toJson();
	}

	@Override
	public String toPrettyJson(MexItem ant, int depth) {
		return ant.toPrettyJson(depth);
	}
}
