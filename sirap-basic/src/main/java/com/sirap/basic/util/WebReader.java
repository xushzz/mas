package com.sirap.basic.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;

public class WebReader {
	
	private String url;
	private boolean printException;
	private String charset;
	
	private String serverCharset;

	public WebReader(String url) {
		this.url = url;
	}
	
	public WebReader(String url, String charset, boolean printException) {
		this.url = url;
		this.charset = charset;
		this.printException = printException;
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
			String msg = ex + "\nURL=>" + url + "\nLocation=>" + WebReader.class.getName();
			if (printException) {
				C.pl2(msg);
			}
			
			throw new MexException(msg);
		}

		return content;
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
			if (printException) {
				C.pl2(ex + "\nURL=>" + url + "\nLocation=>" + IOUtil.class.getName());
			}
		}

		return records;
	}
	
	private BufferedReader createReader() {
		try {
			URLConnection conn = new URL(url).openConnection();
			setUserAgent(conn);
			String charsetInHeader = parseWebCharsetByHeader(conn);
			if(charsetInHeader != null) {
				charset = charsetInHeader;
				serverCharset = charsetInHeader;
			}
			InputStreamReader isr = new InputStreamReader(conn.getInputStream(), charset);
			BufferedReader br = new BufferedReader(isr);
			
			return br;
		} catch (Exception ex) {
			String msg = ex + "\nURL=>" + url + "\nLocation=>" + WebReader.class.getName();
			if (printException) {
				C.pl2(msg);
			}
			
			throw new MexException(msg);
		}
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
		String regex = "<meta http-equiv=\"Content-Type\" content=\"[^>]+ charset=([^\\s;,\"]+)\">";
		String charset = StrUtil.findFirstMatchedItem(regex, content);
		
		return charset;
	}
	
	private void setUserAgent(URLConnection urlConn) {
		urlConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1WOW64rv:25.0) Gecko/20100101 Firefox/25.0");
	}

}
