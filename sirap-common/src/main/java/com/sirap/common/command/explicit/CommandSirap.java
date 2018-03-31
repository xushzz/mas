package com.sirap.common.command.explicit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.basic.util.NetworkUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.domain.SiteSearchEngine;
import com.sirap.common.framework.App;
import com.sirap.common.framework.Janitor;
import com.sirap.common.framework.Stash;
import com.sirap.common.framework.command.InputAnalyzer;

public class CommandSirap extends CommandBase {
	
	private static final String KEY_VERSION = "ver";
	private static final String KEY_USER_TIMEZONE_SET = "u([+-](1[0-2]|[0-9]))";
	private static final String KEY_USER_SETTING = "u";
	private static final String KEY_CONFIGURATION = "c";
	private static final String KEY_USER_CONFIGURATION = "/";
	private static final String KEY_GENERATEDFILE_AUTOOPEN_ONOFF_SWITCH = "ox";
	private static final String KEY_TIMESTAMP_ENABLED_SWITCH = "tx";
	private static final String KEY_HOST = "host";
	private static final String KEY_MAC = "mac";
	private static final String KEY_ASSIGN_CHARSET = "gbk,utf8,utf-8,gb2312,unicode";
	private static final String KEY_FONTS = "fonts";
	private static final String KEY_SHOW_STASH = "stash";
	
	{
		helpMeanings.put("image.formats", Konstants.IMG_FORMATS);
		helpMeanings.put("guest.quits", KEY_EXIT);
		helpMeanings.put("guest.escaper", InputAnalyzer.EXPORT_ESACPE);
	}
	
	@Override
	public boolean handle() {
		
		if(is(KEY_GENERATEDFILE_AUTOOPEN_ONOFF_SWITCH)) {
			boolean flag = !g().isGeneratedFileAutoOpen();
			g().setGeneratedFileAutoOpen(flag);
			String value = flag ? "on" : "off";
			C.pl2("Generated-file auto-open is " + value + ".");
			
			return true;
		}
		
		if(is(KEY_TIMESTAMP_ENABLED_SWITCH)) {
			boolean flag = !g().isExportWithTimestampEnabled();
			g().setExportWithTimestampEnabled(flag);
			String value = flag ? "on" : "off";
			C.pl2("Export with timestamp is " + value + ".");
			
			return true;
		}
		
		if(is(KEY_VERSION)) {
			export(versionAndCopyright());
			return true;
		}

		if(is(KEY_USER_SETTING)) {
			export(getSystemInfo());
			return true;
		}
		
		solo = parseParam(KEY_USER_TIMEZONE_SET);
		if(solo != null) {
			resetTimeZone(Integer.parseInt(solo));
			return true;
		}
		
		String userConfig = KEY_USER_CONFIGURATION;
		if(PanaceaBox.isMac()) {
			userConfig = ";" + KEY_USER_CONFIGURATION;
		}
		
		params = parseParams(userConfig + "(|(.*?))");
		if(params != null) {
			String criteria = params[0];
			String userConfigFile = g().getUserConfigFileName();
			if(userConfigFile != null) {
				List<String> records = IOUtil.readFileIntoList(userConfigFile);
				export2(records, criteria);
				
				return true;
			}
		}
		
		if(is(KEY_CONFIGURATION + KEY_REFRESH)) {
			g().refresh();
			C.pl2("Configuration refreshed.");
			
			return true;
		}
		
		List<SiteSearchEngine> sites = getSiteSearchEngines();
		for(SiteSearchEngine engine:sites) {
			boolean isMatched = conductSiteSearch(engine);
			if(isMatched) {
				return true;
			}
		}
		
		if(handleHttpRequest(command)) {
			return true;
		}
		
		if(is(KEY_HOST)) {
			String result = NetworkUtil.getLocalhostNameIpMac();
			export(result);
			return true;
		}
		
		if(is(KEY_MAC)) {
			List<String> items = NetworkUtil.getLocalMacItems();
			export(items);
			return true;
		}
		
		solo = parseParam(KEY_HOST + "\\s+(.*?)");
		if(solo != null) {
			String result = NetworkUtil.getHostByName(solo);
			export(result);
			
			return true;
		}
		
		if(isIn(KEY_ASSIGN_CHARSET)) {
			String charset = command.replace("-", "").toUpperCase();
			g().setCharsetInUse(charset);
			C.pl2("Charset in use: " + command);
			
			return true;
		}
		
		if(is(KEY_FONTS)) {
			export(MexUtil.allFonts());
			
			return true;
		}
		
		if(is(KEY_SHOW_STASH)) {
			export(Stash.g().print());
			return true;
		}
		
		return false;
	}
	
	private List<SiteSearchEngine> getSiteSearchEngines() {
		List<SiteSearchEngine> engines = new ArrayList<SiteSearchEngine>();
		List<String> engineRecords = g().getValuesByKeyword("site.search.");
		List<String> userDefinedEngines = g().getUserValuesByKeyword("site.search.");
		engineRecords.addAll(userDefinedEngines);
		for(String engineInfo:engineRecords) {
			SiteSearchEngine se = new SiteSearchEngine();
			boolean flag = se.parse(engineInfo);
			if(flag) {
				engines.add(se);
			}
		}
		
		return engines;
	}
	
	private boolean conductSiteSearch(SiteSearchEngine engine) {
		String regex = engine.getPrefix() + " (.+?)";
		String singleParam = parseParam(regex);
		if(singleParam != null) {
			String urlTemplate = engine.getUrlTemplate();
			String url = StrUtil.occupy(urlTemplate, Extractor.encodeURLParam(singleParam));
			if(FileOpener.playThing(url, "page.viewer", true)) {
				C.pl(url);
				String motto = engine.getMotto();
				if(!EmptyUtil.isNullOrEmpty(motto)) {
					C.pl(engine.getMotto());
				}
				C.pl();
				
				return true;
			}
		}
		
		return false;
	}
	
	protected String getSystemInfo() {

		StringBuffer sb = new StringBuffer();
		sb.append(App.USERNAME);
		sb.append(" ").append(g().getSystemInfo());
		
		Date expirationDate = Janitor.g().getExpirationDate();
		if(expirationDate != null) {
			String expDateStr = DateUtil.displayDate(Janitor.g().getExpirationDate(), "yyyyMMdd");
			sb.append(" expire@").append(expDateStr);
		}

		return sb.toString();
	}
	
	protected boolean resetTimeZone(Integer value) {
		int currentTZ = g().getTimeZoneUser();
		boolean isChanged = currentTZ != value;
		
		if(isChanged) {
			g().setTimeZoneUser(value);
			C.pl2("TimeZone reset as GMT" + StrUtil.signValue(value));
		}
		
		return isChanged;
	}
}