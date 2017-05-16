package com.sirap.basic.component.media;

import java.util.List;

import com.sirap.basic.thirdparty.media.MediaFileUtil;

public class FFAnalyzer extends MediaFileAnalyzer {
	
	public FFAnalyzer(String filepath) {
		this.filepath = filepath;
	}
	
	@Override
	public List<String> getDetail() {
		List<String> items = MediaFileUtil.detailWithFF(filepath);
		
		return items;
	}
}
