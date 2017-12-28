package com.sirap.extractor.avron;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;

public class AvronExtractors {
	
	public static final String AREACODE_ROOT = "00";
	
	public static List<ValuesItem> areacodesOfChina() {
		List<ValuesItem> allItems = Lists.newArrayList();
		areacodesOf(allItems, AREACODE_ROOT, 0);
		
		return allItems;
	}
	
	public static List<ValuesItem> areacodesOfGX() {
		ValuesItem vi = new ValuesItem("450000000000");
		vi.add("广西壮族自治区");
		vi.add(AREACODE_ROOT);
		vi.add(1);
		
		List<ValuesItem> allItems = Lists.newArrayList(vi);
		areacodesOf(allItems, vi.getByIndex(0) + "", Integer.parseInt(vi.getByIndex(3) + ""));
		
		return allItems;
	}
	
	public static void areacodesOf(List<ValuesItem> allItems, String topAreacode, int level) {
		Extractor<ValuesItem> neymar = new Extractor<ValuesItem>() {
			@Override
			public String getUrl() {
				printFetching = true;
				String url = "http://permit.mep.gov.cn/permitExt/outside/AjaxRegister.jsp?province=" + topAreacode;
				if(StrUtil.equals(topAreacode, AREACODE_ROOT)) {
					url = "http://permit.mep.gov.cn/permitExt/syssb/xxgk/xxgk!sqqlist.action";
				}
				return url;
			}
			
			@Override
			//<option value='450301000000'>市辖区</option>
			//<option  value="370000000000">山东省</option>
			protected void parseContent() {
				String regex = "<option\\s+value=['\"](\\d+)['\"]>(.+?)</option>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String code = ma.group(1);
					String name = ma.group(2);
					String parentCode = topAreacode;
					ValuesItem vi = new ValuesItem();
					vi.add(code);
					vi.add(name);
					vi.add(parentCode);
					vi.add(level + 1);
					mexItems.add(vi);
				}
			}
		};
		
		List<ValuesItem> tempItems = neymar.process().getItems();
		if(EmptyUtil.isNullOrEmpty(tempItems)) {
			return;
		}
		
		allItems.addAll(tempItems);
		for(ValuesItem vi : tempItems) {
			String currentAreaCode = vi.getByIndex(0) + "";
			int nextLevel = level + 1;
			if(nextLevel >= 3) {
				continue;
			}
			areacodesOf(allItems, currentAreaCode, level + 1);
		}
	}
	
	public static String areacodesOfJson(String fileOrWebUrl) {
		Extractor<String> neymar = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				readIntoSourceList = true;
				
				return fileOrWebUrl;
			}
			
			@Override
			protected void parseContent() {
				//450600000000, 防城港市, 450000000000, 2
				String template = "{\"code\": \"{0}\", \"name\":\"{1}\", \"parent\":\"{2}\", \"level\":\"{3}\"}";
				for(String record : sourceList) {
					List<String> list = StrUtil.split(record, ",");
					if(list.size() < 4) {
						D.ts(list);
						continue;
					}
					String temp = StrUtil.occupy(template, list.get(0), list.get(1), list.get(2), list.get(3));
					mexItems.add(temp);
				}
				
				item = "[" + StrUtil.connectWithComma(mexItems) + "]";
			}
		};
		
		return neymar.process().getItem();
	}
	
	public static String geoLocationsOf(String fileOrWebUrl) {
		Extractor<String> neymar = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				readIntoSourceList = true;
				
				return fileOrWebUrl;
			}
			
			@Override
			/****
			 * {"name": "北京市","center": "116.405285,39.904989"}
			 */
			protected void parseContent() {
				//广西田东锦鑫化工有限公司, 107.12703, 23.64798
				String template = "{\"name\": \"{0}\", \"center\":\"{1},{2}\"}";
				for(String record : sourceList) {
					List<String> list = StrUtil.split(record, ",");
					if(list.size() < 3) {
						C.pl(list);
						continue;
					}
					String temp = StrUtil.occupy(template, list.get(0), list.get(1), list.get(2));
					mexItems.add(temp);
				}
				
				item = "[" + StrUtil.connectWithComma(mexItems) + "]";
				C.pl(item);
			}
		};
		
		return neymar.process().getItem();
	}
	
	public static List<ValuesItem> fetch51JobResumes(String folderName) {
		List<MexFile> files = FileUtil.scanSingleFolder(folderName, 9);
		List<ValuesItem> items = Lists.newArrayList();
		for(MexFile file : files) {
			String path = file.getPath();
			if(StrUtil.isRegexFound("\\.(html|htm|txt)$", path)) {
				items.add(fetch51JobSummary(path));
			}
		}
		
		return items;
	}
	
	public static ValuesItem fetch51JobSummary(String fileOrWebUrl) {
		Extractor<ValuesItem> neymar = new Extractor<ValuesItem>() {
			
			@Override
			public String getUrl() {
				useGBK();
				return fileOrWebUrl;
			}
			
			@Override
			protected void parseContent() {
				ValuesItem vi = new ValuesItem();
				String regex = "<td width=\"400\" style=\"padding-bottom:18px\">(.+?)</td>";
				String info = StrUtil.findFirstMatchedItem(regex, source);
				if(info == null) {
					return;
				}
				info = info.replace("|", "");
				vi.add(getPrettyText(info));
				
				regex = "<td valign=\"bottom\" style=\"line-height:20px;color:#333333;word-break:break-all\">";
				regex += "(.+?)</td>";
				vi.add(getPrettyText(StrUtil.findFirstMatchedItem(regex, source)));
				
				regex = "<td valign=\"top\" width=\"246\" style=\"line-height:20px;color:#333333;word-break:break-all\">";
				regex += "([^<>]+)</td>";
				
				Matcher ma = createMatcher(regex);
				int count = 0, max= 11;
				while(ma.find()) {
					if(count > max) {
						continue;
					}
					count++;
					vi.add(getPrettyText(ma.group(1)));
				}

				item = vi;
			}
		};
		
		return neymar.process().getItem();
	}
	
	//许可申请前信息公开
	//http://permit.mep.gov.cn/permitExt/syssb/xxgk/xxgk!sqqlist.action
	public static String fetchMaxPageOfStagedCompanies() {
		Extractor<String> neymar = new Extractor<String>() {
			@Override
			public
			String getUrl() {
				printFetching = true;
				String url = "http://permit.mep.gov.cn/permitExt/syssb/xxgk/xxgk!sqqlist.action";
				return url;
			}
			
			@Override
			protected void parseContent() {
				String regexMax = "var\\s+pagesum\\s*=\\s*+(\\d+)\\s*;";
				item = StrUtil.findFirstMatchedItem(regexMax, source);
			}
		};
		
		return neymar.process().getItem();
	}
	
	public static List<ValuesItem> fetchStagedCompanies(int page) {
		Extractor<ValuesItem> neymar = new Extractor<ValuesItem>() {

			@Override
			public
			String getUrl() {
				printFetching = true;
				setRequestParams("page.pageNo=" + page);
				setMethodPost(true);
				String url = "http://permit.mep.gov.cn/permitExt/syssb/xxgk/xxgk!sqqlist.action";
				return url;
			}
			
			@Override
			protected void parseContent() {
				String regex = "<tr>\\s*";
				regex += "<td[^<>]*>([^<>]*)</td>\\s*";
				regex += "<td[^<>]*>([^<>]*)</td>\\s*";
				regex += "<td[^<>]*>([^<>]*)</td>\\s*";
				regex += "<td[^<>]*>([^<>]*)</td>\\s*";
				regex += "<td[^<>]*>([^<>]*)</td>\\s*";
				regex += "<td[^<>]*>([^<>]*)</td>\\s*";
				regex += "<td[^<>]*>\\s*<a [^<>]+><img [^<>]+/></a>\\s*</td>\\s*";
				regex += "<td[^<>]*>\\s*<a ([^<>]+)>[^<>]*</a>\\s*</td>\\s*";
				regex += "</tr>";
				Matcher ma = createMatcher(regex, source);
				
				while(ma.find()) {
					ValuesItem item = new ValuesItem();
					String pkid = StrUtil.findFirstMatchedItem("pkid=([a-z\\d]+)\"", ma.group(7));
					item.add(pkid);
					for(int k = 1; k <= 6; k++) {
						String temp = getPrettyText(ma.group(k));
						if(EmptyUtil.isNullOrEmpty(temp)) {
							continue;
						}
						item.add(temp);
					}
					mexItems.add(item);
				}
			}
		};
		
		return neymar.process().getItems();
	}
	
	//许可信息公开
	//http://permit.mep.gov.cn/permitExt/outside/Publicity?pageno=1
	public static String fetchMaxPageOfPublishedCompanies() {
		Extractor<String> neymar = new Extractor<String>() {
			@Override
			public
			String getUrl() {
				printFetching = true;
				String url = "http://permit.mep.gov.cn/permitExt/outside/Publicity?pageno=1";
				return url;
			}
			
			@Override
			protected void parseContent() {
				String regexMax = "<div class=\"page\">\\D*(\\d+)\\D*&nbsp;";
				item = StrUtil.findFirstMatchedItem(regexMax, source);
			}
		};
		
		return neymar.process().getItem();
	}
	
	public static List<ValuesItem> fetchPublishedCompanies(int pageNumber) {
		Extractor<ValuesItem> neymar = new Extractor<ValuesItem>() {

			@Override
			public
			String getUrl() {
				printFetching = true;
				String temp = "http://permit.mep.gov.cn/permitExt/outside/Publicity?pageno={0}";
				String url = StrUtil.occupy(temp, pageNumber);
				return url;
			}
			
			@Override
			protected void parseContent() {
				String regex = "<tr>\\s*";
				regex += "<td[^<>]+>([^<>]*)</td>\\s*";
				regex += "<td[^<>]+>([^<>]*)</td>\\s*";
				regex += "<td[^<>]+>([^<>]*)</td>\\s*";
				regex += "<td[^<>]+>([^<>]*)</td>\\s*";
				regex += "<td[^<>]+>([^<>]*)</td>\\s*";
				regex += "<td[^<>]+>([^<>]*)</td>\\s*";
				regex += "<td[^<>]+>([^<>]*)</td>\\s*";
				regex += "<td[^<>]*>\\s*<a ([^<>]+)><img [^<>]+/></a>\\s*</td>\\s*";
				regex += "</tr>";
				Matcher ma = createMatcher(regex, source);
				
				while(ma.find()) {
					ValuesItem item = new ValuesItem(ma.group(3));
					String pkid = StrUtil.findFirstMatchedItem("dataid=([a-z\\d]+)\"", ma.group(8));
					item.add(pkid);
					for(int k = 0; k < 7; k++) {
						if(k == 2) {
							continue;
						}
						String temp = ma.group(k + 1).trim();
						if(EmptyUtil.isNullOrEmpty(temp)) {
							continue;
						}
						item.add(temp);
					}
					mexItems.add(item);
				}
			}
		};
		
		return neymar.process().getItems();
	}
}
