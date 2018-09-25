package com.sirap.common.framework.command.target;

import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.html.HtmlExporter;
import com.sirap.basic.output.HtmlParams;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.Konfig;
import com.sirap.common.framework.SimpleKonfig;

public class TargetHtml extends TargetFile {
	
	public TargetHtml() {}
	
	public TargetHtml(String path, String filename) {
		this.folderpath = path;
		this.filename = filename;
	}
	
	@Override
	public HtmlParams getParams() {
		if(params instanceof HtmlParams) {
			return (HtmlParams)params;
		} else {
			params = new HtmlParams();
			return (HtmlParams)params;
		}
	}
	
	@Override
	public void export(List records, String options, boolean withTimestamp) {
		String filePath = withTimestamp ? getTimestampPath() : getFilePath();
		HtmlParams params = getParams();
		
		String topInfo = getCommand();
		if(records != null && records.size() > 5) {
			topInfo = "(" + records.size() + ") " + topInfo;
		}
		
		params.setTopInfo(topInfo);
		//IOUtil.readLinesFromStreamByClassLoader(Konfig.KONFIG_FILE, Konstants.CODE_UTF8)
		List<String> htmlTemplate = IOUtil.readLinesFromStreamByClassLoader("template_nice.html", SimpleKonfig.g().getCharsetInUse());
		List<String> htmlContent = HtmlExporter.generateHtmlContent(htmlTemplate, records, params);
		
		IOUtil.saveAsTxt(htmlContent, filePath);
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
