package com.sirap.extractor.manager;

import java.util.List;

import com.sirap.basic.component.Extractor;
import com.sirap.extractor.domain.MMKChannelItem;
import com.sirap.extractor.impl.MMKChannelsExtractor;

public class MMKManager {
	private static MMKManager instance;

	public static MMKManager g() {
		if(instance == null) {
			instance = new MMKManager();
		}
		
		return instance;
	}
	
	public List<MMKChannelItem> allChannels() {
		Extractor<MMKChannelItem> tanaka = new MMKChannelsExtractor();
		tanaka.process();

		return tanaka.getItems();
	}
}
