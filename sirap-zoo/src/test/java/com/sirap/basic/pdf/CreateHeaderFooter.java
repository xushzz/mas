package com.sirap.basic.pdf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

public class CreateHeaderFooter  {
    public static void main(String[] args) throws DocumentException, MalformedURLException, IOException {
	Document document = new Document();
	PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("HeaderFooter.pdf"));
        Rectangle rect = new Rectangle(30, 30, 550, 800);
        writer.setBoxSize("art", rect);
        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
        writer.setPageEvent(event);
        document.open();
        document.add(new Paragraph("This is Page One"));
        document.newPage();
        document.add(new Paragraph("This is Page two"));
        document.close();
	System.out.println("Done");
    }
} 