package com.sirap.extractor;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.extractor.impl.ExtractorFancy;

public class CommandFancy extends CommandBase {

	public boolean handle() {
		String types = StrUtil.connect(new ArrayList<String>(ExtractorFancy.TYPE_METHOD.keySet()), "|");
		singleParam = parseParam("lady(" + types+ ")");
		if(singleParam != null) {
			String type = singleParam.toLowerCase();
			String method = ExtractorFancy.TYPE_METHOD.get(type);
			List<String> links = ExtractorUtil.photos(method, ExtractorFancy.class);

			export(links);
			return true;
		}
		
		return false;
	}
}
