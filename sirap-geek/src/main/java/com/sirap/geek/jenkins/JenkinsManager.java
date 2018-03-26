package com.sirap.geek.jenkins;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.domain.Link;
import com.sirap.common.framework.SimpleKonfig;

@SuppressWarnings({"rawtypes"})
public class JenkinsManager {
	
	private static JenkinsManager instance;
	
	public static final String BUILDS_RESULT_URL_TEMPLATE="{0}/job/{1}/api/json?tree=builds[*]";
	public static final String BUILDS_NUMBERT_URL_TEMPLATE="{0}/job/{1}/api/json?tree=builds[number]";
	public static final String CONSOLE_TEXT_URL_TEMPLATE="{0}/job/{1}/{2}/consoleText";

	private static final String KEY_BUILDS = "builds";
	private static final String KEY_NUMBER = "number";
	private static final String KEY_TIMESTAMP = "timestamp";
	private static final String KEY_DURATION = "duration";
	private static final String KEY_BUILDING = "building";
	private static final String KEY_RESULT = "result";
	private static final String STATUS_SUCCESS = "SUCCESS";
	private static final String STATUS_BUILDING = "BUILDING";
	private static final String STATUS_UNKNOWN = "UNKNOWN";
	
	private String url;

	public static JenkinsManager g() {
		String key = "jenkins.url";
		String url = SimpleKonfig.g().getUserValueOf(key);
		XXXUtil.nullCheck(url, key);
		
		instance = new JenkinsManager(url);
		
		return instance;
	}
	
	public static JenkinsManager g2(String url) {
		instance = new JenkinsManager(url);
		
		return instance;
	}

	public JenkinsManager(String url) {
		this.url = url;
	}
	
	public String getAllBuildsInJson(String jobName) {
		String fullUrl = StrUtil.occupy(BUILDS_RESULT_URL_TEMPLATE, url, jobName);
		C.fetching(fullUrl);
		
		String source = IOUtil.readURL(fullUrl, null);
		
		if(source == null) {
			XXXUtil.alert("This [" + jobName + "] might not be a correct Job name, please check.");
		}
		
		return source;
	}
	
	public JenkinsBuildRecord getLatestBuildRecord(String jobName) {
		List<JenkinsBuildRecord> list = getLatestKBuildRecords(jobName, 1);
		
		if(EmptyUtil.isNullOrEmpty(list)) {
			return null;
		}
		
		return list.get(0);
	}
	
	public List<JenkinsBuildRecord> getLatestKBuildRecords(String jobName, int size) {
		List<JenkinsBuildRecord> records = new ArrayList<>();

		String source = getAllBuildsInJson(jobName);
		Map values = JsonUtil.toMap(source);
		List list = (List)values.get(KEY_BUILDS);
		
		for(Object obj: list) {
			if(obj instanceof Map) {
				Map map = (Map)obj;
				JenkinsBuildRecord item = createBuildRecord(map, jobName);
				records.add(item);
				
				if(records.size() >= size) {
					break;
				}
			}
		}
		
		return records;
	}
	
	public JenkinsBuildRecord getBuildRecordByNumber(String jobName, String buildNumber) {
		String source = getAllBuildsInJson(jobName);
		Map values = JsonUtil.toMap(source);
		List list = (List)values.get(KEY_BUILDS);
		
		for(Object obj: list) {
			if(obj instanceof Map) {
				Map map = (Map)obj;
				String currentId = map.get(KEY_NUMBER) + "";
				if(StrUtil.equals(buildNumber, currentId)) {
					JenkinsBuildRecord item = createBuildRecord(map, jobName);
					
					return item;
				}
			}
		}
		
		return null;
	}
	
	private JenkinsBuildRecord createBuildRecord(Map map, String jobName) {
		String status = STATUS_UNKNOWN;
		
		if(StrUtil.equals(map.get(KEY_BUILDING) + "", "true")) {
			status = STATUS_BUILDING;
		} else if(map.get(KEY_RESULT) != null) {
			status = map.get(KEY_RESULT) + "";
		}
		
		String buildNumber = map.get(KEY_NUMBER) + "";
		JenkinsBuildRecord item = new JenkinsBuildRecord();
		item.setNumber(buildNumber);
		item.setStatus(status);
		
		Date date = DateUtil.parseLongStr(map.get(KEY_TIMESTAMP));
		String dateStr = DateUtil.displayDate(date, DateUtil.DATETIME_SPACE_TIGHT);
		item.setDateStr(dateStr);
		
		String value = map.get(KEY_DURATION) + "";
		long durationInMillis = Long.parseLong(value);
		long seconds = durationInMillis/1000; 
		item.setDuration(seconds + "");

		if(StrUtil.equals(STATUS_SUCCESS, status)) {
//			List<String> list = getUploadedFileLocations(jobName, buildNumber);
//			if(list.size() == 1) {
//				item.setExtra(list.get(0));
//			} else if(list.size() > 1) {
//				item.setExtra(list.toString());
//			}
		}
		
		return item;
	}
	
	public List<String> getUploadedFileLocations(String jobName, Object buildNumber) {
		String fullUrl = StrUtil.occupy(CONSOLE_TEXT_URL_TEMPLATE, url, jobName, buildNumber);
		List<String> list = new ArrayList<>();
		
		Extractor<Link> frank = new Extractor<Link>() {
			
			@Override
			public String getUrl() {
				return fullUrl;
			}
			
			@Override
			protected void parse() {
				String regex = "Uploaded:([^]]*?\\.(war|ear))";
				Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
				while(m.find()) {
					String value = m.group(1);
					list.add(value.trim());
				}
			}
		};
		
		frank.process();
		
		return list;
	}
}
