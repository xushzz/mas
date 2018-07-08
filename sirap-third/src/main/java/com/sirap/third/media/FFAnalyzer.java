package com.sirap.third.media;

import java.util.List;

public class FFAnalyzer extends MediaFileAnalyzer {
	
	public FFAnalyzer(String filepath) {
		this.filepath = filepath;
	}
	
	@Override
	public List<String> getDetail() {
		List<String> items = MediaHelper.detailWithFF(filepath);
		
		return items;
	}
}
