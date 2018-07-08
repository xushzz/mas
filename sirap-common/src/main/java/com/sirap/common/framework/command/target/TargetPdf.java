package com.sirap.common.framework.command.target;

import java.util.List;

import com.sirap.basic.output.PDFParams;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.FileUtil;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.third.msoffice.PdfHelper;

public class TargetPdf extends TargetFile {
	
	public TargetPdf() {}
	
	public TargetPdf(String folderpath, String filename) {
		this.folderpath = folderpath;
		this.filename = filename;
	}
	
	@Override
	public PDFParams getParams() {
		if(params instanceof PDFParams) {
			return (PDFParams)params;
		} else {
			params = new PDFParams();
			return (PDFParams)params;
		}
	}
	
	@Override
	public void export(List records, String options, boolean withTimestamp) {
		String filePath = withTimestamp ? getTimestampPath() : getFilePath();
		PDFParams params = getParams();
		params.setPrintGreyRow(SimpleKonfig.g().isPrintGreyRowWhenPDF());
		params.setPrintTopInfo(SimpleKonfig.g().isPrintTopInfoWhenPDF());
		params.setUseAsianFont(SimpleKonfig.g().isAsianFontWhenPDF());
		
		String topInfo = getCommand();
		if(records != null && records.size() > 5) {
			topInfo = "(" + records.size() + ") " + topInfo;
		}
		
		params.setTopInfo(topInfo);
		
		PdfHelper.export(records, filePath, params);
		C.pl2("Exported => " + FileUtil.canonicalPathOf(filePath));
		if(SimpleKonfig.g().isGeneratedFileAutoOpen()) {
			FileOpener.open(filePath);
		}
		
		return;
	}
	
	@Override
	public String toString() {
		return folderpath + " *** " + filename;
	}
}
