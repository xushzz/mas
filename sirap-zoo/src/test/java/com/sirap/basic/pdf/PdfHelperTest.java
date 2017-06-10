package com.sirap.basic.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sirap.basic.thirdparty.pdf.PdfHelper;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

public class PdfHelperTest {
	
	@Test
	public void docxPages() throws IOException {
//		String sa = "E:\\KDB\\tasks\\0526_TextSearch\\Mac.docx";
//		XWPFDocument docx = new XWPFDocument(POIXMLDocument.openPackage(sa));
//		int pages = docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();//总页数
//		D.pl(sa, pages);
	}
	
	public void count() {
		String dir = "D:\\MasDEV\\exp\\20161103_123035_KDB5_28-1.pdf";
		dir = "D:\\MasDEV\\exp\\20161103_123129_KDB5_3x_2_1.pdf";
		dir = "E:\\KDB\\tasks\\0526_TextSearch\\20160118_224901_z.china.pdf";
		dir = "E:\\KDB\\tasks\\0526_TextSearch\\The Art of Deception_P335.pdf";
		//C.pl(PdfHelper.isEncrypted(dir));
		int size = PdfHelper.howManyPages(dir);
		D.pl(dir, size);
	}
	
	public void select() {
		String dir = "E:/Mas/issues/Issue_PdfMergeSplit/";
		String filepath = dir + "MINZU.pdf";
		String newFilepath = dir + DateUtil.timestamp() + ".pdf";
		List<Integer> pageNumbers = StrUtil.extractIntegers("1,3,5,6,7");
		PdfHelper.selectPages("1,3,5,6,7", filepath, newFilepath);
	}

	public void merge() {
		String dir = "E:/Mas/issues/Issue_PdfMergeSplit/";
		List<String> list = new ArrayList<>();
		list.add(dir+"DEF.pdf");
		list.add(dir+"ABC.pdf");
		list.add(dir+"FS.txt");
		String newPath = dir + DateUtil.timestamp() + ".pdf";
		PdfHelper.merge(list, newPath);
	}
	
	//@Test
	public void pages() {
		String source = "1,5,k3,5-7,1-,2  -  33, 3- 3  4";
		source = "1,5,5-7,12  -  33, 3- 34";
		source = "1,5,3,5-7,23";
		source = "1,5,3,11-7,23,9-16";
		source = "12345-9";
		D.ts();
		List<Integer> numbers = PdfHelper.parsePrintPageNumbers(source, 1919);
		//C.list(numbers);
		D.ts(numbers.size());
	}
}
