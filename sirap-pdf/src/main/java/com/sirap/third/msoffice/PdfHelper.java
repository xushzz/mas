package com.sirap.third.msoffice;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.third.pdf.TableHeaderEvent;

@SuppressWarnings("all")
public class PdfHelper {
	
	public static PdfPTable createSimpleTable(List list) {
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell = table.getDefaultCell();
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		for (Object obj : list) {
			table.addCell(obj + "");
		}
		return table;
	}

	public static boolean export(List objList, String fullFileName, PDFParams params) {
		try {
			Document document = new Document(PageSize.A4, 36, 36, 64, 44);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fullFileName));
			TableHeaderEvent headerEvent = new TableHeaderEvent();
			writer.setBoxSize("art", new Rectangle(36, 64, 559, 778));
			writer.setPageEvent(headerEvent);
			document.open();
			if(params.isPrintTopInfo()) {
				String topInfo = params.getTopInfo();
				if(!EmptyUtil.isNullOrEmpty(topInfo)) {
					insertTopInfo(document, topInfo, params);
				}
			}
			insertTable(document, objList, params);
			document.close();
		} catch (Exception ex) {
			XXXUtil.alert(ex);
		}

		return true;
	}

	public static void insertTopInfo(Document doc, Object info, PDFParams params)
			throws DocumentException {
		PdfPTable datatable = new PdfPTable(1);
		datatable.setWidthPercentage(100);
		datatable.getDefaultCell().setPadding(2);
		datatable.getDefaultCell().setBorder(0);
		Font font = createFont(params.isUseAsianFont());
		font.setStyle(Font.ITALIC);
		Chunk c = new Chunk(info + "", font);
		c.setBackground(new BaseColor(89, 113, 166));
		Paragraph p = new Paragraph(c);
		datatable.addCell(p);
		doc.add(datatable);
	}

	@SuppressWarnings("rawtypes")
	public static void insertTable(Document doc, List<Object> list, PDFParams params)
			throws DocumentException {
		Font font = createFont(params.isUseAsianFont());
		boolean toGray = false;
		int[] cellsWidth = params.getCellWidths();
		if(cellsWidth == null) {
			cellsWidth = MathUtil.kIntsOf(1, 1);
		};
		int[] cellsAlign = params.getCellAligns();
		if(cellsAlign == null) {
			cellsAlign = MathUtil.kIntsOf(1, 1);
		};
		int cols = cellsWidth.length;
		
		PdfPTable datatable = new PdfPTable(cols);
		datatable.setWidths(cellsWidth);
		datatable.setWidthPercentage(100);
		datatable.getDefaultCell().setPadding(2);
		datatable.getDefaultCell().setBorder(0);
		for (int i = 0; i < list.size(); i++) {
			if(params.isPrintGreyRow()) {
				if (toGray) {
					datatable.getDefaultCell().setBackgroundColor(BaseColor.LIGHT_GRAY);
				} else {
					datatable.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
				}
			}
			Object record = list.get(i);
			if(record instanceof List) {
				List items = (List)record;
				for(int k = 0; k < items.size(); k++) {
					String stuff = items.get(k) + "";
					int align = cellsAlign[k];
					datatable.getDefaultCell().setHorizontalAlignment(align);
					datatable.addCell(new Paragraph(stuff, font));	
				}
			} else {
				String stuff = record + "";
				if(stuff.isEmpty()) {
					stuff = " ";
				}
				datatable.addCell(new Paragraph(stuff, font));	
			}
			toGray = !toGray;
		}
		doc.add(datatable);
	}

	public static Font createFont(boolean useAsianFont) {
		Font font = null;
		BaseFont bf = null;
		if(useAsianFont) {
			try {
				String fontName = "STSong-Light";
				String encoding = "UniGB-UCS2-H";
				bf = BaseFont.createFont(fontName, encoding, BaseFont.NOT_EMBEDDED);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (bf != null) {
			font = new Font(bf);
		} else {
			font = new Font();
		}

		return font;
	}
	
	/***
	 * 
	 * @param filepaths
	 * @param newFilepath
	 * @return
	 */
	public static String merge(List<String> filepaths, String newFilepath) {
		PDFMergerUtility mergePdf = new PDFMergerUtility();  
  
		try {
	        for(String filepath : filepaths) {
	            mergePdf.addSource(filepath);  
	        }
	          
	        mergePdf.setDestinationFileName(newFilepath);  
	        mergePdf.mergeDocuments();  
		} catch (Exception ex) {
			throw new MexException(ex.getMessage());
		}
          
        return newFilepath;
	}
	
	public static int pagesOf(String filepath) {
		File file = FileUtil.getIfNormalFile(filepath);
		XXXUtil.nullCheck(file, ":Invalid file path : " + filepath);
		
		try (PDDocument oldPdf = PDDocument.load(file)) {
		    PDPageTree tree = oldPdf.getDocumentCatalog().getPages();
		    int size = tree.getCount();
		    
		    return size;
		} catch (Exception ex) {
			throw new MexException(ex.getMessage());
		}
	}
	
	/****
	 * 
	 * @param pageNumbers
	 * @param filepath
	 * @param newFilepath
	 * @return
	 */
	public static String selectPages(String source, String filepath, String newFilepath) {
		File file = FileUtil.getIfNormalFile(filepath);
		try (PDDocument newPdf = new PDDocument();PDDocument oldPdf = PDDocument.load(file)) {
		    PDPageTree tree = oldPdf.getDocumentCatalog().getPages();
		    int size = tree.getCount();

			List<Integer> pageNumbers = MathUtil.parsePrintPageNumbers(source, size);
			
			boolean hasPage = false;
		    for(Integer number : pageNumbers) {
		    	if(number == null) {
		    		continue;
		    	}
		    	
		    	if(number < 1) {
		    		continue;
		    	}
		    	
		    	if(number > size) {
		    		throw new MexException("The page number " + number + " should not exceed max page number " + size + ".");
		    	}
		    	
		    	PDPage page = tree.get(number - 1);
		    	newPdf.importPage(page);
		    	hasPage = true;
		    }

		    if(!hasPage) {
		    	throw new MexException("No page generated.");
		    }
		    
		    newPdf.save(newFilepath);
		    
		} catch (Exception ex) {
			throw new MexException(ex.getMessage());
		}
		
		return newFilepath;
	}
}
