package com.sirap.common.framework.command.target;

import java.util.List;

import com.sirap.basic.output.PDFParams;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
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
			return new PDFParams();
		}
	}
	
	@Override
	public void export(List records, String options, boolean withTimestamp) {
		String filePath = withTimestamp ? getTimestampPath() : getFilePath();
		PDFParams params = getParams();
		params.setPrintGreyRow(SimpleKonfig.g().isPrintGreyRowWhenPDF());
		params.setPrintTopInfo(SimpleKonfig.g().isPrintTopInfoWhenPDF());
		params.setUseAsianFont(SimpleKonfig.g().isAsianFontWhenPDF());
		params.setTopInfo(topInfo(records));
		
		int columns = OptionUtil.readIntegerPRI(options, "columns", 1);
		
		int[] cells = getPdfCellAligns();
		if(cells == null) {
			cells = MathUtil.kIntsOf(0, columns);
		}
		params.setCellAligns(cells);
		
		cells = getPdfCellWidths();
		if(cells == null) {
			cells = MathUtil.kIntsOf(1, columns);
		}
		params.setCellWidths(cells);
		
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
