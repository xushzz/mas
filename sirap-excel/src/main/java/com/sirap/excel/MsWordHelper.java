package com.sirap.excel;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class MsWordHelper {
	public static final String TYPE_NORMAL = ".doc";
	public static final String TYPE_X = ".docx";
	
	public static int pagesOf(String filepath) {
		if(!StrUtil.isRegexFound("\\.docx$", filepath)) {
			XXXUtil.alert("Deal with .docx only, can't accept " + filepath);
		}
		try {
			XWPFDocument docx = new XWPFDocument(POIXMLDocument.openPackage(filepath));
			int pages = docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();//总页数
			
			return pages;
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}
}
