package com.sirap.basic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.junit.Test;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.MexedMap;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.search.TextSearcher;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;

public class IOUtilTest {

	//@Test
	public void readCharsetMeta() {
		String va = "<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\">";
		C.pl(WebReader.parseWebCharsetByMeta(va));
	}
	public void readCharset() {
		String url = "http://www.cnblogs.com/QQParadise/articles/5020215.html"; //utf-8
		url = "http://www.00cha.com/shouji/?mobile=13692290530"; //gbk
		//String va = IOUtil.readURL(url);
		//String fileName = "E:\\Mas\\exp\\20170424_032125_nessi.txt";
//		C.pl(IOUtil.parseWebCharsetByMeta(IOUtil.readFileWithoutLineSeparator(fileName)));
	}
	
	public void readURL() {
		String charSet = Konstants.CODE_UTF8;
		String url = "http://127.0.0.1:5984/ninja/001";
//		url = "http://www.00cha.com/shouji/?mobile=13692290530";
		String va = IOUtil.readURL(url, "UTF-8", true);
		C.pl(va);
	}
	public void realFormat() throws IOException {  
		 String filepath = "E:\\KDB\\tasks\\0409_MoreVideoDetail\\b.png";
        File file = new File(filepath);  
        // create an image input stream from the specified file  
        ImageInputStream iis = ImageIO.createImageInputStream(file);  
  
        // get all currently registered readers that recognize the image format  
        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);  
        while(iter.hasNext()) {
        	String item = iter.next().getFormatName();
        	D.sink(item);
        }
//        if (!iter.hasNext()) {  
//            throw new RuntimeException("No readers found!");  
//        }  
  
        // get the first reader  
        ImageReader reader = iter.next();  
  
        D.pl(reader.getFormatName());  
  
        // close stream  
        iis.close();  
  
    }
	public void ImageMetadata() throws IOException {  
        int imageIndex = 0;  
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("png");  
        ImageReader reader = readers.next();  
        File bigFile = new File("E:\\Mas\\shot\\20170328_110229_QR_zavier.png");  
        ImageInputStream iis = ImageIO.createImageInputStream(bigFile);  
        reader.setInput(iis, true);  
        IIOMetadata metadata = reader.getImageMetadata(imageIndex);
        D.pl(metadata);
    }  
	
	public void imageTypes() {
		String[] imageFormats = ImageIO.getReaderFormatNames();  
        // [jpg, BMP, bmp, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]  
        System.out.println(Arrays.asList(imageFormats));  
  
        String[] imageFormats1 = ImageIO.getWriterFormatNames();  
        // [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]  
        System.out.println(Arrays.asList(imageFormats1));  
	}
	
	public void fakeProp() throws Exception {
		String filepath = "E:\\KDB\\tasks\\0318_ShortcutWithParams\\fakePro.txt";
		InputStream stream = new FileInputStream(filepath);
		
		MexedMap mm = IOUtil.readKeyValuesIntoMexedMap(stream);
		List<String> keys = new ArrayList(mm.getContainer().keySet());
		C.list(keys);
	}
	public void bing() {
		String citizenfour = "http://www.baidu.com/s?wd=citizenfour";
		C.pl(IOUtil.readURL(citizenfour));
	}
	
	public void cnn() throws Exception {
		String path = "D:/KDB/tasks/0601_HttpClient/cnn.properties";
		InputStream in=new FileInputStream(path);
        BufferedReader bf=new BufferedReader(new InputStreamReader(in,"gbk"));
        Properties props = new Properties();
        props.load(bf);
        
        C.pl(props);
	}
	
	public void excel() {
		String fullFileName = "E:\\" + DateUtil.timestamp() + ".xls";
		IOUtil.saveAsExcel(createRecords(100), fullFileName, null);
	}
	
	public void ignore() {
		String va = "D:/KDB/issues/1215_SqlSingleFile/MARTIN.txt";
		va = "D:/KDB/issues/1215_SqlSingleFile/SQL.txt";
		C.list(IOUtil.readFileIntoList(va));
		C.pl();
		C.pl(IOUtil.readFileWithLineSeparator(va, " "));
		C.pl();
		C.pl(IOUtil.readFileWithLineSeparator(va, " ", "--"));
	}
	
	public void escape() {
		String path = "D:/KDB/tasks/0601_HttpClient/cnn.properties";
		path = "D:/KDB/tasks/0601_Http&Client/cnn.prop=erties";
		
		C.pl(FileUtil.generateLegalFileName(path));
	}
	
	public void cnn2() {
		String path = "D:/KDB/tasks/0601_HttpClient/cnn.properties";
		MexedMap mm = IOUtil.createMexedMapByRegularFile(path);
		C.pl(mm.getContainer());		
	}
	public void textSearch() {
		String foldersStr = "D:/Github/mas/scripts";
//		foldersStr = "F:/Docs/Speech"; 
		foldersStr = "E:/GitProjects/SIRAP/mas/scripts/SHUN";
		String suffixesStr = ".bat;ties;txt";
//		suffixesStr = ".bat;gen";
		List<String> folders = StrUtil.splitByRegex(foldersStr);
		String[] suffixes = suffixesStr.split(";");
		String criteria = "ldap";
		List<MexedObject> items = TextSearcher.search(folders, suffixes, criteria, true);
		C.listMex(items);
	}

	public void readLines() {
		String dir = "E:/Mas/exp/20160925_002348_A.txt";
		String str = IOUtil.readFileWithLineSeparator(dir, "@");
		C.pl(str);
		
	}
	
	//@Test
	public void gitlab() {
		//6720701B662EA4674615684B07F27088BC9345D5103E15D615A5473022324D0AA59A642E449FF9D71EDEF1C601AEBCE7
		String url = null;
		C.pl(IOUtil.readURL(url));
	}
	
	public void pdf() {
		String fullFileName = "E:\\" + DateUtil.timestamp() + ".pdf";
		IOUtil.saveAsPDF(createRecords(100), fullFileName, null);
	}
	
	public static List createRecords(int lines) {
		List<String> records = new ArrayList<>();
        for(int i = 0; i < lines; i++) {
        	records.add("lines " + (i + 1) + " " + RandomUtil.alphanumeric(9));
        }
        
        return records;
	}
	
	public void zhan() {
		String dir = "E:\\MasPro\\misc\\";
		List<String> msg = IOUtil.readFileIntoList(dir + "ndata.js");
		//"m": "adj.\u90a3, \u90a3\u4e2a;\r\npron", "w": "that"
		String regex = "\"m\": \"(.*?)\", \"w\": \"(.*?)\"";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(msg.get(0));
		List<String> list = new ArrayList<String>();
		while(m.find()) {
			String detail = m.group(1);
			String word = m.group(2);
			//detail = StrUtil.replaceUnicodeChars(detail);
			list.add(word + "  " + detail);
		}
		String fullFileName = dir + DateUtil.timestamp() + ".txt";
		list.add(C.getTotal(list.size()));
		IOUtil.saveAsTxt(list, fullFileName);
	}
	
	public void decode() {  
		String unicodeStr = "u6709";
		StringBuffer sb = new StringBuffer();  
	    String str[] = unicodeStr.toUpperCase().split("U");  
	    for(int i=0;i<str.length;i++){  
	      if(str[i].equals("")) continue;  
	      char c = (char)Integer.parseInt("6709",16);  
	      sb.append(c);  
	    }  
	    C.pl(sb);
	}  
	
	public void ws() {
		String url = "http://api.geonames.org/cities?north=44.1&south=-9.9&east=-22.4&west=55.2&username=demo";
		
//		url = "http://api.geonames.org/timezoneJSON?lat=47.01&lng=10.2&username=demo";
		D.pl(IOUtil.readURL(url));
	}
	
	public void encode() {
		String kw = "nihao";
		try {
			C.pl(URLDecoder.decode(kw, "utf8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void copy() {
		String sourceFile = "E:\\Documents\\oldfashion\\Commercial.txt";
		String targetFile = "E:\\MasPro\\exp";
		IOUtil.copyFile(sourceFile, targetFile);
	}
	
	@Test
	public void uuid() {
		for(int i = 0; i < 10; i++) {
			String temp = UUID.randomUUID().toString();
			String t2 = temp.replace("-", "");
			D.pl(temp, t2);
		}
	}
	
	public void fetch() {
		String storage = "E:\\Mas\\";
		String sa = "http://images.china.cn/attachement/jpg/site1007/20120409/0019b91ec74f10ecc31e0c.jpg";
//		sa = "http://www.americanrhetoric.com/speeches/PDFFiles/Barack%20Obama%20-%202004%20DNC%20Address.pdf";
		sa = "http://www.americanrhetoric.com/speeches/PDFFiles/Barack%20Obama%20-%20Nomination%20Victory.pdf";
		sa = "http://www.governmentattic.org/12docs/CIA-FOIAmanual_2010.pdf";
//		sa = "http://images.china.cn/attacheme2nt/jpg/site1007/20120409/0019b91ecs74f10ecc31e0c.jpg";
		sa = "https://www.afio.com/publications/PALADINI%20Chinese%20Intelligence%20Draft%202015Jun27.pdf";
		String filePath = storage + DateUtil.timestamp() + "_" + FileUtil.generateFilenameByUrl(sa);
		if(FileUtil.exists(filePath)) {
			C.pl("Already existed => " + filePath);
			return;
		}
		
		if(IOUtil.downloadNormalFile(sa, filePath, true)) {
			D.pl(filePath);
			PanaceaBox.openFile(filePath);
		} else {
			
		}
	}
}
