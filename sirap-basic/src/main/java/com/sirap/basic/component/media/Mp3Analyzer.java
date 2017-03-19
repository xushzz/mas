package com.sirap.basic.component.media;

import com.sirap.basic.thirdparty.media.MediaFileUtil;
import com.sirap.basic.util.DateUtil;

public class Mp3Analyzer extends MediaFileAnalyzer {
	
	public Mp3Analyzer(String filepath) {
		this.filepath = filepath;
	}

	@Override
	public String getDurationInSeconds() {
		int durationInSeconds = MediaFileUtil.readMp3DurationInSeconds(filepath);
		String inHMS = DateUtil.convertSecondsIntoHourMinuteSecond(durationInSeconds);
		String value = inHMS + ", " + durationInSeconds + " seconds";
		
		return value;
	}
}
