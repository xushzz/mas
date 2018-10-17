package com.sirap.common.framework.command.target;

import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.SimpleKonfig;

public class TargetText extends TargetFile {
	
	public TargetText() {}
	
	public TargetText(String folderpath, String filename) {
		this.folderpath = folderpath;
		this.filename = filename;
	}
	
	@Override
	public void export(List records, String options, boolean withTimestamp) {
		String charset = SimpleKonfig.g().getCharsetInUse();
		String filePath = withTimestamp ? getTimestampPath() : getFilePath();
		IOUtil.saveAsTxtWithCharset(toStringList(records, options), filePath, charset);
		C.pl2("Exported => " + FileUtil.canonicalPathOf(filePath));
		if(SimpleKonfig.g().isGeneratedFileAutoOpen()) {
			FileOpener.open(filePath);
		}
	}
	
	@Override
	public String toString() {
		return folderpath + " *** " + filename;
	}
}
