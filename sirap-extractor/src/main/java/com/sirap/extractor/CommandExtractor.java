package com.sirap.extractor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.framework.command.target.TargetConsole;
	
public class CommandExtractor extends CommandBase {

	private static final String KEY_SOGOU = "sg";
	private static final String KEY_YOUDAO = "yd";
	private static final String KEY_PHOENIX = "social";
	private static final String KEY_POSTCODE = "postcode,youbian";
	private static final String KEY_AREACODE = "areacode,quhao";
	
	public boolean handle() {
		
		singleParam = parseParam(KEY_SOGOU + "\\s(.*?)");
		if(singleParam != null) {
			String kw = singleParam;
			if(!EmptyUtil.isNullOrEmptyOrBlank(kw)) {
				return sogou(kw);
			}
		}
		
		params = parseParams(KEY_YOUDAO + "\\s(.*?)(|,\\s*(\\d{1,5}))");
		if(params != null) {
			String kw = params[0];
			int images = MathUtil.toInteger(params[2], 48);
			if(!EmptyUtil.isNullOrEmptyOrBlank(kw) && images > 0) {
				return youdao(kw.trim(), images);
			}
		}

		String types = StrUtil.connect(new ArrayList<String>(ExtractorNetease.TYPE_METHOD.keySet()), "|");
		singleParam = parseParam("163(" + types+ ")");
		if(singleParam != null) {
			String type = singleParam.toLowerCase();
			String method = ExtractorNetease.TYPE_METHOD.get(type);
			List<String> links = ExtractorUtil.photos(method, ExtractorNetease.class);

			export(links);
			return true;
		}
		
		types = StrUtil.connect(new ArrayList<String>(ExtractorFancy.TYPE_METHOD.keySet()), "|");
		singleParam = parseParam("lady(" + types+ ")");
		if(singleParam != null) {
			String type = singleParam.toLowerCase();
			String method = ExtractorFancy.TYPE_METHOD.get(type);
			List<String> links = ExtractorUtil.photos(method, ExtractorFancy.class);

			export(links);
			return true;
		}
		
		if(is(KEY_PHOENIX)) {
			List<String> links = ExtractorPhoenix.photos();
			
			export(links);
			return true;
		}
		
		if(isIn(KEY_POSTCODE)) {
			List<MexedObject> items = ExtractorChinaPostCodeToolcncn.getAllVillageCodes();
			export(items);
			
			return true;
		}
		
		if(isIn(KEY_AREACODE)) {
			List<MexedObject> items = ExtractorChinaAreaCodeZou114.getAllAreaCodes();
			export(items);
			
			return true;
		}
		
		return false;
	}
	
	public boolean sogou(String param) {
		long start = System.currentTimeMillis();

		List<MexedObject> links = ExtractorUtil.sogouImageLinks(param);
		if(EmptyUtil.isNullOrEmpty(links)) {
			return false;
		}
		
		String folderName = FileUtil.generateLegalFileName(param);
		String path = pathWithSeparator("storage.sogou", Konstants.FOLDER_SOGOU);
		String destination = path + folderName + File.separator;
		
		List<String> pathList = downloadFiles(destination, links, Konstants.SUFFIX_JPG);
		
		if(!pathList.isEmpty()) {
			String lastFile = pathList.get(pathList.size() - 1);
			tryToOpenGeneratedImage(lastFile);
		}

		if(target instanceof TargetConsole) {
			return true;
		} 
		
		if(target.isFileRelated()) {
			export(CollectionUtil.toFileList(pathList));
		} else {
			export(pathList);
		}
		
		long end = System.currentTimeMillis();
		C.time2(start, end);
		
		return true;
	}
	
	public boolean youdao(String param, int amount) {
		long start = System.currentTimeMillis();
		int imagesPerPage = 24;
		
		Set<MexedObject> items = new HashSet<MexedObject>();
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			int startPage = i * imagesPerPage;
			List<MexedObject> temp = ExtractorUtil.youdaoImageLinks(param, startPage);
			if(EmptyUtil.isNullOrEmpty(temp)) {
				break;
			}
			int size1 = items.size();
			items.addAll(temp);
			int size2 = items.size();
			if(size1 == size2) {
				break;
			}
			if(items.size() >= amount) {
				break;
			}
		}
		
		List<MexedObject> links = new ArrayList<MexedObject>(items);
		links = CollectionUtil.top(links, amount);
		String folderName = FileUtil.generateLegalFileName(param);
		String path = pathWithSeparator("storage.youdao", Konstants.FOLDER_YOUDAO);
		String destination = path + folderName + File.separator;
		
		List<String> pathList = downloadFiles(destination, links, Konstants.SUFFIX_JPG);
		
		if(!pathList.isEmpty()) {
			String lastFile = pathList.get(pathList.size() - 1);
			tryToOpenGeneratedImage(lastFile);
		}

		if(target instanceof TargetConsole) {
			return true;
		}
		
		if(target.isFileRelated()) {
			export(CollectionUtil.toFileList(pathList));
		} else {
			export(pathList);
		}
		
		long end = System.currentTimeMillis();
		C.time2(start, end);
		
		return true;
	}
}
