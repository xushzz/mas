package com.sirap.extractor;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.bible.BibleChapterExtractor;
import com.sirap.common.extractor.Extractor;
import com.sirap.common.extractor.impl.ChinaCalendarExtractor;
import com.sirap.common.extractor.impl.EnglishTranslationExtractor;
import com.sirap.common.extractor.impl.RemoteSecurityExtractor;
import com.sirap.common.extractor.impl.TulingExtractor;

public class MinorExtractorTest {
	
	@Test
	public void tuling() {
		String key = "e8c190a005adc401867efd1ad2602f70";
		Extractor<MexedObject> mike = new TulingExtractor(key, "nice try");
		mike.process();
		C.list(mike.getMexItems());
	}

	public void nongli() {
		Extractor mike = new ChinaCalendarExtractor("n20170601");
		mike.process();
		C.list(mike.getMexItems());
	}

	public void bible() {
		String sa = "E:\\KDB\\tasks\\0420_Bible\\titus.txt";
		Extractor mike = new BibleChapterExtractor("titus", 1);
		mike.process();
		C.list(mike.getMexItems());
	}

	public void sfz() {
//		Extractor<MexedObject> jes = new ChinaSFZExtractor();
//		jes.process();
	}
	
	public void compare() {
		String sa = "~ !@#$%^&*()_+`-=[]\\{}|;':\",./<>?L编";
//		sa = "#$";
		C.pl(sa);
		String va = XCodeUtil.urlEncodeUTF8(sa);
		C.pl(va);
		C.pl(XCodeUtil.urlDecodeUTF8(va));
	}
	
	public void chinese() throws Exception {
			String name="中+-*/f文";
		  //URL编码
		  String nameStr=new String(URLEncoder.encode(name,"utf-8").getBytes());
//		  C.pl(nameStr);
		  C.pl(System.getProperty("file.encoding"));
		  String[] codes = {Konstants.CODE_UTF8, Konstants.CODE_GBK, Konstants.CODE_GB2312};
		  for(int i = 0; i < codes.length; i++) {
			  D.pl(codes[i], URLEncoder.encode(name, codes[i]));
		  }
		  C.pl(URLEncoder.encode(name));
//		  C.pl(URLEncoder.encode(name, Konstants.CODE_UTF8));
//		  C.pl(URLEncoder.encode(name, Konstants.CODE_GBK));
//		  C.pl(URLEncoder.encode(name, Konstants.CODE_GB2312));
//		  C.pl(URLEncoder.encode(name, Konstants.CODE_ISO88591));
		  D.pl();
		  String s2 = new String(URLEncoder.encode(name, "UTF-8").getBytes(), "ISO-8859-1");
		  C.pl(s2);
		  //URL解码
		  System.out.println(URLDecoder.decode(new String(s2.getBytes("ISO-8859-1"), "UTF-8"), "UTF-8"));
	}
	
	public void english() {
		String word = "你好";
		EnglishTranslationExtractor james = new EnglishTranslationExtractor(word);
		james.process();
		C.list(james.getMexItems());
	}

	public void reduce() {
		String va = "html>            <html>            <head>		 		<meta";
		va = StrUtil.reduceMultipleSpacesToOne(va);
		C.pl(va);
	}
	
	public void zou114() {
		C.list(ExtractorChinaAreaCodeZou114.getAllAreaCodes());
//		C.list(ExtractorChinaAreaCodeZou114.getAllAreaLinks());
	}
	
//	@Test
	public void toolcncn() {
//		countyLinks = CollectionUtil.top(countyLinks, 2);
//		List<MexedObject> countyLinks = new ArrayList<>();
//		countyLinks.add(new MexedObject("/youbian/chongqing-qijiang"));

//		String regionCode = "547614"; //fengshan,luocheng
//		ExtractorChinaPostcodeYb21.getDetailByRegionCode(regionCode);
//		ExtractorChinaPostcodeYb21.getDetailByRegionCode("546413");
//		C.list(ExtractorChinaPostcodeYb21.getCountyCodesByCityCode("2310"));
		C.list(ExtractorChinaPostCodeToolcncn.getAllVillageCodes());
//		C.list(ExtractorChinaPostcodeToolcncn.getAllCountyCodes());
//		C.list(ExtractorChinaPostCodeToolcncn.getAllStreetNames());
	}
	public void postcodeTowns() {
//		String regionCode = "547614"; //fengshan,luocheng
//		ExtractorChinaPostcodeYb21.getDetailByRegionCode(regionCode);
//		ExtractorChinaPostcodeYb21.getDetailByRegionCode("546413");
//		C.list(ExtractorChinaPostcodeYb21.getCountyCodesByCityCode("2310"));
//		C.list(ExtractorChinaPostCodeYb21.getAllCityCodes());
//		C.list(ExtractorChinaPostcodeYb21.getAllCountyCodes());
	}
	
	public void expSource() {
		String url = "http://bbs.csdn.net/topics/391918862";
		RemoteSecurityExtractor hank = new RemoteSecurityExtractor("jamses#1886");
		hank.process();
		C.pl(hank.getExpiration());
	}
	public void blog() {
		
		String source = "Jinx    exp/james/20161212/exp exp/jack/20161112/exp exp/samuel/20161012/exp ddsds";
		String regex = "exp/jameds/(\\d{8})/exp";
		Matcher m = Pattern.compile(regex).matcher(source);
		while(m.find()) {
			C.pl(m.group(1));
		}
		
		D.sink(); 
	}

	//@Test
	public void liren() {
		List<String> items = ExtractorFancy.carverPhotos();
		C.list(items);
	}
//	public void moko() {
//		List<String> items = ExtractorUtilMoko.modelPhotos();
//		C.list(items);
//	}
//	public void event() {
//		List<Link> items = ExtractorUtilPhoenix.eventLinks();
//		C.list(items);
//	}
	
//	@Test(enabled=true)
//	public void youdao() {
//		String kw = "changzezi";
//		kw = "van persie";
//		List<MexedObject> links = ExtractorUtil.youdaoImageLinks(kw, 0);
//		download(kw, links);
//	}
	
	private void download(String kw, List<MexedObject> links) {
		long start = System.currentTimeMillis();
		String storage = "E:\\MasPro\\youdao\\" + kw + File.separator;
		FileUtil.makeDirectoriesIfNonExist(storage);
		IOUtil.downloadFiles(storage, links, ".jpg", 10);
		long end = System.currentTimeMillis();
		D.sink(end - start);
	}
}
