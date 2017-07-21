package com.sirap.extractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.extractor.impl.ExtractorChinaAreaCodeZou114;
import com.sirap.extractor.impl.ExtractorChinaPostCodeToolcncn;
import com.sirap.extractor.impl.ExtractorNetease;
import com.sirap.extractor.impl.ExtractorPhoenix;
	
public class CommandWholesale extends CommandBase {

	private static final String KEY_SOGOU = "sg";
	private static final String KEY_QIHU360 = "so";
	private static final String KEY_PHOENIX = "fenghuang,phoenix";
	private static final String KEY_POSTCODE = "postcode,youbian";
	private static final String KEY_AREACODE = "areacode,quhao";
	private static final String FOLDER_QIHU360 = "so360";
	
	{
		helpMeanings.put("sogou.url", ExtractorUtil.HOMEPAGE_SOGOU);
		helpMeanings.put("qihu360.url", ExtractorUtil.HOMEPAGE_QIHU360);
		helpMeanings.put("qihu360.folder", FOLDER_QIHU360);
		helpMeanings.put("163.netease.url", "http://www.163.com");
		helpMeanings.put("phoenix.url", ExtractorPhoenix.HOMEPAGE);
		helpMeanings.put("youbian.postcode.url", ExtractorChinaPostCodeToolcncn.HOMEPAGE);
		helpMeanings.put("quhao.areacode.url", ExtractorChinaAreaCodeZou114.HOMEPAGE);
	}
	
	public boolean handle() {
		
		singleParam = parseParam(KEY_SOGOU + "\\s(.*?)");
		if(isSingleParamNotnull()) {
			List<MexObject> links = ExtractorUtil.sogouImageLinks(singleParam);
			if(!EmptyUtil.isNullOrEmpty(links)) {
				String folderName = FileUtil.generateLegalFileName(singleParam);
				String path = pathWithSeparator("storage.sogou", Konstants.FOLDER_SOGOU);
				String whereToSave = path + folderName + File.separator;
				
				batchDownload(links, whereToSave);
			}
			
			return true;
		}
		
		singleParam = parseParam(KEY_QIHU360 + "\\s(.*?)");
		if(isSingleParamNotnull()) {
			List<MexObject> links = ExtractorUtil.qihu360ImageLinks(singleParam);
			if(!EmptyUtil.isNullOrEmpty(links)) {
				String folderName = FileUtil.generateLegalFileName(singleParam);
				String path = pathWithSeparator("storage.so360", FOLDER_QIHU360);
				String whereToSave = path + folderName + File.separator;
				
				batchDownload(links, whereToSave);
			}
			
			return true;
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
	
	public boolean batchDownload(List<MexObject> links, String whereToSave) {
		long start = System.currentTimeMillis();
		
		List<String> pathList = downloadFiles(whereToSave, links, Konstants.SUFFIX_JPG);
		
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
