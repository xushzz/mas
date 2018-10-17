package com.sirap.extractor.avron;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.thread.MasterItemOriented;
import com.sirap.basic.thread.WorkerItemOriented;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;

public class AvronExtractors {
	
	public static final String AREACODE_ROOT = "00";
	
	public static String domain() {
		List<String> names = Lists.newArrayList();
//		for(int i = 0; i < 1000; i++) {
//			String temp = "k" + i;
//			names.add(temp);
//			for(int k = 0; k < 26; k++) {
//					names.add(temp + (char)('a' + k));
//			}
//		}
		
		for(int i = 0; i < 26; i++) {
			String temp = "" + (char)('a' + i);

			for(int k = 0; k < 26; k++) {
				temp = "" + (char)('a' + i) + (char)('a' + k);
				names.add(temp);
//				for(int m = 0; m < 99; m++) {
//					names.add(temp + m);
//				}
			}
		}
		names.clear();
		names = IOUtil.readFileIntoList("E:/Mas/exp/20180330_011913_zz.txt");
//		C.listSome(names, 9);
//		C.list(names);
//		C.total(names.size());;
//		names.clear();
		MasterItemOriented<String, String> george = new MasterItemOriented<>(names, new WorkerItemOriented<String, String>() {

			@Override
			public String process(String name) {
				return domain(name);
			}
		});
		
		List<String> values = george.getValidStringResults();
		Collections.sort(values);
		C.list(values);
		
		return "xxx";
	}
	
	public static String domain(String name) {
		Extractor<String> neymar = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching().usePost();
				String url = "http://whois.juming.com/?{0}.com";
				return StrUtil.occupy(url, name);
			}
			
			@Override
			protected void parse() {
				boolean isAvailable = !source.contains("注册者");
				item = name + ".com  ";
				if(isAvailable) {
					item += "yes";
				} else {
					item += "no";
				}
			}
		};
		
		return neymar.process().getItem();
	}
	
	public static String domainTecent(String name) {
		Extractor<String> neymar = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching().usePost();
				String url = "https://wss.cloud.tencent.com/buy/api/domains/domain/whois_info?mc_gtk=&domain={0}.com";
				return StrUtil.occupy(url, name);
			}
			
			@Override
			protected void parse() {
				C.pl(source);
//				String regex = "\"name\":\"([^\"]+)\"";
//				String temp = StrUtil.findFirstMatchedItem(regex, source);
				boolean isAvailable = !source.contains("info");
				item = name + ".com  ";
				if(isAvailable) {
					item += "yes";
				} else {
					item += "no";
				}
			}
		};
		
		return neymar.process().getItem();
	}
	
	public static String domainAliyun(String name) {
		Extractor<String> neymar = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				showFetching();
				String url = "https://checkapi.aliyun.com/check/checkdomain?domain={0}.com&command=&token=Ye5341df01a6d3b3b462ab7f5a7f3efd2&ua=&currency=&site=&bid=&_csrf_token=&callback=jsonp_1522308891330_11694";
				return StrUtil.occupy(url, name);
			}
			
			@Override
			protected void parse() {
//				C.pl(source);
				String regex = "\"avail\":(\\d)";
				String temp = StrUtil.findFirstMatchedItem(regex, source);
				item = name + ".com";
				if(StrUtil.equals("1", temp)) {
					item += " yes";
				} else {
					item += " no";
				}
			}
		};
		
		return neymar.process().getItem();
	}
	
	public static List<ValuesItem> areacodesOfChina() {
		List<ValuesItem> allItems = Lists.newArrayList();
		areacodesOf(allItems, AREACODE_ROOT, 0);
		
		return allItems;
	}
	
	public static List<ValuesItem> areacodesOfGX() {
		ValuesItem vi = ValuesItem.of("450000000000");
		vi.add("广西壮族自治区");
		vi.add(AREACODE_ROOT);
		vi.add(1);
		
		List<ValuesItem> allItems = Lists.newArrayList(vi);
		areacodesOf(allItems, vi.getByIndex(0) + "", Integer.parseInt(vi.getByIndex(3) + ""));
		
		return allItems;
	}
	
	private static void areacodesOf(List<ValuesItem> allItems, String topAreacode, int level) {
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
			protected void parse() {
				String regex = "<option\\s+value=['\"](\\d+)['\"]>(.+?)</option>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String code = ma.group(1);
					String name = ma.group(2);
					String parentCode = topAreacode;
					ValuesItem vi = ValuesItem.of();
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
				useList();
				return fileOrWebUrl;
			}
			
			@Override
			protected void parse() {
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
				
				item = "[" + StrUtil.connectWithCommaSpace(mexItems) + "]";
			}
		};
		
		return neymar.process().getItem();
	}
	
	public static String geoLocationsOf(String fileOrWebUrl) {
		Extractor<String> neymar = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				useList();
				return fileOrWebUrl;
			}
			
			@Override
			/****
			 * {"name": "北京市","center": "116.405285,39.904989"}
			 */
			protected void parse() {
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
				
				item = "[" + StrUtil.connectWithCommaSpace(mexItems) + "]";
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
			protected void parse() {
				ValuesItem vi = ValuesItem.of();
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
			protected void parse() {
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
				usePost().showFetching();
				String url = "http://permit.mep.gov.cn/permitExt/syssb/xxgk/xxgk!sqqlist.action?page.pageNo=" + page;
				return url;
			}
			
			@Override
			protected void parse() {
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
					ValuesItem item = ValuesItem.of();
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
			protected void parse() {
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
			protected void parse() {
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
					ValuesItem item = ValuesItem.of(ma.group(3));
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
