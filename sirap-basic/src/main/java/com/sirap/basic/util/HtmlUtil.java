package com.sirap.basic.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.data.HtmlData;

public class HtmlUtil {
	
	public static String removeHttpTag(String source) {
		XXXUtil.nullCheck(source, "source");
		String temp = source.replaceAll("<.*?>", "");
		
		return temp;
	}
	
	public static String removeComment(String source) {
		XXXUtil.nullCheck(source, "source");
		String temp = source.replaceAll("<!--.*?-->", "");
		
		return temp;
	}
	
	public static String removeBlockComment(String source) {
		XXXUtil.nullCheck(source, "source");
		String temp = source;
		for(String regex : Konstants.COMMENTS_REGEX) {
			temp = temp.replaceAll(regex, "");
		}
		
		return temp;
	}
	
	/****
	 * https://en.wikipedia.org/wiki/Unicode
	 * Unicode defines a code space of 1,114,112 code points in the range 0hex to 10FFFFhex.
	 * 
	 * http://www.cnblogs.com/fml1com/p/5149269.html
	 * @param source
	 * @return
	 */
	public static String replaceRawUnicode(String source) {
		Matcher ma = StrUtil.createMatcher("&#(\\d{1,7});", source);
		String temp = source;
		while(ma.find()) {
			int unicode = Integer.parseInt(ma.group(1));
			if(unicode == 160) {
				unicode = 32;
			}
			temp = temp.replace(ma.group(0), "" + (char)unicode);
		}
		
		return temp;
	}
	
	public static String replaceHtmlEntities(String source) {
		Matcher ma = StrUtil.createMatcher("&([a-z]{1,99});", source);
		String temp = source;
		while(ma.find()) {
			Integer unicode = HtmlData.EGGS.get(ma.group(1)).getCode();
			if(unicode != null) {
				temp = temp.replace(ma.group(0), "" + (char)unicode.intValue());
			}
		}
		
		return temp;
	}
	
	public static <E extends Object> String toSimpleHtml2(List<E> list) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<body>");
		for(E record:list) {
			sb.append("<br>" + record);
		}
		sb.append("</body>");
		sb.append("</html>");
		
		return sb.toString();
	}
	
	public static <E extends Object> String toSimpleHtml(List<E> list, boolean showSource) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<body><pre>");
		for(E record:list) {
			String temp = record + "";
			temp = temp.replace("<", "&lt;");
			temp = temp.replace(">", "&gt;");
			sb.append("<br>" + temp);
		}
		
		String body = generateLinkSection(sb.toString());
		sb.setLength(0);
		sb.append(body);
		if(showSource) {
			sb.append("<br><br><br>");
			String hostname = NetworkUtil.getLocalhost();
			String source = "<font face=\"verdana\" size=\"1\" color=\"blue\"><i>source: " + hostname + "</i></font>";
			sb.append("<br>" + source);
			String dateStr = DateUtil.strOf(new Date(), DateUtil.GMT);
			sb.append("<br>" + dateStr);
		}
		
		sb.append("</pre></body>");
		sb.append("</html>");
		
		return sb.toString();
	}
	
	public static String generateLinkSection(String record) {
		String regexLink = "<a [^<>]+>[^<>]+</a>";
		Pattern p = Pattern.compile(regexLink, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(record);
		StringBuffer sb = new StringBuffer();
		String random = RandomUtil.alphanumeric(10);
		int count = 1;
		HashMap<String, String> params = new HashMap<>();
		while(m.find()) {
			String link = m.group();
			String value = random + "@" + count;
			m.appendReplacement(sb, value);
			params.put(value, link);
			count++;
		}
		m.appendTail(sb);

		String regexHref = "https?://[^<\\\">\\s]+";
		p = Pattern.compile(regexHref, Pattern.CASE_INSENSITIVE);
		m = p.matcher(sb.toString());
		sb.setLength(0);
		
		while(m.find()) {
			String url = m.group();
			String linkTemp = "<a href=\"{0}\">{0}</a>";
			String link = StrUtil.occupy(linkTemp, url);
			m.appendReplacement(sb, link);
		}
		m.appendTail(sb);
		
		String temp = sb.toString();
		Iterator<String> it = params.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String link = params.get(key);
			temp = temp.replace(key, link);
		}
		
		return temp;
	}
	
	public static String toURLParams(Map<String, String> mapParams) {
		return toURLParams(mapParams);
	}
	
	public static String toURLParams(Map<String, String> mapParams, String encoding) {
		StringBuffer sb = new StringBuffer();
		
		List<String> keys = new ArrayList<String>(mapParams.keySet());
		for(int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = mapParams.get(key);
			if(encoding != null) {
				try {
					value = URLEncoder.encode(value, encoding);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			String pair = createURLParam(key, value);
			sb.append(pair);
			if(i != keys.size() - 1) {
				sb.append("&");
			}
		}
		
		return sb.toString();
	}
	
	public static String[] parseURLParam(String pair) {
		String[] arr = pair.split("=");
		if(arr.length == 2) {
			return arr;
		}
		
		return null;
	}
	
	public static String createURLParam(String key, String value) {
		return key + "=" + value;
	}
}
