package com.sirap.basic.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;

public class WebReader {
	
	private String urlstring;
	private String charset;
	private String serverCharset;
	private boolean isMethodPost;

	public WebReader(String url) {
		this.urlstring = url;
	}
	
	public WebReader(String url, String charset) {
		this.urlstring = url;
		this.charset = charset;
	}
	
	public boolean isMethodPost() {
		return isMethodPost;
	}

	public void setMethodPost(boolean isMethodPost) {
		this.isMethodPost = isMethodPost;
	}
	
	public String getCharset() {
		return charset;
	}

	public String getServerCharset() {
		return serverCharset;
	}

	public String readIntoString() {
		String content = null;
		try {
			BufferedReader br = createReader();

			String record;
			StringBuffer sb = new StringBuffer();
			while ((record = br.readLine()) != null) {
				if(serverCharset == null) {
					serverCharset = parseWebCharsetByMeta(record);
				}
				sb.append(record);
			}

			content = sb.toString();
			sb.setLength(0);
			br.close();
		} catch (Exception ex) {
			throw new MexException(ex);
		}

		return content;
	}
	
	public static String imageTypeOf(Map<String, List<String>> headers) {
		List<String> items = headers.get(Konstants.KEY_CONTENT_TYPE);
		if(EmptyUtil.isNullOrEmpty(items)) {
			return null;
		}
		
		String subtype = null;
		String regex = "image/([^;]+)";
		for(String item : items) {
			Matcher ma = StrUtil.createMatcher(regex, item);
			if(ma.find()) {
				subtype = ma.group(1);
				break;
			}
		}
		
		return subtype;
	}
	
	public static String applicationTypeOf(Map<String, List<String>> headers) {
		List<String> items = headers.get(Konstants.KEY_CONTENT_TYPE);
		if(EmptyUtil.isNullOrEmpty(items)) {
			return null;
		}
		
		String subtype = null;
		String regex = "application/([^;]+)";
		for(String item : items) {
			Matcher ma = StrUtil.createMatcher(regex, item);
			if(ma.find()) {
				subtype = ma.group(1);
				break;
			}
		}
		
		return subtype;
	}
	
	public static String attachmentFilenameOf(Map<String, List<String>> headers) {
		List<String> disposition = headers.get(Konstants.KEY_CONTENT_DISPOSITION);
		if(EmptyUtil.isNullOrEmpty(disposition)) {
			return null;
		}
		
		String filename = null;
		String regex = "filename=([^;]+)";
		for(String item : disposition) {
			Matcher ma = StrUtil.createMatcher(regex, item);
			if(ma.find()) {
				String temp = ma.group(1);
				temp = temp.replace("\"", "");
				temp = XCodeUtil.urlDecodeUTF8(temp);
				temp = FileUtil.generateLegalFileNameBySpace(temp);
				filename = temp.trim();
				break;
			}
		}
		
		return filename;
	}
	
	public static Map<String, List<String>> headersOf(String urlstring) {
		try {
			URL url = new URL(urlstring);
			URLConnection conn = url.openConnection();
			Map<String, List<String>> map = conn.getHeaderFields();
			
			return map;
		} catch (Exception ex) {
			throw new MexException(ex);
		}
	}
	
	public static List<String> headers(String urlstring) {
		List<String> items = Lists.newArrayList();
		try {
			URL url = new URL(urlstring);
			URLConnection conn = url.openConnection();
			Map<String, List<String>> map = conn.getHeaderFields();
			if(EmptyUtil.isNullOrEmpty(map)) {
				throw new MexException("Unavailable url: " + urlstring);
			}
			Iterator<String> it = map.keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				String value = StrUtil.connect(map.get(key));
				if(key == null) {
					key = "Status";
				}
				if(StrUtil.equals(Konstants.KEY_CONTENT_LENGTH, key)) {
					value = value + " (" + FileUtil.formatSize(Long.parseLong(value)) + ")";
					items.add(key + ": " + value);
				} else if(StrUtil.equals(Konstants.KEY_AGE, key)) {
					value = value + " (" + MathUtil.dhmsStrOfSeconds(Integer.parseInt(value)) + ")";
					items.add(key + ": " + value);
				} else {
					String utf8 = StrUtil.newString(value, Konstants.CODE_ISO88591, Konstants.CODE_UTF8);
					items.add(key + ": " + utf8);
				}
//				String utf8 = new String(value.getBytes("ISO-8859-1"), "UTF-8");
			}
		} catch (Exception ex) {
			throw new MexException(ex);
		}
		Collections.sort(items, new Comparator<String>() {

			@Override
			public int compare(String apple, String orange) {
				int v1 = valueOf(apple);
				int v2 = valueOf(orange);
				if(v1 == v2) {
					return apple.toLowerCase().compareTo(orange.toLowerCase());
				} else {
					return v1 - v2;
				}
			}
			
			public int valueOf(String apple) {
				if(apple.startsWith("Status")) {
					return -2;
				} else if(apple.startsWith("Content")) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		
		return items;
	}
	
	public List<String> readIntoList() {
		List<String> records = new ArrayList<>();
		try {
			BufferedReader br = createReader();

			String record;
			while ((record = br.readLine()) != null) {
				if(serverCharset == null) {
					serverCharset = parseWebCharsetByMeta(record);
				}
				records.add(record);
			}

			br.close();
		} catch (Exception ex) {
//			ex.printStackTrace();
			throw new MexException(ex);
		}

		return records;
	}
	
	private BufferedReader createReader() {
		BufferedReader br = null;
		try {
			URLConnection conn = new URL(urlstring).openConnection();
			equip(conn);
			String charsetInHeader = parseWebCharsetByHeader(conn);
			if(charsetInHeader != null) {
				charset = charsetInHeader;
				serverCharset = charsetInHeader;
			}
			InputStreamReader isr = new InputStreamReader(conn.getInputStream(), charset);
			br = new BufferedReader(isr);
		} catch (Exception ex) {
			String explain = XXXUtil.explainResponseException(ex.getMessage());
			String msg = ex.toString() + "\n\t url => " + urlstring;
			if(explain != null) {
				msg += "\n\tstatus code => " + explain;
			}
			
			throw new MexException(msg);
		}
		
		return br;
	}
	
	public static String parseWebCharsetByHeader(URLConnection conn) {
		Map<String, List<String>> map = conn.getHeaderFields();  
        Set<String> keys = map.keySet();  
        Iterator<String> iterator = keys.iterator();  
  
        while (iterator.hasNext()) {  
            String key = iterator.next();
            if(!StrUtil.equals("Content-Type", key)) {
            	continue;
            }
            
            List<String> items = map.get(key);
            for(String item : items) {
            	String charset = StrUtil.findFirstMatchedItem("charset=([^\\s;,]+)", item);
            	if(charset != null) {
            		return charset;
            	}
            }
        }
        
		return null;
	}
	
	public static String parseWebCharsetByMeta(String content) {
		String regex = "<meta http-equiv=\"Content-Type\" content=\"[^>]+charset=([^\\s;,\"]+)\">";
		String charset = StrUtil.findFirstMatchedItem(regex, content);
		
		return charset;
	}
	
	private void equip(URLConnection conn) throws Exception {
		conn.addRequestProperty("User-Agent", "MSIE");
//		conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
//		conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//		urlConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1WOW64rv:25.0) Gecko/20100101 Firefox/25.0");
		if(isMethodPost) {
			conn.setDoOutput(true);
			conn.setDoInput(true);
			PrintWriter out = new PrintWriter(conn.getOutputStream());
	        out.print(StrUtil.getUrlParams(urlstring));
	        out.flush();
		}
	}
	
	public static Date dateOfWebsite(String webUrl) {
		String site = StrUtil.addHttpProtoclIfNeeded(webUrl);
		try {
			if(!HttpUtil.isHttp(site)) {
				XXXUtil.alert("Not a valid web site [{0}]", webUrl);
			}
			long start = System.currentTimeMillis();
			C.pl("Fetching " + site);
            URL url = new URL(site);
            URLConnection conn = url.openConnection();
            conn.connect();
            long end = System.currentTimeMillis();
            C.pl(StrUtil.occupy("Done {0} {1}", StrUtil.secondsCost(start, end), webUrl));
            return DateUtil.dateOf(conn.getDate());
        } catch (Exception ex) {
        	D.pl(ex.getMessage() + " url: " + site);
            ex.printStackTrace();
        }
		
		return null;
	}
	
	public static void main(String[] args) {
		String sa = "http://t.cn/EqgdcyM";
		Object oa = headers(sa);
		D.pjsp(oa);
	}
}
