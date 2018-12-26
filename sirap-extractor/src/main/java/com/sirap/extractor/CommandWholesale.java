package com.sirap.extractor;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.HttpUtil;
import com.sirap.basic.util.ImageUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.domain.Album;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.extractor.impl.ExtractorChinaAreaCodeZou114;
import com.sirap.extractor.impl.ExtractorChinaPostCodeToolcncn;
import com.sirap.extractor.impl.ExtractorNetease;
import com.sirap.extractor.impl.ExtractorPhoenix;
	
public class CommandWholesale extends CommandBase {

	private static final String KEY_SOGOU = "so";
	private static final String KEY_QIHU360 = "hu";
	private static final String KEY_WEIBO = "wo";
	private static final String KEY_PHOENIX = "fenghuang,phoenix";
	private static final String KEY_POSTCODE = "postcode,youbian";
	private static final String KEY_AREACODE = "areacode,quhao";
	private static final String KEY_GET_ALBUM = "ga";
	
	{
		helpMeanings.put("sogou.url", LinksFetchers.HOMEPAGE_SOGOU);
		helpMeanings.put("qihu360.url", LinksFetchers.QIHU360_HTML_QUERY);
		helpMeanings.put("163.netease.url", "http://www.163.com");
		helpMeanings.put("phoenix.url", ExtractorPhoenix.HOMEPAGE);
		helpMeanings.put("youbian.postcode.url", ExtractorChinaPostCodeToolcncn.HOMEPAGE);
		helpMeanings.put("quhao.areacode.url", ExtractorChinaAreaCodeZou114.HOMEPAGE);
	}
	
	public boolean handle() {
		
		solo = parseParam(KEY_GET_ALBUM + "\\s(.+)");
		if(solo != null) {
			if(HttpUtil.isHttp(solo)) {
				Album al = LinksFetchers.fetchAlbum(solo);
				if(al == null) {
					exportEmptyMsg();
				} else {
//					dealWithLinks(al.getLinks(), al.getName(), al.getTag());
					dealWithAlbum(al);
				}
				
				return true;
			} else {
				useLowOptions("-ts");
				int maxpage = OptionUtil.readIntegerPRI(options, "m", 10);
				String keyword = solo;
				List<String> links = LinksFetchers.qihu360ImageLinks(keyword, maxpage);
//				dealWithLinks(links, keyword, "");
				dealWithAlbum(new Album(keyword, links));
			}
		}
		
		solo = parseParam(KEY_SOGOU + "\\s(.*?)");
		if(isSingleParamNotnull()) {
			useLowOptions("-ts");
			List<String> links = LinksFetchers.sogouImageLinks(solo);
//			dealWithLinks(links, solo, "sogou");
			dealWithAlbum(new Album(solo, links).setTag("sogou"));
			
			return true;
		}
		
		params = parseParams(KEY_QIHU360 + "(|\\d{1,3})\\s(.*?)");
		if(isParamsNotnull()) {
			useLowOptions("-ts");
			int maxpage = MathUtil.toInteger(params[0], 3);
			String keyword = params[1];
			List<String> links = LinksFetchers.qihu360ImageLinks(keyword, maxpage);
//			dealWithLinks(links, keyword, "");
			dealWithAlbum(new Album(keyword, links));
			
			return true;
		}
		
		solo = parseParam(KEY_WEIBO + "\\s(.*?)");
		if(isSingleParamNotnull()) {
			int maxPage = OptionUtil.readIntegerPRI(options, "p", 1);
			List<String> sumlinks = Lists.newArrayList();
			for(int page = 1; page <= maxPage; page++) {
				List<String> links = LinksFetchers.weiboImageLinks(solo, page);
				if(links.isEmpty()) {
					C.msg("Found no images from page {0}", page);
					break;
				} else {
					sumlinks.addAll(links);
				}
			}

//			dealWithLinks(total, solo, "weibo");
			dealWithAlbum(new Album(solo, sumlinks).setTag("weibo"));
			
			return true;
		}
		
		String types = StrUtil.regexOfKeys(ExtractorNetease.TYPE_METHOD);
		solo = parseParam("163(" + types+ ")");
		if(solo != null) {
			String type = solo.toLowerCase();
			String method = ExtractorNetease.TYPE_METHOD.get(type);
			List<String> links = ExtractorUtil.photos(method, ExtractorNetease.class);

			export(links);
			return true;
		}
		
		if(isIn(KEY_PHOENIX)) {
			List<String> links = ExtractorPhoenix.photos();
			
			export(links);
			return true;
		}
		
		if(isIn(KEY_POSTCODE)) {
			List<MexObject> items = ExtractorChinaPostCodeToolcncn.getAllVillageCodes();
			export(items);
			
			return true;
		}
		
		if(isIn(KEY_AREACODE)) {
			List<MexObject> items = ExtractorChinaAreaCodeZou114.getAllAreaCodes();
			export(items);
			
			return true;
		}
		
		return false;
	}
	
	private void dealWithAlbum(Album bum) {
		List<String> links = bum.getLinks();
		if(EmptyUtil.isNullOrEmpty(links)) {
			export(links);
			return;
		}
		
		if(OptionUtil.readBooleanPRI(options, "do", true)) {
			String folderName = bum.niceName();
			String whereToSave = pathOfImages() + folderName + "/";
			batchDownload(links, whereToSave);
		} else {
			export(links);
		}
	}

	public boolean batchDownload(List<String> links, String whereToSave) {
		long start = System.currentTimeMillis();
		
		List<String> files = downloadFiles(whereToSave, links, Konstants.DOT_JPG);
		List<String> pathList = Lists.newArrayList();
		for(String filepath : files) {
			File file = new File(filepath);
			if(file.exists()) {
				if(ImageUtil.isValidImage(file.getAbsolutePath())) {
					pathList.add(filepath);
				} else {
//					file.delete();
				}
			}
		}
		
		if(!pathList.isEmpty()) {
			String lastFile = pathList.get(pathList.size() - 1);
			tryToOpenGeneratedImage(lastFile);
		}

		if(target instanceof TargetConsole) {
			return true;
		} 
		
		if(target.isFileRelated()) {
			export(Colls.fileListOf(pathList));
		} else {
			export(pathList);
		}
		
		long end = System.currentTimeMillis();
		C.time2(start, end);
		
		return true;
	}
}
