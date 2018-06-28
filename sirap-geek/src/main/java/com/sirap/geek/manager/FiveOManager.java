package com.sirap.geek.manager;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.thread.MasterItemOriented;
import com.sirap.basic.thread.WorkerItemOriented;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.StrUtil;
import com.sirap.geek.domain.JobLocationItem;

public class FiveOManager {
	
	private static FiveOManager instance;
	
	private FiveOManager() {}
	
	public static FiveOManager g() {
		if(instance == null) {
			instance = new FiveOManager();
		}
		
		return instance;
	}
	
	public List<String> job51(String startpage) {
		Set<JobLocationItem> holder = new LinkedHashSet<>();
		String nextPage = jobIdsAndNextPage(startpage, holder);
		while(nextPage != null) {
			nextPage = jobIdsAndNextPage(nextPage, holder);
		}
		D.sink("size " + holder.size());
		List<JobLocationItem> items = Lists.newArrayList(holder);
		Map<String, Integer> names = Maps.newConcurrentMap();
		for(JobLocationItem item : items) {
			String name = item.getName();
			Integer count = names.get(name);
			if(count == null) {
				count = 0;
			}
			if(count != 0) {
				item.setName(name + (count + 1));
			}
			names.put(name, count + 1);
		}
		String json = "[" + StrUtil.connect(items, ", ") + "]";

		return JsonUtil.getPrettyTextInLines(json);
	}
	
	public String jobIdsAndNextPage(String currentpage, Set<JobLocationItem> holder) {
		ValuesItem vi = jobIdsOf51(currentpage);
		List<String> jobIds = (List<String>)vi.getByIndex(0);
//		jobIds = CollUtil.top(jobIds, 3);
		
		Object nextPageObj = vi.getByIndex(1);
		String nextPage = (String)nextPageObj;
		
		MasterItemOriented<String, String> george = new MasterItemOriented<>(jobIds, new WorkerItemOriented<String, String>() {
			@Override
			public String process(String jobid) {
				JobLocationItem item = getJobItemById(jobid);
				if(item != null) {
					holder.add(item);
				}
				return null;
			}
		});
		
		george.sitAndWait();

		return nextPage;
	}
	
	/***
	 * https://search.51job.com/list/030200,000000,0000,00,9,99,%25E8%25BD%25AF%25E4%25BB%25B6,2,1.html?lang=c&stype=&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&providesalary=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0&address=&line=&specialarea=00&from=&welfare=
	 * @param page
	 * @return
	 */
	public ValuesItem jobIdsOf51(String page) {
		Extractor<ValuesItem> neymar = new Extractor<ValuesItem>() {
			
			@Override
			public String getUrl() {
				useGBK().showFetching();
				return page;
			}

			@Override
			protected void parse() {
				String regex = "name=\"delivery_jobid\"\\s+value=\"([^\"]+)\"";
				Matcher ma = createMatcher(regex);
				List<String> ids = Lists.newArrayList();
				while(ma.find()) {
					ids.add(ma.group(1));
				}
				
				String regexNextPage = "<a href=\"([^\"]+)\">下一页</a>";
				String nextPage = StrUtil.findFirstMatchedItem(regexNextPage, source);
				ValuesItem vi = new ValuesItem();
				vi.add(ids);
				vi.add(nextPage);
				
				item = vi;
			}
		};
		
		return neymar.process().getItem();
	}
	
	public JobLocationItem getJobItemById(String jobId) {
		Extractor<JobLocationItem> neymar = new Extractor<JobLocationItem>() {
			
			@Override
			public String getUrl() {
				useGBK();
				String myUrl = "https://search.51job.com/jobsearch/bmap/map.php?jobid={0}";
				return StrUtil.occupy(myUrl, jobId);
			}

			//var g_company = {tips:"广东铭鸿数据有限公司",lat:"22.821456",lng:"108.412063",address:"青秀区民族大道157号财富国际广场2号楼18楼",city:"南宁",name:"广东铭鸿数据有限公司"};
			@Override
			protected void parse() {
				String regex = "g_company\\s*=\\s*(\\{.+?\\});";
				String info = StrUtil.findFirstMatchedItem(regex, source);
				if(info == null) {
					C.pl("Not found location info for jobId " + jobId);
					return;
				}

				String lng = valueOf("lng", info);
				String lat = valueOf("lat", info);
				String location = lng + "," + lat;
				String address = valueOf("address", info);
				if(StrUtil.isRegexMatched("0\\.[0]{1,}", lng)) {
					location = "108.392544,22.828986";
				} else {
					address += ", " + location;
				}
				String name = valueOf("name", info);
				String city = valueOf("city", info);
				JobLocationItem myitem = new JobLocationItem();
				myitem.setAddress(address);
				myitem.setCity(city);
				myitem.setLocation(location);
				myitem.setName(name);
				myitem.setPhone(jobId);
				
				item = myitem;
			}
			
			private String valueOf(String key, String info) {
				String regex = key + ":\"([^\"]*)\"";
				return StrUtil.findFirstMatchedItem(regex, info);
			}
		};
		
		return neymar.process().getItem();
	}
}
