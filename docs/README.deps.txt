#commons-logging-1.2.jar
needed by pdfbox.jar

#core-3.2.1.jar
QRCode, com.google.zxing:core:jar:3.2.1
needed by project sirap-geek

#javase-2.2.jar
QRCode, com.google.zxing:javase:jar:2.2
needed by project sirap-geek

#pdfbox-2.0.3.jar
PDF, to calculate PDF pages, merge pages and so on.
org.apache.pdfbox.multipdf.PDFMergerUtility;
org.apache.pdfbox.pdmodel.PDDocument;

#fontbox-2.0.3.jar
PDF, org.apache.pdfbox:fontbox:jar:2.0.3
provide font stuff for creating PDF.

#itext-asian-5.2.0.jar
PDF, you can't print asian characters wtihout this jar in place.

#itextpdf-5.5.8.jar
PDF, you can't create PDF without this jar in place.
com.itextpdf.text.pdf.BaseFont;
com.itextpdf.text.pdf.PdfPCell;
com.itextpdf.text.pdf.PdfPTable;
com.itextpdf.text.BaseColor;
com.itextpdf.text.Chunk;
com.itextpdf.text.Document;

#jaudiotagger-2.0.3.jar
you need this while calculating file durations like MP3.

#mail-1.4.jar
regular send and receive email, aka Java Mail.
com.sirap.basic.thirdparty.email.EmailService
import javax.mail.AuthenticationFailedException;

#mysql-connector-java-5.1.31.jar
MySql, to connect mysql database server.

#ojdbc14-10.2.0.4.0.jar
Oracle, to connect oracle database server

#sqljdbc4-4.0.jar
Sqlserver, by microsoft

#poi-3.7.jar
Excel, to create Mircosoft Excel file.

#poi-3.10-FINAL.jar
Excel, to create Mircosoft Excel file.
com.sirap.basic.thirdparty.msoffice.MsExcelHelper
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

#poi-ooxml-3.10-FINAL.jar
Excel2007
com.sirap.basic.thirdparty.msoffice.MsExcelHelper
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

#bcprov-jdk15on-1.55.jar
you need this while dealing with PDF encrypted files.

#poi-ooxml-schemas-3.10-FINAL
Excel2007 xml
com.sirap.basic.thirdparty.msoffice.ExcelXReader
import org.apache.xerces.parsers.SAXParser;

#jsch-0.1.53.jar
SSH, com.jcraft:jsch:0.1.53
http://www.jcraft.com/jsch/
JSch is a pure Java implementation of SSH2.
JSch allows you to connect to an sshd server and use port forwarding, X11 forwarding, file transfer, etc., and you can integrate its functionality into your own Java programs. JSch is licensed under BSD style license.

#dom4j-1.6.1.jar
by poi-ooxml.jar

#poi-ooxml.jar
com.sirap.basic.thirdparty.msoffice.MsExcelHelper
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

#guava-20.0.jar

#httpclient-4.5.5.jar
make http request
com.sirap.basic.thirdparty.http.HttpHelper
import org.apache.http.client.entity.UrlEncodedFormEntity

#httpcore-4.4.9.jar
com.sirap.basic.thirdparty.http.HttpHelper
import org.apache.http.HttpEntity

#httpmime-4.5.5.jar
com.sirap.basic.thirdparty.http.HttpHelper
import org.apache.http.entity.mime.MultipartEntityBuilder

#log4j-1.2.17.jar
used by mybatis.jar

#mybatis-3.4.0.jar
by project sirap-orm

#thumbnailator-0.4.8.jar
Image related, compress
com.sirap.basic.thirdparty.image.ImageFixer
import net.coobird.thumbnailator.Thumbnails;

#xercesImpl-2.9.1.jar
com.sirap.basic.thirdparty.msoffice.ExcelXReader
import org.apache.xerces.parsers.SAXParser;

#xmlbeans-2.3.0.jar
by poi-ooxml-schemas