package com.sirap.common.framework.command.target;

import java.util.List;

import com.sirap.basic.output.ExcelParams;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.FileUtil;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.excel.MsExcelHelper;

public class TargetExcel extends TargetFile {
	
	@Override
	public ExcelParams getParams() {
		if(params instanceof ExcelParams) {
			return (ExcelParams)params;
		} else {
			params = new ExcelParams();
			return (ExcelParams)params;
		}
	}
	
	@Override
	public void export(List records, String options, boolean withTimestamp) {
		String filePath = withTimestamp ? getTimestampPath() : getFilePath();
		ExcelParams params = getParams();
		
		List newList = records;
		
		String topInfo = getCommand();
		if(newList != null && newList.size() > 5) {
			topInfo = "(" + newList.size() + ") " + topInfo;
		}
		
		params.setTopInfo(topInfo);
		
		MsExcelHelper.export(newList, filePath, params);
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
