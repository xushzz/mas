package com.sirap.basic.component.media;

import java.util.List;

import com.sirap.basic.thirdparty.media.MediaFileHelper;

public class FFAnalyzer extends MediaFileAnalyzer {
	
	public FFAnalyzer(String filepath) {
		this.filepath = filepath;
	}
	
	@Override
	public List<String> getDetail() {
		List<String> items = MediaFileHelper.detailWithFF(filepath);
		
		return items;
	}
}
