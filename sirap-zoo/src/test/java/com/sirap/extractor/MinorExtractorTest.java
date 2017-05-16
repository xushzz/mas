package com.sirap.extractor;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.impl.RemoteSecurityExtractor;

public class MinorExtractorTest {

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
