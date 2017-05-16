package com.sirap.common.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.FileUtil;

public class FileManagerTest {
	
	@Test
	public void remoteFolder() {
		//\\EPCNSZXW0153\\HybrisTeamShare
		D.sink();
		List<String> fixedPaths = new ArrayList<String>();
		fixedPaths.add("\\\\EPCNSZXW0153\\HybrisTeamShare");
		List<File> allFiles = FileUtil.scanFolder(fixedPaths, 3, null, false);
		C.list(allFiles);
		D.sink();
	}
}
