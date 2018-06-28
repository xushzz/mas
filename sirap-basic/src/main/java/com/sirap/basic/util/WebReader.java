package com.sirap.basic.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public List<String> readHeaders() {
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
				if(StrUtil.equals(Konstants.KEY_CONTENT_LENGTH, key)) {
					value = value + " (" + FileUtil.formatSize(Long.parseLong(value)) + ")";
				}
				if(key == null) {
					key = "AA-Status";
				}
				items.add(key + ": " + value);
			}
		} catch (Exception ex) {
			throw new MexException(ex);
		}
		
		return CollUtil.sortIgnoreCase(items);
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
		conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
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
			if(!MiscUtil.isHttp(site)) {
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
}
