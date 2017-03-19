package com.sirap.basic.component.media;

import com.sirap.basic.thirdparty.media.MediaFileUtil;

public class FFAnalyzer extends MediaFileAnalyzer {
	
	public FFAnalyzer(String filepath) {
		this.filepath = filepath;
	}

	@Override
	public String getDurationInSeconds() {
		String durationInSeconds = MediaFileUtil.readMediaDurationInSecondsWithFF(filepath);
		
		return durationInSeconds;
	}
	
}
