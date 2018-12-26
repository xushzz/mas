package com.sirap.extractor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MatrixUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.extractor.domain.MeituLassItem;
import com.sirap.extractor.domain.MeituOrgItem;
import com.sirap.extractor.impl.ExtractorFancy;
import com.sirap.extractor.impl.May699Utils;
import com.sirap.extractor.impl.MeituImageLinksExtractor;
import com.sirap.extractor.manager.MeituManager;

public class CommandFancy extends CommandBase {
	
	public static final String KEY_MEITU = "mei";
	public static final String KEY_MAY_MAX = "mmax";
	public static final String KEY_MAY_MAX_TRACE = "mmt";
	public static final String KEY_MAY_LOAD = "mload";

	public boolean handle() {

		regex = "(mmax|mmt)(|\\d{1,4})";
		params = parseParams(regex);
		if(params != null) {
			int top = 1 << 11;
			String type = params[0];
			String mytop = params[1];
			
			if(!mytop.isEmpty()) {
				top = Integer.parseInt(mytop);
			}
			
			if(StrUtil.equals(type, KEY_MAY_MAX)) {
				export(May699Utils.maxSearchId(top));
			} else {
				exportMatrix(May699Utils.traceMaxSearchId(top));
			}
			
			return true;
		}
		
		regex = KEY_MAY_LOAD + "(|\\d{1,4}),(\\d{1,4})";
		params = parseParams(regex);
		if(params != null) {
			String lowstr = params[0];
			int low = lowstr.isEmpty() ? 1 : Integer.parseInt(lowstr);
			int high = Integer.parseInt(params[1]);
			Map<Integer, String> kid = May699Utils.getIdAndWords(low, high);
			exportMatrix(MatrixUtil.matrixOf(kid));
			
			return true;
		}
		
		String types = StrUtil.connect(new ArrayList<String>(ExtractorFancy.TYPE_METHOD.keySet()), "|");
		solo = parseParam("lady(" + types+ ")");
		if(solo != null) {
			String type = solo.toLowerCase();
			String method = ExtractorFancy.TYPE_METHOD.get(type);
			List<String> links = ExtractorUtil.photos(method, ExtractorFancy.class);

			export(links);
			return true;
		}
		
		//fetch lass image links from library path that indicates one or more albums
		solo = parseParam(KEY_MEITU + " (.+)");
		if(solo != null && StrUtil.isRegexMatched("[a-z\\d/]+", solo)) {
			List<MexObject> items = new ArrayList<>();
			MeituImageLinksExtractor justin = new MeituImageLinksExtractor(solo);
			justin.process();
			items.addAll(justin.getItems());
			
			boolean showAll = OptionUtil.readBooleanPRI(options, "all", false);
			if(showAll) {
				List<MexObject> morePages = MeituManager.g().explode(solo, justin.getCountOfAlbum());
				items.addAll(MeituManager.g().getImageLinks(morePages));
			} else {
				int sample = g().getUserNumberValueOf("mei.some", 7);
				items = Colls.top(items, sample);
			}
			
			export(items);

			return true;
		}
		
		//fetch all organizations in library
		if(is(KEY_MEITU + "..") || is(KEY_MEITU + ".orgs")) {
			List<MeituOrgItem> items = MeituManager.g().getAllOrgItems(false);
			Collections.sort(items);
			export(items);
		}
		
		//fetch all lass paths
		if(is(KEY_MEITU + "....") || is(KEY_MEITU + ".lass")) {
			List<MeituLassItem> items = MeituManager.g().getAllLassItems();
			items = new ArrayList<>(new LinkedHashSet<>(items));
			Collections.sort(items);
			export(items);
		}
		
		//fetch lass intros from lass path which locates in txt file
		solo = parseParam(KEY_MEITU + ".intro (.+?)");
		if(solo != null) {
			File file = parseFile(solo);
			if(file != null) {
				String filePath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filePath)) {
					List<String> records = IOUtil.readFileIntoList(filePath);
					List<MexObject> items = MeituManager.g().readLassPath(records);
					List<MeituLassItem> intros = MeituManager.g().getAllLassIntros(items);
					Collections.sort(intros);
					export(intros);
				}
			}
			
			return true;
		}
		
		return false;
	}
}
